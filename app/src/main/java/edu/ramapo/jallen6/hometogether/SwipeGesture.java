package edu.ramapo.jallen6.hometogether;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGesture extends GestureDetector.SimpleOnGestureListener {
    private SwipeHandler handlerDirection1;
    private SwipeHandler handlerDirection2;
    private SwipeDirectionDetector detector;
    SwipeGesture(SwipeHandler dir1, SwipeHandler dir2){
        handlerDirection1 = dir1;
        handlerDirection2 = dir2;

    }


    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
        return false;
    }

}
