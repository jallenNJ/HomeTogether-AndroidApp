package edu.ramapo.jallen6.hometogether;

import android.view.ViewGroup;

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
        pIView.drawToRow();
    }

    public PantryItemView getSingleSelected(){
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

    public void drawAll(){
        for(PantryItemView view :views){
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

        PantryItemView target = findViewByName(name);
        if(target == null){
            return;
        }
        target.drawToRow();

    }

    public PantryItemView findViewByName(String name){
        for(PantryItemView view:views){
            if(view.getModel().getFieldAsString(PantryItem.NAME_FIELD).equals(name)){
                return view;
            }
        }
        return null;
    }

    public void delete(PantryItemView target){
        target.clearModel();
        views.remove(target);

    }

}
