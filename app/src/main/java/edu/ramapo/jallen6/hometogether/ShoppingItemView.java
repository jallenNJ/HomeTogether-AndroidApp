package edu.ramapo.jallen6.hometogether;

import android.widget.CheckBox;
import android.widget.TableRow;

/**
 * Concrete implementation of AbstractItemView for the shopping list
 */
public class ShoppingItemView extends AbstractItemView {

    /**
     * Calls the super init and set the keys
     * @param modelToWatch The model to be observed
     * @param tableRow The tow to update
     */
    public ShoppingItemView(PantryItem modelToWatch, TableRow tableRow){
        init(modelToWatch, tableRow);
        keys = new String[]{PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD};
    }


    /**
     * Calls super draw to row, then appends the checkbox
     */
    void drawToRow(){
       super.drawToRow();
       displayRow.addView(new CheckBox(displayRow.getContext()));
    }

    /**
     * Implements an empty function
     * @return False
     */
    @Override
    protected boolean rowOnRightFling() {
        return false;
    }
}
