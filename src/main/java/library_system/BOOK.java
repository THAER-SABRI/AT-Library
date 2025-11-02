package library_system;

public class BOOK {
    private String title;
    private String author;
    private String isbn;

    public BOOK(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
}
