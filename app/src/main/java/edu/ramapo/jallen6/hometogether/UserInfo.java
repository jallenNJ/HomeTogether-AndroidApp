package edu.ramapo.jallen6.hometogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Locale;

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

        loadFromObject(jsonUser);


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
    public AlertDialog createUserPopUp(@NonNull final Context context,
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

        final AlertDialog dialog =  popUp.create();
        //For every key, in the format {Display: Logic};
        for(final String[] field :new String[][]{{"Name", name, jsonKeys[2]}, {"Shirt Size", shirtSize, jsonKeys[3]},
                {"Shoe Size", shoeSize, jsonKeys[4]}, {"Birthday", birthday, jsonKeys[5]}}){
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
                   modifyUserField(context, field[0], field[2],field[1], dialog).show();
               }
           });

            if(field[2].equals(jsonKeys[5])){
                String[] tokens = field[1].split(" ");
                String month = new DateFormatSymbols().getMonths()[Integer.parseInt(tokens[0])];
                String ordinal;
                int day = Integer.parseInt(tokens[1]);
                switch(day){
                    case 1:
                    case 21:
                    case 31:
                        ordinal = "st";
                        break;
                    case 2:
                    case 22:
                        ordinal = "nd";
                        break;
                    case 3:
                    case 23:
                        ordinal = "rd";
                        break;
                    default:
                        ordinal = "th";

                }
                text.setText(String.format(Locale.getDefault(), "%-11s: %s %d%s",
                        field[0],month, day, ordinal));
            } else{
                text.setText(String.format("%-11s: %s",  field[0],fieldData));
            }


            layout.addView(text);
        }

        dialog.setView(layout);
        return dialog;

    }


    /**
     * Creates the pop-up to modify a field in the user pop up
     * @param context The context of the activity
     * @param displayHeader The value to put in the header
     * @param jsonKey The key in json to use in the object
     * @param value The current value, null if it doesn't exist / matter
     * @return The fully built AlertDialog
     */
    private AlertDialog modifyUserField(@NonNull final Context context,
                                                @NonNull String displayHeader,
                                                @NonNull final String jsonKey,
                                                @Nullable String value,
                                                @NonNull final AlertDialog parentDialog){
        //Create the builder, and set the title based on if its adding or being modified
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(String.format("%s field %s", value != null? "Modify":"Add",displayHeader));
        builder.setCancelable(true);
        //Create the layout and append on the given header
        LinearLayout layout = new LinearLayout(context);
        TextView label = new TextView(context);
        label.setText(displayHeader);
        layout.addView(label);
        final View userDisplay = createDataTypeUserInput(context, displayHeader);
        if(userDisplay != null){
            layout.addView(userDisplay);
        }else{
            throw new RuntimeException("New display header added without adding to user info");
        }




        builder.setView(layout);



        //Add the submit and cancel buttons
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final JSONObject params = new JSONObject();
                try {
                    params.put("dbKey", jsonKey);
                    params.put("dbVal", userDisplay.getTag().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH,
                        NetworkManager.getHostAsBuilder().appendPath("users").toString(),
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //TODO: update object

                                try {
                                    loadFromObject(response.getJSONObject("user"));
                                } catch (JSONException e) {
                                    Toast.makeText(context,
                                           "Changes submitted, failed to update client",
                                            Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                                dialogInterface.dismiss();
                                parentDialog.dismiss();
                                Toast.makeText(context, "Changes submitted",
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        NetworkManager.generateDefaultErrorHandler(context)
                );
                NetworkManager.getInstance(context).addToRequestQueue(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }

    private View createDataTypeUserInput(final Context context, String displayHeader) {
        switch(displayHeader){

            case "Shirt Size":
                final Spinner sizes = new Spinner(context);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        R.layout.support_simple_spinner_dropdown_item,
                        new String[] {"XS-", "XS", "S", "M", "L", "XL", "XL+"});
                sizes.setAdapter(adapter);

                sizes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        sizes.setTag(adapterView.getItemAtPosition(pos));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                sizes.setTag("");
                return sizes;
            case "Shoe Size":
                final EditText text = new EditText(context);
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(editable.toString().equals("")){
                            text.setTag("");
                            return;
                        }
                        int val;
                        try{
                            val = Integer.parseInt(editable.toString());
                        } catch (NumberFormatException ignored){
                            val = -1;
                        }
                        if((val < 1 || val > 100)){
                            Toast.makeText(context, "Please enter a " +
                                    "positive number less than 100",Toast.LENGTH_SHORT).show();
                            text.setText("");
                            //Recursive call sets tag
                        }
                        text.setTag(val);

                    }
                });
                text.setTag("");
                return text;
            case "Birthday":
                final Spinner months = new Spinner(context);
                final Spinner days = new Spinner(context);
                ArrayAdapter<String> monthAdapter= new ArrayAdapter<String>(context,
                    R.layout.support_simple_spinner_dropdown_item,
                    new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                            "Oct", "Nov", "Dec"});



                final LinearLayout layout = new LinearLayout(context);
                class MonthDayStruct{
                    private int monthOutput;
                    private int dayOutput;
                    LinearLayout linear;
                    MonthDayStruct(LinearLayout l){
                        linear = l;
                        monthOutput = 0;
                        dayOutput = 0;
                    }

                    public void setDay(int day) {
                        dayOutput = day;
                        updateTag();
                    }
                    public void setMonth(int month){
                        monthOutput = month;
                        updateTag();
                    }

                    private void updateTag(){
                        linear.setTag(String.format(Locale.ENGLISH, "%02d %02d",
                                monthOutput, dayOutput));
                    }
                }
                final MonthDayStruct data = new MonthDayStruct(layout);
                months.setAdapter(monthAdapter);
                months.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int dayAmount;
                        int selectedIndex = days.getSelectedItemPosition();
                        if(i == 1){ //February, cry
                            dayAmount = 29;
                        }else if(i == 3 || i ==5|| i== 8 || i ==10){
                            dayAmount = 30;
                        }else{
                            dayAmount = 31;
                        }
                        String[] dayArray = new String[dayAmount];
                        for(int j =0; j < dayAmount; j++){
                            dayArray[j] = String.format(Locale.getDefault(),"%02d", j+1);
                        }
                        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(context,
                                R.layout.support_simple_spinner_dropdown_item, dayArray);
                        days.setAdapter(dayAdapter);
                        data.setMonth(i);
                        if(selectedIndex < dayAmount){
                            days.setSelection(selectedIndex);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        data.setDay(i+1);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                layout.addView(months);
                layout.addView(days);
                layout.setTag("");
                return layout;
        }
        return null;
    }

    /**
     * Checks if a field is mutable on the server
     * @param displayHeader The header to check
     * @return True if mutable, false otherwise
     */
    private boolean isMutable(String displayHeader){
        return !displayHeader.equals("Name");
    }

    private void loadFromObject(@NonNull JSONObject jsonUser) throws JSONException{
        int keyIndex = 0;
        id=jsonUser.getString(jsonKeys[keyIndex++]);
        keyIndex++; //THis is for the missing icon
        name = jsonUser.getString(jsonKeys[keyIndex++]);

        shirtSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        shoeSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        birthday = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex]);
    }

}
