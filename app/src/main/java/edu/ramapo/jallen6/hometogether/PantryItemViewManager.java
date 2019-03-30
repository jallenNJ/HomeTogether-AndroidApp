package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * This file manages all AbstractItemViews and keeps consistent features between activities
 */
public class PantryItemViewManager {
    /**
     * The list of all views in the activity
     */
    private ArrayList<AbstractItemView> views;

    /**
     * No arg constructor that init the views array list
     */
    public PantryItemViewManager(){
        views = new ArrayList<>();
    }

    /**
     * Gets the size of the views
     * @return The size of the views, always non-negative
     */
    public int size(){
        return views.size();
    }

    /**
     * Add an AbstractItemView to the manager
     * @param pIView The AbstractItemView to be added
     */
    public void addView(@NonNull AbstractItemView pIView){
        views.add(pIView);
        pIView.drawToRow();
    }

    /**
     * Gets the selected item if and only if one is selected
     * @return The selected item if and only if one is selected, null otherwise, including multiple selections
     */
    public AbstractItemView getSingleSelected(){
        ArrayList<AbstractItemView> update = getSelected();
        if(update.size() != 1){
            return null;
        }
        return update.get(0);

    }


    /**
     * Gets a list of all views which are selected
     * @return The array list of all selected views.
     */
    private ArrayList<AbstractItemView> getSelected(){
        ArrayList<AbstractItemView> selected = new ArrayList<>();
        for(AbstractItemView item:views){
            if(item.isSelected()){
                selected.add(item);
            }
        }
        return selected;
    }

    /**
     * Gets the size of the selected without getting a reference to use
     * @return The amount selected, always non-negative
     */
    private int sizeOfSelected(){
        int selected = 0;
        for(AbstractItemView item:views){
            if(item.isSelected()){
                selected++;
            }
        }
        return selected;
    }

    /**
     * Force a draw call to all views in the manager
     */
    public void drawAll(){
        for(AbstractItemView view :views){
            view.drawToRow();
        }
    }

    /**
     * Force a draw call to view at the specified index
     * @param index The index of the view to give a draw call to
     */
    public void drawView(int index){
        if(index < 0 || index >= views.size()){
            return;
        }

        views.get(index).drawToRow();
    }

    /**
     * Force a draw call to a view which is for a specific name
     * @param name The name of the view to force a draw call to
     */
    public void drawView(@NonNull String name){
        if(name.equals("")){
            return;
        }

        AbstractItemView target = findViewByName(name);
        if(target == null){
            return;
        }
        target.drawToRow();

    }

    /**
     * Find a view by its name and return a reference to it
     * @param name The name to search for
     * @return The view with the name if found, null otherwise
     */
    public AbstractItemView findViewByName(String name){
        for(AbstractItemView view:views){
            if(view.getModel().getFieldAsString(PantryItem.NAME_FIELD).equals(name)){
                return view;
            }
        }
        return null;
    }

    /**
     * Deletes the selected item from the server
     * @param context The context of the activity which called it
     * @throws IllegalStateException Thrown if 0 or 2>= items selected
     */
    public void deleteSelectedFromServer(@NonNull final Context context) throws IllegalStateException{
        final AbstractItemView selected = getSingleSelected();
        if(selected == null){
            throw new IllegalStateException("More than one item selected");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                NetworkManager.getHostAsBuilder().appendPath("household").appendPath("pantry")
                        .appendQueryParameter(PantryItem.NAME_FIELD,
                                selected.getModel().getFieldAsString(PantryItem.NAME_FIELD))
                        .toString()
                , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                       delete(selected);

                    }
                }, NetworkManager.generateDefaultErrorHandler() );

        NetworkManager.getInstance(context).addToRequestQueue(request);
    }

    /**
     * Move an item in the pantry from its current location to its new location
     * @param v The view to be moved
     * @param newLoc The name of the new location to be moved to
     * @param context The context of the calling activity
     * @throws JSONException If the object fails to format as a JSONObject
     */
    public void moveItem(@NonNull final AbstractItemView v,@NonNull String newLoc,@NonNull final Context context)
    throws JSONException{

        JSONObject params = null;
        params = v.getModel().toJSONObject();
        //TODO: validation on newLoc
        params.put( PantryItem.LOCATION_FIELD, newLoc);
        String url = NetworkManager.getHostAsBuilder().appendPath("household")
                .appendPath("pantry").toString();


        //TODO: Move to item manager, throw expection with message and print that out;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //v.getModel().applyUpdate(response); //Should be deleted and on needed
                        delete(v);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                String message = "";
                switch (error.networkResponse.statusCode){
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        message = "Error on field formatting";
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        message = "Session expired, please log in again";
                        break;
                    default:
                        message = "Server error";
                        break;
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

        NetworkManager.getInstance(context).addToRequestQueue(request);


    }

    /**
     * Deletes the item from the view manager but *NOT* the server
     * @param target The item to be removed
     */
    public void delete(AbstractItemView target){
        target.clearModel();
        views.remove(target);

    }

    /**
     * Toggle visibility of items which match the search
     * @param searchType The type of search to run based on the enum value
     * @param searchTerm The term to be used in the search
     */
    public void toggleVisibilityBySearch(PantryItemSearchTerms searchType, String searchTerm){
        if(searchTerm.equals("")){
            setAllVisible();
            return;
        }

        switch (searchType){
            case NAME_SEARCH:
                for(AbstractItemView view:views){
                    view.setRowVisibility(view.modelNameContains(searchTerm));
                }
                break;
            case CATEGORY_SEARCH:
                for(AbstractItemView view:views){
                    view.setRowVisibility(view.modelCategoryContains(searchTerm));
                }
                break;
            case TAG_SEARCH:
                for(AbstractItemView view:views){
                    view.setRowVisibility(view.modelHasTag(searchTerm));
                }
                break;
            case LOCATION_SEARCH:
                for(AbstractItemView view:views){
                    view.setRowVisibility(view.modelInLocation(searchTerm));
                }
                break;
            default:
                Log.e("InvalidEnum", "Invalid enum in toggleVisibilityBySearch");
                setAllVisible();
        }

    }

    /**
     * Set all views to the visible state
     */
    public void setAllVisible(){
        for(AbstractItemView view :views){
            view.setRowVisibility(true);
        }
    }




}
