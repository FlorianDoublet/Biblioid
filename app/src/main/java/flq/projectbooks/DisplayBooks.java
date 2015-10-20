package flq.projectbooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DisplayBooks extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    public final static String GIVE_BOOK = "flq.GIVE_BOOK";

    private int selectedBookIndex;
    private ListView bookList;
    private List<Map<String, String>> listOfBooks ;
    private SimpleAdapter listAdapter;
    private BookLibrary bookLibrary ;
    private BookFilter bookFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_books);
        Intent intent = getIntent();

        bookList = (ListView) findViewById(R.id.bookList);

        bookList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBookIndex = position;
                PopupMenu popupMenu = new PopupMenu(DisplayBooks.this, view);
                popupMenu.setOnMenuItemClickListener(DisplayBooks.this);
                popupMenu.inflate(R.menu.bookclickpopup);
                popupMenu.show();

                return true;
            }
        });
        Intent resultIntent = new Intent();

        if(intent.hasExtra(DisplayFilters.GIVE_FILTER)){
            bookFilter = (BookFilter) intent.getSerializableExtra(DisplayFilters.GIVE_FILTER);
        } else {
            bookLibrary = BookLibrary.getInstance();
        }

        createListView();

        setResult(Activity.RESULT_OK, resultIntent);
    }

    private void createListView(){

        if(bookFilter != null) {
            BookLibrary filteredBooksLibrary = new BookLibrary();
            for (int i = 0; i < BookLibrary.getInstance().getBookList().size(); i++) {
                if (bookFilter.IsSelected(BookLibrary.getInstance().getBookList().get(i))) {
                    filteredBooksLibrary.Add(BookLibrary.getInstance().getBookList().get(i));
                }
            }
            bookLibrary = filteredBooksLibrary;
        }


        listOfBooks = new ArrayList<>();
        ListView bookList = (ListView) findViewById(R.id.bookList);

        for (Book book : bookLibrary.getBookList()) {
            Map<String, String> bookMap = new HashMap<>() ;
            bookMap.put("img", String.valueOf(R.drawable.picturebook));
            bookMap.put("author", book.getAuthor());
            bookMap.put("title", book.getTitle());
            bookMap.put("isbn", book.getIsbn());
            listOfBooks.add(bookMap);
        }

        listAdapter = new SimpleAdapter(this.getBaseContext(), listOfBooks, R.layout.book_detail,
                new String[] {"img", "author", "title", "isbn"},
                new int[] {R.id.img, R.id.author, R.id.title, R.id.isbn});


        bookList.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_books, menu);
        return true;
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
                Intent intent = new Intent(this, CreateBook.class);
                intent.putExtra(GIVE_BOOK, book);
                startActivityForResult(intent, 0);

                return true;
            case R.id.delete_book:
                BookLibrary.getInstance().DeleteBookById(bookLibrary.getBookList().get(selectedBookIndex).id);
                listOfBooks.remove(selectedBookIndex);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Livre effacé" , Toast.LENGTH_SHORT).show();

                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && data.hasExtra(CreateBook.GIVE_BOOK_BACK)){
            Book book = (Book) data.getSerializableExtra(CreateBook.GIVE_BOOK_BACK);

            BookLibrary.getInstance().UpdateOrAddBook(book);
        }

        createListView();
    }

}