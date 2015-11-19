package flq.projectbooks;

import java.io.Serializable;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {
    private long id;
    private String name;
    private String title;
    private String author;
    private String description;

    public BookFilter(){
        id = -1;
        name = "";
        title = "";
        author = "";
        description = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDescription(String description) {
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

    public FilteredBookLibrary getFilteredLibrary(){
        FilteredBookLibrary filteredBooksLibrary = new FilteredBookLibrary();
        for (int i = 0; i < BookLibrary.getInstance().getBookList().size(); i++) {
            if (this.isSelected(BookLibrary.getInstance().getBookList().get(i))) {
                filteredBooksLibrary.Add(BookLibrary.getInstance().getBookList().get(i));
            }
        }
        return filteredBooksLibrary;
    }
}
