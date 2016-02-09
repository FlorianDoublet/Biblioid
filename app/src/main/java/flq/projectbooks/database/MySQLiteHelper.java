package flq.projectbooks.database;

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


/**
 * Created by Quentin on 22/10/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    //Table Names
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_BOOK_FILTERS = "bookFilters";
    public static final String TABLE_AUTHORS = "authors";
    public static final String TABLE_BOOKS_AUTHORS = "books_authors";
    public static final String TABLE_BOOK_FILTERS_AUTHORS = "book_filters_authors";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_BOOKS_CATEGORIES = "books_categories";
    public static final String TABLE_BOOK_FILTERS_CATEGORIES = "book_filters_categories";
    //Common column names
    public static final String COLUMN_ID = "_id";
    //TABLE_AUTHORS and TABLE_CATEGORIES column names
    public static final String COLUMN_AUTHOR_NAME = "name";
    public static final String COLUMN_CATEGORY_NAME = "name";
    //TABLE_BOOKS column names
    public static final String COLUMN_ISBN = "isbn";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DATE_PUBLICATION = "datePublication";
    public static final String COLUMN_NB_PAGES = "nbPages";
    //TABLE_BOOK_FILTERS
    public static final String COLUMN_FILTER_NAME = "name";
    public static final String COLUMN_DATE_PUBLICATION_MIN = "datePublicationMin";
    public static final String COLUMN_DATE_PUBLICATION_MAX = "datePublicationMax";
    public static final String COLUMN_NB_PAGES_MIN = "nbPagesMin";
    public static final String COLUMN_NB_PAGES_MAX = "nbPagesMax";
    //TABLE_BOOKS - TABLE_BOOK_FILTERS column names
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_EDITOR = "editor";
    //TABLE_BOOKS_AUTHORS  and TABLE_BOOKS_CATEGORIES column names
    public static final String COLUMN_BOOK_ID = "book_id";
    //TABLE_BOOK_FILTERS_AUTHORS and TABLE_BOOK_FILTERS_CATEGORIES column names
    public static final String COLUMN_BOOK_FILTER_ID = "book_filter_id";
    //TABLE_BOOKS_AUTHORS - TABLE_BOOK_FILTERS_AUTHORS column names
    public static final String COLUMN_AUTHOR_ID = "author_id";
    //TABLE_BOOKS_CATEGORIES - TABLE_BOOK_FILTERS_CATEGORIES column names
    public static final String COLUMN_CATEGORY_ID = "category_id";


    //important variable
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books.db";
    //TABLE_BOOKS create statement
    private static final String DATABASE_CREATE_BOOKS = "create table "
            + TABLE_BOOKS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_ISBN + " text not null, "
            + COLUMN_IMAGE + " BLOB, "
            + COLUMN_DESCRIPTION + " text ,"
            + COLUMN_DATE_PUBLICATION + " text ,"
            + COLUMN_EDITOR + " text ,"
            + COLUMN_NB_PAGES + " integer "
            + " );";


    //Table Create Statements
    //TABLE_BOOK_FILTERS create statement
    private static final String DATABASE_CREATE_BOOK_FILTERS = "create table "
            + TABLE_BOOK_FILTERS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILTER_NAME + " text not null, "
            + COLUMN_TITLE + " text , "
            + COLUMN_DESCRIPTION + " text ,"
            + COLUMN_DATE_PUBLICATION_MIN + " text ,"
            + COLUMN_DATE_PUBLICATION_MAX + " text ,"
            + COLUMN_EDITOR + " text ,"
            + COLUMN_NB_PAGES_MIN + " integer ,"
            + COLUMN_NB_PAGES_MAX + " integer "
            + " );";
    //TABLE_AUTHORS create statements
    private static final String DATABASE_CREATE_AUTHORS = "create table "
            + TABLE_AUTHORS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_AUTHOR_NAME + " text not null "
            + " );";
    //TABLE_BOOKS_AUTHORS create statements
    private static final String DATABASE_CREATE_BOOKS_AUTHORS = "create table "
            + TABLE_BOOKS_AUTHORS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BOOK_ID + " INTEGER,"
            + COLUMN_AUTHOR_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "),"
            + " FOREIGN KEY (" + COLUMN_AUTHOR_ID + ") REFERENCES " + TABLE_AUTHORS + "(" + COLUMN_ID + ")"
            + " );";
    //TABLE_BOOK_FILTERS_AUTHORS create statements
    private static final String DATABASE_CREATE_BOOK_FILTERS_AUTHORS = "create table "
            + TABLE_BOOK_FILTERS_AUTHORS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BOOK_FILTER_ID + " INTEGER,"
            + COLUMN_AUTHOR_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_BOOK_FILTER_ID + ") REFERENCES " + TABLE_BOOK_FILTERS + "(" + COLUMN_ID + "),"
            + " FOREIGN KEY (" + COLUMN_AUTHOR_ID + ") REFERENCES " + TABLE_AUTHORS + "(" + COLUMN_ID + ")"
            + " );";

    //TABLE_CATEGORIES create statements
    private static final String DATABASE_CREATE_CATEGORIES = "create table "
            + TABLE_CATEGORIES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY_NAME + " text not null "
            + " );";
    //TABLE_BOOKS_CATEGORIES create statements
    private static final String DATABASE_CREATE_BOOKS_CATEGORIES = "create table "
            + TABLE_BOOKS_CATEGORIES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BOOK_ID + " INTEGER,"
            + COLUMN_CATEGORY_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "),"
            + " FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + ")"
            + " );";
    //TABLE_BOOK_FILTERS_CATEGORIES create statements
    private static final String DATABASE_CREATE_BOOK_FILTERS_CATEGORIES = "create table "
            + TABLE_BOOK_FILTERS_CATEGORIES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BOOK_FILTER_ID + " INTEGER,"
            + COLUMN_CATEGORY_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_BOOK_FILTER_ID + ") REFERENCES " + TABLE_BOOK_FILTERS + "(" + COLUMN_ID + "),"
            + " FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + ")"
            + " );";

    private final String DB_FILEPATH;


    //Utility
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        final String packageName = context.getPackageName();
        DB_FILEPATH = "/data/data/" + packageName + "/databases/books.db";
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_BOOKS);
        database.execSQL(DATABASE_CREATE_BOOK_FILTERS);
        database.execSQL(DATABASE_CREATE_AUTHORS);
        database.execSQL(DATABASE_CREATE_BOOKS_AUTHORS);
        database.execSQL(DATABASE_CREATE_BOOK_FILTERS_AUTHORS);
        database.execSQL(DATABASE_CREATE_CATEGORIES);
        database.execSQL(DATABASE_CREATE_BOOKS_CATEGORIES);
        database.execSQL(DATABASE_CREATE_BOOK_FILTERS_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS_CATEGORIES);
        onCreate(db);
    }

    public void dropDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_FILTERS_CATEGORIES);
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

    public void backupDatabase(String dbPath, String filename) throws IOException {

        if (isSDCardWriteable()) {
            //Ouvre la bdd local en tant que flux sortant
            String inFileName = DB_FILEPATH;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = dbPath + "/" + filename;

            //Ouvre la bdd vide en tant que flux sortant
            OutputStream output = new FileOutputStream(outFileName);

            //Transfert du flux entrant vers le flux sortant
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            //Ferme
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
