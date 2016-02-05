package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/4.
 */
public class Indexer {
    private IndexWriter writer; //些索引实例

    /**
     * 实例化indexWriter
     *
     * @param indexDir
     * @throws Exception
     */
    public Indexer(String indexDir) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, indexWriterConfig);

    }

    /**
     * 关闭indexWriter
     *
     * @throws Exception
     */
    public void close() throws Exception {
        writer.close();
    }

    /**
     * 索引指定目录的所有文件
     *
     * @param dataDir
     * @throws Exception
     */
    public int index(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            indexFile(file);
        }
        return writer.numDocs();
    }

    /**
     * 索引指定文件
     *
     * @param file
     */
    private void indexFile(File file) throws Exception {
        System.out.println("索引文件:" + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    /**
     * 获取文档，文档里再设置每个字段
     *
     * @param file
     */
    private Document getDocument(File file) throws Exception {
        Document document = new Document();
        document.add(new TextField("contents", new FileReader(file)));
        document.add(new TextField("fileName", file.getName(), Field.Store.YES));
        document.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES));
        return document;
    }

    public static void main(String[] args) {
        Indexer indexer = null;
        int numIndexed = 0;
        long start = System.currentTimeMillis();
        try {
            indexer = new Indexer("C:\\Users\\伟阳\\Desktop\\Lucene");
            numIndexed = indexer.index("C:\\Users\\伟阳\\Desktop\\Lucene\\data");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (indexer != null) {
                try {
                    indexer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }
}
