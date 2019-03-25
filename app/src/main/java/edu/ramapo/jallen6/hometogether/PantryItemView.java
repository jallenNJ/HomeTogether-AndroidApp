package edu.ramapo.jallen6.hometogether;

import android.content.DialogInterface;
import android.widget.TableRow;
import android.widget.Toast;

public class PantryItemView extends AbstractItemView {



    PantryItemView(PantryItem modelToWatch, TableRow tableRow){
        init(modelToWatch, tableRow);
        keys = new String[] {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.FORMATTED_EXPIRES_FIELD, PantryItem.CATEGORY_FIELD};
    }

    protected boolean rowOnRightFling(){
        //displayRow.setBackgroundColor(Color.YELLOW);
        //TODO" Remove if in shopping cart?
        showConfirmationDialog("Move to shopping cart?",
                "Are you sure you want to move "
                        + model.getFieldAsString(PantryItem.NAME_FIELD) + " to shopping cart?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        model.setSelected(true);
                        try{
                            //TODO: Find define location for shopping
                            ((PantryItemCrud)displayRow.getContext()).moveItem(PantryItemView.this, "shopping");
                        } catch(ClassCastException e){
                            Toast.makeText(displayRow.getContext(),
                                    "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return false;
    }

}
