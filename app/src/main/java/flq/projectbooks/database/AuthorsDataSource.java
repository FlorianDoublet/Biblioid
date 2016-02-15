package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;


public class AuthorsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_AUTHOR_NAME};

    public AuthorsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Author createAuthor(String name) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_AUTHOR_NAME, name);


        long insertId = database.insert(MySQLiteHelper.TABLE_AUTHORS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_AUTHORS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Author newComment = cursorToAuthor(cursor);
        cursor.close();
        return newComment;
    }

    public int updateAuthor(Author author) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_AUTHOR_NAME, author.getName());

        return database.update(MySQLiteHelper.TABLE_AUTHORS, values, MySQLiteHelper.COLUMN_ID + " = " + author.getId(), null);
    }

    public void deleteAuthor(Author author) {
        long id = author.getId();
        System.out.println("Author deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_AUTHORS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Author> getAllAuthors() {
        List<Author> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_AUTHORS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Author author = cursorToAuthor(cursor);
            comments.add(author);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }


    private Author cursorToAuthor(Cursor cursor) {
        Author author = new Author();
        author.setId(cursor.getLong(0));
        author.setName(cursor.getString(1));

        return author;
    }

    public long createBooksAuthors(long book_id, long author_id) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_BOOK_ID, book_id);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_ID, author_id);

        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS_AUTHORS, null,
                values);

        return insertId;
    }

}

