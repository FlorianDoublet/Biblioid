package flq.projectbooks;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity  {

    //Ask the CreateBook activity to start with an empty book
    public final static String ASK_NEW_BOOK = "flq.ASK_NEW_BOOK";

    protected BookLibrary books;
    protected BookFilterCatalog filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        books = new BookLibrary(this);


        //books = BookLibrary.getInstance();
        //books.getBookList().clear();
        /*books.Add(new Book("Harry Pot De Fleur à l'école des poulets", "J.K. Brownie", "1451", null));
        books.Add(new Book("Harry Pot De Fleur et le poulailler secret", "J.K. Brownie", "1452", null));
        books.Add(new Book("Le Seigneur Des Panneaux", "J.R.R. Trollkien", "45187", null));
        books.Add(new Book("Fhamlette", "William Cestpire", "0218", null));
        books.Add(new Book("Les Sirops d'érable", "Victor Jus Go", "45187", null));*/

        filters = BookFilterCatalog.getInstance();

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
        intent.putExtra(ASK_NEW_BOOK, books.getNewBook());
        startActivity(intent);
    }

    public void openDisplayBookActivity(View view) {
        Intent intent = new Intent(this, DisplayBooks.class);
        startActivity(intent);
    }

    public void openDisplayFilterActivity(View view) {
        Intent intent = new Intent(this, DisplayFilters.class);
        startActivity(intent);
    }
}
