package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class Book implements Serializable {
    /*static int nbLivre = 0;
    protected int id;
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
        id = nbLivre;
        nbLivre++;
    }

    public boolean isNew() {
        return isNew;
    }

    public Book(){
        super();
        this.isNew = true;
        id = nbLivre;
        nbLivre++;
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
    }*/

    private long id;
    private String title;
    private String author;
    private String isbn;
    private String image;
    private String description;

    public Book(){
        id = -1;
        title = "";
        author = "";
        isbn = "";
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title;
    }
}
