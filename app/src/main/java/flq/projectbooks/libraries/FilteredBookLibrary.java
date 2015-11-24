package flq.projectbooks.libraries;

import java.util.ArrayList;

import flq.projectbooks.Book;

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
