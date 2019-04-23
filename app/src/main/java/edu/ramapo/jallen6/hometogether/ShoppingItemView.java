package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

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
     * Implements a dialog which allows for the user to choose where the item moves to
     * @return False
     */
    @Override
    protected boolean rowOnRightFling() {
        final Spinner locationSpinner = new Spinner(displayRow.getContext());
        
        String[] locations = ActiveHousehold.getInstance().getPantryLocations();
        if(locations == null){
            locations = new String[]{"unsorted"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(displayRow.getContext(),
                R.layout.support_simple_spinner_dropdown_item, locations);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        AlertDialog.Builder builder = generateConfirmationDialog("Move pantry item",
                "What location do you want to move the item to?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                model.setSelected(true);
                try{
                    ((PantryItemCrud)displayRow.getContext()).moveItem(ShoppingItemView.this,
                            locationSpinner.getSelectedItem().toString());
                } catch(ClassCastException e){
                    Toast.makeText(displayRow.getContext(),
                            "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(locationSpinner);
        builder.show();


        return false;
    }
}
