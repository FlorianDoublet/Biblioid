package flq.projectbooks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin on 22/10/2015.
 */
public class BookFiltersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_FILTER_NAME,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_DESCRIPTION,
            MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN,
            MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX,
            MySQLiteHelper.COLUMN_EDITOR,
            MySQLiteHelper.COLUMN_CATEGORY,
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

    public BookFilter createFilter(String name, String title, String author, String isbn, String datePublicationMin, String datePublicationMax, String editor, String category, int nbPagesMin, int nbPagesMax) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FILTER_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, isbn);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN, datePublicationMin);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX, datePublicationMax);
        values.put(MySQLiteHelper.COLUMN_EDITOR, editor);
        values.put(MySQLiteHelper.COLUMN_CATEGORY, category);
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

    public int updateBookFilter(BookFilter filter){
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FILTER_NAME, filter.getName());
        values.put(MySQLiteHelper.COLUMN_TITLE, filter.getTitle());
        values.put(MySQLiteHelper.COLUMN_AUTHOR, filter.getAuthor());
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, filter.getDescription());
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN, filter.getDatePublicationMin());
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX, filter.getDatePublicationMax());
        values.put(MySQLiteHelper.COLUMN_EDITOR, filter.getEditor());
        values.put(MySQLiteHelper.COLUMN_CATEGORY, filter.getCategory());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MIN, filter.getNbPagesMin());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MAX, filter.getNbPagesMax());
        return database.update(MySQLiteHelper.TABLE_BOOK_FILTERS, values, MySQLiteHelper.COLUMN_ID + " = " +filter.getId(), null);
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
        return filters;
    }


    private BookFilter cursorToBookFilter(Cursor cursor) {
        BookFilter filter = new BookFilter();
        filter.setId(cursor.getLong(0));
        filter.setName(cursor.getString(1));
        filter.setTitle(cursor.getString(2));
        filter.setAuthor(cursor.getString(3));
        filter.setDescription(cursor.getString(4));
        filter.setDatePublications(cursor.getString(5), cursor.getString(6));
        filter.setEditor(cursor.getString(7));
        filter.setCategory(cursor.getString(8));
        filter.setNbPages(cursor.getInt(9), cursor.getInt(10));
        return filter;
    }

}
