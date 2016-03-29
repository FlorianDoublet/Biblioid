package flq.projectbooks.data;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.FilteredBookLibrary;

/**
 * Created by doublet on 01/10/15.
 */
public class BookFilter implements Serializable {
    public static ArrayList<String> spinnerArrayState;
    public static ArrayList<String> spinnerArrayPossession;
    private long id;
    private String name;
    private String title;
    private List<Author> authors;
    private String description;
    private String datePublicationMin;
    private String datePublicationMax;
    private long publisher_id;
    private List<Category> categories;
    private int nbPagesMin;
    private int nbPagesMax;
    private String advancementState;
    private int ratingMin;
    private int ratingMax;
    private int onWishList;
    private int onFavoriteList;
    private int bookState;
    private int possessionState;
    private String comment;
    private long friend_id;

    public BookFilter() {
        id = -1;
        name = "";
        title = "";
        authors = new ArrayList<>();
        description = "";
        datePublicationMin = "";
        datePublicationMax = "";
        publisher_id = -1;
        categories = new ArrayList<>();
        nbPagesMin = 0;
        nbPagesMax = 0;
        advancementState = "";
        ratingMin = 0;
        ratingMax = 0;
        onWishList = 0;
        onFavoriteList = 0;
        bookState = 0;
        possessionState = 0;
        comment = "";
        friend_id = -1;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public long getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(long publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getDatePublicationMin() {
        return datePublicationMin;
    }

    public String getDatePublicationMax() {
        return datePublicationMax;
    }

    public long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(long friend_id) {
        this.friend_id = friend_id;
    }

    public void setDatePublications(String datePublicationMin, String datePublicationMax) {
        this.datePublicationMin = datePublicationMin;
        this.datePublicationMax = datePublicationMax;
    }

    public int getNbPagesMin() {
        return nbPagesMin;
    }

    public int getNbPagesMax() {
        return nbPagesMax;
    }

    public void setNbPages(int nbPagesMin, int nbPagesMax) {
        this.nbPagesMin = nbPagesMin;
        this.nbPagesMax = nbPagesMax;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSelected(Book book) {

        //to filtrate on friend books
        if (friend_id != -1 && book.getFriend_id() != friend_id) {
            return false;
        }

        //check if all authors in the filter are in the book
        if (authors != null && authors.size() != 0) {
            List<Author> authors_book = book.getAuthors();
            for (Author author_filter : authors) {
                Boolean authorMatch = false;
                for (Author author : authors_book) {
                    if (author.getId() == author_filter.getId())
                        authorMatch = true;
                }
                if (!authorMatch)
                    return false;
            }
        }

        if (!title.equals("") && !book.getTitle().contains(title)) {
            return false;
        }
        if (!description.equals("") && !book.getDescription().contains(description)) {
            return false;
        }

        //check if all categories in the filter are in the book
        if (categories != null && categories.size() != 0) {
            List<Category> categories_book = book.getCategories();
            for (Category category_filter : categories) {
                Boolean categoryMatch = false;
                for (Category category : categories_book) {
                    if (category.getId() == category_filter.getId())
                        categoryMatch = true;
                }
                if (!categoryMatch)
                    return false;
            }
        }

        if (publisher_id != -1 && book.getPublisher_id() != publisher_id) {
            return false;
        }

        if (!datePublicationMin.equals("") && !datePublicationMax.equals("")) {
            if ((yearFromString(datePublicationMin) > yearFromString(book.getDatePublication()) || yearFromString(datePublicationMax) < yearFromString(book.getDatePublication())))
                return false;
        }

        if (nbPagesMax != 0 && ((book.getNbPages() < nbPagesMin) || (book.getNbPages() > nbPagesMax))) {
            return false;
        }

        //Check if the book match the advancement state

        switch (advancementState) {
            case "Undetermined":
                break;
            case "Read":
                if (!book.getAdvancementState().equals(advancementState)) {
                    return false;
                }
                break;
            case "Reading":
                if (book.getAdvancementState().equals("Read") || book.getAdvancementState().equals("Not Read")) {
                    return false;
                }
                break;
            case "Not Read":
                if (!book.getAdvancementState().equals(advancementState)) {
                    return false;
                }
                break;
        }

        //Check if the book fulfill the min rating filter
        if (ratingMin != 0 && book.getRating() < ratingMin) {
            return false;
        }

        //Check if the book fulfill the max rating filter
        if (ratingMax != 0 && book.getRating() > ratingMax) {
            return false;
        }

        if (book.getRating() == 0 && (ratingMax != 0 || ratingMin != 0)) {
            return false;
        }

        //Check if the book is in the favorite list
        if (onFavoriteList == 1 && book.getOnFavoriteList() != 1) {
            return false;
        }

        //Check if the book is in the wish list
        if (onWishList == 1 && book.getOnWishList() != 1) {
            return false;
        }

        //Check if the book state match the filter
        if (bookState != 0 && book.getBookState() != (bookState - 1)) {
            return false;
        }

        //Check if the book possession match the filter
        if (possessionState != 0 && book.getPossessionState() != (possessionState - 1)) {
            return false;
        }

        //Check if the book comment contains the filter comment string
        if (comment.equals("") && !book.getComment().contains(comment)) {
            return false;
        }

        return true;
    }

    private boolean isSelectedAuthor(Book book) {

        return false;
    }

    public FilteredBookLibrary getFilteredLibrary() {
        FilteredBookLibrary filteredBooksLibrary = new FilteredBookLibrary();
        for (int i = 0; i < BookLibrary.getInstance().getBookList().size(); i++) {
            if (this.isSelected(BookLibrary.getInstance().getBookList().get(i))) {
                filteredBooksLibrary.Add(BookLibrary.getInstance().getBookList().get(i));
            }
        }
        return filteredBooksLibrary;
    }

    private int yearFromString(String date) {

        if (date.length() > 4) {
            String full_date[];
            if (date.contains("/")) {
                full_date = date.split("/");
            } else {
                full_date = date.split("-");
            }
            if (full_date[0].length() == 4) {
                date = full_date[0];
            } else {
                date = full_date[2];
            }
        }
        return Integer.parseInt(date);
    }

    public String getAdvancementState() {
        return advancementState;
    }

    public void setAdvancementState(String advancementState) {
        this.advancementState = advancementState;
    }

    public int getRatingMin() {
        return ratingMin;
    }

    public void setRatingMin(int ratingMin) {
        this.ratingMin = ratingMin;
    }

    public int getRatingMax() {
        return ratingMax;
    }

    public void setRatingMax(int ratingMax) {
        this.ratingMax = ratingMax;
    }

    public int getOnWishList() {
        return onWishList;
    }

    public void setOnWishList(int onWishList) {
        this.onWishList = onWishList;
    }

    public int getOnFavoriteList() {
        return onFavoriteList;
    }

    public void setOnFavoriteList(int onFavoriteList) {
        this.onFavoriteList = onFavoriteList;
    }

    public int getBookState() {
        return bookState;
    }

    public void setBookState(int bookState) {
        this.bookState = bookState;
    }

    public int getPossessionState() {
        return possessionState;
    }

    public void setPossessionState(int possessionState) {
        this.possessionState = possessionState;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
