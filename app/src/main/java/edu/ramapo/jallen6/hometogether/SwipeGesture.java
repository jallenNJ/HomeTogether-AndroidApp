package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Extends the SimpleOnGesture class and overrides the on fling function, allowing for
 * swipe gestures to be handled
 */
public class SwipeGesture extends GestureDetector.SimpleOnGestureListener {
    ///Handlers to be called by the detector
    private @NonNull SwipeHandler handlerDirection1;
    private @NonNull SwipeHandler handlerDirection2;
    ///The detector to determine if a swipe is a swipe, and what direction
    private @NonNull SwipeDirectionDetector detector;

    private boolean onDownConsume;


    SwipeGesture(@NonNull SwipeDirectionDetector det, @NonNull SwipeHandler dir1,
                 @NonNull SwipeHandler dir2){
        detector = det;
        handlerDirection1 = dir1;
        handlerDirection2 = dir2;
        onDownConsume = false;
    }


    public void setOnDownConsume(boolean val){
        onDownConsume = val;
    }

    /**
     *  Override needs to return true for onFling to be called
     * @param event The user pressing down, ignored in the function, required for override
     * @return false to pass on
     */
    @Override
    public boolean onDown(MotionEvent event) {
        return onDownConsume;
    }

    /**
     * Check to see if the gesture qualifies as a swipe
     * @param e1 The start of the gesture Event
     * @param e2 The end of the gesture Event
     * @param velocityX Velocity along the x axis
     * @param velocityY Velocity along the y axis
     * @return True to consume the gesture, false to pass it along. Returns swipe handler val
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        SwipeDirectionDetector.SWIPE_DIRECTION flingDirection = detector.detect(e1, e2, velocityX, velocityY);
        if(flingDirection == null){
            Log.e("Null Error", "Fling direction returned null");
            return false;
        }

        switch (flingDirection){
            case DIRECTION1:
                return handlerDirection1.onSwipe();
            case DIRECTION2:
                return handlerDirection2.onSwipe();
            case NONE: default: break;
        }
        return false;

    }

}
