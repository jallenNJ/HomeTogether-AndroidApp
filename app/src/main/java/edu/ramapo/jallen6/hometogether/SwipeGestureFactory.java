package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public SwipeGesture build(@NonNull SwipeGestureFactoryType type,
                              @NonNull SwipeHandler handler1,@Nullable SwipeHandler handler2){

        if(handler2 == null){
            handler2 = new SwipeHandler() {
                @Override
                public boolean onSwipe() {
                    return false;
                }
            };
        }

        SwipeDirectionDetector directionDetector = type == SwipeGestureFactoryType.VERTICAL?
                new SwipeVerticalDetector(): new SwipeHorizontalDetector();

        return new SwipeGesture(directionDetector,handler1, handler2);


    }


}
