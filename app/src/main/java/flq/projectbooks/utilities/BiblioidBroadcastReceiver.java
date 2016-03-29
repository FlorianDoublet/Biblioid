package flq.projectbooks.utilities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import flq.projectbooks.UI.fragments.SettingsFragment;

/**
 * Created by flori on 27/02/2016.
 */

//class used to run the application in background when we start the phone
public class BiblioidBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_TIMER = 666;

    public static void runDateReminderCheckerEveryXMinutes(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationEnabled = sharedPref.getBoolean(SettingsFragment.KEY_PREF_NOTIF, false);
        if (notificationEnabled) {

            Intent intentForService = new Intent(context.getApplicationContext(), DateReminderCheckService.class);
            PendingIntent notificationPendingIntent = PendingIntent.getService(context, BiblioidBroadcastReceiver.NOTIFICATION_TIMER,
                    intentForService, PendingIntent.FLAG_CANCEL_CURRENT);

            // BiblioidBroadcastReceiver.cancelAlarmIfExists(context, NOTIFICATION_TIMER);

            //and then we set up a new one
            final Calendar time = Calendar.getInstance();
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);

            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);

            int minutes = 1;
            long oneMinute = 60000;
            //will run the service each minutes * oneMinute ( by default each minute )
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTime()
                    .getTime(), minutes * oneMinute, notificationPendingIntent);
        }

    }

    public static void cancelAlarmIfExists(Context context, int Id) {
        Intent intentForService = new Intent(context.getApplicationContext(), DateReminderCheckService.class);
        PendingIntent notificationPendingIntent = PendingIntent.getService(context.getApplicationContext(), Id,
                intentForService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(notificationPendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        runDateReminderCheckerEveryXMinutes(context);
    }

}
