package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

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

      /*  JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Pantry.this, "Recieved", Toast.LENGTH_SHORT).show();
                    }
                }, NetworkManager.generateDefaultErrorHandler());

        NetworkManager.getInstance(this).addToRequestQueue(request);*/

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
