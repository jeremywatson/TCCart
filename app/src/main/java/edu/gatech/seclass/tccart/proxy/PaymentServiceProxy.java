package edu.gatech.seclass.tccart.proxy;

import java.util.Date;

import edu.gatech.seclass.services.PaymentService;
import edu.gatech.seclass.tccart.TCCartApplication;

/**
 * Proxy class for PaymentService that allows "mock" responses when the application is in a test
 * mode.
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
public class PaymentServiceProxy {

    /**
     * Call CreditCardService.readCreditCard
     * <p/>
     * If "fail".equals(serviceState); always return failure If "success".equals(serviceState);
     * always return success If serviceState is anything else, call PaymentService.processPayment
     *
     * @param firstName    first name
     * @param lastName     last name
     * @param cardNumber   card number
     * @param expDate      expiration date
     * @param securityCode security code
     * @param chargeAmount charge amount
     * @return boolean PaymentService.processPayment response or mock response
     */
    public static boolean processPayment(String firstName, String lastName, String cardNumber,
                                         Date expDate, String securityCode, double chargeAmount) {

        String serviceState = TCCartApplication.serviceState;

        if ("fail".equals(serviceState)) {
            return false;
        } else if ("success".equals(serviceState)) {
            return true;
        }
        return PaymentService.processPayment(firstName, lastName, cardNumber, expDate,
                                             securityCode, chargeAmount);

    }
}