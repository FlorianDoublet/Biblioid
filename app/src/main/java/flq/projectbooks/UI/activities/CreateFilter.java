package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import flq.projectbooks.R;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.database.LinkTablesDataSource;


public class CreateFilter extends ActionBarActivity {


    BookFilter filter = new BookFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_filter);

        Intent intent = getIntent();
        if (intent.hasExtra(DisplayFilters.GIVE_FILTER)) {
            setTitle("Modification d'un filtre");
            filter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_FILTER);
        } else {
            setTitle("Création d'un filtre");
            filter = new BookFilter();
        }

        ((TextView) findViewById(R.id.filterName)).setText(filter.getName());
        ((TextView) findViewById(R.id.filterTitle)).setText(filter.getTitle());
        ((TextView) findViewById(R.id.filterAuthor)).setText(LinkTablesDataSource.authorsToString(filter.getAuthors()));
        ((TextView) findViewById(R.id.filterDescription)).setText(filter.getDescription());
        ((TextView) findViewById(R.id.filterDatePublicationMin)).setText(filter.getDatePublicationMin());
        ((TextView) findViewById(R.id.filterDatePublicationMax)).setText(filter.getDatePublicationMax());
        ((TextView) findViewById(R.id.filterEditeur)).setText(filter.getEditor());
        ((TextView) findViewById(R.id.filterCategorie)).setText(LinkTablesDataSource.categoriesToString(filter.getCategories()));
        ((TextView) findViewById(R.id.filterNbPagesMin)).setText(String.valueOf(filter.getNbPagesMin()));
        ((TextView) findViewById(R.id.filterNbPagesMax)).setText(String.valueOf(filter.getNbPagesMax()));
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

    public void filterCreation(View view) {


        EditText name = (EditText) findViewById(R.id.filterName);
        EditText title = (EditText) findViewById(R.id.filterTitle);
        EditText author = (EditText) findViewById(R.id.filterAuthor);
        EditText isbn = (EditText) findViewById(R.id.filterDescription);
        EditText datePub1 = (EditText) findViewById(R.id.filterDatePublicationMin);
        EditText datePub2 = (EditText) findViewById(R.id.filterDatePublicationMax);
        EditText editor = (EditText) findViewById(R.id.filterEditeur);
        EditText category = (EditText) findViewById(R.id.filterCategorie);
        EditText nbPages1 = (EditText) findViewById(R.id.filterNbPagesMin);
        EditText nbPages2 = (EditText) findViewById(R.id.filterNbPagesMax);


        filter.setName(name.getText().toString());
        filter.setTitle(title.getText().toString());
        filter.setDescription(isbn.getText().toString());
        filter.setDatePublications(datePub1.getText().toString(), datePub2.getText().toString());
        filter.setEditor(editor.getText().toString());
        filter.setNbPages(Integer.parseInt(nbPages1.getText().toString()), Integer.parseInt(nbPages2.getText().toString()));

        //will feed the filter with the good authors
        LinkTablesDataSource.feedBookFilterWithAuthors(filter, author);
        LinkTablesDataSource.feedBookFilterWithCategories(filter, category);

        finish();
    }

}
