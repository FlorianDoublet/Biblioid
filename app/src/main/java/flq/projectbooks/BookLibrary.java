package flq.projectbooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class BookLibrary {

    List<Book> books;

    public BookLibrary() {
        books = new ArrayList<Book>();
    }

    public void Add(Book book){
        books.add(book);
    }

    public List<Book> getBooks(){
        return books;
    }

    public Book getNewBook(){
        return new Book();
    }

    public void DeleteBook(Book book){
        books.remove(book);
    }
}
