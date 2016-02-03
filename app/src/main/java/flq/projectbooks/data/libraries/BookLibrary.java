package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.database.BooksDataSource;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class BookLibrary implements Serializable {

    private static BookLibrary books;
    private static Context context;
    List<Book> bookList;
    private BooksDataSource datasource;

    public BookLibrary() {
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

    public static BookLibrary getInstance() {
        return books;
    }

    public void Add(Book book) {
        bookList.add(book);
        datasource.open();
        datasource.createBook(book.getTitle(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getEditor(), book.getCategory(), book.getNbPages()); //Add book to database
        datasource.close();
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public Book getNewBook() {
        return new Book();
    }

    public void deleteBook(Book book) {
        bookList.remove(book);
    }

    public Book getBookById(long id) {
        for (int j = 0; j < bookList.size(); j++) {
            if (bookList.get(j).getId() == id) {
                return bookList.get(j);
            }
        }
        return null;
    }

    public void deleteBookById(int id) {
        for (int j = 0; j < bookList.size(); j++) {
            if (bookList.get(j).getId() == id) {
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

    public long updateOrAddBook(Book book) {
        long id = book.getId();
        if (id != -1) {
            for (int j = 0; j < bookList.size(); j++) {
                if (bookList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateBook(book); //Update database
                    datasource.close();
                    bookList.set(j, book); //Update local list
                }
            }
        } else {
            datasource.open();
            book = datasource.createBook(book.getTitle(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getEditor(), book.getCategory(), book.getNbPages()); //Add book to database
            bookList = datasource.getAllBooks(); //Update books
            datasource.close();
        }
        return book.getId();
    }

    public boolean checkIfBooksAuthorLinkExist(long book_id, long author_id) {
        datasource.open();
        Boolean bool = datasource.booksAuhorsIdExist(book_id, author_id);
        datasource.close();
        return bool;
    }

    public long addBooksAuthorsLink(long book_id, long author_id) {
        datasource.open();
        long inserted_id = datasource.createBooksAuthors(book_id, author_id);
        datasource.close();
        return inserted_id;
    }

    public void deleteBooksAuthorsLink(long book_id, long author_id) {
        datasource.open();
        datasource.deleteBooksAuthors(book_id, author_id);
        datasource.close();
    }

    public List<Author> getAllAuthorFromABook(Book book) {
        datasource.open();
        List<Author> authors = datasource.getAllAuthorFromABook(book);
        datasource.close();
        return authors;
    }


}
