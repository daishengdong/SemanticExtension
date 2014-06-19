package com.fatty.book;

import java.io.File;   
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;   
import javax.xml.parsers.DocumentBuilderFactory;   

import org.w3c.dom.Document;   
import org.w3c.dom.NodeList;   

public class BookReader {   
	public static List<Book> loadBooks() {   
		try { 
			File f = new File("data/books.xml");   
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			DocumentBuilder builder = factory.newDocumentBuilder();   
			Document doc = builder.parse(f);   
			NodeList nl = doc.getElementsByTagName("book");   
			List<Book> bookList = new ArrayList<Book>();
			for (int i = 0; i < nl.getLength(); ++i) {
				Integer id = Integer.parseInt(doc.getElementsByTagName("id").item(i).getFirstChild().getNodeValue());
				String cover = doc.getElementsByTagName("cover").item(i).getFirstChild().getNodeValue();
				String title = doc.getElementsByTagName("title").item(i).getFirstChild().getNodeValue();
				String author = doc.getElementsByTagName("author").item(i).getFirstChild().getNodeValue();
				String category = doc.getElementsByTagName("category").item(i).getFirstChild().getNodeValue();
				String info = doc.getElementsByTagName("info").item(i).getFirstChild().getNodeValue();
				bookList.add(new Book(id, cover, title, author, category, info));
			} 
			return bookList;
		} catch (Exception e) {   
			e.printStackTrace();   
		}
		return null;
	}   
} 