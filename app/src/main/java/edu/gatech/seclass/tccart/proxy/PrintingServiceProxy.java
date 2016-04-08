package edu.gatech.seclass.tccart.proxy;

import edu.gatech.seclass.services.PrintingService;
import edu.gatech.seclass.tccart.TCCartApplication;

/**
 * Proxy class for PrintingService that allows "mock" responses when the application is in a test
 * mode.
 * <p/>
 * Created by jeremyw on 4/3/16.
 */
public class PrintingServiceProxy {

    /**
     * Call PrintingService.printCard
     * <p/>
     * If "fail".equals(serviceState); always return failure If "success".equals(serviceState);
     * always return success If serviceState is anything else, call PrintingService.printCard
     *
     * @param firstName customer first name
     * @param lastName  customer last name
     * @param hexID     customer hexId
     * @return true if success, false if failure
     */
    public static boolean printCard(String firstName, String lastName, String hexID) {

        String serviceState = TCCartApplication.serviceState;

        if ("fail".equals(serviceState)) {
            return false;
        } else if ("success".equals(serviceState)) {
            return true;
        }
        return PrintingService.printCard(firstName, lastName, hexID);

    }

}
