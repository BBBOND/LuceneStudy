package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/5.
 */
public class IndexerAndSearcher {


    private String[] ids = {"1", "2", "3", "4"};
    private String[] author = {"Jack", "Marry", "John", "Kim"};
    private String[] positions = {"accounting", "technician", "salesperson", "boss"};
    private String[] citys = {"changchun", "ningbo", "taizhou", "shanghai"};
    private String[] titles = {"About Changchun", "About Ningbo", "About Taizhou", "About Shanghai"};
    private String[] contents = {
            "Changchun is cold!",
            "Ningbo is cold too!",
            "Taizhou is my homeland!",
            "Shanghai is business city!"
    };

    private Directory directory;

    /**
     * 获取IndexWriter对象
     *
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() throws Exception {
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, indexWriterConfig);
    }

    /**
     * 生成索引(带权值)
     *
     * @throws Exception
     */
    @Test
    public void index() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene3"));
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document document = new Document();
            document.add(new StringField("id", ids[i], Field.Store.YES));
            document.add(new StringField("author", author[i], Field.Store.YES));
            document.add(new StringField("position", positions[i], Field.Store.YES));
            TextField field = new TextField("title", titles[i], Field.Store.YES);
            if ("boss".equals(positions[i])) {
                field.setBoost(1.5f);
            }
            document.add(field);
//            document.add(new TextField("title", titles[i], Field.Store.YES));
            document.add(new TextField("content", contents[i], Field.Store.NO));
            writer.addDocument(document);//添加文档
        }
        writer.close();
    }

    /**
     * TermQuery查询
     *
     * @throws Exception
     */
    @Test
    public void search() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene3"));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        String searchField = "title";
        String q = "about";
        Term t = new Term(searchField, q);
        Query query = new TermQuery(t);
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配'" + q + "'花费" + (end - start) + " 毫秒,供查询到 " + hits.totalHits + " 个文档");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("author") + " " + document.get("title"));
        }
        reader.close();
    }
}
