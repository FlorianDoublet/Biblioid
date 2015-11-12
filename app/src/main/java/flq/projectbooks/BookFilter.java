package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {
    private long id;
    private String title;
    private String author;
    private String description;

    public BookFilter(){
        id = -1;
        title = "";
        author = "";
        description = "";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String isbn) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSelected(Book book){
        boolean returnValue = true;
        if(!author.equals("") && !book.getAuthor().contains(author)){
            returnValue = false;
        }
        if(!title.equals("") && !book.getTitle().contains(title)){
            returnValue = false;
        }
        if(!description.equals("") && !book.getDescription().contains(description)){
            returnValue = false;
        }
        return returnValue;
    }
}
