package edu.gatech.seclass.tccart;

import android.app.Application;

/**
 * Subclass of the Android application.
 * <p/>
 * The application is subclassed to allow for a few global variables that are used to control the
 * mocking of services during automated testing.
 */
public class TCCartApplication extends Application {

    /**
     * "serviceState" which controls mocking of all external services, except for email when the
     * application is being run through automated tests.
     * <p/>
     * "normal" (default) = pass through to service layer "fail" = fail all calls "success" =
     * success all calls ; return pre-set data for all calls that return more than a boolean.  See
     * Proxy classes for details.
     */
    public static String serviceState = "normal";

    /**
     * "emailServiceState" which controls mocking of email service.
     * <p/>
     * "normal" (default) = pass through to service layer "fail" = fail all calls "success" =
     * success all calls ; return pre-set data for all calls that return more than a boolean.  See
     * Proxy class for details.
     */
    public static String emailServiceState = "normal";

    /**
     * Customer id to return when QRCodeService is mocked as returning success (serviceState =
     * "success")
     */
    public static String customerIdForLookup = "7c86ffee";

    /**
     * Toggle to reset and reseed Realm with default customers on MainActivity.
     */
    public static boolean resetRealmOnMainActivity = false;

    /**
     * Testing variable to be added to time evaluations to mock the advancement of time. This only
     * affects two calculations: 1. whether or not an earned reward should apply given the current
     * time, and 2. whether or not VIP status applies given current time.
     * <p/>
     * It should NOT affect grants
     */
    public static long timeToAddToCalculationForTesting = 0;

}
