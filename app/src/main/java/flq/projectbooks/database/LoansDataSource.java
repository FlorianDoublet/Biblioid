package flq.projectbooks.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import flq.projectbooks.data.Loan;

/**
 * Created by flori on 15/02/2016.
 */
public class LoansDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMLN_LOAN_DATELOAN,
            MySQLiteHelper.COLUMLN_LOAN_DATEREMIDER,
            MySQLiteHelper.COLUMN_BOOK_ID,
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

        values.put(MySQLiteHelper.COLUMLN_LOAN_DATELOAN, dateLoan);
        values.put(MySQLiteHelper.COLUMLN_LOAN_DATEREMIDER, dateRemider);
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

        values.put(MySQLiteHelper.COLUMLN_LOAN_DATELOAN, loan.getDateLoan().toString());
        values.put(MySQLiteHelper.COLUMLN_LOAN_DATEREMIDER, loan.getDateReminder().toString());
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
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = format.parse(cursor.getString(0));
            loan.setDateLoan(date);
            Date date2 = format.parse(cursor.getString(1));
            loan.setDateLoan(date2);
        } catch (Exception e){

        }
        loan.setBook_id(cursor.getLong(2));
        loan.setFriend_id(cursor.getLong(3));

        cursor.close();
        return loan;
    }


}
