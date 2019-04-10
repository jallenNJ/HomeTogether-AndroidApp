package edu.ramapo.jallen6.hometogether;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
    @SuppressLint("ClickableViewAccessibility")
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

        class HouseholdGesture extends GestureDetector.SimpleOnGestureListener {

            //Constants to check distance
            private static final int SWIPE_MIN_DISTANCE = 120;
            private static final int SWIPE_MAX_OFF_PATH = 250;
            private static final int SWIPE_THRESHOLD_VELOCITY = 200;

            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    //Ensure they didn't go too far off path
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                        return false;
                    }
                    //left to right swipe
                    if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        openShoppingList(findViewById(R.id.pantryGraphicsRoot));
                        return true;
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

        }

        //Bind the above class
        final GestureDetector gestureDetector = new GestureDetector(this, new HouseholdGesture());

        ((ViewGroup)findViewById(R.id.pantryGraphicsRoot).getParent()).setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }


    /**
     * OnClick handler which changes activity to the pantry screen
     * @param v The view which was clicked
     */
    public void openPantry(View v){
        Intent intent = new Intent(this, Pantry.class);
        intent.putExtra(Household.ExtraHouseID, houseId);
        if(v != null){
            switch(v.getId()){
                case R.id.householdFridgeImage:
                    intent.putExtra(Pantry.LOAD_CATEGORY, Pantry.LOAD_FRIDGE);
                    break;
                case R.id.householdFreezerImage:
                    intent.putExtra(Pantry.LOAD_CATEGORY, Pantry.LOAD_FREEZER);
                    break;
            }
        }
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
