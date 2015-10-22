package flq.projectbooks;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


public class DisplayBooks extends ActionBarActivity  implements  BookList.OnBookSelected
{

    public final static String GIVE_BOOK = "flq.GIVE_BOOK";
    private BookList fragmentList ;
    private BookInfo fragmentInfoBook ;


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

    static final String STATE_FRAGMENT_LIST = "bookList";
    static final String STATE_FRAGMENT_BOOK = "bookInfo";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_FRAGMENT_LIST, fragmentList);
        savedInstanceState.putSerializable(STATE_FRAGMENT_BOOK, fragmentInfoBook);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_books);

        if (findViewById(R.id.listContainer) != null) {
            if (savedInstanceState != null) {

                fragmentList = (BookList) savedInstanceState.getSerializable(STATE_FRAGMENT_LIST);
                fragmentInfoBook = (BookInfo) savedInstanceState.getSerializable(STATE_FRAGMENT_BOOK);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                getSupportFragmentManager().popBackStack();
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    transaction.replace(R.id.listContainer, fragmentList);
                }else{
                    transaction.replace(R.id.listContainer, fragmentList);
                    if(fragmentInfoBook != null) {
                        transaction.replace(R.id.bookInfoContainer, fragmentInfoBook);
                    }
                }
                transaction.commit();

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
                    .replace(R.id.listContainer, fragmentList).commit();
        }
    }
}
