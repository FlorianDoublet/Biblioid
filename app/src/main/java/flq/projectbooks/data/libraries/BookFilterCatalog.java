package flq.projectbooks.data.libraries;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flq.projectbooks.UI.fragments.SettingsFragment;
import flq.projectbooks.data.Author;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.database.BookFiltersDataSource;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilterCatalog implements Serializable {

    private static BookFilterCatalog bookFilters;
    private static Context context;
    List<BookFilter> bookFilterList;
    private BookFiltersDataSource datasource;

    public BookFilterCatalog() {
        bookFilterList = new ArrayList<>();
        datasource = new BookFiltersDataSource(context);
        loadBookFiltersWithPref(false);
    }

    public BookFilterCatalog(Context _context, String dbName, int dbVersion) {
        bookFilterList = new ArrayList<>();
        datasource = new BookFiltersDataSource(_context, dbName, dbVersion);
        loadBookFiltersWithPref(true);
    }

    public BookFilterCatalog(Context _context) {
        context = _context;
        bookFilters = new BookFilterCatalog();
    }

    public static BookFilterCatalog getInstance() {
        return bookFilters;
    }

    public static BookFilterCatalog getInstanceOrInitialize(Context _context) {
        if (bookFilters == null) {
            new BookFilterCatalog(_context);
        }
        return bookFilters;
    }

    public void loadBookFiltersWithPref(boolean withContext) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_order = sharedPref.getString(SettingsFragment.KEY_PREF_FILTER_DISPLAY_ORDER, "1");


        if (withContext) {
            AuthorLibrary authorLibrary = new AuthorLibrary(datasource.getDbHelper());
            CategoryLibrary categoryLibrary = new CategoryLibrary(datasource.getDbHelper());
            datasource.open();
            bookFilterList = datasource.getAllBookFilters(authorLibrary, categoryLibrary);
            datasource.close();
        } else {
            datasource.open();
            bookFilterList = datasource.getAllBookFilters();
            datasource.close();
        }

        /*if (bookFilterList.size() == 0) {
            BookFilter filterBase1 = new BookFilter();
            filterBase1.setName("Liste de souhait");
            filterBase1.setOnWishList(1);
            this.Add(filterBase1);

            BookFilter filterBase2 = new BookFilter();
            filterBase2.setName("Liste des favoris");
            filterBase2.setOnFavoriteList(1);
            this.Add(filterBase2);

            BookFilter filterBase3 = new BookFilter();
            filterBase3.setName("Livres lus");
            filterBase3.setAdvancementState("Read");
            this.Add(filterBase3);

            BookFilter filterBase4 = new BookFilter();
            filterBase4.setName("Livres en cours de lecture");
            filterBase4.setAdvancementState("Reading");
            this.Add(filterBase4);

            BookFilter filterBase5 = new BookFilter();
            filterBase5.setName("Livres non lus");
            filterBase5.setAdvancementState("Not Read");
            this.Add(filterBase5);
        }*/

        switch (pref_order) {
            case "Ordre de création":
                //Do nothing, default bookList order is in the creation order
                break;
            case "Ordre alphabétique":
                Collections.sort(bookFilterList, new Comparator<BookFilter>() {
                    @Override
                    public int compare(final BookFilter object1, final BookFilter object2) {
                        return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                    }
                });
                break;
            case "Ordre aléatoire":
                Collections.shuffle(bookFilterList);
                break;
        }
    }

    public BookFilter Add(BookFilter filter) {
        //Add in the database
        datasource.open();
        BookFilter newFilter = datasource.createFilter(filter.getName(), filter.getTitle(), filter.getDescription(), filter.getDatePublicationMin(), filter.getDatePublicationMax(), filter.getPublisher_id(), filter.getNbPagesMin(), filter.getNbPagesMax(), filter.getAdvancementState(), filter.getRatingMin(), filter.getRatingMax(), filter.getOnWishList(), filter.getOnFavoriteList(), filter.getBookState(), filter.getPossessionState(), filter.getComment(), filter.getFriend_id());
        datasource.close();

        //Add in the local list
        bookFilterList.add(newFilter);
        return newFilter;
    }

    public List<BookFilter> getBookFilterList() {
        return bookFilterList;
    }

    public BookFilter getNewFilters() {
        return new BookFilter();
    }

    public void deleteFilters(BookFilter filter) {
        bookFilterList.remove(filter);
    }

    public void deleteFilterById(long id) {
        for (int j = 0; j < bookFilterList.size(); j++) {
            if (bookFilterList.get(j).getId() == id) {
                //Remove from database
                BookFilter temp = bookFilterList.get(j);
                datasource.open();
                datasource.deleteBookFilter(temp);
                datasource.close();

                //remove all filters_authors link
                List<Author> authors = getAllAuthorFromABookFilter(temp);
                if (authors != null) {
                    for (Author author : authors) {
                        if (checkIfBookFiltersAuthorLinkExist(temp.getId(), author.getId())) {
                            deleteBookFilterAuthorsLink(temp.getId(), author.getId());
                        }
                    }
                }

                //remove all books_categories link
                List<Category> categories = getAllCategoryFromABookFilter(temp);
                if (categories != null) {
                    for (Category category : categories) {
                        if (checkIfBookFiltersCategoriesLinkExist(temp.getId(), category.getId())) {
                            deleteBookFilterCategoriesLink(temp.getId(), category.getId());
                        }
                    }
                }

                //Remove from local list
                bookFilterList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        loadBookFiltersWithPref(false);
    }

    public long updateOrAddFilter(BookFilter filter) {
        long id = filter.getId();
        if (id != -1) {
            for (int j = 0; j < bookFilterList.size(); j++) {
                if (bookFilterList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateBookFilter(filter); //Update database
                    datasource.close();
                    bookFilterList.set(j, filter); //Update local list
                }
            }
        } else {
            datasource.open();
            filter = datasource.createFilter(filter.getName(), filter.getTitle(), filter.getDescription(), filter.getDatePublicationMin(), filter.getDatePublicationMax(), filter.getPublisher_id(), filter.getNbPagesMin(), filter.getNbPagesMax(), filter.getAdvancementState(), filter.getRatingMin(), filter.getRatingMax(), filter.getOnWishList(), filter.getOnFavoriteList(), filter.getBookState(), filter.getPossessionState(), filter.getComment(), filter.getFriend_id()); //Add book to database
            //bookFilterList = datasource.getAllBookFilters(); //Update books
            loadBookFiltersWithPref(false);
            datasource.close();
        }
        return filter.getId();
    }

    public boolean checkIfBookFiltersAuthorLinkExist(long book_filter_id, long author_id) {
        datasource.open();
        Boolean bool = datasource.bookFiltersAuhorsIdExist(book_filter_id, author_id);
        datasource.close();
        return bool;
    }

    public long addBookFiltersAuthorsLink(long book_id, long author_id) {
        datasource.open();
        long inserted_id = datasource.createBookFiltersAuthors(book_id, author_id);
        datasource.close();
        return inserted_id;
    }

    public void deleteBookFilterAuthorsLink(long book_filter_id, long author_id) {
        datasource.open();
        datasource.deleteBookFilterAuthors(book_filter_id, author_id);
        datasource.close();
    }

    public List<Author> getAllAuthorFromABookFilter(BookFilter filter) {
        datasource.open();
        List<Author> authors = datasource.getAllAuthorFromABookFilter(filter);
        datasource.close();
        return authors;
    }

    public boolean checkIfBookFiltersCategoriesLinkExist(long book_filter_id, long category_id) {
        datasource.open();
        Boolean bool = datasource.bookFiltersCategoriesIdExist(book_filter_id, category_id);
        datasource.close();
        return bool;
    }

    public long addBookFiltersCategoriesLink(long book_id, long category_id) {
        datasource.open();
        long inserted_id = datasource.createBookFiltersCategories(book_id, category_id);
        datasource.close();
        return inserted_id;
    }

    public void deleteBookFilterCategoriesLink(long book_filter_id, long category_id) {
        datasource.open();
        datasource.deleteBookFilterCategories(book_filter_id, category_id);
        datasource.close();
    }

    public List<Category> getAllCategoryFromABookFilter(BookFilter filter) {
        datasource.open();
        List<Category> categories = datasource.getAllCategoryFromABookFilter(filter);
        datasource.close();
        return categories;
    }

}
