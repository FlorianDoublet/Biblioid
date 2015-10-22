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
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_AUTHOR, MySQLiteHelper.COLUMN_ISBN};

    public BookFiltersDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public BookFilter createFilter(String title, String author, String isbn) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_ISBN, isbn);

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

        values.put(MySQLiteHelper.COLUMN_TITLE, filter.getTitle());
        values.put(MySQLiteHelper.COLUMN_AUTHOR, filter.getAuthor());
        values.put(MySQLiteHelper.COLUMN_ISBN, filter.getIsbn());
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
        filter.setTitle(cursor.getString(1));
        filter.setAuthor(cursor.getString(2));
        filter.setIsbn(cursor.getString(3));
        return filter;
    }

}
