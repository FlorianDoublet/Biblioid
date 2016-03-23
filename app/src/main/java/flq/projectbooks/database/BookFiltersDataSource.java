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
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;

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
            MySQLiteHelper.COLUMN_PUBLISHER_ID,
            MySQLiteHelper.COLUMN_NB_PAGES_MIN,
            MySQLiteHelper.COLUMN_NB_PAGES_MAX,
            MySQLiteHelper.COLUMN_ADVANCEMENT_STATE,
            MySQLiteHelper.COLUMN_RATING_MIN,
            MySQLiteHelper.COLUMN_RATING_MAX,
            MySQLiteHelper.COLUMN_ON_WISH_LIST,
            MySQLiteHelper.COLUMN_ON_FAVORITE_LIST,
            MySQLiteHelper.COLUMN_BOOK_STATE,
            MySQLiteHelper.COLUMN_POSSESSION_STATE,
            MySQLiteHelper.COLUMN_COMMENT,
            MySQLiteHelper.COLUMN_FRIEND_ID
    };

    public BookFiltersDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    //constructor only for external friend database
    public BookFiltersDataSource(Context context, String dbName, int dbVersion) {
        dbHelper = new MySQLiteHelper(context, dbName, dbVersion);
    }

    public MySQLiteHelper getDbHelper(){
        return dbHelper;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public BookFilter createFilter(String name, String title, String isbn, String datePublicationMin, String datePublicationMax, long publisher_id, int nbPagesMin, int nbPagesMax, String advancementState, int ratingMin, int ratingMax, int onWishList, int onFavoriteList, int bookState, int possessionState, String comment, long friend_id) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_FILTER_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, isbn);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MIN, datePublicationMin);
        values.put(MySQLiteHelper.COLUMN_DATE_PUBLICATION_MAX, datePublicationMax);
        values.put(MySQLiteHelper.COLUMN_PUBLISHER_ID, publisher_id);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MIN, nbPagesMin);
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MAX, nbPagesMax);
        values.put(MySQLiteHelper.COLUMN_ADVANCEMENT_STATE, advancementState);
        values.put(MySQLiteHelper.COLUMN_RATING_MIN, ratingMin);
        values.put(MySQLiteHelper.COLUMN_RATING_MAX, ratingMax);
        values.put(MySQLiteHelper.COLUMN_ON_WISH_LIST, onWishList);
        values.put(MySQLiteHelper.COLUMN_ON_FAVORITE_LIST, onFavoriteList);
        values.put(MySQLiteHelper.COLUMN_BOOK_STATE, bookState);
        values.put(MySQLiteHelper.COLUMN_POSSESSION_STATE, possessionState);
        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, friend_id);

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
        values.put(MySQLiteHelper.COLUMN_PUBLISHER_ID, filter.getPublisher_id());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MIN, filter.getNbPagesMin());
        values.put(MySQLiteHelper.COLUMN_NB_PAGES_MAX, filter.getNbPagesMax());
        values.put(MySQLiteHelper.COLUMN_ADVANCEMENT_STATE, filter.getAdvancementState());
        values.put(MySQLiteHelper.COLUMN_RATING_MIN, filter.getRatingMin());
        values.put(MySQLiteHelper.COLUMN_RATING_MAX, filter.getRatingMax());
        values.put(MySQLiteHelper.COLUMN_ON_WISH_LIST, filter.getOnWishList());
        values.put(MySQLiteHelper.COLUMN_ON_FAVORITE_LIST, filter.getOnFavoriteList());
        values.put(MySQLiteHelper.COLUMN_BOOK_STATE, filter.getBookState());
        values.put(MySQLiteHelper.COLUMN_POSSESSION_STATE, filter.getPossessionState());
        values.put(MySQLiteHelper.COLUMN_COMMENT, filter.getComment());
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, filter.getFriend_id());
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

    //used to get all books from friend Database
    public List<BookFilter> getAllBookFilters(AuthorLibrary authorLibrary, CategoryLibrary categoryLibrary) {
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
            filter.setAuthors(getAllAuthorFromABookFilter(authorLibrary, filter));
        }
        //used to fill the Categories Array
        for (BookFilter filter : filters) {
            filter.setCategories(getAllCategoryFromABookFilter(categoryLibrary, filter));
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
        filter.setPublisher_id(cursor.getLong(6));
        filter.setNbPages(cursor.getInt(7), cursor.getInt(8));

        filter.setAdvancementState(cursor.getString(9));
        filter.setRatingMin(cursor.getInt(10));
        filter.setRatingMax(cursor.getInt(11));
        filter.setOnWishList(cursor.getInt(12));
        filter.setOnFavoriteList(cursor.getInt(13));
        filter.setBookState(cursor.getInt(14));
        filter.setPossessionState(cursor.getInt(15));
        filter.setComment(cursor.getString(16));
        filter.setFriend_id(cursor.getLong(17));

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

    //only used for external friend database
    public List<Author> getAllAuthorFromABookFilter(AuthorLibrary authorLibrary, BookFilter filter) {
        return LinkTablesDataSource.getAllAuthorFromABookFilter(authorLibrary, database, filter);
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

    //only used for external friend database
    public List<Category> getAllCategoryFromABookFilter(CategoryLibrary categoryLibrary, BookFilter filter) {
        return LinkTablesDataSource.getAllCategoryFromABookFilter(categoryLibrary, database, filter);
    }

    public void deleteBookFilterCategories(long book_filter_id, long category_id) {

        System.out.println("Link deleted with book_filter_id: " + book_filter_id + " and author_id " + category_id);
        database.delete(MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_FILTER_ID
                + " = " + book_filter_id + " and " + MySQLiteHelper.COLUMN_CATEGORY_ID + " = " + category_id, null);
    }

}
