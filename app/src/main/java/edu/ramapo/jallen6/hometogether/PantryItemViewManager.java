package edu.ramapo.jallen6.hometogether;

import android.util.Log;
import android.view.ViewGroup;

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
