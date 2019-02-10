package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public final class ActiveHousehold {
    private static ActiveHousehold instance;
    private String id;
    private String[] members;
    private String[] pantryLocations;


    private ActiveHousehold(){
        id = null;
        members = null;
        pantryLocations = null;
    }

    public boolean isActive(){
        return id != null;
    }


    public void initFromServer(@NonNull String houseId){
        if(houseId == null) {
            return;
        }



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("household")
                        .appendQueryParameter("id", houseId)
                        .appendQueryParameter("fullData", "true").toString(),
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSONR", "DID THE FUNCTION");
                            if(response.getBoolean("status")){
                                Log.d("JSONR", response.getJSONObject("house").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                NetworkManager.generateDefaultErrorHandler());
        try {
            Log.d("JSONR", "Sending request");
            NetworkManager.getInstance().addToRequestQueue(request);
        } catch (InstantiationException e) {
            id = null;
            e.printStackTrace();
        }

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone singleton");
    }

    public static synchronized ActiveHousehold getInstance(){
        if(instance== null){
            instance = new ActiveHousehold();
        }
        return instance;
    }


}
