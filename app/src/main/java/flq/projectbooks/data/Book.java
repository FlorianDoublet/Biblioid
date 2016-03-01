package flq.projectbooks.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doublet F. Delvallet Q. and Delvallet L. on 24/09/15.
 */
public class Book implements Serializable {
    private long id;
    private String title;
    private List<Author> authors;
    private String isbn;
    private byte[] image;
    private String description;
    private String datePublication;
    private long publisher_id;
    private List<Category> categories;
    private int nbPages;
    private long friend_id;
    private String advancementState;
    private int rating ;
    private int onWishList;
    private int onFavoriteList;
    private int bookState;
    private int possessionState;
    private String comment;

    public static ArrayList<String> spinnerArrayState ;
    public static ArrayList<String> spinnerArrayPossession ;

    public Book() {
        id = -1;
        title = "";
        authors = new ArrayList<>();
        isbn = "";
        description = "";
        datePublication = "";
        publisher_id = -1;
        categories = new ArrayList<>();
        nbPages = 0;
        friend_id = -1;
        advancementState = "Not Read";
        rating = 0;
        onWishList = 0;
        onFavoriteList = 0;
        bookState = 0;
        possessionState = 0;
        comment = "";
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        this.datePublication = datePublication;
    }

    public long getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(long publisher_id) {
        this.publisher_id = publisher_id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(long friend_id) {
        this.friend_id = friend_id;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getAdvancementState() {
        return advancementState;
    }

    public void setAdvancementState(String advancementState) {
        this.advancementState = advancementState;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getBookState() {
        return bookState;
    }

    public void setBookState(int bookState) {
        this.bookState = bookState;
    }

    public int getOnFavoriteList() {
        return onFavoriteList;
    }

    public void setOnFavoriteList(int onFavoriteList) {
        this.onFavoriteList = onFavoriteList;
    }

    public int getOnWishList() {
        return onWishList;
    }

    public void setOnWishList(int onWhishList) {
        this.onWishList = onWhishList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPossessionState() {
        return possessionState;
    }

    public void setPossessionState(int possessionState) {
        this.possessionState = possessionState;
    }
}
