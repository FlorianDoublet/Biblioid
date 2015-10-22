package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CreateBook extends ActionBarActivity {

    public final static String GIVE_BOOK_BACK = "flq.NEWBOOK";
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        Intent intent = getIntent();
        if(intent.hasExtra(DisplayBooks.GIVE_BOOK)) {
            setTitle("Modification d'un livre");
            book = (Book) intent.getSerializableExtra(DisplayBooks.GIVE_BOOK);
        } else {
            setTitle("Création d'un livre");
            book = (Book) intent.getSerializableExtra(Main.ASK_NEW_BOOK);
        }

        ((TextView)findViewById(R.id.bookTitle)).setText(book.getTitle());
        ((TextView)findViewById(R.id.bookISBN)).setText(book.getIsbn());
        ((TextView)findViewById(R.id.bookAuthor)).setText(book.getAuthor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_book, menu);
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

    public void bookCreation(View view){
        EditText title = (EditText) findViewById(R.id.bookTitle);
        EditText author = (EditText) findViewById(R.id.bookAuthor);
        EditText isbn = (EditText) findViewById(R.id.bookISBN);

        book.setTitle(title.getText().toString());
        book.setAuthor(author.getText().toString());
        book.setIsbn(isbn.getText().toString());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(GIVE_BOOK_BACK, book);
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }
}
