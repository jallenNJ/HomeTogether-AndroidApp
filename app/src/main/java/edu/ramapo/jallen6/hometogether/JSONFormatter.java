package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

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

}
