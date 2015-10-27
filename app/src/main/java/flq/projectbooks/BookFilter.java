package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {
    private long id;
    private String title;
    private String author;
    private String isbn;

    public BookFilter(){
        id = -1;
        title = "";
        author = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSelected(Book book){
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
