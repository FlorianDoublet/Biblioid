package flq.projectbooks.UI.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import flq.projectbooks.R;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.utilities.BiblioidBroadcastReceiver;
import flq.projectbooks.utilities.DateReminderCheckService;

/**
 * Created by Florian on 15/03/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    //a key is needed for each Preference we want to save
    public static final String KEY_PREF_NOTIF = "pref_notif";
    public static final String KEY_PREF_NOTIF_INTERVAL = "pref_notif_interval";
    public static final String KEY_PREF_BOOKS_DISPLAY_ORDER = "pref_books_display_order";
    public static final String KEY_PREF_FILTER_DISPLAY_ORDER = "pref_filters_display_order";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
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
            SwitchPreference notificationEnabled = (SwitchPreference)findPreference(key);
            if(notificationEnabled.isChecked()){
                BiblioidBroadcastReceiver.runDateReminderCheckerEveryXMinutes(this.getActivity());
            }else{
                //we cancel the alarmTime for notification if we uncheck the switch
                BiblioidBroadcastReceiver.cancelAlarmIfExists(this.getActivity(), BiblioidBroadcastReceiver.NOTIFICATION_TIMER);
            }


        }
        if (key.equals(KEY_PREF_NOTIF_INTERVAL)) {
            //if we change the interval we relaunch the alarmTimer for the service notification. This methode will "update" the alarmTimer
            //BiblioidBroadcastReceiver.runDateReminderCheckerEveryXMinutes(this.getActivity());
        }

        if(key.equals(KEY_PREF_BOOKS_DISPLAY_ORDER)){
            BookLibrary.getInstance().loadBookListWithPref(false);
        }

        if(key.equals(KEY_PREF_FILTER_DISPLAY_ORDER)){
            BookFilterCatalog.getInstance().loadBookFiltersWithPref(false);
        }
    }
}
