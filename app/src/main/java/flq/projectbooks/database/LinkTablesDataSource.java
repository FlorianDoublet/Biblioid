package flq.projectbooks.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;

/**
 * Created by flori on 01/02/2016.
 */
public class LinkTablesDataSource {


    //Global methode to get all Authors from a cursor
    public static List<Author> getAllAuthorFromCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            List<Author> authors = new ArrayList<Author>();
            while (cursor.moveToNext()) {
                authors.add(AuthorLibrary.getInstance().getAuthorById(cursor.getLong(0)));
            }
            cursor.close();
            return authors;
        } else {
            cursor.close();
            return null;
        }
    }

    //Global methode to get all Authors from a cursor for external friend database
    public static List<Author> getAllAuthorFromCursor(AuthorLibrary authorLibrary, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            List<Author> authors = new ArrayList<Author>();
            while (cursor.moveToNext()) {
                authors.add(authorLibrary.getAuthorById(cursor.getLong(0)));
            }
            cursor.close();
            return authors;
        } else {
            cursor.close();
            return null;
        }
    }

    //Global methode to get all Categories from a cursor
    public static List<Category> getAllCategoryFromCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            List<Category> categories = new ArrayList<Category>();
            while (cursor.moveToNext()) {
                categories.add(CategoryLibrary.getInstance().getCategoryById(cursor.getLong(0)));
            }
            cursor.close();
            return categories;
        } else {
            cursor.close();
            return null;
        }
    }

    //Global methode to get all Categories from a cursor  for external friend database
    public static List<Category> getAllCategoryFromCursor(CategoryLibrary categoryLibrary, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            List<Category> categories = new ArrayList<Category>();
            while (cursor.moveToNext()) {
                categories.add(categoryLibrary.getCategoryById(cursor.getLong(0)));
            }
            cursor.close();
            return categories;
        } else {
            cursor.close();
            return null;
        }
    }

    public static List<Author> getAllAuthorFromABookFilter(SQLiteDatabase database, BookFilter filter) {
        long book_filter_id = filter.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? ", new String[]{Long.toString(book_filter_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(cursor);
    }

    //only used for external friend database
    public static List<Author> getAllAuthorFromABookFilter(AuthorLibrary authorLibrary,SQLiteDatabase database, BookFilter filter) {
        long book_filter_id = filter.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? ", new String[]{Long.toString(book_filter_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(authorLibrary, cursor);
    }

    public static List<Category> getAllCategoryFromABookFilter(SQLiteDatabase database, BookFilter filter) {
        long book_filter_id = filter.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_CATEGORY_ID},
                MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? ", new String[]{Long.toString(book_filter_id)}, null, null, null);

        return LinkTablesDataSource.getAllCategoryFromCursor(cursor);
    }

    //only used for external friend database
    public static List<Category> getAllCategoryFromABookFilter(CategoryLibrary categoryLibrary, SQLiteDatabase database, BookFilter filter) {
        long book_filter_id = filter.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_CATEGORY_ID},
                MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? ", new String[]{Long.toString(book_filter_id)}, null, null, null);

        return LinkTablesDataSource.getAllCategoryFromCursor(categoryLibrary, cursor);
    }

    public static List<Author> getAllAuthorFromABook(SQLiteDatabase database, Book book) {
        long book_id = book.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_ID + "=? ", new String[]{Long.toString(book_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(cursor);
    }

    //only used for external friend database
    public static List<Author> getAllAuthorFromABook(AuthorLibrary authorLibrary,SQLiteDatabase database, Book book) {
        long book_id = book.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_AUTHOR_ID},
                MySQLiteHelper.COLUMN_BOOK_ID + "=? ", new String[]{Long.toString(book_id)}, null, null, null);

        return LinkTablesDataSource.getAllAuthorFromCursor(authorLibrary, cursor);
    }

    public static List<Category> getAllCategoriesFromABook(SQLiteDatabase database, Book book) {
        long book_id = book.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_CATEGORY_ID},
                MySQLiteHelper.COLUMN_BOOK_ID + "=? ", new String[]{Long.toString(book_id)}, null, null, null);

        return LinkTablesDataSource.getAllCategoryFromCursor(cursor);
    }

    //only used for external friend database
    public static List<Category> getAllCategoriesFromABook(CategoryLibrary categoryLibrary, SQLiteDatabase database, Book book) {
        long book_id = book.getId();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_CATEGORY_ID},
                MySQLiteHelper.COLUMN_BOOK_ID + "=? ", new String[]{Long.toString(book_id)}, null, null, null);

        return LinkTablesDataSource.getAllCategoryFromCursor(categoryLibrary, cursor);
    }

    //Global method to add a link between 2 tables (only for a link table with 2 foreign key)
    public static long createSimpleLinks(SQLiteDatabase database, String table, String column_object1, long object1_id, String column_object2, long object2_id) {

        ContentValues values = new ContentValues();
        values.put(column_object1, object1_id);
        values.put(column_object2, object2_id);

        long insertId = database.insert(table, null,
                values);

        return insertId;
    }

    public static long createBooksAuthors(SQLiteDatabase database, long book_id, long author_id) {
        return LinkTablesDataSource.createSimpleLinks(database, MySQLiteHelper.TABLE_BOOKS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_ID, book_id, MySQLiteHelper.COLUMN_AUTHOR_ID, author_id);
    }

    public static long createBooksCategories(SQLiteDatabase database, long book_id, long category_id) {
        return LinkTablesDataSource.createSimpleLinks(database, MySQLiteHelper.TABLE_BOOKS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_ID, book_id, MySQLiteHelper.COLUMN_CATEGORY_ID, category_id);
    }

    public static long createBookFiltersAuthors(SQLiteDatabase database, long book_filter_id, long author_id) {
        return LinkTablesDataSource.createSimpleLinks(database, MySQLiteHelper.TABLE_BOOK_FILTERS_AUTHORS, MySQLiteHelper.COLUMN_BOOK_FILTER_ID, book_filter_id, MySQLiteHelper.COLUMN_AUTHOR_ID, author_id);
    }

    public static long createBookFiltersCategories(SQLiteDatabase database, long book_filter_id, long category_id) {
        return LinkTablesDataSource.createSimpleLinks(database, MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, MySQLiteHelper.COLUMN_BOOK_FILTER_ID, book_filter_id, MySQLiteHelper.COLUMN_CATEGORY_ID, category_id);
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

    public static boolean bookFiltersCategoriesIdExist(SQLiteDatabase database, long book_filter_id, long category_id) {
        Boolean bool = false;
        //select ID from TABLE_BOOK_FILTERS_CATEGORIES where BOOK_FILTER_ID = book_id AND CATEGORY_ID = author_id
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOK_FILTERS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_ID}, MySQLiteHelper.COLUMN_BOOK_FILTER_ID + "=? AND "
                + MySQLiteHelper.COLUMN_CATEGORY_ID + "=? ", new String[]{Long.toString(book_filter_id), Long.toString(category_id)}, null, null, null);

        return LinkTablesDataSource.cursorIsntEmpty(cursor);
    }

    public static boolean booksAuhorsIdExist(SQLiteDatabase database, long book_id, long author_id) {
        Boolean bool = false;
        //select ID from TABLE_BOOKS_AUTHORS where BOOK_ID = book_id AND AUTHOR_ID = author_id
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_AUTHORS, new String[]{MySQLiteHelper.COLUMN_ID}, MySQLiteHelper.COLUMN_BOOK_ID + "=? AND "
                + MySQLiteHelper.COLUMN_AUTHOR_ID + "=? ", new String[]{Long.toString(book_id), Long.toString(author_id)}, null, null, null);

        return LinkTablesDataSource.cursorIsntEmpty(cursor);
    }

    public static boolean booksCategoriesIdExist(SQLiteDatabase database, long book_id, long category_id) {
        Boolean bool = false;
        //select ID from TABLE_BOOKS_CATEGORIES where BOOK_ID = book_id AND CATEGORY_ID = category_id
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS_CATEGORIES, new String[]{MySQLiteHelper.COLUMN_ID}, MySQLiteHelper.COLUMN_BOOK_ID + "=? AND "
                + MySQLiteHelper.COLUMN_CATEGORY_ID + "=? ", new String[]{Long.toString(book_id), Long.toString(category_id)}, null, null, null);

        return LinkTablesDataSource.cursorIsntEmpty(cursor);
    }

    public static long feedBookWithAuthor(Book book, EditText author) {
        long book_id = BookLibrary.getInstance().updateOrAddBook(book);
        //manually set the id to the book, to use it later to update this book
        book.setId(book_id);

        List<Author> authors = getAuthorsFromEditText(author);

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
        return book_id;

    }

    public static long feedBookWithCategories(Book book, EditText category) {
        long book_id = BookLibrary.getInstance().updateOrAddBook(book);
        //manually set the id to the book, to use it later to update this book
        book.setId(book_id);

        List<Category> categories = getCategoriesFromEditText(category);

        //check if an older category was removed from the book
        List<Category> old_categories = BookLibrary.getInstance().getAllCategoryFromABook(book);
        if (old_categories != null) {
            for (Category old_category : old_categories) {
                if (!categories.contains(old_category)) {
                    //then delete the link in the table BooksCategories and from the Category List
                    BookLibrary.getInstance().deleteBooksCategoriesLink(book_id, old_category.getId());
                    categories.remove(old_category);
                }
            }
        }

        categories = LinkTablesDataSource.deleteDuplicatedCategories(categories);
        //then add the categories list to the book
        book.setCategories(categories);
        //update the library
        BookLibrary.getInstance().updateOrAddBook(book);


        //will update the BooksCategories Table if a link between the current book and one of his categories don't exist
        for (Category category_a : categories) {
            if (!BookLibrary.getInstance().checkIfBooksCategoriesLinkExist(book_id, category_a.getId())) {
                BookLibrary.getInstance().addBooksCategoriesLink(book_id, category_a.getId());
            }
        }
        return book_id;

    }

    public static long feedBookFilterWithAuthors(BookFilter filter, EditText author) {
        long book_filter_id = BookFilterCatalog.getInstance().updateOrAddFilter(filter);
        filter.setId(book_filter_id);

        List<Author> authors = getAuthorsFromEditText(author);

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
        return book_filter_id;
    }

    public static long feedBookFilterWithCategories(BookFilter filter, EditText category) {
        long book_filter_id = BookFilterCatalog.getInstance().updateOrAddFilter(filter);
        filter.setId(book_filter_id);

        List<Category> categories = getCategoriesFromEditText(category);

        //check if an older category was removed from the bookfilter
        List<Category> old_categories = BookFilterCatalog.getInstance().getAllCategoryFromABookFilter(filter);
        if (old_categories != null) {
            for (Category old_category : old_categories) {
                if (!categories.contains(old_category)) {
                    //then delete the link in the table BookFilterCategories and from the Category List
                    BookFilterCatalog.getInstance().deleteBookFilterCategoriesLink(book_filter_id, old_category.getId());
                    categories.remove(old_category);
                }
            }
        }

        categories = LinkTablesDataSource.deleteDuplicatedCategories(categories);
        //then add the categories list to the book
        filter.setCategories(categories);
        //update the library
        BookFilterCatalog.getInstance().updateOrAddFilter(filter);

        //will update the BooksAuthors Table if a link between the current books and one of his authors don't exist
        for (Category category_a : categories) {
            if (!BookFilterCatalog.getInstance().checkIfBookFiltersCategoriesLinkExist(book_filter_id, category_a.getId())) {
                BookFilterCatalog.getInstance().addBookFiltersCategoriesLink(book_filter_id, category_a.getId());
            }
        }
        return book_filter_id;
    }

    //Get authors from string optionally create new authors
    public static List<Author> getAuthorsFromString(String author) {
        author = author.trim();
        List<Author> authors = new ArrayList<Author>();
        if (!author.equals("")) {
            String[] authors_s = author.split(",");

            //check if the author already existe, else add it in the database
            for (String author_name : authors_s) {
                author_name = author_name.trim();
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

    //Get authors from editText optionally create new authors
    public static List<Author> getAuthorsFromEditText(EditText author) {
        return LinkTablesDataSource.getAuthorsFromString(author.getText().toString());
    }

    //Get categories from editText optionally create new category
    public static List<Category> getCategoriesFromString(String category) {
        category = category.trim();
        List<Category> categories = new ArrayList<Category>();
        if (!category.equals("")) {
            String[] categories_s = category.split(",");

            //check if the category already exist, else add it in the database
            for (String category_name : categories_s) {
                category_name = category_name.trim();
                Category a = CategoryLibrary.getInstance().getCategoryByName(category_name);
                if (a != null) {
                    categories.add(a);
                } else {
                    categories.add(CategoryLibrary.getInstance().Add(new Category(category_name)));
                }
            }
        }

        return categories;
    }

    //Get categories from editText optionally create new category
    public static List<Category> getCategoriesFromEditText(EditText category) {
        return LinkTablesDataSource.getCategoriesFromString(category.getText().toString());
    }

    //delete duplicated authors from an author list
    public static List<Author> deleteDuplicatedAuthors(List<Author> authors) {
        //I don't use hashet here because it destroy the order in the authors list
        int size = authors.size();

        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (authors.get(j).getId() != authors.get(i).getId())
                    continue;
                authors.remove(j);
                // decrease j because the array got re-indexed
                j--;
                // decrease the size of the array
                size--;
            } // for j
        } // for i


        return authors;
    }

    //delete duplicated categories from an author list
    public static List<Category> deleteDuplicatedCategories(List<Category> categories) {
        //I don't use hashet here because it destroy the order in the authors list
        int size = categories.size();

        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (categories.get(j).getId() != categories.get(i).getId())
                    continue;
                categories.remove(j);
                // decrease j because the array got re-indexed
                j--;
                // decrease the size of the array
                size--;
            } // for j
        } // for i


        return categories;
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

    //Method to transform categories of a book into a simple string
    public static String categoriesToString(List<Category> categories) {
        String categorie_s = "";
        if (categories != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (i == 0) {
                    categorie_s += categories.get(i).getName();
                } else
                    categorie_s += ", " + categories.get(i).getName();
            }
        }
        return categorie_s;
    }

}
