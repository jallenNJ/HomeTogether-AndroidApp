package edu.ramapo.jallen6.hometogether;
import android.view.MotionEvent;

/**
 * Abstract class designed to detect the direction of a swipe
 *  derived classes implement the detect function and return an enum based on direction of swipe
 */
public abstract class SwipeDirectionDetector {
    public enum SWIPE_DIRECTION{NONE, DIRECTION1, DIRECTION2}

    protected static final int SWIPE_MIN_DISTANCE = 120; //Minimum distance to be a swipe
    protected static final int SWIPE_MAX_OFF_PATH = 250; // Max distance the swipe can go "off path"
    protected static final int SWIPE_THRESHOLD_VELOCITY = 200; //How fast a swipe needs to be

    /**
     * Function to detect which direction the swipe was in
     * @param e1 Where the swipe started
     * @param e2 Where the swipe ended
     * @param velocityX How fast on the x axis it went
     * @param velocityY How fast on the Y axis it went
     * @return Classes define what Direction1, and 2 mean. None is not a valid swipe
     */
    public abstract SWIPE_DIRECTION detect(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}
