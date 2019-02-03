package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.widget.TableRow;

import java.util.Observer;

public abstract class PantryItemView implements Observer {

    private PantryItem model;
    private TableRow displayRow;

    PantryItemView(PantryItem modelToWatch, TableRow tableRow){
        model = modelToWatch;
        model.addObserver(this);
        displayRow = tableRow;
    }

    boolean isSelected(){
        return model.isSelected();
    }


    abstract TableRow drawItem(Context context);



}
