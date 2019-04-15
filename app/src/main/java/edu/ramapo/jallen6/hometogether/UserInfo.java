package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    private String id;
    //Icon
    private String name;
    private String shirtSize;
    private String shoeSize;
    private String birthday;

    private final static String[] jsonKeys = new String[]{"_id", "icon", "user", "shirtSize",
            "shoeSize", "birthday"};


    UserInfo(@NonNull JSONObject jsonUser) throws JSONException {

        int keyIndex = 0;
        id=jsonUser.getString(jsonKeys[keyIndex++]);
        keyIndex++; //THis is for the missing icon
        name = jsonUser.getString(jsonKeys[keyIndex++]);

        shirtSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        shoeSize = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex++]);
        birthday = JSONFormatter.getStringOrNull(jsonUser, jsonKeys[keyIndex]);

    }

    UserInfo(@NonNull String i,@NonNull String n){
        id = i;
        name= n;

    }


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

    public AlertDialog.Builder createUserPopUp(@NonNull final Context context,
                                               @NonNull final String selfName){
        AlertDialog.Builder popUp = new AlertDialog.Builder(context);

        popUp.setTitle(String.format("Info for Household Member %s",
                JSONFormatter.capitlizeKey(name)));
        popUp.setCancelable(true);

        //Icon

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Todo: write rest of the keys
        for(final String[] field :new String[][]{{"Name", name}, {"Shirt Size", shirtSize},
                {"Shoe Size", shoeSize}, {"Birthday", birthday}}){
            TextView text = new TextView(context);

            final String fieldData = field[1] != null ? field[1] :
                    selfName.equals(name)?
                            "Tap to add"
                            :null;
            if(fieldData == null){
                continue;
            }


           text.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(!isMutable(field[0])){
                       Toast.makeText(context, String.format("%s is not a changeable", field[0]),
                               Toast.LENGTH_SHORT).show();
                       return;
                   }
                   modifyUserField(context, field[0], field[1]).show();
               }
           });

            text.setText(String.format("%s:%s",  field[0],fieldData));
            layout.addView(text);
        }
        popUp.setView(layout);
        return popUp;

    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    private String[] fieldsToStringArray(){
        return new String[]{id, null, name, shirtSize, shoeSize, birthday};
    }

    private AlertDialog.Builder modifyUserField(@NonNull final Context context,
                                                @NonNull String displayHeader,
                                                @Nullable String value){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(String.format("%s field %s", value != null? "Modify":"Add",displayHeader));
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(context);
        TextView label = new TextView(context);
        label.setText(displayHeader);
        layout.addView(label);
        EditText userInputBox = new EditText(context);
        userInputBox.setText(value !=null? value:"");
        layout.addView(userInputBox);
        builder.setView(layout);
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

    private boolean isMutable(String displayHeader){
        return !displayHeader.equals("Name");
    }

}
