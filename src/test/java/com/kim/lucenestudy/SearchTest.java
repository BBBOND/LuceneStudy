package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/5.
 */
public class SearchTest {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Before
    public void Setup() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene"));
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);

    }

    @After
    public void tearDown() throws Exception {
        reader.close();
    }

    /**
     * 对特定项搜索
     *
     * @throws Exception
     */
    @Test
    public void testTermQuery() throws Exception {
        String searchField = "contents";
        String q = "china";
        Term t = new Term(searchField, q);
        Query query = new TermQuery(t);
        TopDocs hits = searcher.search(query, 10);
        System.out.println("匹配'" + q + "',供查询到 " + hits.totalHits + " 个文档");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("fullPath"));
        }
    }

    /**
     * 解析查询表达式
     *
     * @throws Exception
     */
    @Test
    public void testQueryParsers() throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        String searchField = "contents";
//        String q = "china possibility";//或
//        String q = "china AND possibility";//并且
        String q = "chin~";//相近
        QueryParser parser = new QueryParser(searchField, analyzer);
        Query query = parser.parse(q);
        TopDocs hits = searcher.search(query, 10);
        System.out.println("匹配'" + q + "',供查询到 " + hits.totalHits + " 个文档");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("fullPath"));
        }
    }

    /**
     * 分页查询
     *
     * @throws Exception
     */
    @Test
    public void testPagesQuery() throws Exception {
        // TODO: 2016/2/5 每次获取100，通过判断进行分页
    }
}
