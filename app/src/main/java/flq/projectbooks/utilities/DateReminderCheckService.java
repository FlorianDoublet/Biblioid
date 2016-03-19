package flq.projectbooks.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.UI.fragments.BookInfo;
import flq.projectbooks.UI.fragments.SettingsFragment;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.Loan;
import flq.projectbooks.data.libraries.AuthorLibrary;
import flq.projectbooks.data.libraries.BookFilterCatalog;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.CategoryLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;
import flq.projectbooks.data.libraries.PublisherLibrary;


public class DateReminderCheckService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //initialize all library if it's needed
        AuthorLibrary.getInstanceOrInitialize(this);
        CategoryLibrary.getInstanceOrInitialize(this);
        PublisherLibrary.getInstanceOrInitialize(this);
        FriendLibrary.getInstanceOrInitialize(this);
        BookLibrary.getInstanceOrInitialize(this);
        LoanLibrary.getInstanceOrInitialize(this);
        BookFilterCatalog.getInstanceOrInitialize(this);

        List<Loan> loanList = LoanLibrary.getInstance().getLoanList();
        Calendar calendar = Calendar.getInstance();

        for (Loan loan : loanList) {
            if (loan.getDateReminder().before(calendar.getTime())) {
                showLoadNotification(this, loan);
            }
        }

        //to keep the service alive if the app is killed
        return Service.START_STICKY;
    }

    private void showLoadNotification(Context context, Loan loan) {

        Friend friend = FriendLibrary.getInstance().getFriendById(loan.getFriend_id());
        Book book = BookLibrary.getInstance().getBookById(loan.getBook_id());

        Date oldReminder = loan.getDateReminder();
        //update the dateReminder to the local Date
        loan.setDateReminder(Calendar.getInstance().getTime());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        float notificationIntervalFloat = Float.valueOf(sharedPref.getString(SettingsFragment.KEY_PREF_NOTIF_INTERVAL, "1"));
        int notficiationInterval;
        if(notificationIntervalFloat < 1){
            notficiationInterval = (int)(notificationIntervalFloat * 100);
            //we add notificationInterval minutes to the date reminder and update to loan in the database
            loan.addXMinutesToDateReminder(notficiationInterval);
        }else{
            notficiationInterval = (int)notificationIntervalFloat;
            //we add notificationInterval hours to the date reminder and update to loan in the database
            loan.addXHoursToDateReminder(notficiationInterval);
        }



        LoanLibrary.getInstance().updateOrAddLoan(loan);

        String message = "Rappel prêt livre auprès de " + friend.getFirstName() + " " + friend.getLastName();
        message += "\n échéance : " + dateToString(oldReminder);

        //create our intent to display bookInfo
        Intent intent = new Intent(context, DisplayBooks.class);
        intent.putExtra("menuFragment", "BookInfo");
        intent.putExtra("bookId", book.getId());

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notificon)
                        .setContentTitle("Biblioid : " + book.getTitle())
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  H:mm", Locale.FRANCE);


        String date_s = null;
        try {

            date_s = formatter.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }
}

