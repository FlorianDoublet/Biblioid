package flq.projectbooks.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;

/**
 * Created by flori on 01/02/2016.
 */
public class LinkTablesDataSource {


    //Global methode to get all Author from a cursor
    public static List<Author> getAllAuthorFromCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            List<Author> authors = new ArrayList<Author>();
            while (cursor.moveToNext()) {
                authors.add(AuthorLibrary.getInstance().getAuthorById(cursor.getLong(0)));
            }
            return authors;
        } else {
            return null;
        }
    }

    public static List<Author> getAllAuthorFromABookFilter(SQLiteDatabase database, BookFilter filter) {
        long book_filter_id = filter.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? ", new String[]{Long.toString(book_filter_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(cursor);
    }

    public static List<Author> getAllAuthorFromABook(SQLiteDatabase database, Book book) {
        long book_id = book.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_ID + "=? ", new String[]{Long.toString(book_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(cursor);
    }

    //Global method to add a link between authors and an other table (book or filter for example)
    public static long createLinkAuthors(SQLiteDatabase database, String table, String column_object, long object_id, long author_id) {

        ContentValues values = new ContentValues();
        values.put(column_object, object_id);
        values.put(MySQLiteHelper.COLUMN_AUTHOR_ID, author_id);

        long insertId = database.insert(table, null,
                values);

        return insertId;
    }

    public static long createBooksAuthors(SQLiteDatabase database, long book_id, long author_id) {
        return LinkTablesDataSource.createLinkAuthors(database, MySQLiteHelper.TABLE_BOOKS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_ID, book_id, author_id);
    }

    public static long createBookFiltersAuthors(SQLiteDatabase database, long book_filter_id, long author_id) {
        return LinkTablesDataSource.createLinkAuthors(database, MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_FILTER_ID, book_filter_id, author_id);
    }

    public static boolean cursorIsntEmpty(Cursor cursor) {
        Boolean bool = false;

        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            bool = true;
        }
        cursor.close();
        return bool;

    }

    public static boolean bookFiltersAuhorsIdExist(SQLiteDatabase database, long book_filter_id, long author_id) {
        Boolean bool = false;
        //select ID from TABLE_BOOK_FILTERS_AUTHORS where BOOK_FILTER_ID = book_id AND AUTHOR_ID = author_id
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_ID}, MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? AND "
                + MySQLiteHelper.COLUMN_AUTHOR_ID + "=? ", new String[]{Long.toString(book_filter_id), Long.toString(author_id)}, null, null, null);

        return LinkTablesDataSource.cursorIsntEmpty(cursor);
    }

    public static boolean booksAuhorsIdExist(SQLiteDatabase database, long book_id, long author_id) {
        Boolean bool = false;
        //select ID from TABLE_BOOKS_AUTHORS where BOOK_ID = book_id AND AUTHOR_ID = author_id
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_ID}, MySQLiteHelper.COLUMN_BOOK_ID + "=? AND "
                + MySQLiteHelper.COLUMN_AUTHOR_ID + "=? ", new String[]{Long.toString(book_id), Long.toString(author_id)}, null, null, null);

        return LinkTablesDataSource.cursorIsntEmpty(cursor);
    }

    public static void feedBookWithAuthor(Book book, EditText author) {
        long book_id = BookLibrary.getInstance().updateOrAddBook(book);
        //manually set the id to the book, to use it later to update this book
        book.setId(book_id);

        List<Author> authors = getAuthorsFromEditText(author, book_id);

        //check if an older author was removed from the book
        List<Author> old_authors = BookLibrary.getInstance().getAllAuthorFromABook(book);
        if (old_authors != null) {
            for (Author old_author : old_authors) {
                if (!authors.contains(old_author)) {
                    //then delete the link in the table BooksAuthor and from the Author List
                    BookLibrary.getInstance().deleteBooksAuthorsLink(book_id, old_author.getId());
                    authors.remove(old_author);
                }
            }
        }

        authors = LinkTablesDataSource.deleteDuplicatedAuthors(authors);
        //then add the authors list to the book
        book.setAuthors(authors);
        //update the library
        BookLibrary.getInstance().updateOrAddBook(book);


        //will update the BooksAuthors Table if a link between the current books and one of his authors don't exist
        for (Author author_a : authors) {
            if (!BookLibrary.getInstance().checkIfBooksAuthorLinkExist(book_id, author_a.getId())) {
                BookLibrary.getInstance().addBooksAuthorsLink(book_id, author_a.getId());
            }
        }

    }

    public static void feedBookFilterWithAuthors(BookFilter filter, EditText author) {
        long book_filter_id = BookFilterCatalog.getInstance().updateOrAddFilter(filter);
        filter.setId(book_filter_id);

        List<Author> authors = getAuthorsFromEditText(author, book_filter_id);

        //check if an older author was removed from the book
        List<Author> old_authors = BookFilterCatalog.getInstance().getAllAuthorFromABookFilter(filter);
        if (old_authors != null) {
            for (Author old_author : old_authors) {
                if (!authors.contains(old_author)) {
                    //then delete the link in the table BookFilterAuthor and from the Author List
                    BookFilterCatalog.getInstance().deleteBookFilterAuthorsLink(book_filter_id, old_author.getId());
                    authors.remove(old_author);
                }
            }
        }

        authors = LinkTablesDataSource.deleteDuplicatedAuthors(authors);
        //then add the authors list to the book
        filter.setAuthors(authors);
        //update the library
        BookFilterCatalog.getInstance().updateOrAddFilter(filter);

        //will update the BooksAuthors Table if a link between the current books and one of his authors don't exist
        for (Author author_a : authors) {
            if (!BookFilterCatalog.getInstance().checkIfBookFiltersAuthorLinkExist(book_filter_id, author_a.getId())) {
                BookFilterCatalog.getInstance().addBookFiltersAuthorsLink(book_filter_id, author_a.getId());
            }
        }
    }

    //Get authors for editText optionally create new authors
    public static List<Author> getAuthorsFromEditText(EditText author, long object_id) {
        long book_filter_id = object_id;

        List<Author> authors = new ArrayList<Author>();
        if (!author.getText().toString().equals("")) {
            String[] authors_s = author.getText().toString().split(",\\s");

            //check if the author already existe, else add it in the database
            for (String author_name : authors_s) {
                Author a = AuthorLibrary.getInstance().getAuthorByName(author_name);
                if (a != null) {
                    authors.add(a);
                } else {
                    authors.add(AuthorLibrary.getInstance().Add(new Author(author_name)));
                }
            }
        }

        return authors;
    }

    //delete duplicated authors from an author list
    public static List<Author> deleteDuplicatedAuthors(List<Author> authors) {

        //remove possible duplicate authors
        Set<Author> hs = new HashSet<Author>();
        hs.addAll(authors);
        authors.clear();
        authors.addAll(hs);
        return authors;
    }

    //Method to transform authors of a book into a simple string
    public static String authorsToString(List<Author> authors) {
        String author_s = "";
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                if (i == 0) {
                    author_s += authors.get(i).getName();
                } else
                    author_s += ", " + authors.get(i).getName();
            }
        }
        return author_s;
    }

}
