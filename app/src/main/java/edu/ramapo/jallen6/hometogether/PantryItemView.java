package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

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
        keys = new String[] {"name", "quantity", "expires", "category"};
    }

    boolean isSelected(){
        return model.isSelected();
    }
    public void setKeys(@NonNull String[] newKeys){
        if(newKeys.length > 0){
            keys = newKeys;
        }
    }

    //abstract TableRow drawItem(Context context);



    void drawToRow(){
        displayRow.removeAllViews();
        Context context = displayRow.getContext();
        TextView buffer;
      //  TableRow row = new TableRow(context);
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
    }

    public TableRow getDisplayRow(){
        return displayRow;
    }


    @Override
    public void update(Observable observable, Object o) {

    }
}
