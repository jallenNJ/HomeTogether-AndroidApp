package edu.ramapo.jallen6.hometogether;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    private TextView username;
    private TextView pass;
    private RequestQueue requestQueue;
    String url =  "http://192.168.1.18:3000/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.loginUserField);
        pass = findViewById(R.id.loginPassField);
       // textView = findViewById(R.id.output);
       // Button buttonParse = findViewById(R.id.button);

        requestQueue = Volley.newRequestQueue(this);
      /*  buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });*/

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


        requestQueue.add(request);

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


        requestQueue.add(request);
    }


 /*   private void jsonParse(){


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean obj = response.getBoolean("status");
                            String msg = response.getString("message");
                            textView.setText("Status is " + obj + "\n Message: " + msg);
                        } catch (JSONException e) {
                            textView.setText("One required field not defined");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText("An Error boi!");
                        error.printStackTrace();
                    }
                });


        requestQueue.add(request);

    }*/
}
