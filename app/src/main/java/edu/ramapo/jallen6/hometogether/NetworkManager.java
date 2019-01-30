package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class NetworkManager {

    private RequestQueue requestQueue;
    private static NetworkManager managerInstance;
    private static Context callingContext;
    public static final String host = "http://172.18.105.149:3000";


    private NetworkManager (Context context){
        callingContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkManager getInstance(Context context){
        if(managerInstance == null){
            managerInstance = new NetworkManager(context);
        }
        return managerInstance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(callingContext.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public static Uri.Builder getHostAsBuilder(){
        return Uri.parse(NetworkManager.host).buildUpon();
    }

    public static Response.ErrorListener generateDefaultErrorHandler(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }



}
