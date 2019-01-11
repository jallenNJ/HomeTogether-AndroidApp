package edu.ramapo.jallen6.hometogether;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HouseholdSelection extends AppCompatActivity {
    private RequestQueue requestQueue;
    String url =  "http://192.168.1.101:3000/household";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_selection);

        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean obj = response.getBoolean("status");
                            String msg = response.getString("message");

                            Toast.makeText(HouseholdSelection.this, ("Status is " + obj + "\n Message: " + msg), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(HouseholdSelection.this, "Server error", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        requestQueue.add(request);
    }
}
