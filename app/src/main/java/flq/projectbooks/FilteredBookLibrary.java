package flq.projectbooks;

import java.util.ArrayList;

/**
 * Created by flori on 27/10/2015.
 */
public class FilteredBookLibrary extends BookLibrary {

    public FilteredBookLibrary(){
        this.bookList = new ArrayList<Book>();
    }

    @Override
    public void Add(Book book){
        bookList.add(book);
    }
}
