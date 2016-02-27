package flq.projectbooks.UI.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.CreateBook;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;


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

    public void displayBook(final View view) {

        //TextView textViewTitle = (TextView) view.findViewById(R.id.bookInfoTitle);
        //textViewTitle.setText(book.getTitle());

        Drawable drawable = ((RatingBar)view.findViewById(R.id.ratingBar)).getProgressDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);

        TabHost tabs = (TabHost)view.findViewById(R.id.tabHost);
        tabs.setup();


        TabHost.TabSpec detailsTab = tabs.newTabSpec("Détails");
        detailsTab.setIndicator("Détails");
        detailsTab.setContent(R.id.Details);
        tabs.addTab(detailsTab);

        TabHost.TabSpec informationTab = tabs.newTabSpec("Informations");
        informationTab.setIndicator("Informations");
        informationTab.setContent(R.id.Informations);
        tabs.addTab(informationTab);



        final Spinner spinnerBookState = (Spinner)view.findViewById(R.id.spinnerBookState);
        ArrayAdapter<String> spinnerArrayAdapterState = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, Book.spinnerArrayState); //selected item will look like a spinner set from XML
        spinnerArrayAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookState.setAdapter(spinnerArrayAdapterState);

        final Spinner spinnerBookPossession = (Spinner)view.findViewById(R.id.spinnerBookPossession);
        ArrayAdapter<String> spinnerArrayAdapterPossession = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, Book.spinnerArrayPossession); //selected item will look like a spinner set from XML
        spinnerArrayAdapterPossession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookPossession.setAdapter(spinnerArrayAdapterPossession);

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

        List<Category> categories = book.getCategories();
        String category_s = "";
        if (categories != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (i == 0) {
                    category_s += "Genre : " + categories.get(i).getName();
                } else
                    category_s += ", " + categories.get(i).getName();
            }
        }
        textViewCategory.setText(category_s);



        TextView textViewPublisher = (TextView) view.findViewById(R.id.bookInfoPublisher);
        if (book.getPublisher_id()!= -1) {
            textViewPublisher.setText("Publisher : " + PublisherLibrary.getInstance().getPublisherById(book.getPublisher_id()).getName());
        } else {
            textViewPublisher.setHeight(0);
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



        spinnerBookState.setSelection(book.getBookState());
        spinnerBookPossession.setSelection(book.getPossessionState());

        spinnerBookState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean avoidInitialisation = false;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(avoidInitialisation) {
                    book.setBookState(i);
                    BookLibrary.getInstance().updateOrAddBook(book);
                }else{
                    avoidInitialisation = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerBookPossession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean avoidInitialisation = false;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(avoidInitialisation) {
                    book.setBookState(i);
                    BookLibrary.getInstance().updateOrAddBook(book);
                }else{
                    avoidInitialisation = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        RadioGroup radioGroupBookState = (RadioGroup) view.findViewById(R.id.radioGroupBookState);
        final RadioButton radioGroupNotRead = (RadioButton) view.findViewById(R.id.radioButtonNotRead);
        final RadioButton radioGroupReading = (RadioButton) view.findViewById(R.id.radioButtonReading);
        final RadioButton radioGroupRead = (RadioButton) view.findViewById(R.id.radioButtonRead);
        final TextView editTextNbPages = (TextView) view.findViewById(R.id.editTextNbPages);

        editTextNbPages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                book.setAdvancementState(editTextNbPages.getText().toString());
                BookLibrary.getInstance().updateOrAddBook(book);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        if(book.getRating() != 0){
            ratingBar.setRating(book.getRating());
        }

        switch(book.getAdvancementState()){
            case "Not Read":
                radioGroupBookState.check(R.id.radioButtonNotRead);
                ratingBar.setEnabled(false);
                ratingBar.setRating(0);
                break;
            case "Read" :
                radioGroupBookState.check(R.id.radioButtonRead);
                ratingBar.setEnabled(false);
                ratingBar.setRating(0);
                break;
            default:
                radioGroupBookState.check(R.id.radioButtonReading);
                editTextNbPages.setEnabled(true);
                editTextNbPages.setText(book.getAdvancementState());
                ratingBar.setEnabled(true);
                break;
        }

        radioGroupBookState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroupNotRead.isChecked()){
                    editTextNbPages.setEnabled(false);
                    book.setAdvancementState("Not Read");
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(0);
                }
                if(radioGroupReading.isChecked()){
                    editTextNbPages.setEnabled(true);
                    book.setAdvancementState(editTextNbPages.getText().toString());
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(0);
                }
                if(radioGroupRead.isChecked()){
                    editTextNbPages.setEnabled(false);
                    book.setAdvancementState("Read");
                    ratingBar.setEnabled(true);
                    ratingBar.setRating(0);
                }
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                book.setRating((int) v);
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        CheckBox checkBoxFavoriteList = (CheckBox) view.findViewById(R.id.checkBoxFavoriteList);
        if(book.getOnFavoriteList() == 1){
            checkBoxFavoriteList.setChecked(true);
            spinnerBookPossession.setEnabled(false);
        }

        checkBoxFavoriteList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    book.setOnFavoriteList(1);
                }else{
                    book.setOnFavoriteList(0);
                }
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        CheckBox checkBoxWishList = (CheckBox) view.findViewById(R.id.checkBoxWishList);
        if(book.getOnWishList() == 1){
            checkBoxWishList.setChecked(true);
        }
        checkBoxWishList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    book.setOnWishList(1);
                    book.setPossessionState(0);
                    spinnerBookPossession.setSelection(0);
                    spinnerBookPossession.setEnabled(false);
                }else {
                    book.setOnWishList(0);
                    book.setPossessionState(1);
                    spinnerBookPossession.setSelection(1);
                    spinnerBookPossession.setEnabled(true);
                }
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        final TextView editTextComment = (TextView) view.findViewById(R.id.editTextComment);
        if(book.getComment() != null){
            editTextComment.setText(book.getComment());
        }
        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                book.setComment(editTextComment.getText().toString());
                BookLibrary.getInstance().updateOrAddBook(book);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
