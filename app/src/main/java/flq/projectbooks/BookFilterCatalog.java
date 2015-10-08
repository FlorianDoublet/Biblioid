package flq.projectbooks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilterCatalog implements Serializable {

    private static BookFilterCatalog bookFilters = new BookFilterCatalog();

    List<BookFilter> bookFilterList;

    public static BookFilterCatalog getInstance() {
        return bookFilters;
    }

    private BookFilterCatalog() {
        bookFilterList = new ArrayList<>();
    }

    public void Add(BookFilter filter){
        bookFilterList.add(filter);
    }

    public List<BookFilter> getBookFilterList(){
        return bookFilterList;
    }

    public BookFilter getNewFilters(){
        return new BookFilter();
    }

    public void DeleteFilters(BookFilter filter){
        bookFilterList.remove(filter);
    }

}
