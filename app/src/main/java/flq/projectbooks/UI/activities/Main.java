package flq.projectbooks.UI.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.IOException;
import java.util.ArrayList;

import flq.projectbooks.R;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.BookFilter;
import flq.projectbooks.data.Category;
import flq.projectbooks.data.Publisher;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;
import flq.projectbooks.database.MySQLiteHelper;
import flq.projectbooks.utilities.BiblioidBroadcastReceiver;

public class Main extends AppCompatActivity {
    //for DataBase

    public final static String EXTERNAL_DB_NAME = "external.db";
    public final static int DB_VERSION = 1;

    //Ask the CreateBook activity to start with an empty book
    public final static String ASK_NEW_BOOK = "flq.ASK_NEW_BOOK";
    public final static String GIVE_BOOK_WITH_ISBN = "flq.GIVE_BOOK_WITH_ISBN";
    public final static int CREATE_BOOK = 110;
    public final static int CREATE_BOOK_FINISHED_AND_ADD_WITH_SCANNER = 112;
    public final static int CREATE_BOOK_FINISHED_AND_ADD_MANUALLY = 113;

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
        //used to set our actionBar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //line used to be sure that our prefered value are greatly initialized
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //this.deleteDatabase("books.db"); //Effacer la bdd en cas de bug ou en cas de conflit de versions.
        authors = AuthorLibrary.getInstanceOrInitialize(this);
        categories = CategoryLibrary.getInstanceOrInitialize(this);
        publishers = PublisherLibrary.getInstanceOrInitialize(this);
        friends = FriendLibrary.getInstanceOrInitialize(this);
        books = BookLibrary.getInstanceOrInitialize(this);
        loans = LoanLibrary.getInstanceOrInitialize(this);
        filters = BookFilterCatalog.getInstanceOrInitialize(this);

        //UGLY hack to write another dbfile because cloud don't work for me
        MySQLiteHelper db = new MySQLiteHelper(this);
        try {
            db.backupDatabase("/data/data/" + this.getPackageName() + "/databases/", EXTERNAL_DB_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }


        BookLibrary bookLibraryFriend = new BookLibrary(this, EXTERNAL_DB_NAME, DB_VERSION);

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

        //if the notificationPendingIntent is null then we launch the service for notifications
        //if(BiblioidBroadcastReceiver.notificationPendingIntent == null)
        BiblioidBroadcastReceiver.runDateReminderCheckerEveryXMinutes(this);

        if (findViewById(R.id.imgViewCreateBook) != null) {
            ((ImageView) findViewById(R.id.imgViewCreateBook)).setImageDrawable(this.ResizeImage(R.drawable.book));
            ((ImageView) findViewById(R.id.imgViewDisplayBooks)).setImageDrawable(this.ResizeImage(R.drawable.library));
            ((ImageView) findViewById(R.id.imgViewDisplayFilters)).setImageDrawable(this.ResizeImage(R.drawable.filter));
            ((ImageView) findViewById(R.id.imgViewScanBook)).setImageDrawable(this.ResizeImage(R.drawable.barcode));
            ((ImageView) findViewById(R.id.imgViewImportExport)).setImageDrawable(this.ResizeImage(R.drawable.importexport));
            ((ImageView) findViewById(R.id.imgViewDisplayFriend)).setImageDrawable(this.ResizeImage(R.drawable.friend));
            ((ImageView) findViewById(R.id.imgViewOptions)).setImageDrawable(this.ResizeImage(R.drawable.option));
            ((ImageView) findViewById(R.id.imgViewInformations)).setImageDrawable(this.ResizeImage(R.drawable.info));
        }
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
        startActivityForResult(intent, CREATE_BOOK);
    }

    public void openCreateBookActivityWithISBN(String ISBN) {
        CreateBook.resetBookCreation();

        Intent intent = new Intent(this, CreateBook.class);
        intent.putExtra(GIVE_BOOK_WITH_ISBN, ISBN);
        intent.putExtra(ASK_NEW_BOOK, books.getNewBook());
        startActivityForResult(intent, CREATE_BOOK);
    }

    public void openDisplayFriendActivity(View view) {
        Intent intent = new Intent(this, DisplayFriends.class);
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

        if (resultCode == CREATE_BOOK_FINISHED_AND_ADD_MANUALLY) {
            openCreateBookActivity(null);
        }

        if (resultCode == CREATE_BOOK_FINISHED_AND_ADD_WITH_SCANNER) {
            openScannerActivity(null);
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
        updateAllLibrary();
    }

    void updateAllLibrary(){
        BookLibrary.getInstance().updateLocalList();
        BookFilterCatalog.getInstance().updateLocalList();
        AuthorLibrary.getInstance().updateLocalList();
        CategoryLibrary.getInstance().updateLocalList();
        FriendLibrary.getInstance().updateLocalList();
        LoanLibrary.getInstance().updateLocalList();
        PublisherLibrary.getInstance().updateLocalList();

    }

    public void openImportExportActivity(View view) {
        Intent i = new Intent(this, ImportExport.class);
        startActivity(i);
    }

    public void openSettingsActivity(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public Drawable ResizeImage(int imageID) {
        // Get device dimensions
        Display display = getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
                imageID);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageID);
        Drawable drawable = new BitmapDrawable(this.getResources(),
                getResizedBitmap(bMap, newImageHeight, (int) deviceWidth));

        return drawable;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth / 2) / width;
        float scaleHeight = ((float) newHeight / 2) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    public void openInformationActivity(View view) {
        Intent i = new Intent(this, Informations.class);
        startActivity(i);
    }
}
