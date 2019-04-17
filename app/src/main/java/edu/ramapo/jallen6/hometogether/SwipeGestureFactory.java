package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class SwipeGestureFactory {
    public enum SwipeGestureFactoryType {
        VERTICAL, HORIZONTAL
    }


    private SwipeGestureFactory(){

    }

    /**
     * Prevents clone from happening
     * @throws CloneNotSupportedException As this class is static only
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone static only class");
    }



    public static SwipeGesture build(@NonNull SwipeGestureFactoryType type,
                              @Nullable SwipeHandler handler1,@Nullable SwipeHandler handler2){

        if(handler1 == null){
            handler1 = new SwipeHandler() {
                @Override
                public boolean onSwipe() {
                    return false;
                }
            };
        }

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
