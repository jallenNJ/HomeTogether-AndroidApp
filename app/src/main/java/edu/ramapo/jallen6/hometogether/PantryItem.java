package edu.ramapo.jallen6.hometogether;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

public class PantryItem extends Observable {

    private String name;
    private int quantity;
    private String category;
    private String[] tags;
    //private something Data
    private boolean selected;


    public PantryItem(JSONObject jsonPantry) throws JSONException {
        name = jsonPantry.getString("name");
        quantity = jsonPantry.getInt("quantity");
        category = jsonPantry.getString("category");
        JSONArray tagsArray = jsonPantry.getJSONArray("tags");
        //TODO, check what happens if size is zero
        tags = new String[tagsArray.length()];
        for(int i =0; i< tagsArray.length(); i++){
            tags[i] = tagsArray.getString(i);
        }
    }

   // public PantryItem(String n, int q, String c, String[] t){
    //      name = n;
    //      quanity = q;
    //      category=c;
    //      tags = t;
   // }


    public String getFieldAsString(String field){
        field = field.toLowerCase();
        switch (field){
            case "name":
                return name;
            case "quantity":
                return Integer.toString(quantity);
            case "category":
                return category;
            case "tags":
                return tags.toString();
            case "expires":
                Log.e("NotImplemented", field+" getter in PantryItem not implemented");
                return "";
            default:
                Log.e("InvalidSwitchParameter", field + "is not valid", new Exception());
                return "";
        }
    }


    boolean isSelected(){
        return selected;
    }

    public void toggleSelected(){
        selected = !selected;
    }


}
