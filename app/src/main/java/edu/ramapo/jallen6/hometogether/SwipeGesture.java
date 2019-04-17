package edu.ramapo.jallen6.hometogether;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGesture extends GestureDetector.SimpleOnGestureListener {
    private SwipeHandler handlerDirection1;
    private SwipeHandler handlerDirection2;
    private SwipeDirectionDetector detector;
    SwipeGesture(@NonNull SwipeDirectionDetector det, @NonNull SwipeHandler dir1,
                 @NonNull SwipeHandler dir2){
        detector = det;
        handlerDirection1 = dir1;
        handlerDirection2 = dir2;

    }


    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

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
