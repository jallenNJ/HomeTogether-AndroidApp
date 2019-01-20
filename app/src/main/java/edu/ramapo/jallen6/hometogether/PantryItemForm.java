package edu.ramapo.jallen6.hometogether;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class PantryItemForm extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_item_form);


       findViewById(R.id.textView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();

                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });

    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        setDateField((Integer.toString(month) + " " + Integer.toString(day) + ", " + Integer.toString(year)));
    }

    public void setDateField(String text){
        TextView dateField = findViewById(R.id.textView5);
        if(dateField == null){
            return;
        }
        dateField.setText(text);
    }


}
