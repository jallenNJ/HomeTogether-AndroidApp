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

    public PantryItemView updateSelected(){
        ArrayList<PantryItemView> update = getSelected();
        if(update.size() != 1){
            return null;
        }
        return update.get(0);

    }


    private ArrayList<PantryItemView> getSelected(){
        ArrayList<PantryItemView> selected = new ArrayList<>();
        for(PantryItemView item:views){
            if(item.isSelected()){
                selected.add(item);
            }
        }
        return selected;
    }

    private int sizeOfSelected(){
        int selected = 0;
        for(PantryItemView item:views){
            if(item.isSelected()){
                selected++;
            }
        }
        return selected;
    }


}
