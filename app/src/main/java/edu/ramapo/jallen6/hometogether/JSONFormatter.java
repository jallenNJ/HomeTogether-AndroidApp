package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONFormatter {

    private JSONFormatter(){};

    public static String[] JSONArrayToStringArray(@NonNull JSONArray array) throws JSONException {

        String[] returnVal= new String[array.length()];
        for(int i =0; i < array.length();i++){
            returnVal[i] = array.get(i).toString();
        }
        return returnVal;
    }

}
