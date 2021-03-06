package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Loan;

/**
 * Created by flori on 15/02/2016.
 */
public class LoansDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_LOAN_DATELOAN,
            MySQLiteHelper.COLUMN_LOAN_DATEREMIDER,
            MySQLiteHelper.COLUMN_BOOK_ID,
            MySQLiteHelper.COLUMN_FRIEND_ID
    };

    public LoansDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Loan createLoan(String dateLoan, String dateRemider, long book_id, long friend_id) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LOAN_DATELOAN, dateLoan);
        values.put(MySQLiteHelper.COLUMN_LOAN_DATEREMIDER, dateRemider);
        values.put(MySQLiteHelper.COLUMN_BOOK_ID, book_id);
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, friend_id);

        long insertId = database.insert(MySQLiteHelper.TABLE_LOANS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOANS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Loan newComment = cursorToLoan(cursor);
        cursor.close();
        return newComment;
    }

    public int updateLoan(Loan loan) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LOAN_DATELOAN, loan.dateToString(loan.getDateLoan()));
        values.put(MySQLiteHelper.COLUMN_LOAN_DATEREMIDER, loan.dateToString(loan.getDateReminder()));
        values.put(MySQLiteHelper.COLUMN_BOOK_ID, loan.getBook_id());
        values.put(MySQLiteHelper.COLUMN_FRIEND_ID, loan.getFriend_id());


        return database.update(MySQLiteHelper.TABLE_LOANS, values, MySQLiteHelper.COLUMN_ID + " = " + loan.getId(), null);
    }

    public void deleteLoan(Loan loan) {
        long id = loan.getId();
        System.out.println("Loan deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_LOANS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Loan> getAllLoans() {
        List<Loan> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOANS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Loan loan = cursorToLoan(cursor);
            comments.add(loan);
            cursor.moveToNext();
        }

        cursor.close();
        return comments;
    }


    private Loan cursorToLoan(Cursor cursor) {
        Loan loan = new Loan();
        loan.setId(cursor.getLong(0));

        loan.setDateLoan(loan.stringToLoanDate(cursor.getString(1)));
        loan.setDateReminder(loan.stringToLoanDate(cursor.getString(2)));

        loan.setBook_id(cursor.getLong(3));
        loan.setFriend_id(cursor.getLong(4));

        cursor.close();
        return loan;
    }


}
