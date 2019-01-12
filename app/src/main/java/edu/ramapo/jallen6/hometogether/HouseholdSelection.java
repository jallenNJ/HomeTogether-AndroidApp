package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    String url =  NetworkManager.host+"/household";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_selection);

    }



    public void swapToNewHouseHoldForm(View v){
        Intent intent = new Intent(this, NewHouseHoldForm.class);
        startActivity(intent);
        finish();

    }

}
