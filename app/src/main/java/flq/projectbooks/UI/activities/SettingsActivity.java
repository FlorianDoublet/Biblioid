package flq.projectbooks.UI.activities;

import android.app.Activity;
import android.os.Bundle;

import flq.projectbooks.UI.fragments.SettingsFragment;

/**
 * Created by Florian on 15/03/2016.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
