package com.fatty.book;

public class Book {
	private Integer id;
	private String cover;
    private String title;
    private String author;
    private String category;
    private String info;
      
    public Book() {  
        super();  
    }  
    public Book(Integer id, String cover, String title, String author, String category, String info) {  
        super();  
        this.id = id;
        this.cover = cover;
        this.title = title;
        this.author = author;
        this.category = category;
        this.info = info;
    }  
    public synchronized Integer getId() {  
        return id;
    }  
    public synchronized void setId(Integer id) {  
        this.id = id;
    }  
    public synchronized String getCover() {  
        return cover;
    }  
    public synchronized void setCover(String cover) {  
        this.cover = cover;
    }  
    public synchronized String getTitle() {  
        return title;
    }  
    public synchronized void setTitle(String title) {  
        this.title = title;
    }  
    public synchronized String getAuthor() {  
        return author;
    }  
    public synchronized void setAuthor(String author) {  
        this.author = author;  
    }  
    public synchronized String getCategory() {  
        return category;  
    }  
    public synchronized void setCategory(String category) {  
        this.category = category;
    }
    public synchronized String getInfo() {  
        return info;  
    }  
    public synchronized void setInfo(String info) {  
        this.info = info;
    }
    @Override
    public String toString() {  
        return "Book [id: " + id + ", title: " + title + ", cover: " + cover + ", author: " + author + ", category: " + category + ", info: " + info + "]";  
    }  
}  
