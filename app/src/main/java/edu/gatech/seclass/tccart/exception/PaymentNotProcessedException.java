package edu.gatech.seclass.tccart.exception;

/**
 * Exception indicating that payment is not processed.
 * <p/>
 * Created by jeremyw on 3/25/16.
 */
public class PaymentNotProcessedException extends Exception {

    /**
     * Instantiates a new Payment not processed exception.
     *
     * @param s the message to include in the exception
     */
    public PaymentNotProcessedException(String s) {
        super(s);
    }
}
