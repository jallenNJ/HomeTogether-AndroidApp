package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class UserInfo {
    private String id;
    //Icon
    private String name;
    private String shirtSize;
    private String shoeSize;
    private String birthday;

    private final static String[] jsonKeys = new String[]{"_id", "icon", "name", "shirtSize",
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

    public AlertDialog.Builder createUserPopUp(@NonNull Context context){
        AlertDialog.Builder popUp = new AlertDialog.Builder(context);

        popUp.setTitle("ADD TITLE");
        popUp.setCancelable(true);

        //Icon
        String[] fields = fieldsToStringArray();
        TextView text = new TextView(context);
        //fix dupe field
        for(String field :fields){
            if (field == null){
                continue;
            }

            text.setText(String.format("%s\n%s", text.getText(), field));

        }
        popUp.setView(text);
        return popUp;

    }

    private String[] fieldsToStringArray(){
        return new String[]{id, name, name, shirtSize, shoeSize, birthday};
    }


}
