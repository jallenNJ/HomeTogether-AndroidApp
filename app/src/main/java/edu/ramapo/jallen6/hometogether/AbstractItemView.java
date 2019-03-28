package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.CallSuper;
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

/**
 * Abstract base class for the ItemView classes used in the pantry and shopping cart. When it binds
 * any listener it calls a method which can be overridden in the derived class to change the
 * functionality. The actual call sequence should remain consistent.
 *
 * In addition, this class implements the Observer interface, and the update function calls the
 * redraw method
 *
 * @author Joseph Allen
 */
public abstract class AbstractItemView implements Observer {
    protected PantryItem model;     //The model to watch
    protected TableRow displayRow;  //The display row to maintain
    protected String[] keys;        //The keys which the object cares about


    /**
     * This function is a shared init function to be called by all the derived constructors
     * for shared tasks
     * @param modelToWatch The PantryItem model that needs to be observed for changes
     * @param tableRow The TableRow that the view is responsible for maintaining
     */
    protected final void init(PantryItem modelToWatch, TableRow tableRow){
        //Set up the observer
        model = modelToWatch;
        model.addObserver(this);

        //Give default values for a key
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


    /**
     * This is the method for the short on click on the display row
     * @param view The view which was clicked to trigger it.
     */
    protected void rowOnClickHandler(View view){
        if(isSelected()){
            view.setBackgroundColor(Color.TRANSPARENT);

        } else{
            view.setBackgroundColor(Color.RED);
        }
        model.toggleSelected();
    }

    /**
     * The long on click handler to be overridden by the derived classes
     * @param view The view which trigger the event
     * @return True to consume the click, False to pass it along to the on click
     */
    protected boolean rowOnLongClickHandler(final View view){
        //Create a builder to ask if the user wants to modify, delete or cancel
        AlertDialog.Builder builder = new AlertDialog.Builder(displayRow.getContext());
        builder.setCancelable(true);
        builder.setTitle("Info for " + model.getFieldAsString(PantryItem.NAME_FIELD));
        builder.setMessage(model.toString());

        //Modify button which calls the update item in the Activity's class
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
        //Cancel button
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        //Calls the delete method in the Activity's Implementation
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

    /**
     * Function which is called during a right fling on the row
     * @return True to consume the click, false to pass it on
     */
    abstract protected boolean rowOnRightFling();

    /**
     * Function which is called during a left fling on the row, this can be overridden by derived
     * classes to change the functionality
     * @return True to consume the click, false to pass it on
     */
    protected boolean rowOnLeftFling(){
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

    /**
     * This method builds all views within the row with current information, and then binds
     * the on click methods, with function calls to what can be overridden by the methods.
     *
     * If a derived class overrides this function, then they are required to use the super class's
     * implementation.
     */
    @CallSuper
    void drawToRow(){
        displayRow.removeAllViews();

        //Store the context
        Context context = displayRow.getContext();

        //Add all the keys as text views
        for(String key:keys){
            TextView buffer = new TextView(context);
            buffer.setText(model.getFieldAsString(key));
            buffer.setLayoutParams(new TableRow.LayoutParams(1));
            buffer.setGravity(View.TEXT_ALIGNMENT_CENTER);
            displayRow.addView(buffer);
        }

        //Bind the onclick and call the handler
        displayRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               rowOnClickHandler(view);
            }
        });

        //Bind the long click and call the handler
        displayRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return rowOnLongClickHandler(view);
            }
        });

        //Gesture detection class
        class PantryItemGesture extends GestureDetector.SimpleOnGestureListener {

            //Constants to check ditance
            private static final int SWIPE_MIN_DISTANCE = 120;
            private static final int SWIPE_MAX_OFF_PATH = 250;
            private static final int SWIPE_THRESHOLD_VELOCITY = 200;


            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    //Ensure they didn't go too far off path
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    // right to left swipe
                    if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        return rowOnLeftFling();

                    }
                    //left to right swipe
                    if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
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

        //Bind the above class
        final GestureDetector gestureDetector = new GestureDetector(displayRow.getContext(), new PantryItemGesture());

        displayRow.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }


    /**
     * Method to detach the row from its table, clear its table, clear the observer, and the row
     */
    public void clearModel(){
        ((ViewGroup)displayRow.getParent()).removeView(displayRow);
        model.deleteObserver(this);
        model = null;
        displayRow.removeAllViews();
        displayRow = null;
    }

    /**
     * @return The tableRow being displayed. Can be null after a clear
     */
    public TableRow getDisplayRow(){
        return displayRow;
    }

    /**
     * Makes display row visible or gone
     * @param status True to set visible, otherwise for false
     */
    public void setRowVisibility(boolean status){
        displayRow.setVisibility(status ? View.VISIBLE: View.GONE);
    }

    /**
     * See if the model name contains the substring
     * @param subString The substring to search for
     * @return True if it contains the substring, False otherwise or if String is the empty string
     */
    public boolean modelNameContains(@NonNull String subString){
        if(subString.equals("")){
            return false;
        }
        return model.getFieldAsString(PantryItem.NAME_FIELD).toLowerCase()
                .contains(subString.toLowerCase());
    }

    /**
     * See if the model category contains the substring
     * @param subString The substring to search for
     * @return True if it contains the substring, False otherwise or if String is the empty string
     */
    public boolean modelCategoryContains(@NonNull String subString){
        if(subString.equals("")){
            return false;
        }
        return model.getFieldAsString(PantryItem.CATEGORY_FIELD).toLowerCase()
                .contains(subString.toLowerCase());
    }

    /**
     *  See if the model has the tag in full
     * @param subString The tag to match in its entirety
     * @return True if it matches, false otherwise
     */
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

    /**
     * Add a generated view to the row
     * @param v The view to be added
     */
    public void addViewToRow(@NonNull View v){
        displayRow.addView(v);
    }

    @Override
    public void update(Observable observable, Object o) {
        drawToRow();
    }


    /**
     * Create a confirmation dialog for the user
     * @param title The title of the confirmation dialog
     * @param message The message to be displayed in the box
     * @param postiveConfirmListener The postive on confirm listener
     */
    protected final void showConfirmationDialog(@NonNull String title, @NonNull String message, @NonNull DialogInterface.OnClickListener postiveConfirmListener){
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
