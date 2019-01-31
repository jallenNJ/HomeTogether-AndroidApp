package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.widget.TableRow;

import java.util.Observer;

public abstract class PantryItemView implements Observer {

    private PantryItem model;

    PantryItemView(PantryItem modelToWatch){
        model = modelToWatch;
        model.addObserver(this);
    }


    abstract TableRow drawItem(Context context);



}
