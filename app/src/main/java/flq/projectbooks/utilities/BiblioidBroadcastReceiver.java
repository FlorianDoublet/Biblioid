package flq.projectbooks.utilities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;

/**
 * Created by flori on 27/02/2016.
 */

//class used to run the application in background when we start the phone
public class BiblioidBroadcastReceiver extends BroadcastReceiver {

    private static PendingIntent service = null;

    public static void runDateReminderCheckerEveryMinute(Context context) {
        if (service == null) {
            Intent intentForService = new Intent(context.getApplicationContext(), DateReminderCheckService.class);
            final AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            final Calendar time = Calendar.getInstance();
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            if (service == null) {
                service = PendingIntent.getService(context, 0,
                        intentForService, PendingIntent.FLAG_CANCEL_CURRENT);
            }

            long oneMinute = 60000;
            int eachNMinutes = 1;
            //will run the service each eachNminutes * oneMinute ( by default each minute )
            alarmManager.setRepeating(AlarmManager.RTC, time.getTime()
                    .getTime(), eachNMinutes * oneMinute, service);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //create an instance of all the library
        new AuthorLibrary(context);
        new CategoryLibrary(context);
        new PublisherLibrary(context);
        new FriendLibrary(context);
        new BookLibrary(context);
        new LoanLibrary(context);
        new BookFilterCatalog(context);

        runDateReminderCheckerEveryMinute(context);

    }

}
