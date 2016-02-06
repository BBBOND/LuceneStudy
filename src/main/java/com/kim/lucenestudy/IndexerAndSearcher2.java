package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/5.
 */
public class IndexerAndSearcher2 {

    private Integer[] ids = {1, 2, 3};
    private String[] citys = {"changchun", "ningbo", "taizhou"};
    private String[] descs = {
            "Changchun is cold b!",
            "Ningbo is cold too c!",
            "Taizhou is my homeland d!"
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
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器
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
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene4"));
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document document = new Document();
            document.add(new IntField("id", ids[i], Field.Store.YES));
            document.add(new StringField("city", citys[i], Field.Store.YES));
            document.add(new TextField("desc", descs[i], Field.Store.NO));
            writer.addDocument(document);//添加文档
        }
        System.out.println("写入了 " + writer.numDocs() + " 个文档");
        writer.close();
    }

    private IndexReader getReader() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene4"));
        return DirectoryReader.open(directory);
    }

    /**
     * 指定项范围搜索 TermRangeQuery
     *
     * @throws Exception
     */
    @Test
    public void termRangeQuery() throws Exception {
        reader = getReader();
        TermRangeQuery query = new TermRangeQuery("desc", new BytesRef("b".getBytes()), new BytesRef("d".getBytes()), true, true);
        searcher = new IndexSearcher(reader);
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配花费" + (end - start) + " 毫秒,供查询到 " + hits.totalHits + " 个文档");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("id") + "-->" + document.get("city"));
        }
    }

}
