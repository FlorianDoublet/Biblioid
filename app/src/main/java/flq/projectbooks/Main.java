package flq.projectbooks;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity {

    public final static String GIVE_LIST_OF_BOOKS = "flq.LISTOFBOOKS";
    public final static String GIVE_FILTERED_LIST_OF_BOOKS = "flq.FILTEREDLISTOFBOOKS";
    public final static String GIVE_BOOK = "flq.GIVE_BOOK";

    public final static String GIVE_LIST_OF_FILTERS = "flq.LISTOFFILTERS";
    public final static String GIVE_FILTER= "flq.GIVE_FILTER";

    protected BookLibrary books;
    protected BookFilterCatalog filters;
    private List<Book> filteredBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        books = new BookLibrary();

        books.Add(new Book("Harry Pot De Fleur à l'école des poulets", "J.K. Brownie", "1451", null));
        books.Add(new Book("Harry Pot De Fleur et le poulailler secret", "J.K. Brownie", "1452", null));
        books.Add(new Book("Le Seigneur Des Panneaux", "J.R.R. Trollkien", "45187", null));
        books.Add(new Book("Fhamlette", "William Cestpire", "0218", null));
        books.Add(new Book("Les Sirops d'érable", "Victor Jus Go", "45187", null));

        filters = new BookFilterCatalog();

        filters.Add(new BookFilter("", "J.K. Brownie", ""));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openCreateBookActivity(View view) {
        Intent intent = new Intent(this, CreateBook.class);
        intent.putExtra(GIVE_BOOK, books.getNewBook());
        startActivityForResult(intent, 0);
    }

    public void openDisplayBookActivity(View view) {
        Intent intent = new Intent(this, DisplayBooks.class);
        intent.putExtra(GIVE_LIST_OF_BOOKS, books);
        startActivityForResult(intent, 0);
    }

    public void openDisplayFilterActivity(View view) {
        Intent intent = new Intent(this, DisplayFilters.class);
        intent.putExtra(GIVE_LIST_OF_FILTERS, filters);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.hasExtra(CreateBook.GIVE_BOOK_BACK)){
            Book book = (Book) data.getSerializableExtra(CreateBook.GIVE_BOOK_BACK);
            books.Add(book);
        }

        if(data != null && data.hasExtra(DisplayBooks.GIVE_BOOKS_BACK)){
            BookLibrary retrievedBooks = (BookLibrary) data.getSerializableExtra(DisplayBooks.GIVE_BOOKS_BACK);
            books = retrievedBooks ;
        }


        if(data != null && data.hasExtra(DisplayBooks.GIVE_FILTERED_BOOKS_BACK)) {
            BookLibrary retrievedFilteredBooks = (BookLibrary) data.getSerializableExtra(DisplayBooks.GIVE_FILTERED_BOOKS_BACK);

            int j = 0;
            for(int i = 0; i < filteredBooks.size(); i++){
                if(retrievedFilteredBooks.getBooks().size() > 0 && filteredBooks.get(i).id == retrievedFilteredBooks.getBooks().get(i - j).id){
                    books.UpdateBook(retrievedFilteredBooks.getBooks().get(i - j));
                }else{
                    books.DeleteBook(filteredBooks.get(i));
                    j++;
                }
            }
        }

        if(data != null && data.hasExtra(DisplayFilters.GIVE_FILTER_BACK)){
            BookFilterCatalog retrievedFilters = (BookFilterCatalog) data.getSerializableExtra(DisplayFilters.GIVE_FILTER_BACK);
            filters = retrievedFilters ;
        }

        if(data != null && data.hasExtra(DisplayFilters.GIVE_FILTER_FOR_DISPLAY)){
            BookFilterCatalog retrievedFilters = (BookFilterCatalog) data.getSerializableExtra(DisplayFilters.GIVE_FILTER_FOR_DISPLAY);
            filters = retrievedFilters ;

            BookFilter bookFilter = filters.getFilters().get(resultCode);

            BookLibrary filteredBooksLibrary = new BookLibrary();
            filteredBooks = new ArrayList<Book>();
            for (int i = 0; i < books.getBooks().size(); i++) {
                if(bookFilter.IsSelected(books.getBooks().get(i))){
                    filteredBooksLibrary.Add(books.getBooks().get(i));
                    filteredBooks.add(books.getBooks().get(i));
                }
            }
            Intent intent = new Intent(this, DisplayBooks.class);
            intent.putExtra(GIVE_FILTERED_LIST_OF_BOOKS, filteredBooksLibrary);
            startActivityForResult(intent, 0);
        }




    }
}
