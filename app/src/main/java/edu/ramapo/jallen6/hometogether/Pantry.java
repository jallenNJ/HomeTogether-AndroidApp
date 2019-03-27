package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.HttpURLConnection;

public class Pantry extends AppCompatActivity implements PantryItemCrud {

    private final static int FORM_CREATE_CODE = 1;
    public final static int FORM_UPDATE_CODE = 2;
    private String houseId;
    private PantryItemViewManager itemViewManager;
    private TableLayout table;
    private Spinner searchSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        houseId = getIntent().getStringExtra(Household.ExtraHouseID);
        itemViewManager = new PantryItemViewManager();

        String url = NetworkManager.getHostAsBuilder()
                .appendPath("household")
                .appendPath("pantry")
                .appendQueryParameter("id", houseId).toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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

                        //Toast.makeText(Pantry.this, "Got" + Integer.toString(pantryItems.length()) + " items", Toast.LENGTH_SHORT).show();
                        table = findViewById(R.id.pantryTable);
                        final String[] keys = {"name", "quantity", "expires", "category"};
                        TableRow headers = new TableRow(Pantry.this);

                        for (String key : keys) {
                            TextView text = new TextView(Pantry.this);
                            text.setText(key);
                            text.setTypeface(null, Typeface.BOLD);
                            text.setLayoutParams(new TableRow.LayoutParams(1));
                            text.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            headers.addView(text);
                        }
                        table.addView(headers);
                        for(int i = 0; i < pantryItems.length(); i++){


                            //TODO: Test and make this a loop
                            try{
                                JSONObject current = pantryItems.getJSONObject(i);


                                PantryItem currentPantryItem = new PantryItem(current);
                                PantryItemView rowView = new PantryItemView(currentPantryItem, new TableRow(Pantry.this));
                                rowView.drawToRow();

                                table.addView(rowView.getDisplayRow());
                                itemViewManager.addView(rowView);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }

                    }
                }, NetworkManager.generateDefaultErrorHandler());

        NetworkManager.getInstance(this).addToRequestQueue(request);


        searchSpinner = findViewById(R.id.pantrySearchSpinner);
        ((EditText) findViewById(R.id.pantrySearchBarInput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // String searchMode = searchSpinner.getSelectedItem().toString();
                PantryItemSearchTerms term;
                switch (searchSpinner.getSelectedItem().toString().toLowerCase()){
                    case "name":
                        term = PantryItemSearchTerms.NAME_SEARCH;
                        break;
                    case "category":
                        term = PantryItemSearchTerms.CATEGORY_SEARCH;
                        break;
                    case "tag":
                        term = PantryItemSearchTerms.TAG_SEARCH;
                        break;
                    default:
                        Log.e("InvalidSpinner", "Invalid spinner entry in Pantry");
                        return;
                }

                itemViewManager.toggleVisibilityBySearch(term, charSequence.toString());
                //itemViewManager.toggleVisibiltyByNameSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public void createItem(View v){
        Intent intent = new Intent(this, PantryItemForm.class);
        startActivityForResult(intent, FORM_CREATE_CODE);
    }


    public void updateItem(View v){


        //TODO: fix selection issue when accessed via pop up menu
        AbstractItemView selected = itemViewManager.getSingleSelected();

        if(selected == null){
            Toast.makeText(Pantry.this, "Please select only one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PantryItemForm.class);
        PantryItem data = selected.getModel();
        intent.putExtra(PantryItemForm.UPDATE_MODE_EXTRA, true);
        intent.putExtra(PantryItemForm.NAME_EXTRA, data.getFieldAsString(PantryItem.NAME_FIELD));
        intent.putExtra(PantryItemForm.QUANTITY_EXTRA, data.getQuantity());
        intent.putExtra(PantryItemForm.EXPIRE_EXTRA, data.getFieldAsString(PantryItem.EXPIRES_FIELD));
        intent.putExtra(PantryItemForm.CATEGORY_EXTRA, data.getFieldAsString(PantryItem.CATEGORY_FIELD));
        intent.putExtra(PantryItemForm.TAGS_EXTRA, data.getFieldAsString(PantryItem.TAG_FIELD));



        startActivityForResult(intent, FORM_UPDATE_CODE);

    }

    public void deleteItem(View v){

        try{
            itemViewManager.deleteSelectedFromServer(this);
        } catch (IllegalStateException e){
            Toast.makeText(Pantry.this, "Please select only one item",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void moveItem(@NonNull final AbstractItemView v,@NonNull String newLoc) {
        String[] jsonKeys = {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.EXPIRES_FIELD, PantryItem.CATEGORY_FIELD, PantryItem.TAG_FIELD};


        PantryItem item = v.getModel();
        JSONObject params = new JSONObject();
        for (String jsonKey : jsonKeys) {
            String fieldData = item.getFieldAsString(jsonKey);
            try {
                params.put( jsonKey, fieldData);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fieldData.equalsIgnoreCase("")) { //Should never occur if db data is good...
                Log.e("Invalid model", "Model missing required field");
                Toast.makeText(this, "Error, please use the form", Toast.LENGTH_SHORT).show();
                return;
            }


        }


        //TODO: validation on newLoc
        try {
            params.put( PantryItem.LOCATION_FIELD, newLoc);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry").toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //v.getModel().applyUpdate(response); //Should be deleted and on needed
                        itemViewManager.delete(v);
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
                Toast.makeText(Pantry.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case FORM_CREATE_CODE:
                if(resultCode != RESULT_OK){
                    Log.e("Result error",
                            "Result code of  " + Integer.toString(resultCode)
                                    + " in Pantry Create");
                    return;
                }


                try {
                    JSONObject result = new JSONObject(data.getStringExtra(PantryItemForm.JSON_NEW_EXTRA));
                    TableRow row = new TableRow(Pantry.this);
                    table.addView(row);
                    itemViewManager.addView(new PantryItemView(new PantryItem(result), row));
                    //String name = result.getString(PantryItem.NAME_FIELD);
                    //itemViewManager.findViewByName(name).getModel().applyUpdate(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    Log.e("DataNotFound", "Received update to a non existent view" );
                    e.printStackTrace();
                }

                break;

            case FORM_UPDATE_CODE:
                if(resultCode != RESULT_OK){
                    Log.e("Result error",
                            "Result code of  " + Integer.toString(resultCode)
                                    + " in Pantry Update");
                    return;
                }
               // Toast.makeText(this, "Implement redraw on update", Toast.LENGTH_SHORT).show();
               // itemViewManager.drawAll();
                //Log.i("Optimize", "Make pantry update only draw updated row instead of all");
                try {
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
