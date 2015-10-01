package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DisplayFilters extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    public final static String GIVE_FILTER_BACK = "flq.UPDATED_LIST_OF_FILTER";
    public final static String GIVE_FILTER_FOR_DISPLAY = "flq.GIVE_FILTER_FOR_DISPLAY";

    private int selectedFilterIndex;
    private ListView filterList;
    private List<Map<String, String>> listOfFilters;
    private SimpleAdapter listAdapter;
    private BookFilterCatalog bookFilterCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_filters);
        Intent intent = getIntent();

        filterList = (ListView) findViewById(R.id.filterList);

        filterList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterIndex = position;
                PopupMenu popupMenu = new PopupMenu(DisplayFilters.this, view);
                popupMenu.setOnMenuItemClickListener(DisplayFilters.this);
                popupMenu.inflate(R.menu.filterclickpopup);
                popupMenu.show();

                return true;
            }
        });
        bookFilterCatalog = (BookFilterCatalog) intent.getSerializableExtra(Main.GIVE_LIST_OF_FILTERS);
        createListView(bookFilterCatalog);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(GIVE_FILTER_BACK, bookFilterCatalog);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private void createListView(BookFilterCatalog filters){
        listOfFilters = new ArrayList<>();
        ListView filterList = (ListView) findViewById(R.id.filterList);

        for (BookFilter filter : filters.getFilters()) {
            Map<String, String> filterMap = new HashMap<>() ;
            filterMap.put("author", filter.getAuthor());
            filterMap.put("title", filter.getTitle());
            filterMap.put("isbn", filter.getIsbn());
            listOfFilters.add(filterMap);
        }

        listAdapter = new SimpleAdapter(this.getBaseContext(), listOfFilters, R.layout.filter_detail,
                new String[] {"img", "author", "title", "isbn"},
                new int[] {R.id.img, R.id.author, R.id.title, R.id.isbn});


        filterList.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_filters, menu);
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

    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.display_filter:

                Intent intent = new Intent(this, CreateFilter.class);
                intent.putExtra(GIVE_FILTER_FOR_DISPLAY, bookFilterCatalog);
                setResult(selectedFilterIndex, intent);
                finish();

                return true;
            case R.id.delete_filter:
                bookFilterCatalog.getFilters().remove(selectedFilterIndex);
                listOfFilters.remove(selectedFilterIndex);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Filtre effacé" , Toast.LENGTH_SHORT).show();

                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.hasExtra(CreateFilter.GIVE_FILTER_BACK)){
            BookFilter filter = (BookFilter) data.getSerializableExtra(CreateFilter.GIVE_FILTER_BACK);
            bookFilterCatalog.getFilters().set(selectedFilterIndex, filter);

            Map<String, String> filterMap = new HashMap<>() ;
            filterMap.put("author", filter.getAuthor());
            filterMap.put("title", filter.getTitle());
            filterMap.put("isbn", filter.getIsbn());
            listOfFilters.set(selectedFilterIndex, filterMap);

            listAdapter.notifyDataSetChanged();
        }
    }


}
