package flq.projectbooks.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.List;

import flq.projectbooks.R;
import flq.projectbooks.UI.activities.DisplayBooks;
import flq.projectbooks.data.Book;
import flq.projectbooks.data.Friend;
import flq.projectbooks.data.Loan;
import flq.projectbooks.data.libraries.BookLibrary;
import flq.projectbooks.data.libraries.FriendLibrary;
import flq.projectbooks.data.libraries.LoanLibrary;


public class DateReminderCheckService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        List<Loan> loanList = LoanLibrary.getInstance().getLoanList();
        Calendar calendar = Calendar.getInstance();

        for (Loan loan : loanList) {
            if (loan.getDateReminder().before(calendar.getTime())) {
                showLoadNotification(this, loan);
            }

        }

        return Service.START_NOT_STICKY;
    }

    private void showLoadNotification(Context context, Loan loan) {

        Friend friend = FriendLibrary.getInstance().getFriendById(loan.getFriend_id());
        Book book = BookLibrary.getInstance().getBookById(loan.getBook_id());

        //we add 5 minute to the date reminder and update to loan in the database
        loan.addXMinutesToDateReminder(5);
        LoanLibrary.getInstance().updateOrAddLoan(loan);

        String message = "N'oubliez pas votre livre auprès de " + friend.getFirstName() + " " + friend.getLastName();

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
}

