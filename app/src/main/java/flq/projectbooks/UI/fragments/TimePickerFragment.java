package flq.projectbooks.UI.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by flori on 28/02/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    protected Date dateLoan = null;
    protected Date dateReminder = null;
    protected Boolean dateLoanBool = true;
    protected Boolean dateReminderBool = false;
    protected TextView txtV = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Calendar c = Calendar.getInstance();
        if(dateLoanBool && dateLoan!=null){
            c.setTime(dateLoan);
        } else if(dateReminderBool && dateReminder!=null){
            c.setTime(dateReminder);
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        String date_s = "";
        if(dateLoanBool){
            this.dateLoan = cal.getTime();
        }
        if(dateReminderBool){
            this.dateReminder = cal.getTime();
        }
        date_s = dateToString(cal.getTime());
        this.txtV.setText(date_s);

    }

    public Date getDateLoan(){
        return this.dateLoan;
    }

    public Date getDateReminder(){
        return this.dateReminder;
    }

    public void setDateLoan(TextView txtV){
        this.dateLoanBool = true;
        this.dateReminderBool = false;
        this.txtV = txtV;
    }

    public void setDateReminder(TextView txtV){
        this.dateLoanBool = false;
        this.dateReminderBool = true;
        this.txtV = txtV;
    }

    public void initDateLoan(Date date, TextView txtV){
        setDateLoan(txtV);
        this.dateLoan = date;
        this.txtV.setText(dateToString(date));
    }

    public void initDateReminder(Date date, TextView txtV){
        setDateReminder(txtV);
        this.dateReminder = date;
        this.txtV.setText(dateToString(date));
    }

    public void initDateLoan(TextView txtV){
        setDateLoan(txtV);
        this.dateLoan = Calendar.getInstance().getTime();
        this.txtV.setText(dateToString(this.dateLoan));
    }

    public void initDateReminder(TextView txtV){
        setDateReminder(txtV);
        this.dateReminder =  Calendar.getInstance().getTime();
        this.txtV.setText(dateToString(this.dateReminder));
    }

    public String dateToString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("H 'h' mm",  Locale.FRANCE);


        String date_s = null;
        try {

            date_s = formatter.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date_s;
    }
}