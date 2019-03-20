package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;

public class NetworkManager {

    private RequestQueue requestQueue;
    private static NetworkManager managerInstance;
    private static String host = "http://172.17.143.200:3000";
    private static String port = "3000";


    private NetworkManager (Context context){
        requestQueue = getRequestQueue(context);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    @NonNull
    public static String getIP(){
        return host.substring(7, host.length()-5);
    }

    public static void setHost(@NonNull String url){
        if(url.length() < 1){
            return;
        }
        host = "http://"+url+":"+port;
        Log.i("hostName", host);
    }

    public static synchronized NetworkManager getInstance(@NonNull Context context){
        if(managerInstance == null){
            managerInstance = new NetworkManager(context);
        }
        return managerInstance;
    }

    public static synchronized NetworkManager getInstance() throws InstantiationException {
        if(managerInstance == null){
            throw new InstantiationException("NetworkManager not previously instantiated");
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

    public <T> void addToRequestQueue(@NonNull Request<T> req){
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

    public static Response.ErrorListener generateDefaultErrorHandler(@NonNull final Context context){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // int status = error.networkResponse.statusCode;
                error.printStackTrace();
                String message;
                switch (error.networkResponse.statusCode){
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        message = "Invalid data input";
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        message = "Session expired, please log in again";
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        default:
                         message = "Unknown error";
                        break;
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            }
        };
    }

    /*private static boolean checkNetCodeRange(int code, int start){
        return code >= start && code < start+100;
    }

    public static boolean requestSuccess(@NonNull HTTPCodes code){
        return checkNetCodeRange(code.getCode(), 200);
    }
    public static boolean requestClientError(@NonNull HTTPCodes code){
        return checkNetCodeRange(code.getCode(), 400);
    }
    public static boolean requestServerError(@NonNull HTTPCodes code){
        return  checkNetCodeRange(code.getCode(), 500);
    }*/


}
