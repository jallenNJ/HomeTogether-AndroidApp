package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
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
    private String houseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        houseId = getIntent().getStringExtra(Household.ExtraHouseID);

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
                            TableRow row = new TableRow(Pantry.this);

                            //TODO: Test and make this a loop
                            try{
                                JSONObject current = pantryItems.getJSONObject(i);

                                TextView buffer;
                                for(int j =0; j< keys.length;j++ ){
                                    buffer = new TextView(Pantry.this);
                                    buffer.setText(current.getString(keys[j]));
                                    buffer.setLayoutParams(new TableRow.LayoutParams(1));
                                    buffer.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    row.addView(buffer);
                                }
                                table.addView(row);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row = null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case FORM_CREATE_CODE:
                if(resultCode != RESULT_OK){
                    Log.e("Result error",
                            "Result code of  " + Integer.toString(resultCode)
                                    + " in Pantry");
                    return;
                }

                Toast.makeText(this, "Implemented activity return", Toast.LENGTH_SHORT).show();


                break;
            default:
                Log.e("Invalid request code",
                        "Invalid request code of " + Integer.toString(requestCode)
                                + " in Pantry");
                return;
        }
    }


}
