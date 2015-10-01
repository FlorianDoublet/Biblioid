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
    protected Boolean isNew;

    public Book (String title, String author, String isbn, String image) {
        super();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.image = image;
        this.isNew = false;
    }

    public boolean isNew() {
        return isNew;
    }

    public Book(){
        super();
        this.isNew = true;
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
        this.isNew = false;
    }

    public void setImage(String image) {
        this.image = image;
        this.isNew = false;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        this.isNew = false;
    }

    public void setTitle(String title) {
        this.title = title;
        this.isNew = false;
    }
}
