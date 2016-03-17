package flq.projectbooks.UI.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import flq.projectbooks.R;

/**
 * Created by flori on 28/02/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, Serializable {

    public static final String ARG_PARAM1 = "param1";

    protected Date dateLoan = null;
    protected Date dateReminder = null;
    protected Boolean dateLoanBool = true;
    protected Boolean dateReminderBool = false;
    protected TextView txtV = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        Calendar c = Calendar.getInstance();
        if (dateLoanBool && dateLoan != null) {
            c.setTime(dateLoan);
        } else if (dateReminderBool && dateReminder != null) {
            c.setTime(dateReminder);
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        //return new DatePickerDialog(getActivity(), R.style.DatePickerTheme, this, year, month, day);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        String date_s = "";
        if (dateLoanBool) {
            this.dateLoan = cal.getTime();
        }
        if (dateReminderBool) {
            this.dateReminder = cal.getTime();
        }
        date_s = dateToString(cal.getTime());
        this.txtV.setText(date_s);
    }

    public Date getDateLoan() {
        return this.dateLoan;
    }

    public void setDateLoan(TextView txtV) {
        this.dateLoanBool = true;
        this.dateReminderBool = false;
        if (txtV != null) {
            this.txtV = txtV;
        }
    }

    public Date getDateReminder() {
        return this.dateReminder;
    }

    public void setDateReminder(TextView txtV) {
        this.dateLoanBool = false;
        this.dateReminderBool = true;
        if (txtV != null) {
            this.txtV = txtV;
        }
    }

    public void initDateLoan(Date date, TextView txtV) {
        setDateLoan(txtV);
        this.dateLoan = date;
        this.txtV.setText(dateToString(date));
    }

    public void initDateReminder(Date date, TextView txtV) {
        setDateReminder(txtV);
        this.dateReminder = date;
        this.txtV.setText(dateToString(date));
    }

    public void initDateLoan(TextView txtV) {
        setDateLoan(txtV);
        this.dateLoan = Calendar.getInstance().getTime();
        this.txtV.setText(dateToString(this.dateLoan));
    }

    public void initDateReminder(TextView txtV) {
        setDateReminder(txtV);
        this.dateReminder = Calendar.getInstance().getTime();
        this.txtV.setText(dateToString(this.dateReminder));
    }

    public String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.FRANCE);


        String date_s = null;
        try {

            date_s = formatter.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }

}
