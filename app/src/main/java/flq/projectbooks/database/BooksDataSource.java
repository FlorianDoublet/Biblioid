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
            MySQLiteHelper.COLUMN_EDITOR,
            MySQLiteHelper.COLUMN_NB_PAGES};

    public BooksDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Book createBook(String title, String isbn, byte[] image, String description, String datePublication, String editor, int nbPages) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_ISBN, isbn);
        values.put(MySQLiteHelper.COLUMN_IMAGE, image);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION, datePublication);
        values.put(MySQLiteHelper.COLUMN_EDITOR, editor);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES, nbPages);

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
        values.put(MySQLiteHelper.COLUMN_EDITOR, book.getEditor());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES, book.getNbPages());
        return database.update(MySQLiteHelper.TABLE_BOOKS, values, MySQLiteHelper.COLUMN_ID + " = " + book.getId(), null);
    }

    public void deleteBook(Book book) {
        long id = book.getId();
        System.out.println("Book deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Book> getAllBooks() {
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


    private Book cursorToBook(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getLong(0));
        book.setTitle(cursor.getString(1));
        book.setIsbn(cursor.getString(2));
        book.setImage(cursor.getBlob(3));
        book.setDescription(cursor.getString(4));
        book.setDatePublication(cursor.getString(5));
        book.setEditor(cursor.getString(6));
        book.setNbPages(cursor.getInt(7));
        return book;
    }

    public boolean booksAuhorsIdExist(long book_id, long author_id) {
        return LinkTablesDataSource.booksAuhorsIdExist(database, book_id, author_id);
    }

    public List<Author> getAllAuthorFromABook(Book book) {
        return LinkTablesDataSource.getAllAuthorFromABook(database, book);
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

    public long createBooksCategories(long book_id, long category_id) {
        return LinkTablesDataSource.createBooksCategories(database, book_id, category_id);
    }

    public void deleteBooksCategories(long book_id, long category_id) {

        System.out.println("Link deleted with book_id: " + book_id + " and category_id " + category_id);
        database.delete(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_ID
                + " = " + book_id + " and " + MySQLiteHelper.COLUMN_CATEGORY_ID + " = " + category_id, null);
    }


}
