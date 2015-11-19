package flq.projectbooks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.io.Serializable;


/**
 * Created by Quentin on 22/10/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_BOOK_FILTERS = "bookFilters";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_ISBN = "isbn";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_FILTER_NAME = "name";
    public static final String COLUMN_DATE_PUBLICATION = "datePublication";
    public static final String COLUMN_EDITOR = "editor";
    public static final String COLUMN_CATEGORY= "category";
    public static final String COLUMN_NB_PAGES = "nbPages";

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    private final String DB_FILEPATH;

    private static final String DATABASE_CREATE_BOOKS = "create table "
            + TABLE_BOOKS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_ISBN + " text not null, "
            + COLUMN_IMAGE + " BLOB, "
            + COLUMN_DESCRIPTION + " text ,"
            + COLUMN_DATE_PUBLICATION + " text ,"
            + COLUMN_EDITOR + " text ,"
            + COLUMN_CATEGORY + " text ,"
            + COLUMN_NB_PAGES + " integer "

            + " );";

    private static final String DATABASE_CREATE_BOOK_FILTERS = "create table "
            + TABLE_BOOK_FILTERS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILTER_NAME + " text not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_DESCRIPTION + " text not null ,"
            + COLUMN_DATE_PUBLICATION + " text ,"
            + COLUMN_EDITOR + " text ,"
            + COLUMN_CATEGORY + " text ,"
            + COLUMN_NB_PAGES + " integer "
            + " );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        final String packageName = context.getPackageName();
        DB_FILEPATH = "/data/data/" + packageName + "/databases/books.db";
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_BOOKS);
        database.execSQL(DATABASE_CREATE_BOOK_FILTERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS);
        onCreate(db);
    }

    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will
        // commit the created empty database to internal storage.
        close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper
            // will cache it and mark it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public void backupDatabase(String dbPath) throws IOException {

        if (isSDCardWriteable()) {
            // Open your local db as the input stream
            String inFileName = DB_FILEPATH;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = dbPath + "/books.db";
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
        }
    }

    private boolean isSDCardWriteable() {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }


}
