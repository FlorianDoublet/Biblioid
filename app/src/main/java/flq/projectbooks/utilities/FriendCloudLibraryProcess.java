package flq.projectbooks.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.Publisher;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;
import flq.projectbooks.database.MySQLiteHelper;

/**
 * Created by flori on 20/03/2016.
 */
public class FriendCloudLibraryProcess extends AsyncTask<Void, Integer, Void> {

    private final static int DB_VERSION = 1;
    private final static String EXTERNAL_DB_NAME = "external.db";
    private Friend friend;
    private Context mContext;
    private BookLibrary bookLibraryFriend;
    private BookFilterCatalog filterLibrary;
    private PublisherLibrary publisherLibraryFriend;
    private List<Book> friendBookList;
    private List<BookFilter> friendFilterList;
    private String message = "Livres de l'ami ajoutés";
    private String LINK_BEGINING = "https://drive.google.com/uc?export=download&id=";
    private ProgressDialog pd = null;


    public FriendCloudLibraryProcess(Context mContext, Friend friend) {
        super();
        this.friend = friend;
        this.mContext = mContext;
    }

    @Override
    protected void onProgressUpdate(Integer... args) {
        pd.setProgress(args[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(mContext);
        pd.setTitle("Chargement...");
        pd.setMessage("Patientez s'il vous plait.");
        pd.setCancelable(false);
        pd.show();

    }

    @Override
    protected void onPostExecute(Void arg0) {
        super.onPostExecute(arg0);

        if (pd != null) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            pd = null;
        }
        Toast.makeText(mContext, this.message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //first we dowload the file
        if (!downloadFriendDb(LINK_BEGINING + friend.getCloudLink())) {
            return null;
        }

        //we delete all the books of our friend before to add them for the new database retrieve by the cloud
        List<Book> ourBooks = new ArrayList<>(BookLibrary.getInstance().getBookList());
        for (Book book : ourBooks) {
            if (book.getFriend_id() == friend.getId()) {
                BookLibrary.getInstance().deleteBookById(book.getId());
            }
        }

        //we delete all the filters of our friend before to add them for the new database retrieve by the cloud
        List<BookFilter> ourFilters = new ArrayList<>(BookFilterCatalog.getInstance().getBookFilterList());
        for (BookFilter filter : ourFilters) {
            if (filter.getFriend_id() == friend.getId()) {
                BookFilterCatalog.getInstance().deleteFilterById(filter.getId());
            }
        }

        bookLibraryFriend = new BookLibrary(mContext, EXTERNAL_DB_NAME, DB_VERSION);
        filterLibrary = new BookFilterCatalog(mContext, EXTERNAL_DB_NAME, DB_VERSION);
        publisherLibraryFriend = new PublisherLibrary(new MySQLiteHelper(mContext, EXTERNAL_DB_NAME, DB_VERSION));
        friendBookList = bookLibraryFriend.getBookList();
        friendFilterList = filterLibrary.getBookFilterList();

        //treatment for each book
        friendBookTreatment();

        //treatment for each filters
        friendFilterTreatment();


        return null;
    }

    protected Boolean downloadFriendDb(String stringUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                message = "Impossible d'importer les livres de l'ami";
                return false;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            String friend_db_path = "/data/data/" + mContext.getPackageName() + "/databases/" + EXTERNAL_DB_NAME;
            output = new FileOutputStream(friend_db_path);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return false;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            message = "Impossible d'importer les livres de l'ami";
            return false;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return true;
    }

    private void friendBookTreatment() {
        if (friendBookList == null) {
            return;
        }
        for (Book friendBook : friendBookList) {

            //we don't want a book of a friend of our friend
            if (friendBook.getFriend_id() != -1) {
                continue;
            }

            //Create publisher if don't exist and update id of the publisher in the book
            long publisher_id = friendBook.getPublisher_id();
            if (publisher_id != -1) {
                Publisher friendBookPublisher = publisherLibraryFriend.getPublisherById(publisher_id);
                Publisher newPublisher = PublisherLibrary.getInstance().findAndAddAPublisher(friendBookPublisher.getName());
                friendBook.setPublisher_id(newPublisher.getId());
            }


            List<Author> friendBookAuthors = friendBook.getAuthors();
            List<Category> friendBookCaterogies = friendBook.getCategories();

            //add the friend_id to the book to know that it's a friend book
            friendBook.setFriend_id(friend.getId());
            //then add the book to our library
            Book book = BookLibrary.getInstance().Add(friendBook);

            if (friendBookAuthors != null) {
                for (Author author : friendBookAuthors) {
                    Author old_author = AuthorLibrary.getInstance().getAuthorByName(author.getName());
                    //if the author don't exist we create it, else we just replace the new by the old for the good ID
                    if (old_author == null) {
                        author = AuthorLibrary.getInstance().Add(author);
                    } else {
                        author = old_author;
                    }
                    //then we create a link between our new book and the author.
                    BookLibrary.getInstance().addBooksAuthorsLink(book.getId(), author.getId());
                }
            }

            if (friendBookCaterogies != null) {
                for (Category category : friendBookCaterogies) {
                    Category old_category = CategoryLibrary.getInstance().getCategoryByName(category.getName());
                    //if the author don't exist we create it, else we just replace the new by the old for the good ID
                    if (old_category == null) {
                        category = CategoryLibrary.getInstance().Add(category);
                    } else {
                        category = old_category;
                    }
                    //then we create a link between our new book and the author.
                    BookLibrary.getInstance().addBooksCategoriesLink(book.getId(), category.getId());
                }
            }
        }
    }

    private void friendFilterTreatment() {
        if (friendFilterList == null) {
            return;
        }
        for (BookFilter friendFilter : friendFilterList) {

            //we don't want a filter of a friend of our friend
            if (friendFilter.getFriend_id() != -1) {
                continue;
            }

            //Create publisher if don't exist and update id of the publisher in the filter
            long publisher_id = friendFilter.getPublisher_id();
            if (publisher_id != -1) {
                Publisher friendFilterPublisher = publisherLibraryFriend.getPublisherById(publisher_id);
                Publisher newPublisher = PublisherLibrary.getInstance().findAndAddAPublisher(friendFilterPublisher.getName());
                friendFilter.setPublisher_id(newPublisher.getId());
            }


            List<Author> friendFilterAuthors = friendFilter.getAuthors();
            List<Category> friendFilterCaterogies = friendFilter.getCategories();

            //add the friend_id to the filter to know that it's a friend filter
            friendFilter.setFriend_id(friend.getId());
            //then add the filter to our library
            BookFilter filter = BookFilterCatalog.getInstance().Add(friendFilter);

            if (friendFilterAuthors != null) {
                for (Author author : friendFilterAuthors) {
                    Author old_author = AuthorLibrary.getInstance().getAuthorByName(author.getName());
                    //if the author don't exist we create it, else we just replace the new by the old for the good ID
                    if (old_author == null) {
                        author = AuthorLibrary.getInstance().Add(author);
                    } else {
                        author = old_author;
                    }
                    //then we create a link between our new filter and the author.
                    BookFilterCatalog.getInstance().addBookFiltersAuthorsLink(filter.getId(), author.getId());
                }
            }

            if (friendFilterCaterogies != null) {
                for (Category category : friendFilterCaterogies) {
                    Category old_category = CategoryLibrary.getInstance().getCategoryByName(category.getName());
                    //if the author don't exist we create it, else we just replace the new by the old for the good ID
                    if (old_category == null) {
                        category = CategoryLibrary.getInstance().Add(category);
                    } else {
                        category = old_category;
                    }
                    //then we create a link between our new filter and the author.
                    BookFilterCatalog.getInstance().addBookFiltersCategoriesLink(filter.getId(), category.getId());
                }
            }
        }
    }
}
