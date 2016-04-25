package flq.projectbooks.UI.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import flq.projectbooks.R;
import flq.projectbooks.UI.fragments.BookInfo;
import flq.projectbooks.UI.fragments.BookList;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.libraries.BookLibrary;


public class DisplayBooks extends AppCompatActivity implements BookList.OnBookSelected {

    public final static String GIVE_BOOK = "flq.GIVE_BOOK";
    static final String STATE_FRAGMENT_LIST = "bookList";
    static final String STATE_FRAGMENT_BOOK = "bookInfo";
    private BookList fragmentList;
    private BookInfo fragmentInfoBook;

    @Override
    public void OnBookSelected(Book book) {
        FrameLayout layoutBookInfoContainer = (FrameLayout) findViewById(R.id.bookInfoContainer);
        if(layoutBookInfoContainer != null){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            layoutBookInfoContainer.setLayoutParams(param);
        }
        fragmentInfoBook = new BookInfo();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookInfo.ARG_PARAM1, book);
        fragmentInfoBook.setArguments(bundle);
        if (findViewById(R.id.bookInfoContainer) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.bookInfoContainer, fragmentInfoBook).commit();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.listContainer, fragmentInfoBook);
            transaction.commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(STATE_FRAGMENT_LIST, fragmentList);
        savedInstanceState.putParcelable(STATE_FRAGMENT_BOOK, fragmentInfoBook);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //extra wish can be set by the loan notification when we click on it
        String menuFragment = getIntent().getStringExtra("menuFragment");


        setContentView(R.layout.activity_display_books);
        //used to set our actionBar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if(myToolbar != null)
            setSupportActionBar(myToolbar);




        if (findViewById(R.id.listContainer) != null) {
            if (savedInstanceState != null) {

                fragmentList = (BookList) savedInstanceState.getParcelable(STATE_FRAGMENT_LIST);
                fragmentInfoBook = (BookInfo) savedInstanceState.getParcelable(STATE_FRAGMENT_BOOK);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getSupportFragmentManager().popBackStack();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    transaction.replace(R.id.listContainer, fragmentList);
                } else {
                    invalidateOptionsMenu();
                    transaction.replace(R.id.listContainer, fragmentList);
                    if (fragmentInfoBook != null) {
                        transaction.replace(R.id.bookInfoContainer, fragmentInfoBook);
                    } else {
                        FrameLayout layoutBookInfoContainer = (FrameLayout) findViewById(R.id.bookInfoContainer);
                        if(layoutBookInfoContainer != null){
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                    0,
                                    ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);
                            layoutBookInfoContainer.setLayoutParams(param);
                        }
                    }
                }
                transaction.commit();
                return;
            } else {
                FrameLayout layoutBookInfoContainer = (FrameLayout) findViewById(R.id.bookInfoContainer);
                if(layoutBookInfoContainer != null){
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);
                    layoutBookInfoContainer.setLayoutParams(param);
                }
            }
            fragmentList = new BookList();

            Intent intent = getIntent();
            if (intent.hasExtra(DisplayFilters.GIVE_FILTER)) {
                BookFilter bookFilter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_FILTER);
                Bundle bundle = new Bundle();
                bundle.putSerializable(BookList.ARG_PARAM1, bookFilter);
                fragmentList.setArguments(bundle);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.listContainer, fragmentList).commit();
            if (menuFragment != null) {
                long book_id = getIntent().getLongExtra("bookId", -1);
                OnBookSelected(BookLibrary.getInstance().getBookById(book_id));
            }
        }
    }

    public void modifBook(View view) {
        fragmentInfoBook.modifBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_book_activity_item) {
            Main.openCreateBookActivity(this, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragmentList.onActivityResult(requestCode, resultCode, data);
        Main.onActivityResultStatic(this, requestCode, resultCode, data);

    }

}
