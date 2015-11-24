package flq.projectbooks.activities;

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

import flq.projectbooks.BookFilter;
import flq.projectbooks.R;
import flq.projectbooks.libraries.BookFilterCatalog;


public class DisplayFilters extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    public final static String GIVE_EMPTY_FILTER = "flq.EMPTY_FILTER";
    public final static String GIVE_FILTER = "flq.GIVEFILTER";

    private int selectedFilterIndex;
    private ListView filterList;
    private List<Map<String, String>> listOfFilters;
    private SimpleAdapter listAdapter;
    private BookFilterCatalog bookFilterCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_filters);

        filterList = (ListView) findViewById(R.id.filterList);

        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterIndex = position;
                BookFilter bookFilter = bookFilterCatalog.getBookFilterList().get(selectedFilterIndex);

                Intent intent = new Intent(DisplayFilters.this, DisplayBooks.class);
                intent.putExtra(GIVE_FILTER, bookFilter);
                startActivity(intent);
            }
        });

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

        bookFilterCatalog = BookFilterCatalog.getInstance();

        createListView(bookFilterCatalog);
    }

    private void createListView(BookFilterCatalog filters){
        listOfFilters = new ArrayList<>();
        ListView filterList = (ListView) findViewById(R.id.filterList);

        for (BookFilter filter : filters.getBookFilterList()) {
            Map<String, String> filterMap = new HashMap<>() ;
            filterMap.put("name", filter.getName());

            String nbLivreString ;
            int size = filter.getFilteredLibrary().getBookList().size();
            if(size > 1) {
                nbLivreString = size + " livres";
            }else{
                nbLivreString = size + " livre";
            }
            filterMap.put("nbLivre", nbLivreString );
            listOfFilters.add(filterMap);
        }

        listAdapter = new SimpleAdapter(this.getBaseContext(), listOfFilters, R.layout.filter_detail,
                new String[] {"name", "nbLivre"},
                new int[] {R.id.name, R.id.nbLivre});


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
        switch(item.getItemId()){
            case R.id.create_filter_option:
                Intent intent = new Intent(this, CreateFilter.class);
                intent.putExtra(GIVE_EMPTY_FILTER, bookFilterCatalog.getNewFilters());
                startActivityForResult(intent, 0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update_filter:
                BookFilter filter = bookFilterCatalog.getBookFilterList().get(selectedFilterIndex) ;
                Intent intent = new Intent(this, CreateFilter.class);
                intent.putExtra(DisplayFilters.GIVE_FILTER, filter);
                startActivityForResult(intent, 0);

                return true;
            case R.id.delete_filter:
                BookFilterCatalog.getInstance().deleteFilterById((int) bookFilterCatalog.getBookFilterList().get(selectedFilterIndex).getId());
                listOfFilters.remove(selectedFilterIndex);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Filtre effacé", Toast.LENGTH_SHORT).show();

                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        createListView(bookFilterCatalog);
        listAdapter.notifyDataSetChanged();
    }

}
