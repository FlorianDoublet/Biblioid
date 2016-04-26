package flq.projectbooks.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import flq.projectbooks.R;

public class Informations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);
        //used to set our actionBar menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_informations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_book_activity_item) {
            Main.openCreateBookActivity(this, null);
            return true;
        }

        if (id == R.id.display_books_activity_item) {
            Main.openDisplayBookActivity(this, null);
            return true;
        }

        if (id == R.id.display_filters_activity_item) {
            Main.openDisplayFilterActivity(this, null);
            return true;
        }
        if (id == R.id.scan_book_activity_item) {
            Main.openScannerActivity(this, null);
            return true;
        }
        if (id == R.id.import_export_activity_item) {
            Main.openImportExportActivity(this, null);
            return true;
        }
        if (id == R.id.display_friends_activity_item) {
            Main.openDisplayFriendActivity(this, null);
            return true;
        }
        if (id == R.id.preference_activity_item) {
            Main.openSettingsActivity(this, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Main.onActivityResultStatic(this, requestCode, resultCode, data);
    }
}
