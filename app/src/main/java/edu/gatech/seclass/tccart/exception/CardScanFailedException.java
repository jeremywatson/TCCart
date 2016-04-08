package edu.gatech.seclass.tccart.exception;

/**
 * Exception thrown when a Card Scan fails
 * <p/>
 * Created by jeremyw on 3/25/16.
 */
public class CardScanFailedException extends Exception {
    /**
     * Instantiates a new Card scan failed exception.
     *
     * @param s the message to include in the exception
     */
    public CardScanFailedException(String s) {
        super(s);
    }
}
