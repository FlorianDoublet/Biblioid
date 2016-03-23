package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;

/**
 * Created by Quentin on 22/10/2015.
 */
public class BooksDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_ISBN,
            MySQLiteHelper.COLUMN_IMAGE,
            MySQLiteHelper.COLUMN_DESCRIPTION,
            MySQLiteHelper.COLUMN_DATE_PUBLICATION,
            MySQLiteHelper.COLUMN_PUBLISHER_ID,
            MySQLiteHelper.COLUMN_NB_PAGES,
            MySQLiteHelper.COLUMN_FRIEND_ID,
            MySQLiteHelper.COLUMN_ADVANCEMENT_STATE,
            MySQLiteHelper.COLUMN_RATING,
            MySQLiteHelper.COLUMN_ON_WISH_LIST,
            MySQLiteHelper.COLUMN_ON_FAVORITE_LIST,
            MySQLiteHelper.COLUMN_BOOK_STATE,
            MySQLiteHelper.COLUMN_POSSESSION_STATE,
            MySQLiteHelper.COLUMN_COMMENT};

    public BooksDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    //constructor only for external friend database
    public BooksDataSource(Context context, String dbName, int dbVersion) {
        dbHelper = new MySQLiteHelper(context, dbName, dbVersion);
    }

    public MySQLiteHelper getDbHelper(){
        return dbHelper;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public Book createBook(String title, String isbn, byte[] image, String description, String datePublication, long publisher_id, int nbPages, long friend_id, String advancementState, int rating, int onWishList, int onFavoriteList, int bookState, int possessionState, String comment) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_ISBN, isbn);
        values.put(MySQLiteHelper.COLUMN_IMAGE, image);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION, datePublication);
        values.put(MySQLiteHelper.COLUMN_PUBLISHER_ID, publisher_id);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES, nbPages);
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, friend_id);
        values.put(MySQLiteHelper.COLUMN_ADVANCEMENT_STATE, advancementState);
        values.put(MySQLiteHelper.COLUMN_RATING, rating);
        values.put(MySQLiteHelper.COLUMN_ON_WISH_LIST, onWishList);
        values.put(MySQLiteHelper.COLUMN_ON_FAVORITE_LIST, onFavoriteList);
        values.put(MySQLiteHelper.COLUMN_BOOK_STATE, bookState);
        values.put(MySQLiteHelper.COLUMN_POSSESSION_STATE, possessionState);
        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Book newComment = cursorToBook(cursor);
        cursor.close();
        return newComment;
    }


    public int updateBook(Book book) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_TITLE, book.getTitle());
        values.put(MySQLiteHelper.COLUMN_ISBN, book.getIsbn());
        values.put(MySQLiteHelper.COLUMN_IMAGE, book.getImage());
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, book.getDescription());
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION, book.getDatePublication());
        values.put(MySQLiteHelper.COLUMN_PUBLISHER_ID, book.getPublisher_id());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES, book.getNbPages());
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, book.getFriend_id());
        values.put(MySQLiteHelper.COLUMN_ADVANCEMENT_STATE, book.getAdvancementState());
        values.put(MySQLiteHelper.COLUMN_RATING, book.getRating());
        values.put(MySQLiteHelper.COLUMN_ON_WISH_LIST, book.getOnWishList());
        values.put(MySQLiteHelper.COLUMN_ON_FAVORITE_LIST, book.getOnFavoriteList());
        values.put(MySQLiteHelper.COLUMN_BOOK_STATE, book.getBookState());
        values.put(MySQLiteHelper.COLUMN_POSSESSION_STATE, book.getPossessionState());
        values.put(MySQLiteHelper.COLUMN_COMMENT, book.getComment());


        return database.update(MySQLiteHelper.TABLE_BOOKS, values, MySQLiteHelper.COLUMN_ID + " = " + book.getId(), null);
    }

    public void deleteBook(Book book) {
        long id = book.getId();
        System.out.println("Book deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    //get all books but without external table feature like Author or Category
    public List<Book> getAllBooksWithoutExternalTable(){
        List<Book> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            comments.add(book);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }

    public List<Book> getAllBooks() {
        List<Book> comments = getAllBooksWithoutExternalTable();

        //used to fill the Authors Array
        for (Book book : comments) {
            book.setAuthors(getAllAuthorFromABook(book));
        }
        //used to fill the Categories Array
        for (Book book : comments) {
            book.setCategories(getAllCategoryFromABook(book));
        }

        return comments;
    }

    //used to get all books from friend Database
    public List<Book> getAllBooks(AuthorLibrary authorLibrary, CategoryLibrary categoryLibrary) {
        List<Book> comments = getAllBooksWithoutExternalTable();

        //used to fill the Authors Array
        for (Book book : comments) {
            book.setAuthors(getAllAuthorFromABook(authorLibrary, book));
    }
        //used to fill the Categories Array
        for (Book book : comments) {
            book.setCategories(getAllCategoryFromABook(categoryLibrary, book));
        }

        return comments;
    }


    private Book cursorToBook(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getLong(0));
        book.setTitle(cursor.getString(1));
        book.setIsbn(cursor.getString(2));
        book.setImage(cursor.getBlob(3));
        book.setDescription(cursor.getString(4));
        book.setDatePublication(cursor.getString(5));
        book.setPublisher_id(cursor.getLong(6));
        book.setNbPages(cursor.getInt(7));
        book.setFriend_id(cursor.getLong(8));
        book.setAdvancementState(cursor.getString(9));
        book.setRating(cursor.getInt(10));
        book.setOnWishList(cursor.getInt(11));
        book.setOnFavoriteList(cursor.getInt(12));
        book.setBookState(cursor.getInt(13));
        book.setPossessionState(cursor.getInt(14));
        book.setComment(cursor.getString(15));

        return book;
    }

    public boolean booksAuhorsIdExist(long book_id, long author_id) {
        return LinkTablesDataSource.booksAuhorsIdExist(database, book_id, author_id);
    }

    public List<Author> getAllAuthorFromABook(Book book) {
        return LinkTablesDataSource.getAllAuthorFromABook(database, book);
    }

    //only used for external friend database
    public List<Author> getAllAuthorFromABook(AuthorLibrary authorLibrary, Book book) {
        return LinkTablesDataSource.getAllAuthorFromABook(authorLibrary, database, book);
    }

    public long createBooksAuthors(long book_id, long author_id) {
        return LinkTablesDataSource.createBooksAuthors(database, book_id, author_id);
    }

    public void deleteBooksAuthors(long book_id, long author_id) {

        System.out.println("Link deleted with book_id: " + book_id + " and author_id " + author_id);
        database.delete(MySQLiteHelper.TABLE_BOOKS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_ID
                + " = " + book_id + " and " + MySQLiteHelper.COLUMN_AUTHOR_ID + " = " + author_id, null);
    }

    public boolean booksCategoriesIdExist(long book_id, long category_id) {
        return LinkTablesDataSource.booksCategoriesIdExist(database, book_id, category_id);
    }

    public List<Category> getAllCategoryFromABook(Book book) {
        return LinkTablesDataSource.getAllCategoriesFromABook(database, book);
    }

    //only used for external friend database
    public List<Category> getAllCategoryFromABook(CategoryLibrary categoryLibrary, Book book) {
        return LinkTablesDataSource.getAllCategoriesFromABook(categoryLibrary, database, book);
    }

    public long createBooksCategories(long book_id, long category_id) {
        return LinkTablesDataSource.createBooksCategories(database, book_id, category_id);
    }

    public void deleteBooksCategories(long book_id, long category_id) {

        System.out.println("Link deleted with book_id: " + book_id + " and category_id " + category_id);
        database.delete(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_ID
                + " = " + book_id + " and " + MySQLiteHelper.COLUMN_CATEGORY_ID + " = " + category_id, null);
    }


}
