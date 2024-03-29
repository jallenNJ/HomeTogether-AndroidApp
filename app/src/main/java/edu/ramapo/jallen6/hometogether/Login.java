package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;


/**
 *  This class is responsible for running the log-in activity. This screen supports the user name
 *  and password fields, and allows for the user to sign up a new account or log in to an existing
 *  one
 *
 * @author Joseph Allen
 */
public class Login extends AppCompatActivity {


    //The fields of the form
    private TextView username;
    private TextView pass;
    private TextView debugIP;
    //The address to send data to
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Get the user input
        username = findViewById(R.id.loginUserField);
        pass = findViewById(R.id.loginPassField);
        debugIP = findViewById(R.id.debugIPBox);
        debugIP.setText(NetworkManager.getIP());


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("authcheck").toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("status")){
                                MemberBar.setUsername(response.getString("user"));
                                Intent intent = new Intent(Login.this, HouseholdSelection.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "Server error", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        NetworkManager.getInstance(this).addToRequestQueue(request);

        SwipeGestureFactory.buildAndBindConsumingDetector((View)findViewById(R.id.loginButtonLayout).getParent(),
                SwipeGestureFactory.SwipeGestureFactoryType.HORIZONTAL,
                null,
                new SwipeHandler() {
                    @Override
                    public boolean onSwipe() {
                        logIn(null);
                        return true;
                    }
                });


    }



    /**
     *
     *  Event handler for when the user clicks the sign up button. The functions gathers the
     *  form data, formats the parameters in JSON, then submits the request. Uses toasts
     *  to output result
     *  @param v Ignored -- View which triggered function
     *  @author Joseph Allen
     */
    public void signUp(@Nullable View v)  {

        NetworkManager.setHost(debugIP.getText().toString().trim());
        url =  NetworkManager.getHostAsBuilder().appendPath("login").toString();
        //Get the user input and format the JSONObject
        JSONObject params;
        try{
           params = createLoginParams();
        } catch (Exception e){
            Toast.makeText(Login.this, "Sign up failed", Toast.LENGTH_SHORT).show();
            return;
        }

        //Submit the request to the server. On success output result
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                       logIn(null);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error == null || error.networkResponse == null){
                            Toast.makeText(Login.this,
                                    "Invalid server", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        if (error.networkResponse.statusCode == 409) {
                            Toast.makeText(Login.this,
                                    "Username taken", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this,
                                    "Sign-up failed", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }



                    }
                });

        NetworkManager.getInstance(this).addToRequestQueue(request);

    }


    /**
     * This function reads the fields of the form, which are set in the constructor, and formats
     * them into a JSON object to be set to the server
     * @return A formatted JSON object to be sent to the server
     * @throws JSONException when the object fails to create
     * @author Joseph Allen
     */


    private JSONObject createLoginParams() throws JSONException{
        //Create the JSON object from the form fields.
        JSONObject params = new JSONObject();
        params.put("user", username.getText());
        params.put("pass", pass.getText());
        return params;
    }


    /**
     *  This function gets the user input, formats the entered data and sends the data to the server
     *  to be validated. If the server responds with a log in, the screen moves to the household
     *  selection. If app moves when server declines log-in, the user will be greeted with a blank
     *  screen due to the server rejected the request due to them not being logged in.
     *
     * @param v Ignored-- The view which triggered the on click
     * @author Joseph Allen
     */
    public void logIn(@Nullable View v){
        NetworkManager.setHost(debugIP.getText().toString().trim());
        url =  NetworkManager.getHostAsBuilder().appendPath("login").toString();
        //Create the params object from the field
        JSONObject params;
        try{
            params = createLoginParams();
        } catch (Exception e){
            Toast.makeText(Login.this, "Log in failed", Toast.LENGTH_SHORT).show();
            return;
        }

        //Send data to the serve route. If server allows login, move to Household screen
        // Otherwise, display the error
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MemberBar.setUsername(response.getString("user"));
                        } catch (JSONException e) {
                            Toast.makeText(Login.this, "Invalid server response",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Login.this, HouseholdSelection.class);
                        startActivity(intent);
                        finish();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error == null || error.networkResponse == null){
                            Toast.makeText(Login.this,
                                    "Invalid server", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        switch (error.networkResponse.statusCode){
                            case HttpURLConnection.HTTP_NOT_FOUND:
                                Toast.makeText(Login.this, "User not found",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case HttpURLConnection.HTTP_UNAUTHORIZED:
                                Toast.makeText(Login.this, "Invalid log-in",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            default:
                                Toast.makeText(Login.this, "Failed to log in",
                                        Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                        }

                    }
                });


        NetworkManager.getInstance(this).addToRequestQueue(request);
    }

}
