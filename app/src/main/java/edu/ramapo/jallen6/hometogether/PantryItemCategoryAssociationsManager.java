package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * This singleton maintains all the run time references of categories and their default location
 * and if they have a special field name
 */
public final class PantryItemCategoryAssociationsManager {

    /**
     * Private nested class which is a data only class to keep in the map
     */
    private final class PantryItemCategoryAssociations {
        private String defaultLocation;
        private String specialFieldName;

        PantryItemCategoryAssociations(@NonNull String dLocation, @Nullable String sFieldName){
            defaultLocation = dLocation;
            specialFieldName = sFieldName;
        }

        public  String getDefaultLocation(){
            return defaultLocation;
        }
        public  String getSpecialFieldName(){
            return  specialFieldName;
        }
    }

    private static PantryItemCategoryAssociationsManager instance;
    private HashMap<String, PantryItemCategoryAssociations> associations;

    /**
     * Private Constructor for the singleton, which initialized the map
     */
    private PantryItemCategoryAssociationsManager(){
        associations = new HashMap<>();


        //Grain Canned Frozen Fruit Vegetable Meat Ingredient Spice Seafood Other
        associations.put("alcohol", new PantryItemCategoryAssociations("pantry",
                "ABV"));
        associations.put("beverage", new PantryItemCategoryAssociations("fridge",
                null));
        associations.put("grain", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("canned", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("fruit", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("frozen", new PantryItemCategoryAssociations("freezer",
                null));
        associations.put("vegetable", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("meat", new PantryItemCategoryAssociations("fridge",
                null));
        associations.put("ingredient", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("spice", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("seafood", new PantryItemCategoryAssociations("fridge",
                null));
        associations.put("other", new PantryItemCategoryAssociations("pantry",
                null));
    }

    /**
     * Gets the instance of the singleton, creates if it is missing
     * @return The reference to the singleton
     */
    public static synchronized @NonNull PantryItemCategoryAssociationsManager getInstance(){
        if(instance == null){
            instance = new PantryItemCategoryAssociationsManager();
        }
        return instance;
    }

    /**
     * Gets the defualt Location for a given category
     * @param key The category to look for, case insensitve
     * @return The default location for that category to be in
     * @throws InvalidParameterException Thrown if key is not in the map
     */
    public @NonNull String getDefaultLocation(@NonNull String key) throws InvalidParameterException {
        PantryItemCategoryAssociations val = associations.get(key.toLowerCase());
        if(val == null){
            throw new InvalidParameterException("No associations for given key");
        }
        return val.getDefaultLocation();
    }

    /**
     *  Gets the special field name for a given category
     * @param key The category to look for, case insensitive
     * @return The name of the special field, null if category has none
     * @throws InvalidParameterException Thrown if key is not in the map
     */
    public String getSpecialFieldName(@NonNull String key) throws InvalidParameterException{
        PantryItemCategoryAssociations val = associations.get(key.toLowerCase());
        if(val == null){
            throw new InvalidParameterException("No associations for given key");
        }
        return val.getSpecialFieldName();
    }

}
