package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public abstract class AbstractItemView implements Observer {
    protected PantryItem model;
    protected TableRow displayRow;
    protected String[] keys;

    protected void init(PantryItem modelToWatch, TableRow tableRow){
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



    protected void rowOnClickHandler(View view){
        if(isSelected()){
            view.setBackgroundColor(Color.TRANSPARENT);

        } else{
            view.setBackgroundColor(Color.RED);
        }
        model.toggleSelected();
    }

    protected boolean rowOnLongClickHandler(final View view){
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
    abstract protected boolean rowOnRightFling();

    protected boolean rowOnLeftFling(){
        // displayRow.setBackgroundColor(Color.GREEN);
        showConfirmationDialog("Delete Confirmation",
                "Are you sure you want to delete "
                        + model.getFieldAsString(PantryItem.NAME_FIELD)+"?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: Confirm Fix  of mostly repeated code?
                        // or move to function
                        model.setSelected(true);

                        try{
                            ((PantryItemCrud)displayRow.getContext()).deleteItem(displayRow);
                        } catch(ClassCastException e){
                            Toast.makeText(displayRow.getContext(),
                                    "Failed to update from this screen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return false;
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
               rowOnClickHandler(view);
            }
        });

        displayRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return rowOnLongClickHandler(view);
            }
        });

        class PantryItemGesture extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_MIN_DISTANCE = 120;
            private static final int SWIPE_MAX_OFF_PATH = 250;
            private static final int SWIPE_THRESHOLD_VELOCITY = 200;


            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        return rowOnLeftFling();

                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        return rowOnRightFling();
                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                //Return false to allow for the onclick to file
                return false;
            }

        }
        final GestureDetector gestureDetector = new GestureDetector(displayRow.getContext(), new PantryItemGesture());

        displayRow.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
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

    public void addViewToRow(@NonNull View v){
        displayRow.addView(v);
    }

    @Override
    public void update(Observable observable, Object o) {
        drawToRow();
    }

    protected void showConfirmationDialog(@NonNull String title, @NonNull String message, @NonNull DialogInterface.OnClickListener postiveConfirmListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(displayRow.getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", postiveConfirmListener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

}
