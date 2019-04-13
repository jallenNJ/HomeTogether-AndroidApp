package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Gives functionality to a LinearLayout which acts as the view for the members in a household
 */
public class MemberBar implements Observer {
    private LinearLayout memberLayout; //The layout being used as the bar

    MemberBar(@NonNull LinearLayout layout){
        //Save a reference and attach observer
        memberLayout = layout;
        ActiveHousehold.getInstance().addObserver(this);
    }

    /**
     * Removes itself as an observer
     * @throws Throwable Any exceptions gets rethrown
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        ActiveHousehold.getInstance().deleteObserver(this);
    }

    /**
     * Updates the view to stay inline with the model
     * @param observable The observed object being changed, always the layout
     * @param o Any information it pasases, will be null
     */
    @Override
    public void update(Observable observable, Object o) {
        if(memberLayout == null){ //Should never happen but a good check
            throw new IllegalStateException("memberLayout has become null");
        }

        //Clear the views and get reference to the cache
        memberLayout.removeAllViewsInLayout();
        ActiveHousehold cache = ActiveHousehold.getInstance();

        //For each member, make the button
        for(int i =0; i < cache.getMembersSize(); i++){
            String name =  cache.getMemberName(i);
            Button button = new Button(memberLayout.getContext());
            button.setText(name);
            button.setTag(new UserInfo(cache.getMemberId(i), cache.getMemberName(i)));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((UserInfo) view.getTag()).createUserPopUp(memberLayout.getContext()).show();

                }
            });
            memberLayout.addView(button);
        }

        //Add the new member button
        Button button = new Button(memberLayout.getContext());
        button.setText("DEBUG: Add member");
        memberLayout.addView(button);

        //Bind the on clock listener to add a new member
        // Creates a dialog to add them by their full name
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(memberLayout.getContext(), R.style.AppDialogTheme);
                builder.setTitle("Username of member to add");

                // Set up the input
                final EditText input = new EditText(memberLayout.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // memberToAddText = input.getText().toString();
                        Toast.makeText(memberLayout.getContext(), input.getText().toString(), Toast.LENGTH_SHORT).show();
                        //TODO: Send request to find member to add;
                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.GET,
                                NetworkManager.getHostAsBuilder().appendPath("users")
                                        .appendQueryParameter("username", input.getText().toString())
                                        .toString(),
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try{
                                            if(response.getBoolean("status")){
                                                displayFoundUserList(response.getJSONArray("users"));
                                            }
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                            Toast.makeText(memberLayout.getContext(),
                                                    "No users found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                        NetworkManager.generateDefaultErrorHandler());
                        NetworkManager.getInstance(memberLayout.getContext()).getRequestQueue().add(request);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }


    /**
     * Creates an alert dialog to show the users which were found with a search
     * @param userList Tbe Array of found users
     * @throws JSONException Exception based on the parsing of userList or if none were found
     */
    public void displayFoundUserList(JSONArray userList) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(memberLayout.getContext(), R.style.AppDialogTheme);
        builder.setTitle("Choose member");
        if(userList.length() == 0){
            throw new JSONException("No members were found");
        }
        //TODO: Use json formatter?
        final String[] users = new String[userList.length()];
        for(int i =0; i < userList.length(); i++){
            users[i] = userList.getJSONObject(i).getString("user");
        }
        builder.setItems(users, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                //TODO: Implement the user selection

                JSONObject params = new JSONObject();
                try {
                    params.put("username", users[which]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(memberLayout.getContext(),
                            "Failed to add", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Request to add the click member to the household
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.PUT,
                        NetworkManager.getHostAsBuilder()
                                .appendPath("household").appendPath("member")
                               // .appendQueryParameter("username",users[which])
                                .toString(),
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try{
                                    //TODO: Check if this works on server rewrite
                                    if(response.getBoolean("status")){
                                        ActiveHousehold.getInstance().refresh();
                                    } else{
                                        throw new JSONException("Server reported failure");
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                    Toast.makeText(memberLayout.getContext(),
                                            "User failed to add", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        NetworkManager.generateDefaultErrorHandler());
                NetworkManager.getInstance(memberLayout.getContext()).getRequestQueue().add(request);


            }
        });
        builder.show();
    }
}
