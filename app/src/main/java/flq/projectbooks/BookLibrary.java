package flq.projectbooks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class BookLibrary implements Serializable {

    private static BookLibrary books = new BookLibrary();

    List<Book> bookList;

    public static BookLibrary getInstance() {
        return books;
    }

    public BookLibrary() {
        bookList = new ArrayList<>();
    }

    public void Add(Book book){
        bookList.add(book);
    }

    public List<Book> getBookList(){
        return bookList;
    }

    public Book getNewBook(){
        return new Book();
    }

    public void DeleteBook(Book book){
        bookList.remove(book);
    }

    public void DeleteBookById(int id){
        for(int j = 0; j < bookList.size(); j++){
            if(bookList.get(j).id == id){
                bookList.remove(j);
                return;
            }
        }
    }

    public void UpdateOrAddBook( Book book){
        int id = book.id;
        for(int j = 0; j < bookList.size(); j++){
           if(bookList.get(j).id == id){
               bookList.set(j, book);
               return;
           }
        }
        bookList.add(book);
    }

}
