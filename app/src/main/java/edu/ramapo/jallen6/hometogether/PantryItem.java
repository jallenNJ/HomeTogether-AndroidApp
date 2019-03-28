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

/**
 * Model representation of the a pantry item
 */
public class PantryItem extends Observable {

    //Public field names to be read
    public static final String NAME_FIELD = "name"; /// Name field define
    public static final String QUANTITY_FIELD = "quantity"; /// Quantity field define
    public static final String EXPIRES_FIELD = "expires"; /// Expires field define
    public static final String FORMATTED_EXPIRES_FIELD = "fexpires"; /// Formatted expires field define
    public static final String CATEGORY_FIELD = "category"; /// Category field define
    public static final String TAG_FIELD = "tags"; ///Tag field define
    public static final String LOCATION_FIELD = "location"; ///Location field define


    //Data storage fields
    private String name;
    private int quantity;
    private String category;
    private String[] tags;
    private String expires;
    private String formattedExpires;
    private String location;
    private boolean selected;

    /**
     * Fills the field with all the information
     * @param jsonPantry The information being tracked
     * @throws JSONException Any exception in the parsing of the data
     */
    public PantryItem(JSONObject jsonPantry) throws JSONException {
        //Fill the field
        applyUpdate(jsonPantry);


        //Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY", Locale.US);

        //NOTE: This assumes the date is formatted correctly in the server
        int month = Integer.parseInt(expires.split(" ")[0]);
        int day = Integer.parseInt(expires.split(" ")[1].split(",")[0]);
        int year = Integer.parseInt(expires.split(", ")[1]);
        Calendar cal = new GregorianCalendar(year,month,day);
        formattedExpires = sdf.format(cal.getTime());
       // cal = new GregorianCalendar(Integer.parseInt(split[1]), Integer.parseInt(split[0]), month);
    }

    /**
     * Fills the data members of class from a JSONObject
     * @param jsonPantry The Object to parse from
     * @throws JSONException An exception in parsing the objectg
     */
    //TODO: handle partially correct JSONobject
    public void applyUpdate(JSONObject jsonPantry) throws JSONException {
        name = jsonPantry.getString(NAME_FIELD);
        quantity = jsonPantry.getInt(QUANTITY_FIELD);
        category = jsonPantry.getString(CATEGORY_FIELD);
        expires = jsonPantry.getString(EXPIRES_FIELD);
        location = jsonPantry.getString(LOCATION_FIELD);
        JSONArray tagsArray = jsonPantry.getJSONArray(TAG_FIELD);
        //TODO, check what happens if size is zero
        tags = new String[tagsArray.length()];
        for(int i =0; i< tagsArray.length(); i++){
            tags[i] = tagsArray.getString(i);
        }


        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Creates a formatted string of the Name, Quantity, Tag, and Formmated Expires field on new
     * new lines with a header.
     *
     * Example Format:
     * Name: TestItem
     * Quantity: 4
     * Tags: DebugItem, Cold, Frozen
     * Expires: Jan 2nd, 1970
     *
     *
     * @return A formatted String of the above description.
     */
    @NonNull
    public String toString(){
        return "Name: " + name +
                "\nQuantity: " + getFieldAsString(QUANTITY_FIELD) +
                "\nTags: " + getFieldAsString(TAG_FIELD) +
                "\nExpires: " + getFieldAsString(FORMATTED_EXPIRES_FIELD);

    }

    /**
     * Returns a field from the object based on the string look up. It is recommended to use
     * this classes name defines to always have a match
     * @param field The field to search for, should use a class define to access
     * @return The contents of the field, empty string if invalid
     */
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
                return expires;
            case FORMATTED_EXPIRES_FIELD:
                return formattedExpires;
            case LOCATION_FIELD:
                return location;
            default:
                Log.e("InvalidSwitchParameter", field + "is not valid", new Exception());
                return "";
        }
    }

    /**
     * Gets quantity of the item as an int
     * @return The quantity, which will always be non-negative
     */
    public int getQuantity(){
        return quantity;
    }

    /**
     * Gets the tags as String array
     * @return The tags
     */
    public String[] getTags(){
        return tags;
    }


    /**
     * Returns if the object is selected or not
     * @return True if selected, false otherwise
     */
    boolean isSelected(){
        return selected;
    }

    /**
     * Sets the objects selection status to s
     * @param s The status for the object to be set to
     */
    public void setSelected(boolean s){
        selected = s;
    }

    /**
     * Toggle the selection status of the object
     */
    public void toggleSelected(){
        selected = !selected;
    }

    /**
     * Convert the object to its JSONObject form
     * @return The JSONObject representation of the object
     * @throws JSONException Any exception thrown while converting back to the JSONObject form
     */
    public JSONObject toJSONObject() throws JSONException{
        String[] jsonKeys = {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.EXPIRES_FIELD, PantryItem.CATEGORY_FIELD, PantryItem.TAG_FIELD};

        JSONObject params = new JSONObject();
        for (String jsonKey : jsonKeys) {
            String fieldData = getFieldAsString(jsonKey);

            if (fieldData.equalsIgnoreCase("")) { //Should never occur if db data is good...
                Log.e("Invalid model", "Model missing required field");
                throw new JSONException("Field had no data, possible db or network corruption");
            }
            params.put( jsonKey, fieldData);
        }
        return params;
    }
}
