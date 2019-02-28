package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class Pantry extends AppCompatActivity {

    private final static int FORM_CREATE_CODE = 1;
    public final static int FORM_UPDATE_CODE = 2;
    private String houseId;
    private PantryItemViewManager itemViewManager;
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
                        TableLayout table = findViewById(R.id.pantryTable);
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

    }


    public void createItem(View v){
        Intent intent = new Intent(this, PantryItemForm.class);
        startActivityForResult(intent, FORM_CREATE_CODE);
    }


    public void updateItem(View v){

        //Get selected
        //Make sure not null
        // Start form with data prefilled
        //  Apply edit
        //Update row

        PantryItemView selected = itemViewManager.getSingleSelected();
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
        intent.putExtra(PantryItemForm.CATEGORY_EXTRA, data.getFieldAsString(PantryItem.CATEGORY_FIELD));
        intent.putExtra(PantryItemForm.TAGS_EXTRA, data.getFieldAsString(PantryItem.TAG_FIELD));



        startActivityForResult(intent, FORM_UPDATE_CODE);

    }

    public void deleteItem(View v){
        PantryItemView selected = itemViewManager.getSingleSelected();
        if(selected == null){
            Toast.makeText(Pantry.this, "Please select only one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //JSONObject params = new JSONObject();
       /* try {
            params.put(PantryItem.NAME_FIELD,
                    selected.getModel().getFieldAsString(PantryItem.NAME_FIELD));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }*/


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                NetworkManager.getHostAsBuilder().appendPath("household").appendPath("pantry")
                        .appendQueryParameter(PantryItem.NAME_FIELD,
                                selected.getModel().getFieldAsString(PantryItem.NAME_FIELD))
                        .toString()
                , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // String message = "";
                        try {
                            if(response.getBoolean("status")){
                               Toast.makeText(Pantry.this, "Deleted", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(Pantry.this, "Failed to Deleted", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                      //  Toast.makeText(PantryItemForm.this, message, Toast.LENGTH_SHORT).show();
                    }
                }, NetworkManager.generateDefaultErrorHandler() );

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

                Toast.makeText(this, "Implemented activity return", Toast.LENGTH_SHORT).show();

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
                    itemViewManager.findViewByName(name).getModel().applyUpdate(result);
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
