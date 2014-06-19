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
    /* 创建简单中文分析器 创建索引使用的分词器必须和查询时候使用的分词器一样，否则查询不到想要的结果 */  
    private static Analyzer analyzer = new IKAnalyzer(true);  
    // 索引保存目录  
    private static File indexFile = new File("indexs/");  
  
    /** 
     * 创建索引文件到磁盘中永久保存 
     */  
    public static void createIndexFile(List<Book> bookList) {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************创建索引开始**********************");  
        Directory directory = null;  
        IndexWriter indexWriter = null;  
        try {  
            // 创建哪个版本的IndexWriterConfig，根据参数可知lucene是向下兼容的，选择对应的版本就好  
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);  
            // 创建磁盘目录对象  
            directory = new SimpleFSDirectory(indexFile);  
            indexWriter = new IndexWriter(directory, indexWriterConfig);  
            // indexWriter = new IndexWriter(directory, analyzer, true,IndexWriter.MaxFieldLength.UNLIMITED);  
            // 这上面是使用内存保存索引的创建索引写入对象的例子，和这里的实现方式不一样，但是效果是一样的  
  
            // 为了避免重复插入数据，每次测试前 先删除之前的索引  
            indexWriter.deleteAll();  
            // 获取实体对象  
            for (int i = 0; i < bookList.size(); i++) {  
                Book Book = bookList.get(i);  
                // indexWriter添加索引  
                Document doc = new Document();

                doc.add(new Field("id", Book.getId().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED));  
                doc.add(new Field("cover", Book.getCover().toString(),Field.Store.YES, Field.Index.NOT_ANALYZED));  
                doc.add(new Field("title", Book.getTitle().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("author", Book.getAuthor().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("category", Book.getCategory().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                doc.add(new Field("info", Book.getInfo().toString(),Field.Store.YES, Field.Index.ANALYZED));  
                // 添加到索引中去  
                indexWriter.addDocument(doc);  
                // System.out.println("索引添加成功：第" + (i + 1) + "次！！");  
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
        // System.out.println("创建索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        // System.out.println("*****************创建索引结束**********************");  
    }  
  
    /** 
     * 直接读取索引文件，查询索引记录 
     *  
     * @throws IOException 
     */  
    public static void openIndexFile() {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************读取索引开始**********************");  
        List<Book> Books = new ArrayList<Book>();  
        // 得到索引的目录  
        Directory directory = null;  
        IndexReader indexReader = null;  
        try {  
            directory = new SimpleFSDirectory(indexFile);  
            // 根据目录打开一个indexReader  
            indexReader = IndexReader.open(directory);  
            //indexReader = IndexReader.open(directory,false);  
            System.out.println("在索引文件中总共插入了" + indexReader.maxDoc() + "条记录。");  
            // 获取第一个插入的document对象  
            // Document minDoc = indexReader.document(0);  
            // 获取最后一个插入的document对象  
            // Document maxDoc = indexReader.document(indexReader.maxDoc() - 1);  
            // document对象的get(字段名称)方法获取字段的值  
            // System.out.println("第一个插入的document对象的标题是：" + minDoc.get("title"));  
            // System.out.println("最后一个插入的document对象的标题是：" + maxDoc.get("title"));  
            //indexReader.deleteDocument(0);  
            int docLength = indexReader.maxDoc();  
            for (int i = 0; i < docLength; i++) {  
                Document doc = indexReader.document(i);  
                Book Book = new Book();  
                if (doc.get("id") == null) {  
                    System.out.println("id为空");  
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
            // System.out.println("显示所有插入的索引记录：");  
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
        // System.out.println("直接读取索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        // System.out.println("*****************读取索引结束**********************");  
    }  
  
    /** 
     * 查看IKAnalyzer 分词器是如何将一个完整的词组进行分词的 
     *  
     * @param text 
     * @param isMaxWordLength 
     */  
    public static List<String> splitWord(String text) {  
        try {
        	List<String> list = new ArrayList<String>();
            // 创建分词对象  
            StringReader reader = new StringReader(text);  
            // 分词  
            TokenStream ts = analyzer.tokenStream("", reader);  
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
            // 遍历分词数据  
            // System.out.print("IKAnalyzer把关键字拆分的结果是：");  
            while (ts.incrementToken()) {  
                // System.out.print("【" + term.toString() + "】");  
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
     * 根据关键字实现全文检索 
     */  
    public static List<Book> search(String keyword) {  
        // long startTime = System.currentTimeMillis();  
        // System.out.println("*****************查询索引开始**********************");  
        IndexReader indexReader = null;  
        IndexSearcher indexSearcher = null;  
        List<Book> bookList = new ArrayList<Book>();  
        try {  
            indexReader = IndexReader.open(FSDirectory.open(indexFile));  
            // 创建一个排序对象，其中SortField构造方法中，第一个是排序的字段，第二个是指定字段的类型，第三个是是否升序排列，true：升序，false：降序。  
            // Sort sort = new Sort(new SortField[] {new SortField("title", SortField.STRING, false), new SortField("author", SortField.STRING, false) });  
            Sort sort = new Sort();
            // 创建搜索类  
            indexSearcher = new IndexSearcher(indexReader);  
            // 下面是创建QueryParser 查询解析器  
            // QueryParser支持单个字段的查询，但是MultiFieldQueryParser可以支持多个字段查询，建议用后者这样可以实现全文检索的功能。  
            // QueryParser queryParser = new QueryParser(Version.LUCENE_36, "title", analyzer);  
            QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, new String[] {"title", "author", "category" ,"info"}, analyzer);  
            // 利用queryParser解析传递过来的检索关键字，完成Query对象的封装  
            Query query = queryParser.parse(keyword);  
            // splitWord(keyword); // 显示拆分结果  
            // 执行检索操作  
            TopDocs topDocs = indexSearcher.search(query, 100, sort);  
            System.out.println("一共查到:" + topDocs.totalHits + "记录");  
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
            // 像百度，谷歌检索出来的关键字如果有，除了显示在列表中之外还会高亮显示。Lucenen也支持高亮功能，正常应该是<font color='red'></font>这里用【】替代，使效果更加明显  
            SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("【", "】");  
            // 具体怎么实现的不用管，直接拿来用就好了。  
            Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query));  
  
            for (int i = 0; i < scoreDoc.length; i++) {  
                // 内部编号 ,和数据库表中的唯一标识列一样  
                int doc = scoreDoc[i].doc;  
                // 根据文档id找到文档  
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
                    // 传递的长度表示检索之后匹配长度，这个会导致返回的内容不全  
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
                // 需要注意的是 如果使用了高亮显示的操作，查询的字段中没有需要高亮显示的内容 highlighter会返回一个null回来。  
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
        System.out.println("根据关键字" + keyword + "检索到的结果如下：");  
        for (Book book : bookList) {  
            System.out.println(book);
            // Img.show(book.getCover());
        }  
        return bookList;
        // long endTime = System.currentTimeMillis();  
        // System.out.println("全文索引文件成功，总共花费" + (endTime - startTime) + "毫秒。");  
        // System.out.println("*****************查询索引结束**********************");  
    }  
}  