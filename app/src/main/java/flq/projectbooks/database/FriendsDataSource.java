package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Friend;

/**
 * Created by flori on 15/02/2016.
 */
public class FriendsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_FRIEND_FIRSTNAME,
            MySQLiteHelper.COLUMN_FRIEND_LASTNAME,
            MySQLiteHelper.COLUMN_FRIEND_CLOUDLINK};

    public FriendsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Friend createFriend(String firstName, String lastName, String cloudLink) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FRIEND_FIRSTNAME, firstName);
        values.put(MySQLiteHelper.COLUMN_FRIEND_LASTNAME, lastName);
        values.put(MySQLiteHelper.COLUMN_FRIEND_CLOUDLINK, cloudLink);


        long insertId = database.insert(MySQLiteHelper.TABLE_FRIENDS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Friend newComment = cursorToFriend(cursor);
        cursor.close();
        return newComment;
    }

    public int updateFriend(Friend friend) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FRIEND_FIRSTNAME, friend.getFirstName());
        values.put(MySQLiteHelper.COLUMN_FRIEND_LASTNAME, friend.getLastName());
        values.put(MySQLiteHelper.COLUMN_FRIEND_CLOUDLINK, friend.getCloudLink());

        return database.update(MySQLiteHelper.TABLE_FRIENDS, values, MySQLiteHelper.COLUMN_ID + " = " + friend.getId(), null);
    }

    public void deleteFriend(Friend friend) {
        long id = friend.getId();
        System.out.println("Friend deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FRIENDS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Friend> getAllFriends() {
        List<Friend> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Friend friend = cursorToFriend(cursor);
            comments.add(friend);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }


    private Friend cursorToFriend(Cursor cursor) {
        Friend friend = new Friend();
        friend.setId(cursor.getLong(0));
        friend.setFirstName(cursor.getString(1));
        friend.setLastName(cursor.getString(2));
        friend.setCloudLink(cursor.getString(3));

        return friend;
    }
}
