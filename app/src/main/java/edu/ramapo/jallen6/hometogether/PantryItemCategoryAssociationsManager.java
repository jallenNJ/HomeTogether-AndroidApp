package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.HashMap;

public final class PantryItemCategoryAssociationsManager {

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

    private PantryItemCategoryAssociationsManager(){
        associations = new HashMap<>();


        //Grain Canned Frozen Fruit Vegetable Meat Ingredient Spice Seafood Other
        associations.put("alcohol", new PantryItemCategoryAssociations("pantry",
                "ABV"));
        associations.put("beverage", new PantryItemCategoryAssociations("refrigerator",
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
        associations.put("meat", new PantryItemCategoryAssociations("refrigerator",
                null));
        associations.put("ingredient", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("spice", new PantryItemCategoryAssociations("pantry",
                null));
        associations.put("seafood", new PantryItemCategoryAssociations("refrigerator",
                null));
        associations.put("other", new PantryItemCategoryAssociations("pantry",
                null));
    }

    public static synchronized PantryItemCategoryAssociationsManager getInstance(){
        if(instance == null){
            instance = new PantryItemCategoryAssociationsManager();
        }
        return instance;
    }

    public String getDefaultLocation(@NonNull String key) throws InvalidParameterException {
        PantryItemCategoryAssociations val = associations.get(key.toLowerCase());
        if(val == null){
            throw new InvalidParameterException("No associations for given key");
        }
        return val.getDefaultLocation();
    }
    public String getSpecialFieldName(@NonNull String key) throws InvalidParameterException{
        PantryItemCategoryAssociations val = associations.get(key.toLowerCase());
        if(val == null){
            throw new InvalidParameterException("No associations for given key");
        }
        return val.getSpecialFieldName();
    }
}
