package flq.projectbooks.data.libraries;

import java.util.ArrayList;

import flq.projectbooks.data.Book;

/**
 * Created by flori on 27/10/2015.
 */
public class FilteredBookLibrary extends BookLibrary {

    public FilteredBookLibrary() {
        this.bookList = new ArrayList<Book>();
    }

    @Override
    public Book Add(Book book) {
        bookList.add(book);
        return book;
    }
}
