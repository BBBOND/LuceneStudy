package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/5.
 */
public class Searcher {
    public static void search(String indexDir, String q) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("contents", analyzer);
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs hits = indexSearcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配 "+q+" 共花费 "+(end-start)+" 毫秒,查询到 "+hits.totalHits+" 条记录");
        for (ScoreDoc scoreDoc : hits.scoreDocs){
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("fullPath"));
        }
        reader.close();
    }

    public static void main(String[] args) {
        String indexDir = "C:\\Users\\伟阳\\Desktop\\Lucene";
        String q = "UNESCO";
        try {
            search(indexDir,q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
