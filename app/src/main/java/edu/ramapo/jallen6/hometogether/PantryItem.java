package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

public class PantryItem extends Observable {

    public static final String NAME_FIELD = "name";
    public static final String QUANTITY_FIELD = "quantity";
    public static final String EXPIRES_FIELD = "expires";
    public static final String CATEGORY_FIELD = "category";
    public static final String TAG_FIELD = "tags";


    private String name;
    private int quantity;
    private String category;
    private String[] tags;
    private String expires;
    //private something Data
    private boolean selected;


    public PantryItem(JSONObject jsonPantry) throws JSONException {
        applyUpdate(jsonPantry);

    }

    //TODO: handle partially correct JSONobject
    public void applyUpdate(JSONObject jsonPantry) throws JSONException {
        name = jsonPantry.getString(NAME_FIELD);
        quantity = jsonPantry.getInt(QUANTITY_FIELD);
        category = jsonPantry.getString(CATEGORY_FIELD);
        expires = jsonPantry.getString(EXPIRES_FIELD);
        JSONArray tagsArray = jsonPantry.getJSONArray(TAG_FIELD);
        //TODO, check what happens if size is zero
        tags = new String[tagsArray.length()];
        for(int i =0; i< tagsArray.length(); i++){
            tags[i] = tagsArray.getString(i);
        }

        this.setChanged();
        this.notifyObservers();
    }

    @NonNull
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("Name: ");
        result.append(name);
        result.append("\nQuantity: ");
        result.append(getFieldAsString(CATEGORY_FIELD));
        result.append("\nTags: ");
        result.append(getFieldAsString(TAG_FIELD));
        result.append("\nExpires: ");
        result.append(getFieldAsString(EXPIRES_FIELD));
        return result.toString();

    }

    public String getFieldAsString(String field){
        field = field.toLowerCase();
        switch (field){
            case NAME_FIELD:
                return name;
            case QUANTITY_FIELD:
                return Integer.toString(quantity);
            case CATEGORY_FIELD:
                return category;
            case TAG_FIELD:
                StringBuilder builder = new StringBuilder();
                for(String tag:tags){
                    builder.append(tag);
                    builder.append(", ");
                }
                String result = builder.toString();

                if(result.length() > 2){
                    result= result.substring(0, result.length() -2);
                }
                return result;

            case EXPIRES_FIELD:
                //Log.e("NotImplemented", field+" getter in PantryItem not implemented");
                //return "";
                return expires;
            default:
                Log.e("InvalidSwitchParameter", field + "is not valid", new Exception());
                return "";
        }
    }

    public int getQuantity(){
        return quantity;
    }
    public String[] getTags(){
        return tags;
    }


    boolean isSelected(){
        return selected;
    }

    public void toggleSelected(){
        selected = !selected;
    }


}
