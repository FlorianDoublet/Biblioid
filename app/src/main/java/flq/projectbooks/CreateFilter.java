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


public class CreateFilter extends ActionBarActivity {


    BookFilter filter = new BookFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_filter);

        Intent intent = getIntent();
        if(intent.hasExtra(DisplayFilters.GIVE_FILTER)) {
            setTitle("Modification d'un filtre");
            filter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_FILTER);
        } else {
            setTitle("Création d'un filtre");
            filter = new BookFilter();
        }

        ((TextView)findViewById(R.id.filterName)).setText(filter.getName());
        ((TextView)findViewById(R.id.filterTitle)).setText(filter.getTitle());
        ((TextView)findViewById(R.id.filterAuthor)).setText(filter.getAuthor());
        ((TextView)findViewById(R.id.filterDescription)).setText(filter.getDescription());
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


        EditText name = (EditText) findViewById(R.id.filterName);
        EditText title = (EditText) findViewById(R.id.filterTitle);
        EditText author = (EditText) findViewById(R.id.filterAuthor);
        EditText isbn = (EditText) findViewById(R.id.filterDescription);

        filter.setName(name.getText().toString());
        filter.setTitle(title.getText().toString());
        filter.setAuthor(author.getText().toString());
        filter.setDescription(isbn.getText().toString());

        BookFilterCatalog.getInstance().UpdateOrAddFilter(filter);

        finish();
    }
}
