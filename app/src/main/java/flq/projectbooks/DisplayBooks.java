package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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


public class DisplayBooks extends ActionBarActivity  implements  BookList.OnBookSelected
{

    public final static String GIVE_BOOK = "flq.GIVE_BOOK";
    private BookList fragmentList ;
    private BookInfo fragmentInfoBook ;


    @Override
    public void OnBookSelected(Book book) {
        if (findViewById(R.id.bookInfoContainer) != null) {
            fragmentInfoBook = new BookInfo();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BookInfo.ARG_PARAM1, book);
            fragmentInfoBook.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.bookInfoContainer, fragmentInfoBook).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_books);

        if (findViewById(R.id.listContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentList = new BookList();

            Intent intent = getIntent();
            if(intent.hasExtra(DisplayFilters.GIVE_FILTER)){
                BookFilter bookFilter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_FILTER);
                Bundle bundle = new Bundle();
                bundle.putSerializable(BookList.ARG_PARAM1, bookFilter);
                fragmentList.setArguments(bundle);
            }



            getSupportFragmentManager().beginTransaction()
                    .add(R.id.listContainer, fragmentList).commit();
        }
    }
}
