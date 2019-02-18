package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for caching the data of the user's currently selected household
 * and allows for quick look-ups of data.
 *
 */
public final class ActiveHousehold {
    private static ActiveHousehold instance;
    private String id;
    private String name;
    private String[] members;
    private String[] pantryLocations;


    private ActiveHousehold(){
        resetObject();

    }

    /**
     * This function sets all member functions of the object to null to clear the object for reuse
     * or when an error occurs.
     */
    private void resetObject(){
        id = null;
        name = null;
        members = null;
        pantryLocations = null;
    }

    /**
     * This function returns if the object is currently initialized to a house
     * @return true if id != null, or false if id is null
     */
    public boolean isActive(){
        return id != null;
    }

    public int getMembersSize(){
        if(isActive()){
            return members.length;
        } else{
            return 0;
        }
    }

    public String getMemberId(int index){
        if(isActive()){
            if(index < getMembersSize() && index > 0){
                return members[index];
            }
            return "";
        }
        return null;
    }


    public String[] getPantryLocations(){
        return pantryLocations;
    }

    /**
     * Has the object send a request to the server to get the data for the household
     * @param houseId The household to query the server for. Must be nonnull
     */
    public void initFromServer(@NonNull String houseId){
        if(houseId == null || houseId.equals("")) {
            return;
        }


        //Build the request to the get route of the household to query all cacheable data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("household")
                        .appendQueryParameter("id", houseId)
                        .appendQueryParameter("activeData", "true").toString(),
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getBoolean("status")){ //If server reported success
                                Log.d("JSONR", response.getJSONObject("house").toString());
                                //Get the house object from the response
                                JSONObject household = response.getJSONObject("house");

                                //Store the data in the appropriate member fields
                                name = household.getString("name");
                                pantryLocations =  JSONFormatter.JSONArrayToStringArray(
                                        household.getJSONArray("pantryLocations"));
                                members = JSONFormatter.JSONArrayToStringArray(
                                        household.getJSONArray("members"));


                            } else{ //Server rejected our request
                            Log.d("JSONFailed", "Response returned status false"
                                    + response.toString());
                            }
                        } catch (JSONException e) { //Most likely a mal formed JSON object
                            resetObject();
                            Log.d("JSONFailedToParse", "Activehousehold got an invalid json Object");
                            e.printStackTrace();
                        }
                    }
                },
                NetworkManager.generateDefaultErrorHandler());
        try {
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

    /**
     * Gets the singleton in memory. If it doesn't exist it will create it
     * @return The reference to the ActiveHousehold singleton
     */
    public static synchronized ActiveHousehold getInstance(){
        if(instance== null){
            instance = new ActiveHousehold();
        }
        return instance;
    }
}
