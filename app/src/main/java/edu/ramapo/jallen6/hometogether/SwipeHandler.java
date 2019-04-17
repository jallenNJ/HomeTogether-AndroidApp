package edu.ramapo.jallen6.hometogether;

/**
 * Interface to allow for a Swipe Gesture to call functionality of a passed object
 */
public interface SwipeHandler {
    /**
     * Called when a detector has determined this swipe occurred
     * @return true to consume the gesture, false to pass it one
     */
    boolean onSwipe();
}
