package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;

/**
 * Created by Quentin on 22/10/2015.
 */
public class BookFiltersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_FILTER_NAME,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_DESCRIPTION,
            MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN,
            MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX,
            MySQLiteHelper.COLUMN_EDITOR,
            MySQLiteHelper.COLUMN_NB_PAGES_MIN,
            MySQLiteHelper.COLUMN_NB_PAGES_MAX};

    public BookFiltersDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public BookFilter createFilter(String name, String title, String isbn, String datePublicationMin, String datePublicationMax, String editor, int nbPagesMin, int nbPagesMax) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FILTER_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, isbn);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN, datePublicationMin);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX, datePublicationMax);
        values.put(MySQLiteHelper.COLUMN_EDITOR, editor);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MIN, nbPagesMin);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MAX, nbPagesMax);

        long insertId = database.insert(MySQLiteHelper.TABLE_BOOK_FILTERS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        BookFilter newFilter = cursorToBookFilter(cursor);
        cursor.close();
        return newFilter;
    }

    public int updateBookFilter(BookFilter filter) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FILTER_NAME, filter.getName());
        values.put(MySQLiteHelper.COLUMN_TITLE, filter.getTitle());
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, filter.getDescription());
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN, filter.getDatePublicationMin());
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX, filter.getDatePublicationMax());
        values.put(MySQLiteHelper.COLUMN_EDITOR, filter.getEditor());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MIN, filter.getNbPagesMin());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MAX, filter.getNbPagesMax());
        return database.update(MySQLiteHelper.TABLE_BOOK_FILTERS, values, MySQLiteHelper.COLUMN_ID + " = " + filter.getId(), null);
    }

    public void deleteBookFilter(BookFilter filter) {
        long id = filter.getId();
        System.out.println("Book deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BOOK_FILTERS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<BookFilter> getAllBookFilters() {
        List<BookFilter> filters = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BookFilter filter = cursorToBookFilter(cursor);
            filters.add(filter);
            cursor.moveToNext();
        }
        cursor.close();
        //used to fill the Authors Array
        for (BookFilter filter : filters) {
            filter.setAuthors(getAllAuthorFromABookFilter(filter));
        }
        //used to fill the Categories Array
        for (BookFilter filter : filters) {
            filter.setCategories(getAllCategoryFromABookFilter(filter));
        }
        return filters;
    }


    private BookFilter cursorToBookFilter(Cursor cursor) {
        BookFilter filter = new BookFilter();
        filter.setId(cursor.getLong(0));
        filter.setName(cursor.getString(1));
        filter.setTitle(cursor.getString(2));
        filter.setDescription(cursor.getString(3));
        filter.setDatePublications(cursor.getString(4), cursor.getString(5));
        filter.setEditor(cursor.getString(6));
        filter.setNbPages(cursor.getInt(7), cursor.getInt(8));
        return filter;
    }

    public boolean bookFiltersAuhorsIdExist(long book_filter_id, long author_id) {
        return LinkTablesDataSource.bookFiltersAuhorsIdExist(database, book_filter_id, author_id);
    }

    public long createBookFiltersAuthors(long book_filter_id, long author_id) {
        return LinkTablesDataSource.createBookFiltersAuthors(database, book_filter_id, author_id);
    }

    public List<Author> getAllAuthorFromABookFilter(BookFilter filter) {
        return LinkTablesDataSource.getAllAuthorFromABookFilter(database, filter);
    }

    public void deleteBookFilterAuthors(long book_filter_id, long author_id) {

        System.out.println("Link deleted with book_filter_id: " + book_filter_id + " and author_id " + author_id);
        database.delete(MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_FILTER_ID
                + " = " + book_filter_id + " and " + MySQLiteHelper.COLUMN_AUTHOR_ID + " = " + author_id, null);
    }

    public boolean bookFiltersCategoriesIdExist(long book_filter_id, long category_id) {
        return LinkTablesDataSource.bookFiltersCategoriesIdExist(database, book_filter_id, category_id);
    }

    public long createBookFiltersCategories(long book_filter_id, long categories_id) {
        return LinkTablesDataSource.createBookFiltersCategories(database, book_filter_id, categories_id);
    }

    public List<Category> getAllCategoryFromABookFilter(BookFilter filter) {
        return LinkTablesDataSource.getAllCategoryFromABookFilter(database, filter);
    }

    public void deleteBookFilterCategories(long book_filter_id, long category_id) {

        System.out.println("Link deleted with book_filter_id: " + book_filter_id + " and author_id " + category_id);
        database.delete(MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_FILTER_ID
                + " = " + book_filter_id + " and " + MySQLiteHelper.COLUMN_CATEGORY_ID + " = " + category_id, null);
    }

}
