package edu.ramapo.jallen6.hometogether;

import android.view.View;

//This interface is for an activity to implement when it has pantry items which can have CRUD
// applied. This allows for the views to communicate up to the main activity;
public interface PantryItemCrud {



   void updateItem (View v);
   void deleteItem (View v);
   void moveItem (PantryItemView v, String newLoc);
}
