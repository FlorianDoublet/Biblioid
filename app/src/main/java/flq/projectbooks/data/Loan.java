package flq.projectbooks.data;


import java.util.Date;

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
        this.dateLoan = dateLoan;
        this.dateReminder = dateReminder;
        this.book_id = book_id;
        this.friend_id = friend_id;
    }

    public Loan(long friend_id, long book_id, Date dateReminder, long id, Date dateLoan) {
        this.friend_id = friend_id;
        this.book_id = book_id;
        this.dateReminder = dateReminder;
        this.id = id;
        this.dateLoan = dateLoan;
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
}