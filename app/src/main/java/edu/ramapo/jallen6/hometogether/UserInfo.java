package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data storage for any instance of a user, commonly used
 */
public class UserInfo {
    private String id; ///The id for the user
    //Icon
    private String name; /// The display name of the user
    private String shirtSize; ///Valid values [XS-, XS, S, M, L, XL. XL+]
    private String shoeSize; /// Valid values [0.0-100.0]
    private String birthday; /// Format MM DD, YYYY

    ///The JSON keys expected when interacting with an input object
    private final static String[] jsonKeys = new String[]{"_id", "icon", "user", "shirtSize",
            "shoeSize", "birthday"};


    /**
     * Constructor which formats the object from the JSON representation
     * @param jsonUser Must contain keys: id and name. Rest are optional
     * @throws JSONException On a parse error on the id or name fields
     */
    UserInfo(@NonNull JSONObject jsonUser) throws JSONException {

        int keyIndex = 0;
        id=jsonUser.getString(jsonKeys[keyIndex++]);
        keyIndex++; //THis is for the missing icon
        name = jsonUser.getString(jsonKeys[keyIndex++]);

        shirtSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        shoeSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        birthday = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex]);

    }


    /**
     * Convert the object back to JSON
     * @return The JSON representation of the object
     * @throws JSONException On any formatting error
     */
    public JSONObject toJson() throws JSONException{
        JSONObject json = new JSONObject();
        int keyIndex = 0;
        json.put(jsonKeys[keyIndex++], id);
        keyIndex++; //For missing icon
        json.put(jsonKeys[keyIndex++], name);
        json.put(jsonKeys[keyIndex++], shirtSize);
        json.put(jsonKeys[keyIndex++], shoeSize);
        json.put(jsonKeys[keyIndex], birthday);
        return json;
    }

    /**
     * Gets the user id
     * @return The MongoId of the user
     */
    public String getId(){
        return id;
    }

    /**
     * Gets the user's name
     * @return The username of the user
     */
    public String getName(){
        return name;
    }

    /**
     * Creates the pop-up for all user info, and gives edit options if tapped on self
     * @param context The context of the currently running activity
     * @param selfName The name of the current user account, for checking if self tap
     * @return The fully built AlertDialog
     */
    public AlertDialog.Builder createUserPopUp(@NonNull final Context context,
                                               @NonNull final String selfName){
        AlertDialog.Builder popUp = new AlertDialog.Builder(context);

        // Create the title and append on this user's name
        popUp.setTitle(String.format("Info for Household Member %s",
                JSONFormatter.capitalizeKey(name)));
        popUp.setCancelable(true);

        //Icon

        //Create a vertical layout to put all the views into
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //For every key, in the format {Display: Logic};
        for(final String[] field :new String[][]{{"Name", name}, {"Shirt Size", shirtSize},
                {"Shoe Size", shoeSize}, {"Birthday", birthday}}){
            //Create the view
            TextView text = new TextView(context);

            //If the field is null && user is editing their self, set as add prompt
            // otherwise add it
            final String fieldData = field[1] != null ?
                    field[1]
                    : selfName.equals(name)?
                            "Tap to add"
                            :null;

            if(fieldData == null){
                text.setText(String.format("%s: %s",  field[0], "No data provided"));
                text.setTypeface(null, Typeface.ITALIC);
                layout.addView(text);
                continue;
            }


            // Set on click for the field to have it editable
           text.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //Check to make sure the key is mutable
                   if(!isMutable(field[0])){
                       Toast.makeText(context, String.format("%s is not a changeable", field[0]),
                               Toast.LENGTH_SHORT).show();
                       return;
                   }
                   //If it is, add
                   modifyUserField(context, field[0], field[1]).show();
               }
           });

            text.setText(String.format("%s: %s",  field[0],fieldData));
            layout.addView(text);
        }
        popUp.setView(layout);
        return popUp;

    }


    /**
     * Creates the pop-up to modify a field in the user pop up
     * @param context The context of the activity
     * @param displayHeader The value to put in the header
     * @param value The current value, null if it doesn't exist / matter
     * @return The fully built AlertDialog
     */
    private AlertDialog.Builder modifyUserField(@NonNull final Context context,
                                                @NonNull String displayHeader,
                                                @Nullable String value){
        //Create the builder, and set the title based on if its adding or being modified
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(String.format("%s field %s", value != null? "Modify":"Add",displayHeader));
        builder.setCancelable(true);
        //Create the layout and append on the given header
        LinearLayout layout = new LinearLayout(context);
        TextView label = new TextView(context);
        label.setText(displayHeader);
        layout.addView(label);
        //Create the edit box, and pre fill the text if it was given
        EditText userInputBox = new EditText(context);
        userInputBox.setText(value !=null? value:"");
        layout.addView(userInputBox);
        builder.setView(layout);

        //Add the submit and cancel buttons
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Do submit", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder;
    }

    /**
     * Checks if a field is mutable on the server
     * @param displayHeader The header to check
     * @return True if mutable, false otherwise
     */
    private boolean isMutable(String displayHeader){
        return !displayHeader.equals("Name");
    }

}
