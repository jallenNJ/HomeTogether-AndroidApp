package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class PantryItemView implements Observer {

    private PantryItem model;
    private TableRow displayRow;
    private String[] keys;

    PantryItemView(PantryItem modelToWatch, TableRow tableRow){
        model = modelToWatch;
        model.addObserver(this);
        displayRow = tableRow;
        keys = new String[] {PantryItem.NAME_FIELD, PantryItem.QUANTITY_FIELD,
                PantryItem.FORMATTED_EXPIRES_FIELD, PantryItem.CATEGORY_FIELD};
    }

    boolean isSelected(){
        return model.isSelected();
    }
    public void setKeys(@NonNull String[] newKeys){
        if(newKeys.length > 0){
            keys = newKeys;
        }
    }

    public final PantryItem getModel(){
        return model;
    }


    void drawToRow(){
        displayRow.removeAllViews();

        Context context = displayRow.getContext();
        TextView buffer;

        for(String key:keys){
            buffer = new TextView(context);
            buffer.setText(model.getFieldAsString(key));
            buffer.setLayoutParams(new TableRow.LayoutParams(1));
            buffer.setGravity(View.TEXT_ALIGNMENT_CENTER);
            displayRow.addView(buffer);
        }

        displayRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelected()){
                    view.setBackgroundColor(Color.TRANSPARENT);

                } else{
                    view.setBackgroundColor(Color.RED);
                }
                model.toggleSelected();

            }
        });

        displayRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(displayRow.getContext());
                builder.setCancelable(true);
                builder.setTitle("Info for " + model.getFieldAsString(PantryItem.NAME_FIELD));
                builder.setMessage(model.toString());

                builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        model.setSelected(true);

                        try{
                            ((PantryItemCrud)displayRow.getContext()).updateItem(view);
                        } catch(ClassCastException e){
                            Toast.makeText(displayRow.getContext(),
                                    "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        model.setSelected(true);

                        try{
                            ((PantryItemCrud)displayRow.getContext()).deleteItem(view);
                        } catch(ClassCastException e){
                            Toast.makeText(displayRow.getContext(),
                                    "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();

                //Return true to "consume" click and stop onclick from firing
                return true;
            }
        });
    }

    public void clearModel(){
        ((ViewGroup)displayRow.getParent()).removeView(displayRow);
        model.deleteObserver(this);
        model = null;
        displayRow.removeAllViews();
        displayRow = null;
    }

    public TableRow getDisplayRow(){
        return displayRow;
    }

    public void setRowVisibility(boolean status){
        displayRow.setVisibility(status ? View.VISIBLE: View.GONE);
    }

    public boolean modelNameContains(@NonNull String subString){
        if(subString.equals("")){
            return false;
        }
       return model.getFieldAsString(PantryItem.NAME_FIELD).toLowerCase()
               .contains(subString.toLowerCase());
    }

    public boolean modelCategoryContains(@NonNull String subString){
        if(subString.equals("")){
            return false;
        }
        return model.getFieldAsString(PantryItem.CATEGORY_FIELD).toLowerCase()
                .contains(subString.toLowerCase());
    }

    public boolean modelHasTag(@NonNull String subString){
        if(subString.equals("")){
            return false;
        }
        subString = subString.toLowerCase();
        String[] tags = model.getTags();
        for(String tag:tags){
            if(subString.equals(tag.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object o) {
        drawToRow();
    }
}
