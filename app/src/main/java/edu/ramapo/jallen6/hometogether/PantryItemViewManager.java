package edu.ramapo.jallen6.hometogether;

import java.util.ArrayList;

public class PantryItemViewManager {
    private ArrayList<PantryItemView> views;

    public PantryItemViewManager(){
        views = new ArrayList<>();
    }


    public int size(){
        return views.size();
    }

    public void addView(PantryItemView pIView){
        views.add(pIView);
    }


}
