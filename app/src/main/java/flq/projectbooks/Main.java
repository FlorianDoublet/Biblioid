package flq.projectbooks;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.IOException;
import java.util.ArrayList;

/*


https://github.com/spacecowboy/NoNonsense-FilePicker
https://github.com/passy/Android-DirectoryChooser


 */

public class Main extends ActionBarActivity  {

    //Ask the CreateBook activity to start with an empty book
    public final static String ASK_NEW_BOOK = "flq.ASK_NEW_BOOK";

    public final static int FILE_CODE_IMPORT = 1;
    public final static int FILE_CODE_EXPORT = 2;

    protected BookLibrary books;
    protected BookFilterCatalog filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.deleteDatabase("books.db");
        books = new BookLibrary(this);
        filters = new BookFilterCatalog(this);

        /*books.Add(new Book("Harry Pot De Fleur à l'école des poulets", "J.K. Brownie", "1451", null));
        books.Add(new Book("Harry Pot De Fleur et le poulailler secret", "J.K. Brownie", "1452", null));
        books.Add(new Book("Le Seigneur Des Panneaux", "J.R.R. Trollkien", "45187", null));
        books.Add(new Book("Fhamlette", "William Cestpire", "0218", null));
        books.Add(new Book("Les Sirops d'érable", "Victor Jus Go", "45187", null));*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void openCreateBookActivity(View view) {
        Intent intent = new Intent(this, CreateBook.class);
        intent.putExtra(ASK_NEW_BOOK, books.getNewBook());
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

    public void exportDatabase(View view) {
        Intent i = new Intent(this, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE_EXPORT);
    }

    public void importDatabase(View view) {
        Intent i = new Intent(this, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE_IMPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MySQLiteHelper db = new MySQLiteHelper(this);


        if (requestCode == FILE_CODE_IMPORT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            try {
                db.importDatabase(uri.getPath());
                Toast.makeText(this, "Données chargées.", Toast.LENGTH_LONG).show();
                BookLibrary.getInstance().updateLocalList();
                BookFilterCatalog.getInstance().updateLocalList();
            } catch (IOException e) {
                //Toast.makeText(this, "Erreur, impossible d'importer les données.", Toast.LENGTH_LONG).show();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        if (requestCode == FILE_CODE_EXPORT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            try {
                db.backupDatabase(uri.getPath());
                Toast.makeText(this, "Données sauvegardées.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
