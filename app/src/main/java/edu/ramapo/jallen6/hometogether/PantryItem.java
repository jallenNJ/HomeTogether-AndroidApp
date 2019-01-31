package edu.ramapo.jallen6.hometogether;

import java.util.Observable;

public class PantryItem extends Observable {

    private String name;
    private int quanity;
    private String category;
    private String[] tags;
    //private something Data
    private boolean selected;

    public PantryItem(String n, int q, String c, String[] t){
          name = n;
          quanity = q;
          category=c;
          tags = t;
    }



    boolean isSelected(){
        return selected;
    }








}
