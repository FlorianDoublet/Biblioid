package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        datasource.open();
        bookFilterList = datasource.getAllBookFilters();
        datasource.close();
        if(bookFilterList.size() == 0){
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

            BookFilter filterBase6 = new BookFilter();
            filterBase6.setName("Note supérieure à 4");
            filterBase6.setRatingMin(4);
            this.Add(filterBase6);

            BookFilter filterBase7 = new BookFilter();
            filterBase7.setName("Note strictement inférieure à 3");
            filterBase7.setRatingMax(2);
            this.Add(filterBase7);

            BookFilter filterBase8 = new BookFilter();
            filterBase8.setName("Note égale à 5");
            filterBase8.setRatingMin(5);
            filterBase8.setRatingMax(5);
            this.Add(filterBase8);

            BookFilter filterBase9 = new BookFilter();
            filterBase9.setName("Livre prétés");
            filterBase9.setPossessionState(3);
            this.Add(filterBase9);

            BookFilter filterBase10 = new BookFilter();
            filterBase10.setName("Livre possédés");
            filterBase10.setPossessionState(2);
            this.Add(filterBase10);
        }
    }

    public BookFilterCatalog(Context _context) {
        context = _context;
        bookFilters = new BookFilterCatalog();
    }

    public static BookFilterCatalog getInstance() {
        return bookFilters;
    }

    public void Add(BookFilter filter) {
        //Add in the database
        datasource.open();
        datasource.createFilter(filter.getName(), filter.getTitle(), filter.getDescription(), filter.getDatePublicationMin(), filter.getDatePublicationMax(), filter.getPublisher_id(), filter.getNbPagesMin(), filter.getNbPagesMax(), filter.getAdvancementState(), filter.getRatingMin(), filter.getRatingMax(), filter.getOnWishList(), filter.getOnFavoriteList(), filter.getBookState(), filter.getPossessionState(), filter.getComment());
        datasource.close();

        //Add in the local list
        bookFilterList.add(filter);
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

    public void deleteFilterById(int id) {
        for (int j = 0; j < bookFilterList.size(); j++) {
            if (bookFilterList.get(j).getId() == id) {
                //Remove from database
                BookFilter temp = bookFilterList.get(j);
                datasource.open();
                datasource.deleteBookFilter(temp);
                datasource.close();

                //Remove from local list
                bookFilterList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        bookFilterList = datasource.getAllBookFilters();
        datasource.close();
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
            filter = datasource.createFilter(filter.getName(), filter.getTitle(), filter.getDescription(), filter.getDatePublicationMin(), filter.getDatePublicationMax(), filter.getPublisher_id(), filter.getNbPagesMin(), filter.getNbPagesMax(), filter.getAdvancementState(), filter.getRatingMin(), filter.getRatingMax(), filter.getOnWishList(), filter.getOnFavoriteList(), filter.getBookState(), filter.getPossessionState(), filter.getComment()); //Add book to database
            bookFilterList = datasource.getAllBookFilters(); //Update books
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
