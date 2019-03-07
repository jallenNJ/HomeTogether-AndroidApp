package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

/**
 * This class is responsible for caching the data of the user's currently selected household
 * and allows for quick look-ups of data.
 *
 */
//TODO: Handle failed network request not clearing flag
public final class ActiveHousehold extends Observable {
    private static ActiveHousehold instance;
    private String id;
    private String name;
    private String[] memberIds;
    private String[] memberNames;
    private String[] pantryLocations;


    private boolean pendingNetworkRequest;

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
        memberIds = null;
        memberNames = null;
        pantryLocations = null;
        pendingNetworkRequest = false;
    }

    /**
     * This function returns if the object is currently initialized to a house
     * @return true if there is an id and no outgoing network requests. False otherwise
     */
    public boolean isActive(){
        return id != null && !pendingNetworkRequest;
    }

    public int getMembersSize(){
        if(isActive()){
            return memberIds.length;
        } else{
            return 0;
        }
    }

    public String getMemberId(int index){
        if(isActive()){
            if(index < getMembersSize() && index >= 0){
                return memberIds[index];
            }
            return "";
        }
        return null;
    }

    public String getMemberName(int index){
        if(isActive()){
            if(index < memberNames.length && index >= 0){
                return memberNames[index];
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
    public void initFromServer(@NonNull final String houseId){
        if(houseId == null || houseId.equals("")) {
            return;
        }

        pendingNetworkRequest = true;
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
                                id = household.getString("_id");
                                name = household.getString("name");
                                pantryLocations =  JSONFormatter.JSONArrayToStringArray(
                                        household.getJSONArray("pantryLocations"));
                                memberIds = JSONFormatter.JSONArrayToStringArray(
                                        household.getJSONArray("members"));
                                resolveMemberIds();

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
            pendingNetworkRequest = false;
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

    public void refresh(){
        //TODO: Clear?
        initFromServer(id);
    }


    private void resolveMemberIds(){
        if(memberIds == null || memberIds.length == 0){
            pendingNetworkRequest = false;
            return;
        }

        StringBuilder idString = new StringBuilder();
        for(String id:memberIds){
            idString.append(id);
            idString.append(",");
        }
        idString.deleteCharAt(idString.length()-1);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("users").appendQueryParameter("resolveIds", idString.toString()).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

                                JSONArray users = response.getJSONArray("names");
                                if(memberIds.length != users.length()){
                                    throw new JSONException("Recieved data does not match requested size");
                                }
                                if(memberNames == null || memberNames.length != memberIds.length){
                                    memberNames = new String[memberIds.length];
                                }

                                for(int i =0; i < users.length(); i++){
                                    JSONObject currentUser = users.getJSONObject(i);
                                    //TODO: Confirm order is based on specifed order
                                    memberNames[i] = currentUser.getString("user");
                                }


                                pendingNetworkRequest = false;
                                ActiveHousehold.getInstance().setChanged();
                                ActiveHousehold.getInstance().notifyObservers();

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            pendingNetworkRequest = false;
                        }
                    }
                },
                NetworkManager.generateDefaultErrorHandler());

        //Send
        try {
            NetworkManager.getInstance().addToRequestQueue(request);
        } catch (InstantiationException e) {
            id=null;
            pendingNetworkRequest = false;
            e.printStackTrace();
        }
    }
}
