package edu.ramapo.jallen6.hometogether;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Dialog for creating the date picker prompt
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        if(!(getActivity() instanceof DatePickerDialog.OnDateSetListener)){
            Log.e("No Instance", "Date picker fragment called by a class that did not implement DatePickerDialog.OnDateSetListener");
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), ((DatePickerDialog.OnDateSetListener) getActivity()), year, month, day);
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.e("No Instance", "Date picker fragment called by a class that did not implement DatePickerDialog.OnDateSetListener");

    }
}