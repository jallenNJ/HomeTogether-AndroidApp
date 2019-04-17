package edu.ramapo.jallen6.hometogether;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Static factory to create (and bind) gestures to detect swipes
 */
public final class SwipeGestureFactory {
    /**
     * Enums to be used for the type of swipe the GestureDetector will be looking for
     */
    public enum SwipeGestureFactoryType {
        VERTICAL, HORIZONTAL
    }

    private final static SwipeHandler defaultGesture; //Handler to used when null provided

    static {
        //Have it return false to pass on gesture
        defaultGesture = new SwipeHandler() {
            @Override
            public boolean onSwipe() {
                return false;
            }
        };
    }

    /**
     * Static, so constructor should never be called
     */
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


    /**
     * Create a SwipeGesture and attach the handlers to it
     * @param type The type of Swipe the detector will be looking for
     * @param handler1 The handler to be called on the detectors first swipe
     * @param handler2 The handler to be called on the detectors second swipe
     * @return The built SwipeGesture
     */
    public static SwipeGesture build(@NonNull SwipeGestureFactoryType type,
                              @Nullable SwipeHandler handler1,@Nullable SwipeHandler handler2){

        //If either handler is null, attach the default static handler
        if(handler1 == null){
            handler1 = defaultGesture;
        }
        if(handler2 == null){
            handler2 = defaultGesture;
        }

        //Attach the correct detector
        SwipeDirectionDetector directionDetector = type == SwipeGestureFactoryType.VERTICAL?
                new SwipeVerticalDetector(): new SwipeHorizontalDetector();

        //Create and return the gesture
        return new SwipeGesture(directionDetector,handler1, handler2);

    }

    /**
     * Build the Swipe detector, and attach it to the view
     * @param context The context for the Gesture Detector
     * @param view The view to attach the Swipe Detector too
     * @param type The type of Swipe Detector to use
     * @param handler1 The handler for the first swipe of the detector
     * @param handler2 The handler for the second swipe of the detector
     */
    public static void buildAndBindDetector(@NonNull Context context, @NonNull View view,
                                            @NonNull SwipeGestureFactoryType type,
                                            @Nullable SwipeHandler handler1,
                                            @Nullable SwipeHandler handler2 ){

        //Call build to get the detector
        final GestureDetector gestureDetector = new GestureDetector(context,
                build(type, handler1, handler2));

        //Attach the listener to the view
        view.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }
}
