package flq.projectbooks.UI.activities;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
                    }
                }
                transaction.commit();
                return;
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

}
