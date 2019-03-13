package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


public class PantryItemForm extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{


    public final static String UPDATE_MODE_EXTRA = "update";
    public final static String NAME_EXTRA = "name";
    public final static String QUANTITY_EXTRA="quantity";
    public final static String EXPIRE_EXTRA = "expires";
    public final static String CATEGORY_EXTRA = "category";
    public final static String TAGS_EXTRA = "tags";
    public final static String JSON_UPDATED_EXTRA = "jsonUpdated";
    public final static String JSON_NEW_EXTRA = "jsonNew";

    private boolean updateMode;

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


       String[] locations = ActiveHousehold.getInstance().getPantryLocations();
        if(locations == null){
            locations = new String[]{"unsorted"};
        }

       Spinner spinner = findViewById(R.id.testSpinner);
       ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
               R.layout.support_simple_spinner_dropdown_item, locations);
       adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
       spinner.setAdapter(adapter);

        Intent intent = getIntent();
        updateMode = intent.getBooleanExtra(UPDATE_MODE_EXTRA, false);
        if(updateMode){
            TextView nameField = (TextView)findViewById(R.id.pantryItemFormNameField);
            nameField.setText(intent.getStringExtra(NAME_EXTRA));
            nameField.setEnabled(false);
            ((TextView)findViewById(R.id.pantryItemFormQuantityField)).setText( Integer.toString(intent.getIntExtra(QUANTITY_EXTRA, 0)));
            ((TextView) findViewById(R.id.pantryItemFormCategoryField)).setText(intent.getStringExtra(CATEGORY_EXTRA));
            ((TextView) findViewById(R.id.pantryItemFormTagField)).setText(intent.getStringExtra(TAGS_EXTRA));
            String expires = intent.getStringExtra(EXPIRE_EXTRA);
            //String[] parsedExpires = expires.split(" ");
            setDateField(expires);

            ((Button) findViewById(R.id.pantryItemFormCreate)).setText("Update(DEBUG)");
        }
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


        int requestMethod = updateMode ? Request.Method.PATCH: Request.Method.PUT;

        JsonObjectRequest request = new JsonObjectRequest(requestMethod, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = updateMode ? " updated " : " created ";
                        String key = updateMode ? JSON_UPDATED_EXTRA : JSON_NEW_EXTRA;
                        String jsonKey = updateMode ? "updated": "entry";
                        Intent intent = new Intent();
                        try {

                            intent.putExtra(JSON_NEW_EXTRA,
                                    response.getJSONObject(jsonKey).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(PantryItemForm.this,
                                "Item" + message + "successfully", Toast.LENGTH_SHORT).show();

                        setResult(Activity.RESULT_OK, intent);
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String message = "";
                switch (error.networkResponse.statusCode){
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        message = "Error on field formatting";
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        message = "Session expired, please log in again";
                        break;
                    default:
                        message = "Server error";
                        break;
                }
                Toast.makeText(PantryItemForm.this, message, Toast.LENGTH_SHORT).show();
            }
        });

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
