package edu.ramapo.jallen6.hometogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT );
        layoutParams.setMargins(10,20,10,10);
        displayRow.setLayoutParams(layoutParams);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(displayRow.getContext(), R.style.AppDialogTheme);
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
        final Context context = displayRow.getContext();
        final Calendar current = Calendar.getInstance();

        //Add all the keys as text views
        for(String key:keys){
            TextView buffer = new TextView(context);
            buffer.setText(model.getFieldAsString(key).length() > 15?
                    model.getFieldAsString(key).substring(0,12)+"..."
                    :model.getFieldAsString(key));
            buffer.setLayoutParams(new TableRow.LayoutParams(1));
            buffer.setTextColor(ContextCompat.getColor(context, R.color.fontColor));
            buffer.setShadowLayer(1,5,5,ContextCompat.getColor(context, R.color.shadowColor));
            buffer.setGravity(Gravity.LEFT);
            if(key.equals(PantryItem.FORMATTED_EXPIRES_FIELD)){

                Calendar thisDate = JSONFormatter.fullDateStringToCalendar( model.getFieldAsString(PantryItem.EXPIRES_FIELD));
                if(thisDate.get(Calendar.YEAR) < current.get(Calendar.YEAR) ||
                        ((thisDate.get(Calendar.YEAR) == current.get(Calendar.YEAR)) &&
                                thisDate.get(Calendar.DAY_OF_YEAR) < current.get(Calendar.DAY_OF_YEAR))){
                    buffer.setTextColor(Color.RED);
                    //TODO: FIX this elseif to handle new years
                } else if(thisDate.get(Calendar.DAY_OF_YEAR) +5 -
                        current.get(Calendar.DAY_OF_YEAR)> 0 &&
                        thisDate.get(Calendar.DAY_OF_YEAR) < current.get(Calendar.DAY_OF_YEAR) + 5 ){
                    buffer.setTextColor(Color.YELLOW);
                }

            }
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


        SwipeGestureFactory.buildAndBindDetector(displayRow,
                SwipeGestureFactory.SwipeGestureFactoryType.HORIZONTAL,
                new SwipeHandler() {
                    @Override
                    public boolean onSwipe() {
                        return rowOnLeftFling();
                    }
                },
                new SwipeHandler() {
                    @Override
                    public boolean onSwipe() {
                        return rowOnRightFling();
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
     * Search for if the model in in a location in the pantry
     * @param location The location to see if the item is located in, case insensitive
     * @return True if in the location, false otherwise
     */
    public boolean modelInLocation (@NonNull String location){
        if(location.equals("")){
            return false;
        }
        return model.getFieldAsString(PantryItem.LOCATION_FIELD)
                .toLowerCase().equals(location.toLowerCase());
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
     * Creates  a confirmation dialog for the user
     * @param title The title of the confirmation dialog
     * @param message The message to be displayed in the box
     * @param positiveConfirmListener The positive on confirm listener
     * @return The builder of the AlertDialog
     */
    protected final AlertDialog.Builder generateConfirmationDialog(@NonNull String title,
                                                                   @NonNull String message,
                                                                   @NonNull DialogInterface
                                                                           .OnClickListener
                                                                           positiveConfirmListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(displayRow.getContext(), R.style.AppDialogTheme);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", positiveConfirmListener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder;
    }

    /**
     * Create and show a confirmation dialog for the user
     * @param title The title of the confirmation dialog
     * @param message The message to be displayed in the box
     * @param positiveConfirmListener The positive on confirm listener
     */
    protected final void showConfirmationDialog(@NonNull String title, @NonNull String message,
                                                @NonNull DialogInterface.OnClickListener
                                                        positiveConfirmListener){

        generateConfirmationDialog(title, message, positiveConfirmListener).show();
    }

}
