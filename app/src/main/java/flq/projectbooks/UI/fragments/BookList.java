package flq.projectbooks.UI.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.CreateBook;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.database.LinkTablesDataSource;
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

    private void displayMultiSelectionButtons() {
        getView().findViewById(R.id.multiSelectionCancel).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.multiSelectionNewFilter).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.multiSelectionDelete).setVisibility(View.VISIBLE);
        LinearLayout l = (LinearLayout) getView().findViewById(R.id.gridzoomLayout);
        l.setWeightSum(5);
    }

    private void hideMultiSelectionButtons() {
        getView().findViewById(R.id.multiSelectionCancel).setVisibility(View.GONE);
        getView().findViewById(R.id.multiSelectionNewFilter).setVisibility(View.GONE);
        getView().findViewById(R.id.multiSelectionDelete).setVisibility(View.GONE);
        LinearLayout l = (LinearLayout) getView().findViewById(R.id.gridzoomLayout);
        l.setWeightSum(2);
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
            String def_string = "Filtre ";
            if(bookFilter.getFriend_id() != -1){
                def_string += " amis ";
            }
            getActivity().setTitle(def_string + "\" " + bookFilter.getName() + " \"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        gridViewBooks = (GridView) view.findViewById(R.id.gridViewBooks);

        bookLibrary = BookLibrary.getInstance();
        createGridView(view);

        gridViewBooks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!listAdapter.isMultiSelectionActivated()){
                    displayMultiSelectionButtons();
                    listAdapter.activateMultiSelection();
                    listAdapter.changeSelection(position);
                    listAdapter.notifyDataSetInvalidated();
                    gridViewBooks.invalidate();
                }

                /*selectedBookIndex = position;
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.setOnMenuItemClickListener(BookList.this);
                popupMenu.inflate(R.menu.bookclickpopup);
                popupMenu.show();*/

                return true;
            }
        });

        gridViewBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listAdapter.isMultiSelectionActivated()) {
                    listAdapter.changeSelection(position);
                    listAdapter.notifyDataSetInvalidated();
                    gridViewBooks.invalidate();
                } else {
                    mBookListener.OnBookSelected(bookLibrary.getBookList().get(position));
                }
            }
        });

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

        view.findViewById(R.id.multiSelectionCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter.desactivateMultiSelection();
                hideMultiSelectionButtons();
                listAdapter.notifyDataSetInvalidated();
                gridViewBooks.invalidate();
            }
        });

        view.findViewById(R.id.multiSelectionDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Suppression de livre(s)")
                        .setMessage("Êtes-vous sûr de procéder à la suppression de votre sélection ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //BookLibrary.getInstance().deleteBookById((int) bookLibrary.getBookList().get(selectedBookIndex).getId());
                                listAdapter.deleteSelectedBooks();
                                bookLibrary = BookLibrary.getInstance();
                                createGridView(view);
                                listAdapter.desactivateMultiSelection();
                                hideMultiSelectionButtons();
                                listAdapter.notifyDataSetInvalidated();
                                gridViewBooks.invalidate();
                            }

                        })
                        .setNegativeButton("Non", null)
                        .show();
            }
        });

        view.findViewById(R.id.multiSelectionNewFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFilterWithBooksDialogs(view);
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


        listAdapter.notifyDataSetInvalidated();
        gridViewBooks.invalidate();
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

    public void createFilterWithBooksDialogs(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Ajouter le titre du filtre qui contiendra les livres choisis");

        // Set up the input
        final EditText input = new EditText(view.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();

                        BookFilter filter = new BookFilter();
                        filter.setName(name);
                        List<Category> cs = LinkTablesDataSource.getCategoriesFromString(name);
                        //filter.setCategories(cs);
                        LinkTablesDataSource.feedBookFilterWithCategories(filter, cs);
                        //BookFilterCatalog.getInstance().Add(filter);

                        listAdapter.addBooksToFilter(cs);
                        listAdapter.desactivateMultiSelection();
                        hideMultiSelectionButtons();
                        listAdapter.notifyDataSetInvalidated();
                        gridViewBooks.invalidate();
                    }
                }
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );

        builder.show();
    }

    // Container Activity must implement this interface
    public interface OnBookSelected {
        public void OnBookSelected(Book book);
    }
}
