package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to convert JSON data to Data Structures within the program
 */
public class JSONFormatter {

    /**
     * Static class, constructor not needed
     */
    private JSONFormatter(){}

    /**
     * Convert a JSONArray to a Java String[] so it can be accessed without a try catch
     * @param array The JSONArray to convert
     * @return A string[] of the same size of the JSONArray containing the string values
     * @throws JSONException Any JSONExceptions thrown while parsing the data
     */
    public static String[] JSONArrayToStringArray(@NonNull JSONArray array) throws JSONException {

        String[] returnVal= new String[array.length()];
        for(int i =0; i < array.length();i++){
            returnVal[i] = array.get(i).toString();
        }
        return returnVal;
    }

    /**
     * Extracts the same field from a JSONArray of JSONObjects and puts into a string []
     * @param array The JSONArray of JSONObjects
     * @param key The key in the JSONObjects
     * @return The String array of all the specified fields
     * @throws JSONException Thrown when key is invalid
     */
    public static String[] getFieldFromJSONArrayOfJSONObjects(@NonNull JSONArray array,
                                                              @NonNull String key)
                                                              throws JSONException{

        String[] result = new String[array.length()];
        for(int i =0; i < array.length(); i++){
            result[i] = array.getJSONObject(i).getString(key);
        }
        return result;
    }


    /**
     * Gets the string from an object, or returns null when an exception would be thrown
     * @param jsonObject The object to extract the key from
     * @param key The key to be used on the item
     * @return The data of the field as a string, or null if it didn't exist
     */
    public static String getStringOrNull(@NonNull JSONObject jsonObject,@NonNull String key){
        try{
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Takes the string and capitalizes the first letter
     * @param key The key to be capitalized
     * @return The key with a capital first character
     */
    public static String capitalizeKey(@NonNull String key){
        if(key.length() == 0){
            return key;
        }
        return key.length() == 1?
                Character.toString(Character.toUpperCase(key.charAt(0)))
                : Character.toUpperCase(key.charAt(0)) + key.substring(1);

    }

}
