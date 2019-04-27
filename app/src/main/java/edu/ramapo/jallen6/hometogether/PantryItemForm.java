package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;

/**
 * Activity Logic for the the Pantry Item form, which can do updates and creationms
 */
public class PantryItemForm extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{


    //Extra fields define
    public final static String UPDATE_MODE_EXTRA = "update"; /// Update field extra define
    public final static String NAME_EXTRA = "name"; /// Name field extra define
    public final static String QUANTITY_EXTRA="quantity"; /// Quantity field extra define
    public final static String EXPIRE_EXTRA = "expires"; /// Expires field extra define
    public final static String CATEGORY_EXTRA = "category"; ///Category field extra define
    public final static String JSON_UPDATED_EXTRA = "jsonUpdated"; /// JSON Updated field define extra
    public final static String JSON_NEW_EXTRA = "jsonNew"; /// JSON New field defien extra

    private boolean updateMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_item_form);


        //Create the date picker fragment
       findViewById(R.id.pantryItemFormExpiresLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });

        SwipeGestureFactory.buildAndBindDetector( findViewById(R.id.pantryItemFormExpiresLayout),
                SwipeGestureFactory.SwipeGestureFactoryType.HORIZONTAL,
                null,
                new SwipeHandler() {
                    @Override
                    public boolean onSwipe() {
                        TextView view = findViewById(R.id.pantryItemFormExpiresField);
                        view.setText(R.string.NeverExipiresString);
                        view.setTag(PantryItem.NEVER_EXPIRE);
                        return true;
                    }});

       //Get the pantry location from the cache
       String[] locations = ActiveHousehold.getInstance().getPantryLocations();
        if(locations == null){
            locations = new String[]{"unsorted"};
        }

        //Load the text into the spinner
       final Spinner locSpinner = findViewById(R.id.pantryItemFormLocationSpinner);
       ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
               R.layout.support_simple_spinner_dropdown_item, locations);
       adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
       locSpinner.setAdapter(adapter);



       Spinner catSpinner = findViewById(R.id.pantryItemFormCategorySpinner);
       //To be used in inner class
        final String[] finalLocations = locations;
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               PantryItemCategoryAssociationsManager cache = PantryItemCategoryAssociationsManager.getInstance();
               String targetLoc = cache.getDefaultLocation(adapterView.getSelectedItem().toString());
               boolean set = false;
               for(int j = 0; j < finalLocations.length; j++){
                   if(finalLocations[j].toLowerCase().equals(targetLoc)){
                       set = true;
                       locSpinner.setSelection(j);
                       break;
                   }
               }
               if(!set){
                   throw new IllegalStateException("Did not find location in resolve");
               }
               String specialFieldName = cache.getSpecialFieldName(adapterView.getSelectedItem().toString());
               if(specialFieldName != null){
                   findViewById(R.id.pantryItemFormSpecialLayout).setVisibility(View.VISIBLE);
                   ((TextView) findViewById(R.id.pantryItemFormSpecialLabel)).setText(specialFieldName);
               }else{
                   findViewById(R.id.pantryItemFormSpecialLayout).setVisibility(View.GONE);
                   ((TextView) findViewById(R.id.pantryItemFormSpecialLabel)).setText("");
               }


           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {
                return;
           }
       });

       //Get the data from the intent
        Intent intent = getIntent();
        updateMode = intent.getBooleanExtra(UPDATE_MODE_EXTRA, false);
        if(updateMode){ //If updating an existing item
            //Prefill the form

            TextView nameField = (TextView)findViewById(R.id.pantryItemFormNameField);
            nameField.setText(intent.getStringExtra(NAME_EXTRA));
            nameField.setEnabled(false); //Name cannot be editted
            ((TextView)findViewById(R.id.pantryItemFormQuantityField)).setText( Integer.toString(intent.getIntExtra(QUANTITY_EXTRA, 0)));
            String category = intent.getStringExtra(CATEGORY_EXTRA).toLowerCase();
            String [] catSpinnerItems = getResources().getStringArray(R.array.pantryCategory);
            boolean found = false;
            for(int i =0; i < catSpinnerItems.length; i++){
                if(catSpinnerItems[i].toLowerCase().equals(category)){
                    found = true;
                    catSpinner.setSelection(i);
                }
            }
            //If not found, set to other
            if(!found){
                catSpinner.setSelection(catSpinnerItems.length-1);
            }
            String expires = intent.getStringExtra(EXPIRE_EXTRA);
            //String[] parsedExpires = expires.split(" ");
            setDateField(expires);

            ((Button) findViewById(R.id.pantryItemFormCreate)).setText("Update(DEBUG)");
        }
    }


    //TODO: Make name changeable by deleting old and creating new
    /**
     * On click handler to submit the form to the server regardless of if its an update or creation
     * @param v The view which was clicked
     */
    public void submitForm(View v){
        TextView buffer;
        //All the field ids to check
        int[] formIds = {R.id.pantryItemFormNameField, R.id.pantryItemFormQuantityField,
                R.id.pantryItemFormExpiresField /*, R.id.pantryItemFormCategoryField,
                R.id.pantryItemFormTagField*/};

        //And their associated keys
        String[] jsonKeys = {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.EXPIRES_FIELD};


        //TODO: Move this to JSON formatter?
        //For all the fields, load the key with the object
        JSONObject params = new JSONObject();
        for(int i = 0; i < formIds.length; i++){
            buffer = findViewById(formIds[i]);
            String fieldData = formIds[i] == R.id.pantryItemFormExpiresField ?
                    buffer.getTag().toString().trim()
                    : buffer.getText().toString().trim();

            //buffer.getText().toString().trim();
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

        //Get the spinner field separately
        try{
            params.put(PantryItem.CATEGORY_FIELD, ((Spinner)findViewById(R.id.pantryItemFormCategorySpinner)).getSelectedItem().toString());
            params.put(PantryItem.LOCATION_FIELD, ((Spinner)findViewById(R.id.pantryItemFormLocationSpinner)).getSelectedItem().toString());
        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry").toString();

        //Set request method. Patch if update, put if create
        int requestMethod = updateMode ? Request.Method.PATCH: Request.Method.PUT;

        JsonObjectRequest request = new JsonObjectRequest(requestMethod, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Create the message string based on if its update or create
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
                    case HttpURLConnection.HTTP_CONFLICT:
                        message = "Duplicate item already exists";
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


    /**
     * Formats the date based on what was inputted in form MM DD, YYYY
     * @param view The view which created this
     * @param year The inputted year
     * @param month The zero-indexed month
     * @param day The one-indexed day
     */
    public void onDateSet(DatePicker view, int year, int month, int day){
        setDateField((Integer.toString(month) + " " + Integer.toString(day) + ", " + Integer.toString(year)));
    }

    /**
     * Applies the formatted date to the text view and raw date to the tag
     * @param text The text to apply
     */
    private  void setDateField(String text){
        TextView dateField = findViewById(R.id.pantryItemFormExpiresField);
        if(dateField == null){
            return;
        }
        if(text.equals(PantryItem.NEVER_EXPIRE)){
            dateField.setText("Never Expires");
        } else{
            dateField.setText(PantryItem.formatPantryDate(text));
        }

        dateField.setTag(text);
    }


}
