package edu.gatech.seclass.tccart.proxy;

import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.tccart.TCCartApplication;

/**
 * Proxy class for EmailService that allows "mock" responses when the application is in a test
 * mode.
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
public class EmailServiceProxy {

    /**
     * Call EmailService.sendEMail
     * <p/>
     * If "fail".equals(serviceState); always return failure If "success".equals(serviceState);
     * always return success If serviceState is anything else, call EmailService.sendEMail
     *
     * @param recipient recipient
     * @param subject   subject
     * @param body      body
     * @return boolean EmailService.sendEMail response or mock response
     */
    public static boolean sendEMail(final String recipient, final String subject, final String
            body) {

        String serviceState = TCCartApplication.emailServiceState;

        if ("fail".equals(serviceState)) {
            return false;
        } else if ("success".equals(serviceState)) {
            return true;
        }
        return EmailService.sendEMail(recipient, subject, body);
    }
}
