package flq.projectbooks.UI.fragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.CreateBook;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.libraries.BookLibrary;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfo extends Fragment implements Parcelable {
    public static final String ARG_PARAM1 = "param1";


    private Book book;
    private Menu menu;

    public BookInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookInfo.
     * @book Book Parameter 1.
     */
    public static BookInfo newInstance(Book book) {
        BookInfo fragment = new BookInfo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, book);
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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ARG_PARAM1);
        }
    }


    public void modifBook() {
        Intent intent = new Intent(getActivity(), CreateBook.class);
        intent.putExtra(DisplayBooks.GIVE_BOOK, book);
        startActivityForResult(intent, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_info, container, false);
        displayBook(view);

        return view;
    }

    public void displayBook(View view) {

        TextView textViewTitle = (TextView) view.findViewById(R.id.bookInfoTitle);
        textViewTitle.setText(book.getTitle());

        TextView textViewAuthor = (TextView) view.findViewById(R.id.bookInfoAuthor);

        List<Author> authors = book.getAuthors();
        String author_s = "";
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                if (i == 0) {
                    author_s += "Par " + authors.get(i).getName();
                } else
                    author_s += ", " + authors.get(i).getName();
            }
        }
        textViewAuthor.setText(author_s);

        TextView textViewDescription = (TextView) view.findViewById(R.id.bookInfoDescription);
        textViewDescription.setText(book.getDescription());

        TextView textViewDatePub = (TextView) view.findViewById(R.id.bookInfoDatePublication);
        if (!book.getDatePublication().equals("")) {
            textViewDatePub.setText("le " + book.getDatePublication());
        } else {
            textViewDatePub.setHeight(0);
        }


        TextView textViewCategory = (TextView) view.findViewById(R.id.bookInfoCategorie);
        if (!book.getCategory().equals("")) {
            textViewCategory.setText("Genre : " + book.getCategory());
        } else {
            textViewCategory.setHeight(0);
        }


        TextView textViewEditor = (TextView) view.findViewById(R.id.bookInfoEditor);
        if (!book.getEditor().equals("")) {
            textViewEditor.setText("Editeur : " + book.getEditor());
        } else {
            textViewEditor.setHeight(0);
        }


        TextView textViewNbPages = (TextView) view.findViewById(R.id.bookInfoNbPages);
        if (book.getNbPages() != 0) {
            textViewNbPages.setText(String.valueOf(book.getNbPages()) + " pages");
        } else {
            textViewNbPages.setHeight(0);
        }


        ImageView imageView = (ImageView) view.findViewById(R.id.bookInfoImage);
        if (book.getImage() != null) {
            imageView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length)));
        } else {
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
