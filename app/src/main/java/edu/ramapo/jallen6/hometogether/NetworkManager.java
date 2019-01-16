package edu.ramapo.jallen6.hometogether;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkManager {

    private RequestQueue requestQueue;
    private static NetworkManager managerInstance;
    private static Context callingContext;
    public static final String host = "http://192.168.1.103:3000";


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




}
