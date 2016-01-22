package flq.projectbooks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import flq.projectbooks.Book;
import flq.projectbooks.BookFilter;
import flq.projectbooks.R;
import flq.projectbooks.activities.CreateBook;
import flq.projectbooks.activities.DisplayBooks;
import flq.projectbooks.libraries.BookLibrary;


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link BookList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookList extends Fragment implements PopupMenu.OnMenuItemClickListener, Parcelable {
    public static final String ARG_PARAM1 = "param1";


    private OnBookSelected mBookListener;

    private int selectedBookIndex;
    private GridView gridViewBooks;
    private List<Map<String, Object>> listOfBooks ;
    private SimpleAdapter listAdapter;
    private BookLibrary bookLibrary ;
    private BookFilter bookFilter;

    @Override
    public void writeToParcel(Parcel dest, int flags){

    }

    @Override
    public int describeContents(){
        return 0;
    }

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

    private void createGridView(View view){
        if(bookFilter != null) {
            bookLibrary = bookFilter.getFilteredLibrary();
        }
        listOfBooks = new ArrayList<>();

        for (Book book : bookLibrary.getBookList()) {
            Map<String, Object> bookMap = new HashMap<>() ;
            if(book.getImage() != null){
                byte[] img = book.getImage();
                bookMap.put("img", new BitmapDrawable(BitmapFactory.decodeByteArray(img, 0, img.length)));
            }else{

                bookMap.put("img", String.valueOf(R.drawable.picturebook));
            }
            bookMap.put("author", book.getAuthor());
            bookMap.put("title", book.getTitle());
            bookMap.put("isbn", book.getIsbn());
            listOfBooks.add(bookMap);
        }

        listAdapter = new SimpleAdapter(getActivity().getBaseContext(), listOfBooks, R.layout.book_detail,
                new String[] {"img", "title"},
                new int[] {R.id.img, R.id.text});

        listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view.getId() == R.id.img) {
                    if (data.getClass() != String.class) {
                        ImageView imageView = (ImageView) view;
                        Drawable drawable = (Drawable) data;
                        imageView.setImageDrawable(drawable);
                    } else {
                        ImageView imageView = (ImageView) view;
                        imageView.setImageResource(getResources().getIdentifier(data.toString(), "drawable", "flq.projectbooks"));
                    }
                    return true;
                }
                return false;
            }
        });


        //gridViewBooks.setAdapter(new listAdapter(this));
        gridViewBooks.setAdapter(listAdapter);
    }

    private void createListView(View view, int bookListCode, boolean evenBooks){
        if(bookFilter != null) {
            bookLibrary = bookFilter.getFilteredLibrary();
        }


        listOfBooks = new ArrayList<>();
        ListView bookList = (ListView) view.findViewById(bookListCode);


        List<Book> subList = new ArrayList<Book>();
        List<Book> tempBookList = bookLibrary.getBookList();

        if(evenBooks){
            for(int i = 0; i < tempBookList.size(); i += 2){
                subList.add(tempBookList.get(i));
            }
        }else{
            for(int i = 1; i < tempBookList.size()-1; i += 2){
                subList.add(tempBookList.get(i));
            }
        }

        for (Book book : subList) {
            Map<String, Object> bookMap = new HashMap<>() ;
            if(book.getImage() != null){
                byte[] img = book.getImage();
                bookMap.put("img", new BitmapDrawable(BitmapFactory.decodeByteArray(img, 0, img.length)));
            }else{

                bookMap.put("img", String.valueOf(R.drawable.picturebook));
            }
            bookMap.put("author", book.getAuthor());
            bookMap.put("title", book.getTitle());
            bookMap.put("isbn", book.getIsbn());
            listOfBooks.add(bookMap);
        }

        listAdapter = new SimpleAdapter(getActivity().getBaseContext(), listOfBooks, R.layout.book_detail,
                new String[] {"img", "author", "title", "isbn"},
                new int[] {R.id.img});

        listAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view.getId() == R.id.img ) {
                    if( data.getClass() != String.class) {
                        ImageView imageView = (ImageView) view;
                        Drawable drawable = (Drawable) data;
                        imageView.setImageDrawable(drawable);
                    }else{
                        ImageView imageView = (ImageView) view;
                        imageView.setImageResource(getResources().getIdentifier(data.toString(), "drawable", "flq.projectbooks"));
                    }
                    return true;
                }
                return false;
            }
        });


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
                BookLibrary.getInstance().deleteBookById((int)bookLibrary.getBookList().get(selectedBookIndex).getId());
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

}
