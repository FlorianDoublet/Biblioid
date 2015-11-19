package flq.projectbooks;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilterCatalog implements Serializable {

    private BookFiltersDataSource datasource ;
    private static BookFilterCatalog bookFilters;
    List<BookFilter> bookFilterList;
    private static Context context ;

    public static BookFilterCatalog getInstance() {
        return bookFilters;
    }

    public BookFilterCatalog(){
        bookFilterList = new ArrayList<>();
        datasource = new BookFiltersDataSource(context);
        datasource.open();
        bookFilterList = datasource.getAllBookFilters();
        datasource.close();
    }

    public BookFilterCatalog(Context _context) {
        context = _context;
        bookFilters = new BookFilterCatalog();
    }

    public void Add(BookFilter filter){
        //Add in the database
        datasource.open();
        datasource.createFilter(filter.getName(), filter.getTitle(), filter.getAuthor(), filter.getDescription(), filter.getDatePublication(), filter.getEditor(), filter.getCategory(), filter.getNbPages());
        datasource.close();

        //Add in the local list
        bookFilterList.add(filter);
    }

    public List<BookFilter> getBookFilterList(){
        return bookFilterList;
    }

    public BookFilter getNewFilters(){
        return new BookFilter();
    }

    public void deleteFilters(BookFilter filter){
        bookFilterList.remove(filter);
    }

    public void deleteFilterById(int id){
        for(int j = 0; j < bookFilterList.size(); j++){
            if(bookFilterList.get(j).getId() == id){
                //Remove from database
                BookFilter temp = bookFilterList.get(j);
                datasource.open();
                datasource.deleteBookFilter(temp);
                datasource.close();

                //Remove from local list
                bookFilterList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        bookFilterList = datasource.getAllBookFilters();
        datasource.close();
    }

    public void UpdateOrAddFilter(BookFilter filter){
        long id = filter.getId();
        if(id != -1) {
            for (int j = 0; j < bookFilterList.size(); j++) {
                if (bookFilterList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateBookFilter(filter); //Update database
                    datasource.close();
                    bookFilterList.set(j, filter); //Update local list
                    return;
                }
            }
        } else {
            datasource.open();
            datasource.createFilter(filter.getName(), filter.getTitle(), filter.getAuthor(), filter.getDescription(), filter.getDatePublication(), filter.getEditor(), filter.getCategory(), filter.getNbPages()); //Add book to database
            bookFilterList = datasource.getAllBookFilters(); //Update books
            datasource.close();
        }
    }

}
