package flq.projectbooks.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.Book;
import flq.projectbooks.bdd.BooksDataSource;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class BookLibrary implements Serializable {

    private BooksDataSource datasource ;
    private static BookLibrary books ;
    List<Book> bookList;
    private static Context context ;

    public static BookLibrary getInstance() {
        return books;
    }

    public BookLibrary(){
        bookList = new ArrayList<>();
        datasource = new BooksDataSource(context);
        datasource.open();
        bookList = datasource.getAllBooks();
        datasource.close();
    }

    public BookLibrary(Context _context) {
        context = _context;
        books = new BookLibrary();
    }

    public void Add(Book book){
        bookList.add(book);
        datasource.open();
        datasource.createBook(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getEditor(), book.getCategory(), book.getNbPages()); //Add book to database
        datasource.close();
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
            if(bookList.get(j).getId() == id){
                //Remove from database
                Book temp = bookList.get(j);
                datasource.open();
                datasource.deleteBook(temp);
                datasource.close();

                //Remove from local list
                bookList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        bookList = datasource.getAllBooks();
        datasource.close();
    }

    public void UpdateOrAddBook(Book book){
        long id = book.getId();
        if(id != -1) {
            for (int j = 0; j < bookList.size(); j++) {
                if (bookList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateBook(book); //Update database
                    datasource.close();
                    bookList.set(j, book); //Update local list
                    return;
                }
            }
        } else {
            datasource.open();
            datasource.createBook(book.getTitle(), book.getAuthor(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getEditor(), book.getCategory(), book.getNbPages()); //Add book to database
            bookList = datasource.getAllBooks(); //Update books
            datasource.close();
        }
    }

}
