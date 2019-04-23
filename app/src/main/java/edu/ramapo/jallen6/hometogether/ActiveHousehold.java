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

    public static final String SHOPPING_LOCATION = "shopping"; ///The name of the shopping location


    private static ActiveHousehold instance; /// The instance of the singleton
    private String id; //The id of the active household
    private String name; //The localized name of the household
    private String[] memberIds; //The ids of the members in the house
    private UserInfo[] memberInfo; //An array of the userInfo objects
    private String[] pantryLocations; //The string localized names in the pantry

    private boolean pendingNetworkRequest; //Bool for if there is a network request

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
        memberInfo = null;
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

    /**
     *  The amount of members in the household
     * @return Length of the memberIds if active, 0 otherwise
     */
    public int getMembersSize(){
        if(isActive()){
            return memberIds.length;
        } else{
            return 0;
        }
    }

    /**
     *  Get the member id if house is active
     * @param index The index to search for
     * @return The ID if house is active and index valid, empty string if active but invalid index, null otherwise
     *
     */
    public String getMemberId(int index){
        if(isActive()){
            if(index < getMembersSize() && index >= 0){
                return memberIds[index];
            }
            return "";
        }
        return null;
    }

    /**
     *  Get the member name if house is active
     * @param index The index to search for
     * @return The name if member is active and index valid, null if the name is not active
     *
     */
    public String getMemberName(int index) throws ArrayIndexOutOfBoundsException {

        UserInfo user = getMemberInfo(index);
        return user != null? user.getName(): null;


    }

    public UserInfo getMemberInfo (int index){
        if(isActive()){
            if(index < memberInfo.length && index >=0){
                return memberInfo[index];
            }
            throw new ArrayIndexOutOfBoundsException("Index is not in valid range");
        }
        return null;
    }

    /**
     * Get all pantry locations
     * @return The array of pantry locations
     */
    public String[] getPantryLocations(){
        return pantryLocations;
    }

    /**
     * Has the object send a request to the server to get the data for the household
     * @param houseId The household to query the server for. Must be nonnull
     */
    public void initFromServer(@NonNull final String houseId){
        if(houseId.equals("")) {
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

                        } catch (JSONException e) { //Most likely a mal formed JSON object
                            resetObject();
                            Log.d("JSONFailedToParse",
                                    "Activehousehold got an invalid json Object");
                            e.printStackTrace();
                        }
                    }
                },
                NetworkManager.generateDefaultErrorHandler())
                ;
        try {
            NetworkManager.getInstance().addToRequestQueue(request);
        } catch (InstantiationException e) {
            id = null;
            pendingNetworkRequest = false;
            e.printStackTrace();
        }
    }

    /**
     * Prevents clone from happening
     * @throws CloneNotSupportedException As this class is a singleton
     */
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

    /**
     * Refreshes the object by calling init again
     */
    public void refresh(){
        //TODO: Clear?
        initFromServer(id);
    }


    /**
     * Function which takes the member ids and converts it to their names by sending a request to
     * the server
     */
    private void resolveMemberIds(){
        if(memberIds == null || memberIds.length == 0){
            pendingNetworkRequest = false;
            return;
        }

        //Get all the ids and send a comma separated string
        StringBuilder idString = new StringBuilder();
        for(String id:memberIds){
            idString.append(id);
            idString.append(",");
        }
        //Remove the trailing comma
        idString.deleteCharAt(idString.length()-1);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("users")
                        .appendQueryParameter("resolveIds", idString.toString()).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("status")) {

                                JSONArray users = response.getJSONArray("names");
                                if(memberIds.length != users.length()){
                                    throw new JSONException("Received data does not match requested size");
                                }
                                if(memberInfo == null || memberInfo.length != memberIds.length){
                                    memberInfo = new UserInfo[memberIds.length];
                                }

                                for(int i =0; i < users.length(); i++){
                                    JSONObject currentUser = users.getJSONObject(i);
                                    //TODO: Confirm order is based on specified order
                                    memberInfo[i] = new UserInfo(currentUser);
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
