package flq.projectbooks.UI.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import flq.projectbooks.R;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;
import flq.projectbooks.utilities.BiblioidBroadcastReceiver;

/**
 * Created by Florian on 15/03/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    //a key is needed for each Preference we want to save
    public static final String KEY_PREF_NOTIF = "pref_notif";
    public static final String KEY_PREF_NOTIF_INTERVAL = "pref_notif_interval";
    public static final String KEY_PREF_BOOKS_DISPLAY_ORDER = "pref_books_display_order";
    public static final String KEY_PREF_FILTER_DISPLAY_ORDER = "pref_filters_display_order";
    public static final String KEY_GRID_ZOOM = "pref_grid_zoom";
    public static final String KEY_PREF_ERASE_DATABASE = "pref_erase_database";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Preference button = (Preference)findPreference(getString(R.string.pref_erase_database));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                confirmDialogRAZ(getActivity());
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    //method to save preferences
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(KEY_PREF_NOTIF)) {
            //do something here?
            SwitchPreference notificationEnabled = (SwitchPreference) findPreference(key);
            if (notificationEnabled.isChecked()) {
                BiblioidBroadcastReceiver.runDateReminderCheckerEveryXMinutes(this.getActivity());
            } else {
                //we cancel the alarmTime for notification if we uncheck the switch
                BiblioidBroadcastReceiver.cancelAlarmIfExists(this.getActivity(), BiblioidBroadcastReceiver.NOTIFICATION_TIMER);
            }


        }
        if (key.equals(KEY_PREF_NOTIF_INTERVAL)) {
            //if we change the interval we relaunch the alarmTimer for the service notification. This methode will "update" the alarmTimer
            //BiblioidBroadcastReceiver.runDateReminderCheckerEveryXMinutes(this.getActivity());
        }

        if (key.equals(KEY_PREF_BOOKS_DISPLAY_ORDER)) {
            BookLibrary.getInstance().loadBookListWithPref(false);
        }

        if (key.equals(KEY_PREF_FILTER_DISPLAY_ORDER)) {
            BookFilterCatalog.getInstance().loadBookFiltersWithPref(false);
        }

    }

    private void confirmDialogRAZ(Context context) {
        final AlertDialog alert = new AlertDialog.Builder(
                context).create();
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
        getActivity().deleteDatabase("books.db");
        Toast.makeText(getActivity(), "Données remises à zéro", Toast.LENGTH_LONG).show();
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
}
