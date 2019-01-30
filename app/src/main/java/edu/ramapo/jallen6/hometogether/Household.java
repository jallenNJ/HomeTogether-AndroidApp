package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class Household extends AppCompatActivity {
    public final static String ExtraHouseID = "houseID";


   // private final String url = NetworkManager.host+"/household";
    private String houseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        Intent intent = getIntent();

        houseId = intent.getStringExtra(ExtraHouseID);





        String url = NetworkManager.getHostAsBuilder()
                .appendPath("household")
                .appendQueryParameter("id", houseId).toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Household.this, "Recieved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);


    }


    public void openPantry(View v){
        Intent intent = new Intent(this, Pantry.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        startActivity(intent);
    }
}
