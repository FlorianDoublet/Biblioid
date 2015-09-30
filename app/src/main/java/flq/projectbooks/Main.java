package flq.projectbooks;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Main extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "flq.LISTOFBOOKS";
    protected BookLibrary books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        books = new BookLibrary();

        books.Add(new Book("Harry Pot De Fleur", "J.K. Brownie", "1451", null));
        books.Add(new Book("Le Seigneur Des Panneaux", "J.R.R. Trollkien", "45187", null));
        books.Add(new Book("Fhamlette", "William Cestpire", "0218", null));
        books.Add(new Book("Les Sirops d'érable", "Victor Jus Go", "45187", null));

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
        Intent intent = new Intent(this, createBook.class);

        startActivityForResult(intent, 0);
    }

    public void openDisplayBookActivity(View view) {
        Intent intent = new Intent(this, displayBooks.class);
        intent.putExtra(EXTRA_MESSAGE, books);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.hasExtra(createBook.EXTRA_MESSAGE)){
            Book book = (Book) data.getSerializableExtra(createBook.EXTRA_MESSAGE);
            books.Add(book);
        }
    }
}
