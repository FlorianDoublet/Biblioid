package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {

    protected String title;
    protected String author;
    protected String isbn;

    public BookFilter (String title, String author, String isbn) {
        super();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public BookFilter(){
        super();
        this.title = "";
        this.author = "";
        this.isbn = "";
    }

    public String getAuthor() {
        return author;
    }


    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean IsSelected(Book book){
        boolean returnValue = true;
        if(!author.equals("") && !book.getAuthor().equals(author)){
            returnValue = false;
        }
        if(!title.equals("") && !book.getTitle().equals(title)){
            returnValue = false;
        }
        if(!isbn.equals("") && !book.getIsbn().equals(isbn)){
            returnValue = false;
        }
        return returnValue;
    }
}
