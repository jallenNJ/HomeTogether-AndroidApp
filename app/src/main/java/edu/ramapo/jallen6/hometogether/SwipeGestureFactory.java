package edu.ramapo.jallen6.hometogether;

public final class SwipeGestureFactory {
    public enum SwipeGestureFactoryType {
        VERTICAL, HORIZONTAL
    }

    private static SwipeGestureFactory factoryInstance;

    private SwipeGestureFactory(){

    }

    public static synchronized SwipeGestureFactory getInstance(){
        if(factoryInstance == null){
            factoryInstance = new SwipeGestureFactory();
        }
        return factoryInstance;
    }


}
