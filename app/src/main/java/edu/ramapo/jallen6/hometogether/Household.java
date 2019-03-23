package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

public class Household extends AppCompatActivity {
    public final static String ExtraHouseID = "houseID";


   // private final String url = NetworkManager.host+"/household";
    private String houseId;
    private MemberBar memberBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        Intent intent = getIntent();

        houseId = intent.getStringExtra(ExtraHouseID);
        memberBar = new MemberBar((LinearLayout) findViewById(R.id.householdMemberBar));


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




        LinearLayout memberBar = findViewById(R.id.householdMemberBar);

        //ActiveHousehold.getInstance().g
    }


    public void openPantry(View v){
        Intent intent = new Intent(this, Pantry.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        startActivity(intent);
    }

    public void openShoppingList(View v){
        Intent intent = new Intent(this, ShoppingList.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        startActivity(intent);
    }
}
