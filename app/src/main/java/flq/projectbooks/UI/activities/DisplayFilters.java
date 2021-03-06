package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.utilities.FilterAdapter;


public class DisplayFilters extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public final static String GIVE_EMPTY_FILTER = "flq.EMPTY_FILTER";
    public final static String GIVE_FILTER = "flq.GIVEFILTER";

    private int selectedFilterIndex;
    private ListView filterList;
    private FilterAdapter listAdapter;
    private BookFilterCatalog bookFilterCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_filters);
        //used to set our actionBar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        filterList = (ListView) findViewById(R.id.filterList);
        filterList.setItemsCanFocus(false);

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

    private void createListView(BookFilterCatalog filters) {
        ListView filterList = (ListView) findViewById(R.id.filterList);

        List<Integer> numberOfBookPerFilter = new ArrayList<>();
        List<Integer> numberOfFriendBookPerFilter = new ArrayList<>();
        List<List<ImageView>> mListImagesViews = new ArrayList<>();
        for (BookFilter filter : filters.getBookFilterList()) {
            numberOfBookPerFilter.add(0);
            numberOfFriendBookPerFilter.add(0);
            mListImagesViews.add(new ArrayList<ImageView>());
        }
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);


        for (Book book : BookLibrary.getInstance().getBookList()) {
            int i = 0;
            for (BookFilter filter : filters.getBookFilterList()) {
                if (filter.isSelected(book)) {
                    if (book.getFriend_id() == -1) {
                        numberOfBookPerFilter.set(i, numberOfBookPerFilter.get(i) + 1);
                    } else {
                        numberOfFriendBookPerFilter.set(i, numberOfFriendBookPerFilter.get(i) + 1);
                    }


                    ImageView imgView = new ImageView(getApplicationContext());
                    imgView.setLayoutParams(lp);

                    byte[] b = book.getImage();
                    if (b != null) {
                        imgView.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(b, 0, b.length)));
                    } else {
                        imgView.setImageResource(R.drawable.picturebook);
                    }
                    mListImagesViews.get(i).add(imgView);
                }
                i++;
            }
        }

        int i = 0;
        List<String> names = new ArrayList<>();
        List<String> filterNbBooks = new ArrayList<>();
        List<String> filterNbFriendBooks = new ArrayList<>();
        List<Boolean> belongToFriends = new ArrayList<>();
        for (BookFilter filter : filters.getBookFilterList()) {
            String nbBookString;
            String nbFriendBookString;
            int size = numberOfBookPerFilter.get(i);
            if (size > 1) {
                nbBookString = size + " livres +";
            } else {
                nbBookString = size + " livre +";
            }
            if (filter.getFriend_id() != -1) {
                nbBookString = "";
            }
            size = numberOfFriendBookPerFilter.get(i);
            if (size > 1) {
                nbFriendBookString = size + " livres d'ami";
            } else {
                nbFriendBookString = size + " livre d'ami";
            }
            filterNbBooks.add(nbBookString);
            filterNbFriendBooks.add(nbFriendBookString);
            names.add(filter.getName());

            belongToFriends.add(filter.getFriend_id() != -1);
            i++;
        }

        listAdapter = new FilterAdapter(mListImagesViews, names, filterNbBooks, filterNbFriendBooks, belongToFriends, this.getBaseContext());

        filterList.setAdapter(listAdapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        bookFilterCatalog = BookFilterCatalog.getInstance();

        createListView(bookFilterCatalog);
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

        if(id == R.id.create_filter_option) {
            Intent intent = new Intent(this, CreateFilter.class);
            intent.putExtra(GIVE_EMPTY_FILTER, bookFilterCatalog.getNewFilters());
            startActivityForResult(intent, 0);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_book_activity_item) {
            Main.openCreateBookActivity(this, null);
            return true;
        }

        if (id == R.id.display_books_activity_item) {
            Main.openDisplayBookActivity(this, null);
            return true;
        }

        if (id == R.id.display_filters_activity_item) {
            Main.openDisplayFilterActivity(this, null);
            return true;
        }
        if (id == R.id.scan_book_activity_item) {
            Main.openScannerActivity(this, null);
            return true;
        }
        if (id == R.id.import_export_activity_item) {
            Main.openImportExportActivity(this, null);
            return true;
        }
        if (id == R.id.display_friends_activity_item) {
            Main.openDisplayFriendActivity(this, null);
            return true;
        }
        if (id == R.id.preference_activity_item) {
            Main.openSettingsActivity(this, null);
            return true;
        }
        if (id == R.id.informations_activity_item) {
            Main.openInformationActivity(this, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_filter:
                BookFilter filter = bookFilterCatalog.getBookFilterList().get(selectedFilterIndex);
                if (filter.getFriend_id() == -1 && !BookFilterCatalog.isADefaultFilter(filter)) {
                    Intent intent = new Intent(this, CreateFilter.class);
                    intent.putExtra(DisplayFilters.GIVE_FILTER, filter);
                    startActivityForResult(intent, 0);
                } else if(filter.getFriend_id() != -1) {
                    Toast.makeText(this, "Ce filtre ne peut pas être modifié car il appartient à un ami.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Ce filtre ne peut pas être modifié car c'est un filtre par défaut", Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.delete_filter:
                BookFilterCatalog.getInstance().deleteFilterById((int) bookFilterCatalog.getBookFilterList().get(selectedFilterIndex).getId());
                //listOfFilters.remove(selectedFilterIndex);
                listAdapter.deleteElement(selectedFilterIndex);
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
        Main.onActivityResultStatic(this, requestCode, resultCode, data);
    }

}
