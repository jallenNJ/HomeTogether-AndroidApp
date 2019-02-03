package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class NetworkManager {

    private RequestQueue requestQueue;
    private static NetworkManager managerInstance;
    private static String host = "http://172.18.107.235:3000";
    private static String port = "3000";


    private NetworkManager (Context context){
        requestQueue = getRequestQueue(context);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    public static void setHost(String url){
        if(url.length() < 1){
            return;
        }
        host = "http://"+url+":"+port;
    }

    public static synchronized NetworkManager getInstance(Context context){
        if(managerInstance == null){
            managerInstance = new NetworkManager(context);
        }
        return managerInstance;
    }

    private RequestQueue getRequestQueue(Context context){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            throw new IllegalStateException("Request queue was not initialized " +
                    "This is most likely caused by an invalid context being passed to getInstance");
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
