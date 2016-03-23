package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Publisher;

/**
 * Created by flori on 13/02/2016.
 */
public class PublishersDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_PUBLISHER_NAME};

    public PublishersDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public PublishersDataSource(MySQLiteHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Publisher createPublisher(String name) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_PUBLISHER_NAME, name);


        long insertId = database.insert(MySQLiteHelper.TABLE_PUBLISHERS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PUBLISHERS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Publisher newComment = cursorToPublisher(cursor);
        cursor.close();
        return newComment;
    }

    public int updatePublisher(Publisher publisher) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_PUBLISHER_NAME, publisher.getName());

        return database.update(MySQLiteHelper.TABLE_PUBLISHERS, values, MySQLiteHelper.COLUMN_ID + " = " + publisher.getId(), null);
    }

    public void deletePublisher(Publisher publisher) {
        long id = publisher.getId();
        System.out.println("Publisher deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_PUBLISHERS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Publisher> getAllPublishers() {
        List<Publisher> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PUBLISHERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Publisher publisher = cursorToPublisher(cursor);
            comments.add(publisher);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }


    private Publisher cursorToPublisher(Cursor cursor) {
        Publisher publisher = new Publisher();
        publisher.setId(cursor.getLong(0));
        publisher.setName(cursor.getString(1));

        return publisher;
    }


}

