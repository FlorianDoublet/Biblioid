package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link BookList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookList extends Fragment implements PopupMenu.OnMenuItemClickListener, Serializable  {
    public static final String ARG_PARAM1 = "param1";


    private OnBookSelected mBookListener;

    private int selectedBookIndex;
    private ListView bookList;
    private List<Map<String, String>> listOfBooks ;
    private SimpleAdapter listAdapter;
    private BookLibrary bookLibrary ;
    private BookFilter bookFilter;


    /**
     *     /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bookFilter bookFilter Parameter 1.
     * @return A new instance of fragment BookList.
     */
    public static BookList newInstance(BookFilter bookFilter) {
        BookList fragment = new BookList();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, bookFilter);
        fragment.setArguments(args);
        return fragment;
    }

    public BookList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bookFilter = (BookFilter) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        bookList = (ListView) view.findViewById(R.id.bookList);

        bookList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBookIndex = position;
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.setOnMenuItemClickListener(BookList.this);
                popupMenu.inflate(R.menu.bookclickpopup);
                popupMenu.show();

                return true;
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBookListener.OnBookSelected(bookLibrary.getBookList().get(position));
            }
        });

        bookLibrary = BookLibrary.getInstance();
        createListView(view);

        return view;
    }





    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mBookListener = (OnBookSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBookSelected");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBookListener = null;
    }


    // Container Activity must implement this interface
    public interface OnBookSelected {
        public void OnBookSelected(Book book);
    }


    private void createListView(View view){


        if(bookFilter != null) {

            FilteredBookLibrary filteredBooksLibrary = new FilteredBookLibrary();
            for (int i = 0; i < BookLibrary.getInstance().getBookList().size(); i++) {
                if (bookFilter.isSelected(BookLibrary.getInstance().getBookList().get(i))) {
                    filteredBooksLibrary.Add(BookLibrary.getInstance().getBookList().get(i));
                }
            }
            bookLibrary = filteredBooksLibrary;
        }


        listOfBooks = new ArrayList<>();
        ListView bookList = (ListView) view.findViewById(R.id.bookList);

        for (Book book : bookLibrary.getBookList()) {
            Map<String, String> bookMap = new HashMap<>() ;
            bookMap.put("img", String.valueOf(R.drawable.picturebook));
            bookMap.put("author", book.getAuthor());
            bookMap.put("title", book.getTitle());
            bookMap.put("isbn", book.getIsbn());
            listOfBooks.add(bookMap);
        }

        listAdapter = new SimpleAdapter(getActivity().getBaseContext(), listOfBooks, R.layout.book_detail,
                new String[] {"img", "author", "title", "isbn"},
                new int[] {R.id.img, R.id.author, R.id.title, R.id.isbn});


        bookList.setAdapter(listAdapter);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_display_books, menu);
        return true;
    }*/

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
            case R.id.edit_book:
                Book book = bookLibrary.getBookList().get(selectedBookIndex);
                Intent intent = new Intent(getActivity(), CreateBook.class);
                intent.putExtra(DisplayBooks.GIVE_BOOK, book);
                startActivityForResult(intent, 0);

                return true;
            case R.id.delete_book:
                BookLibrary.getInstance().DeleteBookById((int)bookLibrary.getBookList().get(selectedBookIndex).getId());
                listOfBooks.remove(selectedBookIndex);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Livre effacé", Toast.LENGTH_SHORT).show();

                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        createListView(getView());
    }

}
