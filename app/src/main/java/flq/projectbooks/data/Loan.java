package flq.projectbooks.data;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by flori on 15/02/2016.
 */
public class Loan {
    private long id;
    private Date dateLoan;
    private Date dateReminder;
    private long book_id;
    private long friend_id;

    public Loan(){
        this.id = -1;
        this.dateLoan = null;
        this.dateReminder = null;
        this.book_id = -1;
        this.friend_id = -1;
    }

    public Loan(Date dateLoan, Date dateReminder, long book_id, long friend_id) {
        this.id = -1;
        this.dateLoan = dateLoan;
        this.dateReminder = dateReminder;
        this.book_id = book_id;
        this.friend_id = friend_id;
    }

    public Loan( long id, long friend_id, long book_id, Date dateReminder, Date dateLoan) {
        this.friend_id = friend_id;
        this.book_id = book_id;
        this.dateReminder = dateReminder;
        this.id = id;
        this.dateLoan = dateLoan;
    }

    public Loan(String dateLoan, String dateReminder, long book_id, long friend_id) {
        this.id = -1;
        this.dateLoan = stringToLoanDate(dateLoan);
        this.dateReminder = stringToLoanDate(dateReminder);
        this.book_id = book_id;
        this.friend_id = friend_id;
    }

    public Loan(long id, long friend_id, long book_id, String dateReminder, String dateLoan) {
        this.friend_id = friend_id;
        this.book_id = book_id;
        this.id = id;
        this.dateLoan = stringToLoanDate(dateLoan);
        this.dateReminder = stringToLoanDate(dateReminder);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateLoan() {
        return dateLoan;
    }

    public void setDateLoan(Date dateLoan) {
        this.dateLoan = dateLoan;
    }

    public Date getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(Date dateReminder) {
        this.dateReminder = dateReminder;
    }

    public long getBook_id() {
        return book_id;
    }

    public void setBook_id(long book_id) {
        this.book_id = book_id;
    }

    public long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(long friend_id) {
        this.friend_id = friend_id;
    }

    public Date stringToLoanDate(String date_s){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy  H:mm", Locale.FRANCE);
        Date date = null;
        try {

            date = formatter.parse(date_s);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String dateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy  H:mm",  Locale.FRANCE);


        String date_s = null;
        try {

            date_s = formatter.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }

    //method to add X minute to the date Reminder of a loan
    public void addXMinutesToDateReminder(int minutes){

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDateReminder());
        cal.add(Calendar.MINUTE, minutes);
        this.setDateReminder(cal.getTime());

    }

    //method to add X hours to the date Reminder of a loan
    public void addXHoursToDateReminder(int hours){

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDateReminder());
        cal.add(Calendar.HOUR_OF_DAY, hours);
        this.setDateReminder(cal.getTime());

    }

    //method to add X hours to the date Reminder of a loan
    public void addXDaysToDateReminder(int days){

        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDateReminder());
        cal.add(Calendar.DAY_OF_MONTH, days);
        this.setDateReminder(cal.getTime());

    }

}