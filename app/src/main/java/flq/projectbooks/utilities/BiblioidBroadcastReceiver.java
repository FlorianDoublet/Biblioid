package flq.projectbooks.utilities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import flq.projectbooks.UI.activities.SettingsActivity;
import flq.projectbooks.UI.fragments.SettingsFragment;

/**
 * Created by flori on 27/02/2016.
 */

//class used to run the application in background when we start the phone
public class BiblioidBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_TIMER = 666;
    public static PendingIntent notificationPendingIntent = null;

    public static void runDateReminderCheckerEveryXMinutes(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        float minfloat = Float.parseFloat(sharedPref.getString(SettingsFragment.KEY_PREF_NOTIF_INTERVAL, ""));
        int minutes = 0;
        //it mean that it's minutes
        if(minfloat < 1){
            minutes = (int)(minfloat*100);
        } else {
            //it mean that it's hours
            minutes = ((int)minfloat)*60;
        }

        if(notificationPendingIntent == null){
            Intent intentForService = new Intent(context.getApplicationContext(), DateReminderCheckService.class);
            notificationPendingIntent = PendingIntent.getService(context, BiblioidBroadcastReceiver.NOTIFICATION_TIMER,
                    intentForService, PendingIntent.FLAG_CANCEL_CURRENT);
        }


        //we cancel our current alarm timer
        BiblioidBroadcastReceiver.cancelAlarmIfExists(context, notificationPendingIntent);

        //and then we set up a new one
        final Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);

        final AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);


        long oneMinute = 60000;
        //will run the service each minutes * oneMinute ( by default each minute )
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTime()
                .getTime(), minutes * oneMinute, notificationPendingIntent);

    }

    public static void cancelAlarmIfExists(Context context,PendingIntent notificationPendingIntent){
        try{
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(notificationPendingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notificationEnabled = sharedPref.getBoolean(SettingsFragment.KEY_PREF_NOTIF, false);
        if(notificationEnabled){

            runDateReminderCheckerEveryXMinutes(context);

        }

    }

}
