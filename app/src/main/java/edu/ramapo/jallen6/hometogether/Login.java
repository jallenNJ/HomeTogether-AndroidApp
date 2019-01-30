package edu.ramapo.jallen6.hometogether;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class Login extends AppCompatActivity {

    private TextView username;
    private TextView pass;
    String url =  NetworkManager.host+"/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.loginUserField);
        pass = findViewById(R.id.loginPassField);


        //TODO: Implement checking of being logged in once server supports it
       /* JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                NetworkManager.getHostAsBuilder().appendPath("authcheck").toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("status")){
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

        NetworkManager.getInstance(this).addToRequestQueue(request);*/

    }



    public void signUp(View v)  {
        JSONObject params;
        try{
           params = createLoginParams();
        } catch (Exception e){
            Toast.makeText(Login.this, "Sign up failed", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean obj = response.getBoolean("status");
                            String msg = response.getString("message");

                            Toast.makeText(Login.this, ("Status is " + obj + "\n Message: " + msg), Toast.LENGTH_SHORT).show();
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

    }


    private JSONObject createLoginParams() throws JSONException{
        JSONObject params = new JSONObject();
        params.put("user", username.getText());
        params.put("pass", pass.getText());
        return params;
    }


    public void logIn(View v){
        JSONObject params;
        try{
            params = createLoginParams();
        } catch (Exception e){
            Toast.makeText(Login.this, "Log in failed", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean obj = response.getBoolean("status");
                            String msg = response.getString("message");

                            Toast.makeText(Login.this, ("Status is " + obj + "\n Message: " + msg), Toast.LENGTH_SHORT).show();
                            if(obj){
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
    }

}
