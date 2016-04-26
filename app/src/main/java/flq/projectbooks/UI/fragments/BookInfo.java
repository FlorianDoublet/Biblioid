package flq.projectbooks.UI.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.CreateBook;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Author;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.Loan;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfo extends Fragment implements Parcelable {
    public static final String ARG_PARAM1 = "param1";

    private DatePickerFragment datePickerFragment = new DatePickerFragment();
    private TimePickerFragment timePickerFragment = new TimePickerFragment();
    private ArrayAdapter<String> spinnerArrayAdapterFriend;
    private Book book;
    private Menu menu;
    private FragmentActivity myContext;

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

    public static Date createOneDateWithDateAndTime(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar timecal = Calendar.getInstance();
        timecal.setTime(time);

        cal.set(Calendar.HOUR_OF_DAY, timecal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timecal.get(Calendar.MINUTE));
        Date finalDate = cal.getTime();
        return finalDate;
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
        Book.initSpinnerArrayStateAndSpinnerArrayPossession();
        displayBook(view);

        return view;
    }

    public void displayBook(View view) {
        Drawable drawable = ((RatingBar) view.findViewById(R.id.ratingBar)).getProgressDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);

        TabHost tabs = (TabHost) view.findViewById(R.id.tabHost);
        tabs.setup();

        if (tabs.getTabWidget().getTabCount() != 3) {
            TabHost.TabSpec detailsTab = tabs.newTabSpec("Détails");
            detailsTab.setIndicator("Détails");
            detailsTab.setContent(R.id.Details);
            tabs.addTab(detailsTab);

            TabHost.TabSpec informationTab = tabs.newTabSpec("Informations");
            informationTab.setIndicator("Informations");
            informationTab.setContent(R.id.Informations);
            tabs.addTab(informationTab);

            TabHost.TabSpec pretTab = tabs.newTabSpec("Prêt");
            pretTab.setIndicator("Prêt");
            pretTab.setContent(R.id.Pret);
            tabs.addTab(pretTab);
        }

        final Spinner spinnerBookState = (Spinner) view.findViewById(R.id.spinnerBookState);
        ArrayAdapter<String> spinnerArrayAdapterState = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, Book.spinnerArrayState); //selected item will look like a spinner set from XML
        spinnerArrayAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookState.setAdapter(spinnerArrayAdapterState);

        final Spinner spinnerBookPossession = (Spinner) view.findViewById(R.id.spinnerBookPossession);
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

        String htmlText = " %s ";
        String myData = "<html><body style=\"text-align:justify\" bgcolor=\"#" + Integer.toHexString(getResources().getColor(R.color.background_material_dark) & 0x00ffffff) + "\">" + "<font color=\"#" + Integer.toHexString(getResources().getColor(R.color.dim_foreground_material_dark) & 0x00ffffff) + "\">" + book.getDescription() + "</font></body></Html>";

        WebView webView = (WebView) view.findViewById(R.id.bookInfoDescription);

        webView.loadData(String.format(htmlText, myData), "text/html; charset=utf-8", "utf-8");

        TextView textViewInfoTitle = (TextView) view.findViewById(R.id.bookInfoTitle);
        textViewInfoTitle.setText(book.getTitle());

        TextView textViewInfoISBN = (TextView) view.findViewById(R.id.bookInfoISBN);
        if(!book.getIsbn().equals("")){
            textViewInfoISBN.setText("ISBN : " + book.getIsbn());
        }

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
        if (book.getPublisher_id() != -1) {
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


        Loan previousLoan = LoanLibrary.getInstance().getLoanByBookId(book.getId());
        if (previousLoan != null) {
            spinnerBookPossession.setEnabled(false);
        } else {
            spinnerBookPossession.setEnabled(true);
        }

        spinnerBookPossession.setSelection(book.getPossessionState());

        spinnerBookState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean avoidInitialisation = false;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (avoidInitialisation) {
                    book.setBookState(i);
                    BookLibrary.getInstance().updateOrAddBook(book);
                } else {
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
                if (avoidInitialisation) {
                    book.setPossessionState(i);
                    BookLibrary.getInstance().updateOrAddBook(book);
                } else {
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
        if (book.getRating() != 0) {
            ratingBar.setRating(book.getRating());
        }

        switch (book.getAdvancementState()) {
            case "Not Read":
                radioGroupBookState.check(R.id.radioButtonNotRead);
                ratingBar.setEnabled(false);
                ratingBar.setRating(0);
                break;
            case "Read":
                radioGroupBookState.check(R.id.radioButtonRead);
                ratingBar.setEnabled(true);
                break;
            default:
                radioGroupBookState.check(R.id.radioButtonReading);
                editTextNbPages.setEnabled(true);
                editTextNbPages.setText(book.getAdvancementState());
                ratingBar.setEnabled(false);
                ratingBar.setRating(0);
                break;
        }

        radioGroupBookState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroupNotRead.isChecked()) {
                    editTextNbPages.setEnabled(false);
                    book.setAdvancementState("Not Read");
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(0);
                }
                if (radioGroupReading.isChecked()) {
                    editTextNbPages.setEnabled(true);
                    book.setAdvancementState(editTextNbPages.getText().toString());
                    ratingBar.setEnabled(false);
                    ratingBar.setRating(0);
                }
                if (radioGroupRead.isChecked()) {
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
        if (book.getOnFavoriteList() == 1) {
            checkBoxFavoriteList.setChecked(true);
            spinnerBookPossession.setEnabled(false);
        }

        checkBoxFavoriteList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    book.setOnFavoriteList(1);
                } else {
                    book.setOnFavoriteList(0);
                }
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        CheckBox checkBoxWishList = (CheckBox) view.findViewById(R.id.checkBoxWishList);
        if (book.getOnWishList() == 1) {
            checkBoxWishList.setChecked(true);
        }
        checkBoxWishList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    book.setOnWishList(1);
                    book.setPossessionState(0);
                    spinnerBookPossession.setSelection(0);
                    spinnerBookPossession.setEnabled(false);
                } else {
                    book.setOnWishList(0);
                    book.setPossessionState(1);
                    spinnerBookPossession.setSelection(1);
                    spinnerBookPossession.setEnabled(true);
                }
                BookLibrary.getInstance().updateOrAddBook(book);
            }
        });

        final TextView editTextComment = (TextView) view.findViewById(R.id.editTextComment);
        if (book.getComment() != null) {
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


        //part for loan

        final LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Button validateLoanButton = (Button) view.findViewById(R.id.btnValidateLoan);
        final Button removeLoanButton = (Button) view.findViewById(R.id.btnRemoveLoan);

        validateLoanButton.setVisibility(View.GONE);
        removeLoanButton.setVisibility(View.GONE);

        view.findViewById(R.id.loanDateLoanTextViewTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialogForDateLoan(view);
            }
        });

        view.findViewById(R.id.imageButtonCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogForDateLoan(view);
            }
        });

        view.findViewById(R.id.loanDateReminderTextViewTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialogForDateReminder(view);
            }
        });

        view.findViewById(R.id.imageButtonDateReminderCalendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogForDateReminder(view);
            }
        });

        view.findViewById(R.id.btnValidateLoan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLoan(view);
            }
        });

        view.findViewById(R.id.btnRemoveLoan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLoan(view);
            }
        });

        initFriendSpinner(view);


        Loan loan = LoanLibrary.getInstance().getLoanByBookId(book.getId());
        if (loan != null) {

            //we show the buttons
            p.weight = 1;
            removeLoanButton.setLayoutParams(p);
            validateLoanButton.setLayoutParams(p);
            removeLoanButton.setVisibility(View.VISIBLE);
            validateLoanButton.setVisibility(View.VISIBLE);
            validateLoanButton.setText(R.string.loan_button_modify);

            Friend friend = FriendLibrary.getInstance().getFriendById(loan.getFriend_id());
            ((CheckBox) view.findViewById(R.id.loanCheckBox)).setChecked(true);
            //use to automatically select the right friend
            ((Spinner) view.findViewById(R.id.friendSpinner)).setSelection(spinnerArrayAdapterFriend.getPosition(friend.getFirstName() + " " + friend.getLastName()));
            //init the dates textview
            this.datePickerFragment.initDateLoan(loan.getDateLoan(), (TextView) view.findViewById(R.id.loanDateLoanTextViewDate));
            this.datePickerFragment.initDateReminder(loan.getDateReminder(), (TextView) view.findViewById(R.id.loanDateReminderTextViewDate));
            //init the dates textview
            this.timePickerFragment.initDateLoan(loan.getDateLoan(), (TextView) view.findViewById(R.id.loanDateLoanTextViewTime));
            this.timePickerFragment.initDateReminder(loan.getDateReminder(), (TextView) view.findViewById(R.id.loanDateReminderTextViewTime));
        } else {
            //init the dates textview
            this.datePickerFragment.initDateLoan((TextView) view.findViewById(R.id.loanDateLoanTextViewDate));
            this.datePickerFragment.initDateReminder((TextView) view.findViewById(R.id.loanDateReminderTextViewDate));
            //init the dates textview
            this.timePickerFragment.initDateLoan((TextView) view.findViewById(R.id.loanDateLoanTextViewTime));
            this.timePickerFragment.initDateReminder((TextView) view.findViewById(R.id.loanDateReminderTextViewTime));
        }

        CheckBox loanCheckbox = ((CheckBox) view.findViewById(R.id.loanCheckBox));
        loanCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {

                                                            if (removeLoanButton.getVisibility() == View.VISIBLE)
                                                                p.weight = 1;
                                                            else
                                                                p.weight = 2;
                                                            validateLoanButton.setLayoutParams(p);
                                                            validateLoanButton.setVisibility(View.VISIBLE);


                                                        } else {
                                                            if (removeLoanButton.getVisibility() == View.VISIBLE) {
                                                                p.weight = 2;
                                                                removeLoanButton.setLayoutParams(p);
                                                            }
                                                            validateLoanButton.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
        );

        if (book.getFriend_id() != -1) {
            //isFriendBook
            ((TextView) view.findViewById(R.id.isFriendBook)).setText("Ce livre appartient à " + FriendLibrary.getInstance().getFriendById(book.getFriend_id()).getFirstName());
            view.findViewById(R.id.isFriendBook).setVisibility(View.VISIBLE);

            ratingBar.setEnabled(false);
            radioGroupBookState.setEnabled(false);
            radioGroupNotRead.setEnabled(false);
            radioGroupRead.setEnabled(false);
            radioGroupReading.setEnabled(false);
            textViewNbPages.setEnabled(false);
            checkBoxFavoriteList.setEnabled(false);
            checkBoxWishList.setEnabled(false);
            spinnerBookPossession.setEnabled(false);
            spinnerBookState.setEnabled(false);
            editTextComment.setEnabled(false);
            view.findViewById(R.id.btnUpdateBook);
            tabs.getTabWidget().getChildTabViewAt(2).setVisibility(View.GONE);
        }

    }

    public void validateLoan(View view) {
        //treatment for the loan system

        Spinner friend = (Spinner) getView().findViewById(R.id.friendSpinner);
        CheckBox loanCheckbox = (CheckBox) getView().findViewById(R.id.loanCheckBox);

        Loan previousLoan = LoanLibrary.getInstance().getLoanByBookId(book.getId());

        if (loanCheckbox.isChecked()) {
            //Set the book in the possession state "Lend"
            ((Spinner) getView().findViewById(R.id.spinnerBookPossession)).setSelection(2);
            book.setBookState(2);

            long friend_id = FriendLibrary.getInstance().getFriendByFirstNameAndLastName(friend.getSelectedItem().toString()).getId();
            Loan loan = LoanLibrary.getInstance().getLoanByBookAndFriendId(book.getId(), friend_id);
            Date dateLoan = createOneDateWithDateAndTime(datePickerFragment.getDateLoan(), timePickerFragment.getDateLoan());
            Date dateReminder = createOneDateWithDateAndTime(datePickerFragment.getDateReminder(), timePickerFragment.getDateReminder());
            if (loan != null) {
                loan.setDateLoan(dateLoan);
                loan.setDateReminder(dateReminder);
            } else {
                loan = new Loan(dateLoan, dateReminder, book.getId(), friend_id);
                //if we are here and the previousLoan isn't null then it mean that the friend is different so we delete the older loan
                if (previousLoan != null) {
                    LoanLibrary.getInstance().deleteLoanByLoanId(previousLoan.getId());
                }
            }
            LoanLibrary.getInstance().updateOrAddLoan(loan);
        } else if (previousLoan != null) {
            //Set the book in the possession state "Possessed"
            ((Spinner) getView().findViewById(R.id.spinnerBookPossession)).setSelection(1);
            book.setBookState(1);
            //here it mean that this book have a previous loan but we don't want it anymore so we juste delete this loan.
            LoanLibrary.getInstance().deleteLoanByLoanId(previousLoan.getId());
            //and reset all loan field
            resetLoanField(view);
        }

        TabHost tabs = (TabHost) getView().findViewById(R.id.tabHost);
        tabs.setCurrentTab(0);
    }

    public void deleteLoan(View view) {

        Loan previousLoan = LoanLibrary.getInstance().getLoanByBookId(book.getId());
        if (previousLoan != null) {
            //here it mean that this book have a previous loan but we don't want it anymore so we juste delete this loan.
            LoanLibrary.getInstance().deleteLoanByLoanId(previousLoan.getId());
        }

        ((Spinner) getView().findViewById(R.id.spinnerBookPossession)).setSelection(1);
        book.setBookState(1);

        //we set all field to default value
        resetLoanField(view);

        TabHost tabs = (TabHost) getView().findViewById(R.id.tabHost);
        tabs.setCurrentTab(0);
    }

    //reset all loan field
    private void resetLoanField(View view) {
        ((CheckBox) getView().findViewById(R.id.loanCheckBox)).setChecked(false);
        ((Spinner) getView().findViewById(R.id.friendSpinner)).setSelection(0);
        this.datePickerFragment.initDateLoan((TextView) getView().findViewById(R.id.loanDateLoanTextViewDate));
        this.datePickerFragment.initDateReminder((TextView) getView().findViewById(R.id.loanDateReminderTextViewDate));
        //init the dates textview
        this.timePickerFragment.initDateLoan((TextView) getView().findViewById(R.id.loanDateLoanTextViewTime));
        this.timePickerFragment.initDateReminder((TextView) getView().findViewById(R.id.loanDateReminderTextViewTime));
    }

    private void initFriendSpinner(View view) {
        //View view = findViewById(android.R.id.content).getRootView();
        ArrayList<String> friendSpinnerString = new ArrayList<String>();
        List<Friend> friends = FriendLibrary.getInstance().getFriendList();
        for (Friend friend : friends) {
            friendSpinnerString.add(friend.getFirstName() + " " + friend.getLastName());
        }

        Spinner spinnerFriend = (Spinner) view.findViewById(R.id.friendSpinner);
        spinnerArrayAdapterFriend = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, friendSpinnerString);
        spinnerArrayAdapterFriend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFriend.setAdapter(spinnerArrayAdapterFriend);
    }

    public void showTimePickerDialogForDateLoan(View v) {
        //before to call the diaglog you have to define which date you gonna create (for example reminder or date-loan)
        timePickerFragment.setDateReminder((TextView) getView().findViewById(R.id.loanDateLoanTextViewTime));
        timePickerFragment.show(myContext.getFragmentManager(), "timePicker");

    }

    public void showTimePickerDialogForDateReminder(View v) {
        //before to call the diaglog you have to define which date you gonna create (for example reminder or date-loan)
        timePickerFragment.setDateReminder((TextView) getView().findViewById(R.id.loanDateReminderTextViewTime));
        timePickerFragment.show(myContext.getFragmentManager(), "timePicker");

    }

    public void showDatePickerDialogForDateLoan(View v) {
        //before to call the diaglog you have to define which date you gonna create (for example reminder or date-loan)
        TextView txtView = (TextView) getView().findViewById(R.id.loanDateLoanTextViewDate);
        datePickerFragment.setDateLoan(txtView);
        datePickerFragment.show(myContext.getFragmentManager(), "datePicker");
    }

    public void showDatePickerDialogForDateReminder(View v) {
        //before to call the diaglog you have to define which date you gonna create (for example reminder or date-loan)
        datePickerFragment.setDateReminder((TextView) getView().findViewById(R.id.loanDateReminderTextViewDate));
        datePickerFragment.show(myContext.getFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        book = BookLibrary.getInstance().getBookById(book.getId());
        displayBook(this.getView());
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
