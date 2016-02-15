package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Category;

/**
 * Created by flori on 08/02/2016.
 */
public class CategoriesDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_CATEGORY_NAME};

    public CategoriesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Category createCategory(String name) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_CATEGORY_NAME, name);


        long insertId = database.insert(MySQLiteHelper.TABLE_CATEGORIES, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Category newComment = cursorToCategory(cursor);
        cursor.close();
        return newComment;
    }

    public int updateCategory(Category category) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_CATEGORY_NAME, category.getName());

        return database.update(MySQLiteHelper.TABLE_CATEGORIES, values, MySQLiteHelper.COLUMN_ID + " = " + category.getId(), null);
    }

    public void deleteCategory(Category category) {
        long id = category.getId();
        System.out.println("Category deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_CATEGORIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Category> getAllCategories() {
        List<Category> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CATEGORIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            comments.add(category);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }


    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getLong(0));
        category.setName(cursor.getString(1));

        return category;
    }

    public long createBooksCategories(long book_id, long category_id) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_BOOK_ID, book_id);
        values.put(MySQLiteHelper.COLUMN_CATEGORY_ID, category_id);

        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, null,
                values);

        return insertId;
    }
}
