package edu.ramapo.jallen6.hometogether;

import android.content.DialogInterface;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * Concrete Implementation of AbstractItemView for the Pantry Screen
 */
public class PantryItemView extends AbstractItemView {


    /**
     * Constructor which calls the super init and sets the keys
     * @param modelToWatch The model to be observed
     * @param tableRow The table row to maintain
     */
    PantryItemView(PantryItem modelToWatch, TableRow tableRow){
        init(modelToWatch, tableRow);
        keys = new String[] {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.FORMATTED_EXPIRES_FIELD, PantryItem.CATEGORY_FIELD};
    }

    /**
     *  Override to move item to shopping cart with a confirm dialog
     * @return Always False to allow the click to go through
     */
    protected boolean rowOnRightFling(){
        showConfirmationDialog("Move to shopping cart?",
                "Are you sure you want to move "
                        + model.getFieldAsString(PantryItem.NAME_FIELD) + " to shopping cart?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        model.setSelected(true);
                        try{
                            ((PantryItemCrud)displayRow.getContext()).moveItem(
                                    PantryItemView.this, ActiveHousehold.SHOPPING_LOCATION);
                        } catch(ClassCastException e){
                            Toast.makeText(displayRow.getContext(),
                                    "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return false;
    }
}
