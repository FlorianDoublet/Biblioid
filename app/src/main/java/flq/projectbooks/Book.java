package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class Book implements Serializable {
    protected String title;
    protected String author;
    protected String isbn;
    protected String image;

    public Book (String title, String author, String isbn, String image) {
        super();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.image = image;
    }

    public Book(){
        super();
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
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

    public void setImage(String image) {
        this.image = image;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
