package com.fatty.searcher;

import java.io.File;  
import java.io.IOException;  
import java.io.StringReader;  
import java.util.ArrayList;  
import java.util.List;  

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field;  
import org.apache.lucene.index.CorruptIndexException;  
import org.apache.lucene.index.IndexReader;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
import org.apache.lucene.queryParser.MultiFieldQueryParser;  
import org.apache.lucene.queryParser.ParseException;  
import org.apache.lucene.queryParser.QueryParser;  
import org.apache.lucene.search.IndexSearcher;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.ScoreDoc;  
import org.apache.lucene.search.Sort;  
import org.apache.lucene.search.SortField;  
import org.apache.lucene.search.TopDocs;  
import org.apache.lucene.search.highlight.Highlighter;  
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;  
import org.apache.lucene.search.highlight.QueryScorer;  
// import org.apache.lucene.search.highlight.SimpleFragmenter;  
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;  
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.FSDirectory;  
import org.apache.lucene.store.SimpleFSDirectory;  
import org.apache.lucene.util.Version;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  

import com.fatty.book.Book;
import com.fatty.image.Img;
  
public class Searcher {  
    /* ���������ķ����� ��������ʹ�õķִ�������Ͳ�ѯʱ��ʹ�õķִ���һ���������ѯ������Ҫ�Ľ�� */  
    private static Analyzer analyzer = new IKAnalyzer(true);  
    // ��������Ŀ¼  
    private static File indexFile = new File("indexs/");  
  
    /** 
     * ���������ļ������������ñ��� 
     */  
    public static void createIndexFile(List<Book> bookList) {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************����������ʼ**********************");  
        Directory directory = null;  
        IndexWriter indexWriter = null;  
        try {  
            // �����ĸ��汾��IndexWriterConfig�����ݲ�����֪lucene�����¼��ݵģ�ѡ���Ӧ�İ汾�ͺ�  
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);  
            // ��������Ŀ¼����  
            directory = new SimpleFSDirectory(indexFile);  
            indexWriter = new IndexWriter(directory, indexWriterConfig);  
            // indexWriter = new IndexWriter(directory, analyzer, true,IndexWriter.MaxFieldLength.UNLIMITED);  
            // ��������ʹ���ڴ汣�������Ĵ�������д���������ӣ��������ʵ�ַ�ʽ��һ��������Ч����һ����  
  
            // Ϊ�˱����ظ��������ݣ�ÿ�β���ǰ ��ɾ��֮ǰ������  
            indexWriter.deleteAll();  
            // ��ȡʵ�����  
            for (int i = 0; i < bookList.size(); i++) {  
                Book Book = bookList.get(i);  
                // indexWriter�������  
                Document doc = new Document();

                doc.add(new Field("id", Book.getId().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED));  
                doc.add(new Field("cover", Book.getCover().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED));  
                doc.add(new Field("title", Book.getTitle().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("author", Book.getAuthor().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("category", Book.getCategory().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("info", Book.getInfo().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                // ��ӵ�������ȥ  
                indexWriter.addDocument(doc);  
                // System.out.println("������ӳɹ�����" + (i + 1) + "�Σ���");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexWriter != null) {  
                try {  
                    indexWriter.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (directory != null) {  
                try {  
                    directory.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        // long endTime = System.currentTimeMillis();
        // System.out.println("���������ļ��ɹ����ܹ�����" + (endTime - startTime) + "���롣");  
        // System.out.println("*****************������������**********************");  
    }  
  
    /** 
     * ֱ�Ӷ�ȡ�����ļ�����ѯ������¼ 
     *  
     * @throws IOException 
     */  
    public static void openIndexFile() {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************��ȡ������ʼ**********************");  
        List<Book> Books = new ArrayList<Book>();  
        // �õ�������Ŀ¼  
        Directory directory = null;  
        IndexReader indexReader = null;  
        try {  
            directory = new SimpleFSDirectory(indexFile);  
            // ����Ŀ¼��һ��indexReader  
            indexReader = IndexReader.open(directory);  
            //indexReader = IndexReader.open(directory,false);  
            System.out.println("�������ļ����ܹ�������" + indexReader.maxDoc() + "����¼��");  
            // ��ȡ��һ�������document����  
            // Document minDoc = indexReader.document(0);  
            // ��ȡ���һ�������document����  
            // Document maxDoc = indexReader.document(indexReader.maxDoc() - 1);  
            // document�����get(�ֶ�����)������ȡ�ֶε�ֵ  
            // System.out.println("��һ�������document����ı����ǣ�" + minDoc.get("title"));  
            // System.out.println("���һ�������document����ı����ǣ�" + maxDoc.get("title"));  
            //indexReader.deleteDocument(0);  
            int docLength = indexReader.maxDoc();  
            for (int i = 0; i < docLength; i++) {  
                Document doc = indexReader.document(i);  
                Book Book = new Book();  
                if (doc.get("id") == null) {  
                    System.out.println("idΪ��");  
                } else {  
                    Book.setId(Integer.parseInt(doc.get("id")));  
                    Book.setCover(doc.get("cover"));  
                    Book.setTitle(doc.get("title"));  
                    Book.setAuthor(doc.get("author"));  
                    Book.setCategory(doc.get("category"));  
                    Book.setInfo(doc.get("info"));  
                    Books.add(Book);  
                }  
            }  
            // System.out.println("��ʾ���в����������¼��");  
            for (Book Book : Books) {  
                System.out.println(Book);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexReader != null) {  
                try {  
                    indexReader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (directory != null) {  
                try {  
                    directory.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        // long endTime = System.currentTimeMillis();  
        // System.out.println("ֱ�Ӷ�ȡ�����ļ��ɹ����ܹ�����" + (endTime - startTime) + "���롣");  
        // System.out.println("*****************��ȡ��������**********************");  
    }  
  
    /** 
     * �鿴IKAnalyzer �ִ�������ν�һ�������Ĵ�����зִʵ� 
     *  
     * @param text 
     * @param isMaxWordLength 
     */  
    public static List<String> splitWord(String text) {  
        try {
        	List<String> list = new ArrayList<String>();
            // �����ִʶ���  
            StringReader reader = new StringReader(text);  
            // �ִ�  
            TokenStream ts = analyzer.tokenStream("", reader);  
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
            // �����ִ�����  
            // System.out.print("IKAnalyzer�ѹؼ��ֲ�ֵĽ���ǣ�");  
            while (ts.incrementToken()) {  
                // System.out.print("��" + term.toString() + "��");  
            	list.add(term.toString());
            }  
            reader.close();  
            return list;
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // System.out.println();  
		return null;
    }  
  
    /** 
     * ���ݹؼ���ʵ��ȫ�ļ��� 
     */  
    public static List<Book> search(String keyword) {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************��ѯ������ʼ**********************");  
        IndexReader indexReader = null;  
        IndexSearcher indexSearcher = null;  
        List<Book> bookList = new ArrayList<Book>();  
        try {  
            indexReader = IndexReader.open(FSDirectory.open(indexFile));  
            // ����һ�������������SortField���췽���У���һ����������ֶΣ��ڶ�����ָ���ֶε����ͣ����������Ƿ��������У�true������false������  
            // Sort sort = new Sort(new SortField[] {new SortField("title", SortField.STRING, false), new SortField("author", SortField.STRING, false) });  
            Sort sort = new Sort();
            // ����������  
            indexSearcher = new IndexSearcher(indexReader);  
            // �����Ǵ���QueryParser ��ѯ������  
            // QueryParser֧�ֵ����ֶεĲ�ѯ������MultiFieldQueryParser����֧�ֶ���ֶβ�ѯ�������ú�����������ʵ��ȫ�ļ����Ĺ��ܡ�  
            // QueryParser queryParser = new QueryParser(Version.LUCENE_36, "title", analyzer);  
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, new String[] {"title", "author", "category" ,"info"}, analyzer);  
            // ����queryParser�������ݹ����ļ����ؼ��֣����Query����ķ�װ  
            Query query = queryParser.parse(keyword);  
            // splitWord(keyword); // ��ʾ��ֽ��  
            // ִ�м�������  
            TopDocs topDocs = indexSearcher.search(query, 100, sort);  
            System.out.println("һ���鵽:" + topDocs.totalHits + "��¼");  
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
            // ��ٶȣ��ȸ���������Ĺؼ�������У�������ʾ���б���֮�⻹�������ʾ��LucenenҲ֧�ָ������ܣ�����Ӧ����<font color='red'></font>�����á��������ʹЧ����������  
            SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("��", "��");  
            // ������ôʵ�ֵĲ��ùܣ�ֱ�������þͺ��ˡ�  
            Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query));  
  
            for (int i = 0; i < scoreDoc.length; i++) {  
                // �ڲ���� ,�����ݿ���е�Ψһ��ʶ��һ��  
                int doc = scoreDoc[i].doc;  
                // �����ĵ�id�ҵ��ĵ�  
                Document mydoc = indexSearcher.doc(doc);  
  
                String id = mydoc.get("id");  
				String cover = mydoc.get("cover");
                String title = mydoc.get("title");  
				String author = mydoc.get("author");
        		String category = mydoc.get("category");
				String info = mydoc.get("info");

                TokenStream tokenStream = null;  
                if (title != null && !title.equals("")) {  
                    tokenStream = analyzer.tokenStream("title",new StringReader(title));  
                    // ���ݵĳ��ȱ�ʾ����֮��ƥ�䳤�ȣ�����ᵼ�·��ص����ݲ�ȫ  
                    // highlighter.setTextFragmenter(new SimpleFragmenter(title.length()));   
                    title = highlighter.getBestFragment(tokenStream, title);  
                }  
                if (author!= null && !author.equals("")) {  
                    tokenStream = analyzer.tokenStream("author",new StringReader(author));  
                    author = highlighter.getBestFragment(tokenStream, author);  
                }  
                if (category!= null && !category.equals("")) {  
                    tokenStream = analyzer.tokenStream("category",new StringReader(category));  
                    category = highlighter.getBestFragment(tokenStream, category);  
                }  
                if (info!= null && !info.equals("")) {  
                    tokenStream = analyzer.tokenStream("info",new StringReader(info));  
                    info = highlighter.getBestFragment(tokenStream, info);  
                }  
                // ��Ҫע����� ���ʹ���˸�����ʾ�Ĳ�������ѯ���ֶ���û����Ҫ������ʾ������ highlighter�᷵��һ��null������  
                bookList.add(new Book(Integer.valueOf(id),
        				cover == null ? mydoc.get("cover") : cover,
                		title == null ? mydoc.get("title") : title,
        				author == null ? mydoc.get("author") : author,
        				category == null ? mydoc.get("category") : category,
        				info == null ? mydoc.get("info") : info));
            }  
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InvalidTokenOffsetsException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } finally {  
            if (indexSearcher != null) {  
                try {  
                    indexSearcher.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (indexReader != null) {  
                try {  
                    indexReader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        System.out.println("���ݹؼ���" + keyword + "�������Ľ�����£�");  
        for (Book book : bookList) {  
            System.out.println(book);
            // Img.show(book.getCover());
        }  
        return bookList;
        // long endTime = System.currentTimeMillis();  
        // System.out.println("ȫ�������ļ��ɹ����ܹ�����" + (endTime - startTime) + "���롣");  
        // System.out.println("*****************��ѯ��������**********************");  
    }  
}  