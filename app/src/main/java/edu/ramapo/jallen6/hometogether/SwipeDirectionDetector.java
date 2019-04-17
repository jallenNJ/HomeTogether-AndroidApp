package edu.ramapo.jallen6.hometogether;
import android.view.MotionEvent;

public abstract class SwipeDirectionDetector {
    public enum SWIPE_DIRECTION{NONE, DIRECTION1, DIRECTION2}


    //Constants to check distance
    protected static final int SWIPE_MIN_DISTANCE = 120;
    protected static final int SWIPE_MAX_OFF_PATH = 250;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 200;


    public abstract SWIPE_DIRECTION detect(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}
