package edu.ramapo.jallen6.hometogether;

import android.view.MotionEvent;

public class SwipeVerticalDetectpr extends SwipeDirectionDetector {
    @Override
    public SWIPE_DIRECTION detect(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return SWIPE_DIRECTION.NONE;
    }
}
