package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity Logic for the Pantry Class which contains the logic for showing and changing all objects
 * In addition, implements the PantryItemCrud interface which allows for the pantry items to be
 * able to call functions in the activty
 */
public class Pantry extends AppCompatActivity implements PantryItemCrud {

    public final static String LOAD_CATEGORY = "catLoad";
    private final static int LOAD_PANTRY = 0; //Private as only used in default value
    public final static int LOAD_FRIDGE =1;
    public final static int LOAD_FREEZER =2;

    private final static int FORM_CREATE_CODE = 1; ///Activity Return Code for creating
    private final static int FORM_UPDATE_CODE = 2; ///Activity Return Code for updating

    private String houseId; //The household id being view
    private PantryItemViewManager itemViewManager; //The item view manager
    private TableLayout table; //The table to display the data
    private Spinner searchSpinner; //The spinner for where to search
    private Spinner locationSpinner; //The spinner for the pantry locations for search
    private Spinner categorySpinner; //The spinner for the pantry categories for search


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        //Get the intent and create the view manager
        houseId = getIntent().getStringExtra(Household.ExtraHouseID);
        itemViewManager = new PantryItemViewManager();

        String url = NetworkManager.getHostAsBuilder()
                .appendPath("household")
                .appendPath("pantry")
                .appendQueryParameter("id", houseId).toString();

        //Request to get all the items and build the table
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Get the items
                        JSONArray pantryItems = new JSONArray();
                        try{
                            if(!response.getBoolean("status")){
                                Toast.makeText(Pantry.this, "Failed to retrieve pantry items",
                                        Toast.LENGTH_SHORT).show();
                            }


                            pantryItems = response.getJSONArray("pantry");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Add them to the table
                        table = findViewById(R.id.pantryTable);
                        final String[] keys = {"name", "quantity", "expires", "category"};
                        TableRow headers = new TableRow(Pantry.this);

                        //Create the table header
                        for (String key : keys) {
                            TextView text = new TextView(Pantry.this);
                            text.setText(key);
                            text.setTypeface(null, Typeface.BOLD);
                            text.setLayoutParams(new TableRow.LayoutParams(1));
                            text.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            text.setTextColor(ContextCompat.getColor(Pantry.this, R.color.fontColor));
                            text.setShadowLayer(1, 5,5,
                                    ContextCompat.getColor(Pantry.this, R.color.shadowColor));
                            headers.addView(text);
                        }
                        table.addView(headers);

                        //Add all the table contents
                        for(int i = 0; i < pantryItems.length(); i++){

                            try{
                                JSONObject current = pantryItems.getJSONObject(i);


                                //Create Model, view, draw, and add to manager
                                PantryItem currentPantryItem = new PantryItem(current);
                                PantryItemView rowView = new PantryItemView(currentPantryItem,
                                        new TableRow(Pantry.this));
                                rowView.drawToRow();

                                table.addView(rowView.getDisplayRow());
                                itemViewManager.addView(rowView);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                            int catToLoad = getIntent().getIntExtra(LOAD_CATEGORY, LOAD_PANTRY);

                            if(catToLoad != LOAD_PANTRY){
                                searchSpinner.setSelection(2,true);
                                locationSpinner.setSelection(catToLoad,true);
                            }

                        }

                    }
                }, NetworkManager.generateDefaultErrorHandler());

        NetworkManager.getInstance(this).addToRequestQueue(request);


        //Store the reference to the search spinners type
        searchSpinner = findViewById(R.id.pantrySearchSpinner);
        locationSpinner = findViewById(R.id.pantrySearchLocationSpinner);
        categorySpinner = findViewById(R.id.pantrySearchCategorySpinner);

        //Set the locations for the location spinner from the cache
        //TODO: Update on cache update?
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, ActiveHousehold.getInstance().getPantryLocations());
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemViewManager.setAllVisible();
                if(searchSpinner.getSelectedItem().toString().equals("Location")){
                    findViewById(R.id.pantrySearchBarInput).setVisibility(View.GONE);
                    locationSpinner.setVisibility(View.VISIBLE);
                    categorySpinner.setVisibility(View.GONE);
                } else if(searchSpinner.getSelectedItem().toString().equals("Category")){
                    locationSpinner.setVisibility(View.GONE);
                    categorySpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.pantrySearchBarInput).setVisibility(View.GONE);
                }
                else{
                    findViewById(R.id.pantrySearchBarInput).setVisibility(View.VISIBLE);
                    categorySpinner.setVisibility(View.GONE);
                    locationSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemViewManager.toggleVisibilityBySearch(PantryItemSearchTerms.LOCATION_SEARCH,
                        locationSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemViewManager.toggleVisibilityBySearch(PantryItemSearchTerms.CATEGORY_SEARCH,
                        categorySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ((EditText) findViewById(R.id.pantrySearchBarInput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             *  Call search when text changes
             * @param charSequence unused
             * @param i unused
             * @param i1 unused
             * @param i2 unused
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Get the search type from the spinner
                PantryItemSearchTerms term;
                switch (searchSpinner.getSelectedItem().toString().toLowerCase()){
                    case "name":
                        term = PantryItemSearchTerms.NAME_SEARCH;
                        break;
                    default:
                        Log.e("InvalidSpinner", "Invalid spinner entry in Pantry");
                        return;
                }

                //Run the search wit the current tag
                itemViewManager.toggleVisibilityBySearch(term, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }


    /**
     * On click handler for user wanted to create a new item
     * @param v The view which was clicked
     */
    public void createItem(View v){
        Intent intent = new Intent(this, PantryItemForm.class);
        startActivityForResult(intent, FORM_CREATE_CODE);
    }


    /**
     * On click handler for user that wants to update an existing item
     * @param v The view which was clicked
     */
    public void updateItem(View v){

        //TODO: fix selection issue when accessed via pop up menu (IE, not clearing previous
        AbstractItemView selected = itemViewManager.getSingleSelected();

        if(selected == null){
            Toast.makeText(Pantry.this, "Please select only one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Add all the required information to the intent
        //TODO: GET JSON object and pass as a toString?
        Intent intent = new Intent(this, PantryItemForm.class);
        PantryItem data = selected.getModel();
        intent.putExtra(PantryItemForm.UPDATE_MODE_EXTRA, true);
        intent.putExtra(PantryItemForm.NAME_EXTRA, data.getFieldAsString(PantryItem.NAME_FIELD));
        intent.putExtra(PantryItemForm.QUANTITY_EXTRA, data.getQuantity());
        intent.putExtra(PantryItemForm.EXPIRE_EXTRA, data.getFieldAsString(PantryItem.EXPIRES_FIELD));
        intent.putExtra(PantryItemForm.CATEGORY_EXTRA, data.getFieldAsString(PantryItem.CATEGORY_FIELD));

        startActivityForResult(intent, FORM_UPDATE_CODE);

    }

    /**
     * On click handler for deleteing an item
     * @param v View which was clicked
     */
    public void deleteItem(View v){

        try{
            itemViewManager.deleteSelectedFromServer(this);
        } catch (IllegalStateException e){
            Toast.makeText(Pantry.this, "Please select only one item",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Function to move an existing item to a different location in the pantry
     * @param v The AbstractItemView being moved
     * @param newLoc The location it is being moved too
     */
    @Override
    public void moveItem(@NonNull final AbstractItemView v,@NonNull String newLoc) {
        try {
            itemViewManager.moveItem(v, newLoc, this);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to move item", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handler for creation/update activities
     * @param requestCode The code the activity was called with
     * @param resultCode The response code from the object
     * @param data The data it passed back (if applicable)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case FORM_CREATE_CODE: //Object being created
                if(resultCode != RESULT_OK){ //If failed
                    Log.e("Result error",
                            "Result code of  " + Integer.toString(resultCode)
                                    + " in Pantry Create");
                    return;
                }


                try {
                    //Get the result from the activity, and add it to the table
                    JSONObject result = new JSONObject(data.getStringExtra(PantryItemForm.JSON_NEW_EXTRA));
                    TableRow row = new TableRow(Pantry.this);
                    table.addView(row);
                    itemViewManager.addView(new PantryItemView(new PantryItem(result), row));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    Log.e("DataNotFound", "Received update to a non existent view" );
                    e.printStackTrace();
                }

                break;

            case FORM_UPDATE_CODE: //After an update
                if(resultCode != RESULT_OK){ //If it failed
                    Log.e("Result error",
                            "Result code of  " + Integer.toString(resultCode)
                                    + " in Pantry Update");
                    return;
                }
                try {
                    //Get the result from the activity, find the view, and apply the update with a
                    // redraw
                    JSONObject result = new JSONObject(data.getStringExtra(PantryItemForm.JSON_UPDATED_EXTRA));
                    String name = result.getString(PantryItem.NAME_FIELD);
                    AbstractItemView updated = itemViewManager.findViewByName(name);
                    updated.getModel().applyUpdate(result);
                    updated.drawToRow();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    Log.e("DataNotFound", "Received update to a non existent view" );
                    e.printStackTrace();
                }
                break;
            default:
                Log.e("Invalid request code",
                        "Invalid request code of " + Integer.toString(requestCode)
                                + " in Pantry");
                return;
        }
    }
}
