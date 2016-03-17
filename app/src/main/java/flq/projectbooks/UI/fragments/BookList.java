package flq.projectbooks.UI.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.CreateBook;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.utilities.BookAdapter;


/**
 * A simple {@link Fragment} subclass.
 * <p/>
 * Use the {@link BookList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookList extends Fragment implements PopupMenu.OnMenuItemClickListener, Parcelable {
    public static final String ARG_PARAM1 = "param1";


    private OnBookSelected mBookListener;

    private int selectedBookIndex;
    private GridView gridViewBooks;
    private List<Map<String, Object>> listOfBooks;
    private BookAdapter listAdapter;
    private BookLibrary bookLibrary;
    private BookFilter bookFilter;

    public BookList() {
        // Required empty public constructor
    }

    /**
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
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

        gridViewBooks = (GridView) view.findViewById(R.id.gridViewBooks);


        gridViewBooks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

        gridViewBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBookListener.OnBookSelected(bookLibrary.getBookList().get(position));
            }
        });

        bookLibrary = BookLibrary.getInstance();
        createGridView(view);

        view.findViewById(R.id.layoutZoomPlusGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nbColumns = gridViewBooks.getNumColumns();
                if (nbColumns > 1) {
                    gridViewBooks.setNumColumns(nbColumns - 1);
                }

            }
        });

        view.findViewById(R.id.layoutZoomMinusGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nbColumns = gridViewBooks.getNumColumns();
                gridViewBooks.setNumColumns(nbColumns + 1);
            }
        });

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

    private void createGridView(View view) {
        if (bookFilter != null) {
            bookLibrary = bookFilter.getFilteredLibrary();
        }

        listAdapter = new BookAdapter(bookLibrary.getBookList(), view.getContext(), gridViewBooks);

        gridViewBooks.setAdapter(listAdapter);
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


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_display_books, menu);
        return true;
    }*/

    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_book:
                Book book = bookLibrary.getBookList().get(selectedBookIndex);
                Intent intent = new Intent(getActivity(), CreateBook.class);
                intent.putExtra(DisplayBooks.GIVE_BOOK, book);
                startActivityForResult(intent, 0);

                return true;
            case R.id.delete_book:
                BookLibrary.getInstance().deleteBookById((int) bookLibrary.getBookList().get(selectedBookIndex).getId());
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
        createGridView(this.getView());
    }

    // Container Activity must implement this interface
    public interface OnBookSelected {
        public void OnBookSelected(Book book);
    }

}
