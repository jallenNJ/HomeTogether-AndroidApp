package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Observable;

public class PantryItem extends Observable {

    public static final String NAME_FIELD = "name";
    public static final String QUANTITY_FIELD = "quantity";
    public static final String EXPIRES_FIELD = "expires";
    public static final String FORMATTED_EXPIRES_FIELD = "fexpires";
    public static final String CATEGORY_FIELD = "category";
    public static final String TAG_FIELD = "tags";


    private String name;
    private int quantity;
    private String category;
    private String[] tags;
    private String expires;
    private String formattedExpires;
    private boolean selected;


    public PantryItem(JSONObject jsonPantry) throws JSONException {
        applyUpdate(jsonPantry);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY", Locale.US);

        int month = Integer.parseInt(expires.split(" ")[0]);
        int day = Integer.parseInt(expires.split(" ")[1].split(",")[0]);
        int year = Integer.parseInt(expires.split(", ")[1]);
        Calendar cal = new GregorianCalendar(year,month,day);
        formattedExpires = sdf.format(cal.getTime());
       // cal = new GregorianCalendar(Integer.parseInt(split[1]), Integer.parseInt(split[0]), month);
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
        result.append(getFieldAsString(QUANTITY_FIELD));
        result.append("\nTags: ");
        result.append(getFieldAsString(TAG_FIELD));
        result.append("\nExpires: ");
        result.append(getFieldAsString(FORMATTED_EXPIRES_FIELD));
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
            case FORMATTED_EXPIRES_FIELD:
                return formattedExpires;
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
