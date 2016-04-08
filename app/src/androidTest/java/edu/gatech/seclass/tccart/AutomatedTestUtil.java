package edu.gatech.seclass.tccart;

/**
 * Utilities to use with automated testing
 * <p/>
 * Created by jeremyw on 4/8/16.
 */
public class AutomatedTestUtil {

    /**
     * Tear down to be used in all classes
     */
    public static void tearDown() {
        TCCartApplication.serviceState = "normal";
        TCCartApplication.emailServiceState = "normal";
        TCCartApplication.timeToAddToCalculationForTesting = 0;
        TCCartApplication.resetRealmOnMainActivity = false;
        TCCartApplication.customerIdForLookup = "7c86ffee";
    }

}
