package flq.projectbooks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import flq.projectbooks.Book;
import flq.projectbooks.R;
import flq.projectbooks.activities.CreateBook;
import flq.projectbooks.activities.CreateFilter;
import flq.projectbooks.activities.DisplayBooks;
import flq.projectbooks.libraries.BookLibrary;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfo extends Fragment implements Parcelable {
    public static final String ARG_PARAM1 = "param1";


    private Book book;

    @Override
    public void writeToParcel(Parcel dest, int flags){

    }

    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @book Book Parameter 1.
     * @return A new instance of fragment BookInfo.
     */
    public static BookInfo newInstance(Book book) {
        BookInfo fragment = new BookInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, book);
        fragment.setArguments(args);
        return fragment;
    }

    public BookInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_book_info, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.modif_book_option:
                Intent intent = new Intent(getActivity(), CreateBook.class);
                intent.putExtra(DisplayBooks.GIVE_BOOK, book);
                startActivityForResult(intent, 0);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_info, container, false);
        displayBook(view);

        return view ;
    }

    public void displayBook(View view){

        TextView textViewTitle = (TextView) view.findViewById(R.id.bookInfoTitle);
        textViewTitle.setText(book.getTitle());

        TextView textViewAuthor = (TextView) view.findViewById(R.id.bookInfoAuthor);
        if(!book.getAuthor().equals("")){
            textViewAuthor.setText("Par " + book.getAuthor());
        }else{
            textViewAuthor.setHeight(0);
        }


        TextView textViewDescription = (TextView) view.findViewById(R.id.bookInfoDescription);
        textViewDescription.setText(book.getDescription());

        TextView textViewDatePub = (TextView) view.findViewById(R.id.bookInfoDatePublication);
        if(!book.getDatePublication().equals("")){
            textViewDatePub.setText("le " + book.getDatePublication());
        }else{
            textViewDatePub.setHeight(0);
        }


        TextView textViewCategory = (TextView) view.findViewById(R.id.bookInfoCategorie);
        if(!book.getCategory().equals("")){
            textViewCategory.setText("Genre : " + book.getCategory());
        }else{
            textViewCategory.setHeight(0);
        }


        TextView textViewEditor = (TextView) view.findViewById(R.id.bookInfoEditor);
        if(!book.getEditor().equals("")){
            textViewEditor.setText("Editeur : " + book.getEditor());
        }else{
            textViewEditor.setHeight(0);
        }


        TextView textViewNbPages = (TextView) view.findViewById(R.id.bookInfoNbPages);
        if(book.getNbPages() != 0){
            textViewNbPages.setText(String.valueOf(book.getNbPages()) + " pages");
        }else{
            textViewNbPages.setHeight(0);
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.bookInfoImage);
        if(book.getImage() != null) {
            imageView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length)));
        }else{
            imageView.getLayoutParams().height = 0;
            imageView.requestLayout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        book = BookLibrary.getInstance().getBookById(book.getId());

        displayBook(this.getView());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
