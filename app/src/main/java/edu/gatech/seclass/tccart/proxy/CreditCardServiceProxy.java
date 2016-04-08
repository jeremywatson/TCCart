package edu.gatech.seclass.tccart.proxy;

import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.tccart.TCCartApplication;

/**
 * Proxy class for CreditCardService that allows "mock" responses when the application is in a test
 * mode.
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
public class CreditCardServiceProxy {

    /**
     * Call CreditCardService.readCreditCard
     * <p/>
     * If "fail".equals(serviceState); always return failure If "success".equals(serviceState);
     * always return success If serviceState is anything else, call CreditCardService.readCreditCard
     *
     * @return String CreditCardService.readCreditCard response or mock response
     */
    public static String readCreditCard() {

        String serviceState = TCCartApplication.serviceState;

        if ("fail".equals(serviceState)) {
            return "ERR";
        } else if ("success".equals(serviceState)) {
            return "Ralph#Hapschatt#9328895960019440#12312016#111";
        }
        return CreditCardService.readCreditCard();

    }
}
