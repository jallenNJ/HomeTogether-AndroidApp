package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PantryItemViewManager {
    private ArrayList<AbstractItemView> views;

    public PantryItemViewManager(){
        views = new ArrayList<>();
    }


    public int size(){
        return views.size();
    }

    public void addView(AbstractItemView pIView){
        views.add(pIView);
        pIView.drawToRow();
    }

    public AbstractItemView getSingleSelected(){
        ArrayList<AbstractItemView> update = getSelected();
        if(update.size() != 1){
            return null;
        }
        return update.get(0);

    }


    private ArrayList<AbstractItemView> getSelected(){
        ArrayList<AbstractItemView> selected = new ArrayList<>();
        for(AbstractItemView item:views){
            if(item.isSelected()){
                selected.add(item);
            }
        }
        return selected;
    }

    private int sizeOfSelected(){
        int selected = 0;
        for(AbstractItemView item:views){
            if(item.isSelected()){
                selected++;
            }
        }
        return selected;
    }

    public void drawAll(){
        for(AbstractItemView view :views){
            view.drawToRow();
        }
    }

    public void drawView(int index){
        if(index < 0 || index >= views.size()){
            return;
        }

        views.get(index).drawToRow();
    }

    public void drawView(String name){
        if(name == null || name.equals("")){
            return;
        }

        AbstractItemView target = findViewByName(name);
        if(target == null){
            return;
        }
        target.drawToRow();

    }

    public AbstractItemView findViewByName(String name){
        for(AbstractItemView view:views){
            if(view.getModel().getFieldAsString(PantryItem.NAME_FIELD).equals(name)){
                return view;
            }
        }
        return null;
    }

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

    public void delete(AbstractItemView target){
        target.clearModel();
        views.remove(target);

    }

    public void toggleVisibilityBySearch(PantryItemSearchTerms searchType, String searchTerm){
        if(searchTerm.equals("")){
            setAllVisibile();
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
            default:
                Log.e("InvalidEnum", "Invalid enum in toggleVisibilityBySearch");
                setAllVisibile();
        }

    }

    private void setAllVisibile(){
        for(AbstractItemView view :views){
            view.setRowVisibility(true);
        }
    }




}
