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

/**
 * This singleton class manages the Volley Request queue for the app to communicate with the server
 * and maintains the host and port of the server
 */
public class NetworkManager {

    private RequestQueue requestQueue;  //The request queue to send off requests
    private static NetworkManager managerInstance;  //The singleton instance
    private static String host = "http://172.17.137.213:3000"; //The protocol and host
    private static String port = "3000"; //The port the server is using


    /**
     * Creates the singleton with the application context and sets cookie polcy
     * @param context The application context to create with
     */
    private NetworkManager (Context context){
        requestQueue = getRequestQueue(context);
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    /**
     * Get the ip address of the server
     * @return The just the ip of the server from the host string
     */
    @NonNull
    public static String getIP(){
        return host.substring(7, host.length()-5);
    }

    /**
     * Sets the ip of the server for debugging
     * @param url The new ip of the server
     */
    public static void setHost(@NonNull String url){
        if(url.length() < 1){
            return;
        }
        host = "http://"+url+":"+port;
    }

    /**
     * Get the instance of the singleton to send a request. Allows for creating a new one if it doesn't exist
     * @param context The context to create the request with
     * @return The instance of the singleton
     */
    public static synchronized NetworkManager getInstance(@NonNull Context context){
        if(managerInstance == null){
            managerInstance = new NetworkManager(context);
        }
        return managerInstance;
    }

    /**
     * Get the instance of the singleton to send a request. The singleton must have already been created to be referenced without a context
     * @return The instance of the singleton
     * @throws InstantiationException Singleton is not instantiated and without a context it cannot be created
     */
    public static synchronized NetworkManager getInstance() throws InstantiationException {
        if(managerInstance == null){
            throw new InstantiationException("NetworkManager not previously instantiated");
        }
        return managerInstance;
    }

    /**
     * Get the request queue
     * @param context The application context to create the queue with
     * @return The Request queue to send a request on
     */
    private RequestQueue getRequestQueue(Context context){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Get the requestQueue if its instantiated
     * @return The request queue to add requests too
     */

    //TODO: This should be private, check what uses this an update it
    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            throw new IllegalStateException("Request queue was not initialized " +
                    "This is most likely caused by an invalid context being passed to getInstance");
        }
        return requestQueue;
    }

    /**
     * Add a generated request directly to the request queue
     * @param req The request which is to be sent to the server
     * @param <T> The response of the type specified by the request
     */
    public <T> void addToRequestQueue(@NonNull Request<T> req){
        getRequestQueue().add(req);
    }

    /**
     * Gets the host name as a URI Builder so requests can be built by the method that needs it
     * @return The host as a URIbuilder ready to accept path/query options
     */
    public static Uri.Builder getHostAsBuilder(){
        return Uri.parse(NetworkManager.host).buildUpon();
    }

    /**
     * Generate a default error handler which does not have access to the context
     * @return The default response Error Listener
     */
    //TODO: Make sure this is only being used by instances without the context
    public static Response.ErrorListener generateDefaultErrorHandler(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    /**
     * Generates a default error handler which makes a Toast containg user information
     * @param context The context to sends the toasts to
     * @return The error handler which can handle the codes from the server
     */
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
}
