package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class PantryItemForm extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_item_form);


       findViewById(R.id.pantryItemFormExpiresLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();

                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });
    }

    public void submitForm(View v){
        TextView buffer;
        int[] formIds = {R.id.pantryItemFormNameField, R.id.pantryItemFormQuantityField,
                R.id.pantryItemFormExpiresField, R.id.pantryItemFormCategoryField,
                R.id.pantryItemFormTagField};

        String[] jsonKeys = {"name", "quantity", "expires", "category", "tag"};


        JSONObject params = new JSONObject();
        for(int i = 0; i < formIds.length; i++){
            buffer = findViewById(formIds[i]);
            String fieldData = buffer.getText().toString().trim();
            if (fieldData.equalsIgnoreCase("")) {
                buffer.setError("This field can not be blank");
                return;
            }
            try{
                params.put(jsonKeys[i], fieldData);
            } catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
                return;
            }

        }


        String url = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry").toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = "Server error";
                        try {
                            if(response.getBoolean("status")){
                                Intent intent = new Intent();
                               // intent.putExtra("id", response.getString("key"));
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                                return;
                            }
                            message = response.getString("message");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(PantryItemForm.this, message, Toast.LENGTH_SHORT).show();
                }
                }, NetworkManager.generateDefaultErrorHandler() );

        NetworkManager.getInstance(this).addToRequestQueue(request);

    }




public void onDateSet(DatePicker view, int year, int month, int day){
        setDateField((Integer.toString(month) + " " + Integer.toString(day) + ", " + Integer.toString(year)));
    }

    private  void setDateField(String text){
        TextView dateField = findViewById(R.id.pantryItemFormExpiresField);
        if(dateField == null){
            return;
        }
        dateField.setText(text);
    }


}
