package com.kim.lucenestudy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by 伟阳 on 2016/2/5.
 */
public class Indexer2 {

    private String[] ids = {"1", "2", "3"};
    private String[] citys = {"changchun", "ningbo", "taizhou"};
    private String[] descs = {
            "Changchun is cold!",
            "Ningbo is cold too!",
            "Taizhou is my homeland!"
    };

    private Directory directory;

    @Before
    public void setUp() throws Exception {
        directory = FSDirectory.open(Paths.get("F:\\Lucene\\Lucene2"));
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document document = new Document();
            document.add(new StringField("id", ids[i], Field.Store.YES));
            document.add(new StringField("city", citys[i], Field.Store.YES));
            document.add(new TextField("desc", descs[i], Field.Store.NO));
            writer.addDocument(document);//添加文档
        }
        writer.close();
    }

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
     * 测试写入几个文档
     *
     * @throws Exception
     */
    @Test
    public void testIndexWriter() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("写入了 " + writer.numDocs() + " 个文档");
        writer.close();
    }

    /**
     * 测试读取文档
     *
     * @throws Exception
     */
    @Test
    public void testIndexReader() throws Exception {
        IndexReader reader = DirectoryReader.open(directory);
        System.out.println("最大文档数:" + reader.maxDoc());
        System.out.println("实际文档数:" + reader.numDocs());
        reader.close();
    }

    /**
     * 在合并前测试删除
     * @throws Exception
     */
    @Test
    public void testDeleteBeforeMerge() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("删除前:" + writer.numDocs());
        writer.deleteDocuments(new Term("id", "1"));
        writer.commit();
        System.out.println("最大文档数:" + writer.maxDoc());
        System.out.println("实际文档数:" + writer.numDocs());
        writer.close();
    }

    /**
     * 在合并后测试删除
     * @throws Exception
     */
    @Test
    public void testDeleteAfterMerge() throws Exception {
        IndexWriter writer = getWriter();
        System.out.println("删除前:" + writer.numDocs());
        writer.deleteDocuments(new Term("id", "1"));
        writer.forceMergeDeletes();//合并 强制删除
        System.out.println("最大文档数:" + writer.maxDoc());
        System.out.println("实际文档数:" + writer.numDocs());
        writer.close();
    }

    /**
     * 测试更新
     * @throws Exception
     */
    @Test
    public void testUpdate()throws Exception{
        IndexWriter writer = getWriter();
        Document document = new Document();
        document.add(new StringField("id", "1", Field.Store.YES));
        document.add(new StringField("city", "changchun", Field.Store.YES));
        document.add(new TextField("desc", "Changchun is very cold!", Field.Store.NO));
        writer.updateDocument(new Term("id","1"),document);
        writer.close();
    }
}
