package edu.ramapo.jallen6.hometogether;

import android.view.MotionEvent;

public class SwipeVerticalDetector extends SwipeDirectionDetector {

    private static final SWIPE_DIRECTION up = SWIPE_DIRECTION.DIRECTION1;
    private static final SWIPE_DIRECTION down = SWIPE_DIRECTION.DIRECTION2;

    @Override
    public SWIPE_DIRECTION detect(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Ensure they didn't go too far off path or too slow
            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                    || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY)
                return SWIPE_DIRECTION.NONE;
            // Up swipe
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE ) {
                return up;
            }

            if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE ){
                return down;
            }


        return SWIPE_DIRECTION.NONE;

    }
}
