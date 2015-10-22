package flq.projectbooks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Quentin on 22/10/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_ISBN = "isbn";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_CREATE = "create table "
            + TABLE_BOOKS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_ISBN + " text not null, "
            + COLUMN_IMAGE + " text, "
            + COLUMN_DESCRIPTION + " text "
            + " );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }
}
