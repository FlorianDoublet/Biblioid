package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import flq.projectbooks.R;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Publisher;
import flq.projectbooks.data.libraries.PublisherLibrary;
import flq.projectbooks.database.LinkTablesDataSource;


public class CreateFilter extends AppCompatActivity {
    BookFilter filter = new BookFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_filter);
        //used to set our actionBar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
        Publisher p = PublisherLibrary.getInstance().getPublisherById(filter.getPublisher_id());
        String publisher_name = "";
        if (p != null) publisher_name = p.getName();
        ((TextView) findViewById(R.id.filterPublisher)).setText(publisher_name);
        ((TextView) findViewById(R.id.filterCategorie)).setText(LinkTablesDataSource.categoriesToString(filter.getCategories()));
        ((TextView) findViewById(R.id.filterNbPagesMin)).setText(String.valueOf(filter.getNbPagesMin()));
        ((TextView) findViewById(R.id.filterNbPagesMax)).setText(String.valueOf(filter.getNbPagesMax()));

        switch (filter.getAdvancementState()) {
            case "Read":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterRead);
                break;
            case "Reading":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterReading);
                break;
            case "Not Read":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterNotRead);
                break;
            case "Undetermined":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterUndetermined);
                break;
        }

        if (filter.getOnFavoriteList() == 1) {
            ((CheckBox) findViewById(R.id.checkBoxFilterFavorites)).setChecked(true);
        }

        if (filter.getOnWishList() == 1) {
            ((CheckBox) findViewById(R.id.checkBoxFilterWishList)).setChecked(true);
        }

        Spinner spinnerFilterBookState = ((Spinner) findViewById(R.id.spinnerFilterBookState));
        Spinner spinnerFilterBookPossession = ((Spinner) findViewById(R.id.spinnerFilterPossession));


        ArrayAdapter<String> spinnerArrayAdapterState = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, BookFilter.spinnerArrayState); //selected item will look like a spinner set from XML
        spinnerArrayAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterBookState.setAdapter(spinnerArrayAdapterState);


        ArrayAdapter<String> spinnerArrayAdapterPossession = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, BookFilter.spinnerArrayPossession); //selected item will look like a spinner set from XML
        spinnerArrayAdapterPossession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterBookPossession.setAdapter(spinnerArrayAdapterPossession);

        spinnerFilterBookState.setSelection(filter.getBookState());
        spinnerFilterBookPossession.setSelection(filter.getPossessionState());

        ((EditText) findViewById(R.id.editTextFilterComment)).setText(filter.getComment());

        ArrayList spinnerArrayRating = new ArrayList<>();
        spinnerArrayRating.add("Indifférent");
        spinnerArrayRating.add("1");
        spinnerArrayRating.add("2");
        spinnerArrayRating.add("3");
        spinnerArrayRating.add("4");
        spinnerArrayRating.add("5");


        Spinner spinnerFilterRatingMin = ((Spinner) findViewById(R.id.spinnerFilterRatingMin));
        ArrayAdapter<String> spinnerArrayAdapterRatingMin = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArrayRating); //selected item will look like a spinner set from XML
        spinnerArrayAdapterRatingMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterRatingMin.setAdapter(spinnerArrayAdapterRatingMin);

        Spinner spinnerFilterRatingMax = ((Spinner) findViewById(R.id.spinnerFilterRatingMax));
        ArrayAdapter<String> spinnerArrayAdapterRatingMax = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArrayRating); //selected item will look like a spinner set from XML
        spinnerArrayAdapterRatingMax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterRatingMax.setAdapter(spinnerArrayAdapterRatingMin);

        spinnerFilterRatingMin.setSelection(filter.getRatingMin());
        spinnerFilterRatingMax.setSelection(filter.getRatingMax());
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
        EditText publisher = (EditText) findViewById(R.id.filterPublisher);
        EditText category = (EditText) findViewById(R.id.filterCategorie);
        EditText nbPages1 = (EditText) findViewById(R.id.filterNbPagesMin);
        EditText nbPages2 = (EditText) findViewById(R.id.filterNbPagesMax);

        Spinner ratingMin = (Spinner) findViewById(R.id.spinnerFilterRatingMin);
        Spinner ratingMax = (Spinner) findViewById(R.id.spinnerFilterRatingMax);
        CheckBox cBFavorites = (CheckBox) findViewById(R.id.checkBoxFilterFavorites);
        CheckBox cBWishList = (CheckBox) findViewById(R.id.checkBoxFilterWishList);
        Spinner spinnerBookState = (Spinner) findViewById(R.id.spinnerFilterBookState);
        Spinner spinnerPossession = (Spinner) findViewById(R.id.spinnerFilterPossession);
        EditText comment = (EditText) findViewById(R.id.editTextFilterComment);
        RadioButton rbRead = (RadioButton) findViewById(R.id.radioButtonFilterRead);
        RadioButton rbReading = (RadioButton) findViewById(R.id.radioButtonFilterReading);
        RadioButton rbNotRead = (RadioButton) findViewById(R.id.radioButtonFilterNotRead);
        RadioButton rbUndetermined = (RadioButton) findViewById(R.id.radioButtonFilterUndetermined);

        filter.setName(name.getText().toString());
        filter.setTitle(title.getText().toString());
        filter.setDescription(isbn.getText().toString());
        filter.setDatePublications(datePub1.getText().toString(), datePub2.getText().toString());
        filter.setPublisher_id(PublisherLibrary.getInstance().findAndAddAPublisher(publisher.getText().toString()).getId());
        filter.setNbPages(Integer.parseInt(nbPages1.getText().toString()), Integer.parseInt(nbPages2.getText().toString()));


        if (rbRead.isChecked()) {
            filter.setAdvancementState("Read");
        } else {
            if (rbReading.isChecked()) {
                filter.setAdvancementState("Reading");
            } else {
                if (rbNotRead.isChecked()) {
                    filter.setAdvancementState("Not Read");
                } else {
                    if (rbUndetermined.isChecked()) {
                        filter.setAdvancementState("Undetermined");
                    }
                }
            }
        }

        filter.setRatingMin((int) ratingMin.getSelectedItemId());
        filter.setRatingMax((int) ratingMax.getSelectedItemId());
        filter.setOnFavoriteList(cBFavorites.isChecked() ? 1 : 0);
        filter.setOnWishList(cBWishList.isChecked() ? 1 : 0);
        filter.setBookState(spinnerBookState.getSelectedItemPosition());
        filter.setPossessionState(spinnerPossession.getSelectedItemPosition());
        filter.setComment(comment.getText().toString());

        switch (filter.getAdvancementState()) {
            case "Read":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterRead);
                break;
            case "Reading":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterReading);
                break;
            case "Not Read":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterNotRead);
                break;
            case "Undetermined":
                ((RadioGroup) findViewById(R.id.radioGroupFilter)).check(R.id.radioButtonFilterUndetermined);
                break;
        }

        ((Spinner) findViewById(R.id.spinnerFilterRatingMin)).setSelection(filter.getRatingMin());
        ((Spinner) findViewById(R.id.spinnerFilterRatingMax)).setSelection(filter.getRatingMax());

        if (filter.getOnFavoriteList() == 1) {
            ((CheckBox) findViewById(R.id.checkBoxFilterFavorites)).setChecked(true);
        }

        if (filter.getOnWishList() == 1) {
            ((CheckBox) findViewById(R.id.checkBoxFilterWishList)).setChecked(true);
        }

        ((Spinner) findViewById(R.id.spinnerFilterBookState)).setSelection(filter.getBookState());
        ((Spinner) findViewById(R.id.spinnerFilterPossession)).setSelection(filter.getPossessionState());

        ((EditText) findViewById(R.id.editTextFilterComment)).setText(filter.getComment());

        //will feed the filter with the good authors
        LinkTablesDataSource.feedBookFilterWithAuthors(filter, author);
        LinkTablesDataSource.feedBookFilterWithCategories(filter, category);

        finish();
    }

}
