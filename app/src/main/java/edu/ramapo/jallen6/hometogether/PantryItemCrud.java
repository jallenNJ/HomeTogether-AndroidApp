package edu.ramapo.jallen6.hometogether;

import android.view.View;

/**This interface is for an activity to implement when it has pantry items which can have CRUD
 * applied. This allows for the views to communicate up to the main activity
 */
public interface PantryItemCrud {
   /**
    * Used to update an item from another class
    * @param v The view which triggered it
    */
   void updateItem (View v);

   /**
    * Used to delete an item from another class
    * @param v The view which triggered it
    */
   void deleteItem (View v);

   /**
    * Moves an item to a new location without other modifications
    * @param v The item being moved
    * @param newLoc The new location to be moved too
    */
   void moveItem (AbstractItemView v, String newLoc);
}
