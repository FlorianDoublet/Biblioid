package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class CreateFilter extends ActionBarActivity {

    public final static String GIVE_FILTER_BACK = "flq.NEWFILTER";
    public BookFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_filter);
        Intent intent = getIntent();
        filter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_EMPTY_FILTER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_filter, menu);
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

    public void filterCreation(View view){
        EditText title = (EditText) findViewById(R.id.filterTitle);
        EditText author = (EditText) findViewById(R.id.filterAuthor);
        EditText isbn = (EditText) findViewById(R.id.filterISBN);

        filter.setTitle(title.getText().toString());
        filter.setAuthor(author.getText().toString());
        filter.setIsbn(isbn.getText().toString());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(GIVE_FILTER_BACK, filter);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}
