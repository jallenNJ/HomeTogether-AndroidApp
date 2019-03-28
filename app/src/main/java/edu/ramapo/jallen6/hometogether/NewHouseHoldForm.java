package edu.ramapo.jallen6.hometogether;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity Logic for the NewHouseholdForm activity, which is house a new household can be created
 *  by a user
 */
public class NewHouseHoldForm extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house_hold_form);
    }


    /**
     * On Click Handler to get the field data and submit the form
     * @param v The view which was clicked
     */
    public void createHouseHold(View v){

        //Get the data from the form, and return if it fails
        JSONObject params = null;
        try {
            params = readForm();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(params == null){
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                NetworkManager.getHostAsBuilder().appendPath("household").toString(),
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(NewHouseHoldForm.this, "Answer received", Toast.LENGTH_SHORT).show();
                        //TODO: Make response to parent to swap to new button
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);

    }

    /**
     * Read the user form and put it into a JSONObject
     * @return A JSONObject consisting of the form fields
     * @throws JSONException A JSON Exception related to creating the object
     */
    public JSONObject readForm() throws JSONException {
        JSONObject params = new JSONObject();

        String name =  ((TextView)findViewById(R.id.newHouseHoldNameField)).getText().toString();
        if(name.equals("")){
            return null;
        }
        params.put("name",name);
        return params;
    }
}
