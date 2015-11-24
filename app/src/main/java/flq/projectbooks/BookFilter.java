package flq.projectbooks;


import java.io.Serializable;

import flq.projectbooks.libraries.BookLibrary;
import flq.projectbooks.libraries.FilteredBookLibrary;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {
    private long id;
    private String name;
    private String title;
    private String author;
    private String description;
    private String datePublicationMin;
    private String datePublicationMax;
    private String editor;
    private String category;
    private int nbPagesMin;
    private int nbPagesMax;


    public BookFilter(){
        id = -1;
        name = "";
        title = "";
        author = "";
        description = "";
        datePublicationMin = "";
        datePublicationMax = "";
        editor = "";
        category = "";
        nbPagesMin = 0;
        nbPagesMax = 0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getDatePublicationMin() {
        return datePublicationMin;
    }

    public String getDatePublicationMax() {
        return datePublicationMax;
    }

    public void setDatePublications(String datePublicationMin, String datePublicationMax) {
        this.datePublicationMin = datePublicationMin;
        this.datePublicationMax = datePublicationMax;
    }

    public int getNbPagesMin() {
        return nbPagesMin;
    }
    public int getNbPagesMax() {
        return nbPagesMax;
    }

    public void setNbPages(int nbPagesMin, int nbPagesMax) {
        this.nbPagesMin = nbPagesMin;
        this.nbPagesMax = nbPagesMax;
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

        if(!category.equals("") && !book.getCategory().contains(category)){
            returnValue = false;
        }

        if(!editor.equals("") && !book.getEditor().contains(editor)){
            returnValue = false;
        }

        if(!datePublicationMin.equals("") && !datePublicationMax.equals("")) {
            if((yearFromString(datePublicationMin) > yearFromString(book.getDatePublication()) || yearFromString(datePublicationMax) < yearFromString(book.getDatePublication())))
                returnValue = false;
        }

        if(nbPagesMax != 0 && ((book.getNbPages() < nbPagesMin) || (book.getNbPages() > nbPagesMax))){
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

    private int yearFromString(String date){

        if(date.length() > 4){
            String full_date[] = date.split("/");
            date = full_date[2];
        }
        return Integer.parseInt(date);
    }
}
