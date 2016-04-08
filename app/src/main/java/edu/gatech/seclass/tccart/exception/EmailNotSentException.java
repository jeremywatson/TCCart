package edu.gatech.seclass.tccart.exception;

/**
 * Exception class used to indicate emails are not sent Created by jeremyw on 3/25/16.
 */
public class EmailNotSentException extends Exception {

    /**
     * Instantiates a new Email not sent exception.
     *
     * @param s the message to include in the exception
     */
    public EmailNotSentException(String s) {
        super(s);
    }
}
