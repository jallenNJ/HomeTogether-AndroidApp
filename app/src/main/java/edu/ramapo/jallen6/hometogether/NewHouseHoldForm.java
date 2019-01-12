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

public class NewHouseHoldForm extends AppCompatActivity {

    final String url = NetworkManager.host + "/household";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_house_hold_form);
    }




    public void createHouseHold(View v){

        JSONObject params = null;
        try {
            params = readForm();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(params == null){
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(NewHouseHoldForm.this, "Answer recieved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);

    }

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
