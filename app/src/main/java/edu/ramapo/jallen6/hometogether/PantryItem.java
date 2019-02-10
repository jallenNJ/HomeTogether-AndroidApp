package edu.ramapo.jallen6.hometogether;

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

    boolean isSelected(){
        return selected;
    }

    public void toggleSelected(){
        selected = !selected;
    }


}
