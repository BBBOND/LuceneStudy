package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Paths;

/**
 * 中文分词
 * Created by 伟阳 on 2016/2/7.
 */
public class IndexerAndSearcher3 {

    private Integer[] ids = {1, 2, 3};
    private String[] citys = {"长春", "宁波", "台州"};
    private String[] descs = {
            "长春很冷!",
            "宁波也很冷!",
            "台州是我的家乡!"
    };

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    /**
     * 获取IndexWriter对象
     *
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() throws Exception {
//        Analyzer analyzer = new StandardAnalyzer(); //标准分词器
        Analyzer analyzer = new SmartChineseAnalyzer();//中文分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, indexWriterConfig);
    }

    /**
     * 生成索引
     *
     * @throws Exception
     */
    @Test
    public void index() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene5"));
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document document = new Document();
            document.add(new IntField("id", ids[i], Field.Store.YES));
            document.add(new StringField("city", citys[i], Field.Store.YES));
            document.add(new TextField("desc", descs[i], Field.Store.YES));
            writer.addDocument(document);
        }
        System.out.println("写入了 " + writer.numDocs() + " 个文档");
        writer.close();
    }

    /**
     * 中文分词搜索
     *
     * @throws Exception
     */
    @Test
    public void search() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene5"));
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser("desc", analyzer);
        Query query = parser.parse("冷");
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配花费" + (end - start) + " 毫秒,供查询到 " + hits.totalHits + " 个文档");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("id") + "-->" + document.get("city"));
        }
    }

    /**
     * 中文分词搜索高亮显示
     *
     * @throws Exception
     */
    @Test
    public void highlighterSearch() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene5"));
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser = new QueryParser("desc", analyzer);
        Query query = parser.parse("冷");
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配花费" + (end - start) + " 毫秒,供查询到 " + hits.totalHits + " 个文档");

        //设置高亮显示
        //设置显示高亮的样式
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
        //获取得分
        QueryScorer scorer = new QueryScorer(query);
        //获取得分高的片段
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);

        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
//            System.out.println(document.get("id") + "-->" + document.get("city"));
            String desc = document.get("desc");
            if (desc != null) {
                //显示权重高的片段  摘要
                TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
                System.out.println(highlighter.getBestFragment(tokenStream, desc));
            }
        }
    }
}
