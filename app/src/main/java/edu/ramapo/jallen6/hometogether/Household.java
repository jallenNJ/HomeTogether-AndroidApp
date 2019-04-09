package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

/**
 * Activity logic for the Household Activity, which will allow for the user to get to the pantry
 * and shopping cart
 */
public class Household extends AppCompatActivity {

    public final static String ExtraHouseID = "houseID"; ///Information for the extra house id

    private String houseId; // The id of the household
    private MemberBar memberBar; //The member bar class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        Intent intent = getIntent();

        //Initialize the member cars
        houseId = intent.getStringExtra(ExtraHouseID);
        memberBar = new MemberBar((LinearLayout) findViewById(R.id.householdMemberBar));


        String url = NetworkManager.getHostAsBuilder()
                .appendPath("household")
                .appendQueryParameter("id", houseId).toString();

        //Get the household data for the selected house
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Household.this, "Received", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        NetworkManager.getInstance(this).addToRequestQueue(request);

        LinearLayout memberBar = findViewById(R.id.householdMemberBar);

    }


    /**
     * OnClick handler which changes activity to the pantry screen
     * @param v The view which was clicked
     */
    public void openPantry(View v){
        Intent intent = new Intent(this, Pantry.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        startActivity(intent);
    }

    /**
     * OnClickHandler which changes activity to the shopping screen
     * @param v The view which was clicked
     */
    public void openShoppingList(View v){
        Intent intent = new Intent(this, ShoppingList.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        startActivity(intent);
    }
}
