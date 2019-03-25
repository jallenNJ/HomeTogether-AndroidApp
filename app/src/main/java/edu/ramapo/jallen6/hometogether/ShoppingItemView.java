package edu.ramapo.jallen6.hometogether;

import android.widget.CheckBox;
import android.widget.TableRow;

import java.util.Observable;

public class ShoppingItemView extends AbstractItemView {


    public ShoppingItemView(PantryItem modelToWatch, TableRow tableRow){
        init(modelToWatch, tableRow);
        keys = new String[]{PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD};
    }


    void drawToRow(){
       super.drawToRow();
       displayRow.addView(new CheckBox(displayRow.getContext()));
    }

    @Override
    protected boolean rowOnRightFling() {
        return false;
    }
}
