package flq.projectbooks.data.libraries;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flq.projectbooks.UI.fragments.SettingsFragment;
import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Category;
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
        loadBookListWithPref(false);
    }

    public BookLibrary(Context _context, String dbName, int dbVersion) {
        bookList = new ArrayList<>();
        datasource = new BooksDataSource(_context, dbName, dbVersion);
        loadBookListWithPref(true);
    }

    public BookLibrary(Context _context) {
        context = _context;
        books = new BookLibrary();
    }

    public static BookLibrary getInstance() {
        return books;
    }

    public static BookLibrary getInstanceOrInitialize(Context _context) {
        if (books == null) {
            new BookLibrary(_context);
        }
        return books;
    }

    public void loadBookListWithPref(boolean withContext) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_order = sharedPref.getString(SettingsFragment.KEY_PREF_BOOKS_DISPLAY_ORDER, "1");

        if (withContext) {
            AuthorLibrary authorLibrary = new AuthorLibrary(datasource.getDbHelper());
            CategoryLibrary categoryLibrary = new CategoryLibrary(datasource.getDbHelper());
            datasource.open();
            bookList = datasource.getAllBooks(authorLibrary, categoryLibrary);
            datasource.close();
        } else {
            datasource.open();
            bookList = datasource.getAllBooks();
            datasource.close();
        }


        switch (pref_order) {
            case "Ordre de création":
                //Do nothing, default bookList order is in the creation order
                break;
            case "Ordre alphabétique":
                Collections.sort(bookList, new Comparator<Book>() {
                    @Override
                    public int compare(final Book object1, final Book object2) {
                        return object1.getTitle().compareTo(object2.getTitle());
                    }
                });
                break;
            case "Ordre aléatoire":
                Collections.shuffle(bookList);
                break;
        }
    }

    public Book Add(Book book) {
        datasource.open();
        Book newBook = datasource.createBook(book.getTitle(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getPublisher_id(), book.getNbPages(), book.getFriend_id(), book.getAdvancementState(), book.getRating(), book.getOnWishList(), book.getOnFavoriteList(), book.getBookState(), book.getPossessionState(), book.getComment()); //Add book to database
        datasource.close();
        bookList.add(newBook);
        return newBook;
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

    public void deleteBookById(long id) {
        for (int j = 0; j < bookList.size(); j++) {
            if (bookList.get(j).getId() == id) {
                //Remove from database
                Book temp = bookList.get(j);
                datasource.open();
                datasource.deleteBook(temp);
                datasource.close();


                //if this book was in a loan, delete the loan
                LoanLibrary.getInstance().deleteLoanByBookId(temp.getId());

                //remove all books_authors link
                List<Author> authors = getAllAuthorFromABook(temp);
                if (authors != null) {
                    for (Author author : authors) {
                        if (checkIfBooksAuthorLinkExist(temp.getId(), author.getId())) {
                            deleteBooksAuthorsLink(temp.getId(), author.getId());
                        }
                    }
                }
                //remove all books_categories link
                List<Category> categories = getAllCategoryFromABook(temp);
                if (categories != null) {
                    for (Category category : categories) {
                        if (checkIfBooksCategoriesLinkExist(temp.getId(), category.getId())) {
                            deleteBooksCategoriesLink(temp.getId(), category.getId());
                        }
                    }
                }
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
            book = datasource.createBook(book.getTitle(), book.getIsbn(), book.getImage(), book.getDescription(), book.getDatePublication(), book.getPublisher_id(), book.getNbPages(), book.getFriend_id(), book.getAdvancementState(), book.getRating(), book.getOnWishList(), book.getOnFavoriteList(), book.getBookState(), book.getPossessionState(), book.getComment()); //Add book to database
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

    public boolean checkIfBooksCategoriesLinkExist(long book_id, long category_id) {
        datasource.open();
        Boolean bool = datasource.booksCategoriesIdExist(book_id, category_id);
        datasource.close();
        return bool;
    }

    public long addBooksCategoriesLink(long book_id, long category_id) {
        datasource.open();
        long inserted_id = datasource.createBooksCategories(book_id, category_id);
        datasource.close();
        return inserted_id;
    }

    public void deleteBooksCategoriesLink(long book_id, long category_id) {
        datasource.open();
        datasource.deleteBooksCategories(book_id, category_id);
        datasource.close();
    }

    public List<Category> getAllCategoryFromABook(Book book) {
        datasource.open();
        List<Category> categories = datasource.getAllCategoryFromABook(book);
        datasource.close();
        return categories;
    }
}
