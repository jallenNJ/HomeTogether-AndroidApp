package edu.ramapo.jallen6.hometogether;

import android.view.MotionEvent;

public class SwipeHorizontalDetector extends SwipeDirectionDetector {

    private static final SWIPE_DIRECTION left = SWIPE_DIRECTION.DIRECTION1;
    private static final SWIPE_DIRECTION right = SWIPE_DIRECTION.DIRECTION2;

    @Override
    public SWIPE_DIRECTION detect(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Ensure they didn't go too far off path
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH ||
                Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) // and not too slow
            return SWIPE_DIRECTION.NONE;
        // right to left swipe
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) {
            return left;

        }
        //left to right swipe
        if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
            return right;
        }

        return SWIPE_DIRECTION.NONE;
    }
}
