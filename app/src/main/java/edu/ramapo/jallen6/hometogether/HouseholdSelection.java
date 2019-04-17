package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

/**
 * Activity Logic for the Household Selection Screen
 */
public class HouseholdSelection extends AppCompatActivity {
    private final int NEW_HOUSEHOLD = 1; //Activity Result Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_selection);


        TextView greetingLabel = findViewById(R.id.householdSelectionGreetingLabel);
        greetingLabel.setText(String.format("%s %s!", greetingLabel.getText(),
                MemberBar.getUsername()));
        //Send request to get all the households the user is a member of
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("household").toString()
                , null,
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



        //Bind the gesture
        final GestureDetector gestureDetector = new GestureDetector(this,
                SwipeGestureFactory.build(SwipeGestureFactory.SwipeGestureFactoryType.VERTICAL,
                        new SwipeHandler() {
                            @Override
                            public boolean onSwipe() {
                                swapToNewHouseHoldForm();
                                return false;
                            }
                        },
                        null));

        findViewById(R.id.householdSelectionParentView).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


    }


    /**
     * Callback function to add all the found household names to the list to be selected
     * @param response The response object to get the data from
     */
    private void addCurrentHouseholds(JSONObject response){
        JSONArray data = null;
        //Get the data from the Object, and return if it doesn't exist
        try {
            data = response.getJSONArray("households");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        LinearLayout layout = findViewById(R.id.allHouseholdLayout);

        //For every item, create a button with the name of the house as the text, and the
        // id as the tag
        for(int i =0; i <data.length(); i++){
            JSONObject current;
            String houseName;
            String id;
            //Get the fields from the JSONObject
            try {
                current = data.getJSONObject(i);
                houseName = current.getString("name");
                id = current.getString("_id");
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            //Ensure it contains content
            if(id.equals("") || houseName.equals("")){
                continue;
            }

            //Create the button
            Button newButton = new Button(this);
            newButton.setText(houseName);
            newButton.setTag(R.id.tagHouseID, id);

            //Add the onclick, which goes to the household activity and caches house data
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HouseholdSelection.this, Household.class);
                    //TODO: Pass in household name
                    intent.putExtra(Household.ExtraHouseID, view.getTag(R.id.tagHouseID).toString());
                    ActiveHousehold.getInstance().initFromServer(view.getTag(R.id.tagHouseID).toString());
                    startActivity(intent);
                    finish();
                }
            });
            layout.addView(newButton);

        }

    }


    /**
     * Swap to activity to make a new household
     */
    public void swapToNewHouseHoldForm(){
        Intent intent = new Intent(this, NewHouseHoldForm.class);
        startActivityForResult(intent, NEW_HOUSEHOLD);

    }

    /**
     * Handler for when a called activity returns
     * @param requestCode The code it was called with
     * @param resultCode The result from its operation
     * @param data Any data it handed back
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_HOUSEHOLD) {
            if (resultCode == RESULT_OK) {
                this.finish();
            } else{
                Log.e("Activity Result Failure", "New household form reported error");
            }
        }
    }

}
