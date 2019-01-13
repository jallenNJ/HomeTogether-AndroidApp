package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HouseholdSelection extends AppCompatActivity {
    String url =  NetworkManager.host+"/household";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_selection);



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addCurrentHouseholds(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);


    }


    private void addCurrentHouseholds(JSONObject response){
        JSONArray data = null;
        try {
            data = response.getJSONArray("households");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        LinearLayout layout = findViewById(R.id.allHouseholdLayout);
        for(int i =0; i <data.length(); i++){
            JSONObject current;
            String houseName;
            String id;
            try {
                current = data.getJSONObject(i);
                houseName = current.getString("name");
                id = current.getString("_id");
                Toast.makeText(HouseholdSelection.this, id, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            if(id.equals("") || houseName.equals("")){
                continue;
            }

            Button newButton = new Button(this);
            newButton.setText(houseName);
            newButton.setTag(R.id.tagHouseID, id);

            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(HouseholdSelection.this, view.getTag(R.id.tagHouseID) + "was clicked", Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(newButton);

        }

    }




    public void swapToNewHouseHoldForm(View v){
        Intent intent = new Intent(this, NewHouseHoldForm.class);
        startActivity(intent);
        finish();

    }

}
