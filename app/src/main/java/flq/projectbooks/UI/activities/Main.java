package flq.projectbooks.UI.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;
import flq.projectbooks.database.MySQLiteHelper;
import flq.projectbooks.utilities.BiblioidBroadcastReceiver;

public class Main extends ActionBarActivity {

    //Ask the CreateBook activity to start with an empty book
    public final static String ASK_NEW_BOOK = "flq.ASK_NEW_BOOK";
    public final static String GIVE_BOOK_WITH_ISBN = "flq.GIVE_BOOK_WITH_ISBN";
    public final static String ASK_NEW_FRIEND = "flq.ASK_NEW_FRIEND";


    protected BookLibrary books;
    protected BookFilterCatalog filters;
    protected AuthorLibrary authors;
    protected CategoryLibrary categories;
    protected PublisherLibrary publishers;
    protected FriendLibrary friends;
    protected LoanLibrary loans;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Loan loan = new Loan();
        Date d = loan.stringToLoanDate("01/01/2012 10:48");
        String s = loan.dateToString(d);*/

        //this.deleteDatabase("books.db"); //Effacer la bdd en cas de bug ou en cas de conflit de versions.
        authors = AuthorLibrary.getInstanceOrInitialize(this);
        categories = CategoryLibrary.getInstanceOrInitialize(this);
        publishers = PublisherLibrary.getInstanceOrInitialize(this);
        friends =  FriendLibrary.getInstanceOrInitialize(this);
        books =  BookLibrary.getInstanceOrInitialize(this);
        loans =  LoanLibrary.getInstanceOrInitialize(this);
        filters = BookFilterCatalog.getInstanceOrInitialize(this);

        Book.initSpinnerArrayStateAndSpinnerArrayPossession();

        BookFilter.spinnerArrayState = new ArrayList<>();
        BookFilter.spinnerArrayPossession = new ArrayList<>();
        BookFilter.spinnerArrayState.add("Indéterminé");
        BookFilter.spinnerArrayState.add("Comme neuf");
        BookFilter.spinnerArrayState.add("Très bon état");
        BookFilter.spinnerArrayState.add("Bon état");
        BookFilter.spinnerArrayState.add("État correct");
        BookFilter.spinnerArrayState.add("Mauvais état");
        BookFilter.spinnerArrayState.add("Incomplet");
        BookFilter.spinnerArrayPossession.add("Indeterminé");
        BookFilter.spinnerArrayPossession.add("Liste de souhait");
        BookFilter.spinnerArrayPossession.add("Possédé");
        BookFilter.spinnerArrayPossession.add("Prété");
        BookFilter.spinnerArrayPossession.add("Vendu");
        BookFilter.spinnerArrayPossession.add("Perdu");
        BiblioidBroadcastReceiver.runDateReminderCheckerEveryMinute(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_raz) {
            confirmDialogRAZ(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openScannerActivity(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    public void openCreateBookActivity(View view) {
        CreateBook.resetBookCreation();
        Intent intent = new Intent(this, CreateBook.class);
        intent.putExtra(ASK_NEW_BOOK, books.getNewBook());
        startActivity(intent);
    }

    public void openCreateBookActivityWithISBN(String ISBN) {
        CreateBook.resetBookCreation();

        Intent intent = new Intent(this, CreateBook.class);
        intent.putExtra(GIVE_BOOK_WITH_ISBN, ISBN);
        intent.putExtra(ASK_NEW_BOOK, books.getNewBook());
        startActivity(intent);
    }

    public void openCreateFriendActivity(View view) {
        Intent intent = new Intent(this, CreateFriend.class);
        intent.putExtra(ASK_NEW_FRIEND, friends.getNewFriend());
        startActivity(intent);
    }


    public void openDisplayBookActivity(View view) {
        Intent intent = new Intent(this, DisplayBooks.class);
        startActivity(intent);
    }

    public void openDisplayFilterActivity(View view) {
        Intent intent = new Intent(this, DisplayFilters.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MySQLiteHelper db = new MySQLiteHelper(this);

        if (data != null && data.hasExtra(("SCAN_RESULT"))) {
            String ISBN = data.getStringExtra("SCAN_RESULT");
            openCreateBookActivityWithISBN(ISBN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void confirmDialogRAZ(Context context) {
        final AlertDialog alert = new AlertDialog.Builder(
                new ContextThemeWrapper(context, android.R.style.Theme_Dialog))
                .create();
        alert.setTitle("Attention");
        alert.setMessage("Voulez-vous vraiment effacer les données ?");
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        eraseDatabase();

                        alert.dismiss();
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();

                    }
                });

        alert.show();
    }

    void eraseDatabase() {
        deleteDatabase("books.db");
        Toast.makeText(this, "Données remises à zéro", Toast.LENGTH_LONG).show();
        BookLibrary.getInstance().updateLocalList();
        BookFilterCatalog.getInstance().updateLocalList();
    }

    public void openImportExportActivity(View view) {
        Intent i = new Intent(this, ImportExport.class);
        startActivity(i);
    }

}
