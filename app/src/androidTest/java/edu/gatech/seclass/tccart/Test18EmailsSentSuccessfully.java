package edu.gatech.seclass.tccart;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Automated test for test case 18.0 - Emails sent successfully
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test18EmailsSentSuccessfully {

    /**
     * The Main.
     */
    @Rule
    public final CustomActivityTestRule<MainActivity> main = new CustomActivityTestRule<>
            (MainActivity.class);

    /**
     * Sets .
     */
    @Before
    public void setup() {
        closeSoftKeyboard();
        TCCartApplication.serviceState = "success";
        TCCartApplication.emailServiceState = "success";
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        AutomatedTestUtil.tearDown();
    }

    /**
     * See description at class level
     */
    @Test
    public void test() {

        // Run through a transaction up to the scan credit card activity
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("10"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());

        // Make sure the receipt email went out
        onView(withId(R.id.chargeSuccessEmailText)).check(matches(withText("Receipt email " +
                                                                                   "successfully " +
                                                                                   "sent to " +
                                                                                   "customer!\n")));
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        // OK, let's earn a reward now
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("30"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());

        // Make sure we got both receipt and reward emails
        onView(withId(R.id.chargeSuccessEmailText)).check(matches(withText("Receipt email " +
                                                                                   "successfully " +
                                                                                   "sent to " +
                                                                                   "customer!\n$3" +
                                                                                   " reward email" +
                                                                                   " successfully" +
                                                                                   " sent!\n")));
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        // OK, let's earn everything now
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("400"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());

        // Make sure we got all 3 emails
        onView(withId(R.id.chargeSuccessEmailText)).check(matches(withText("Receipt email " +
                                                                                   "successfully " +
                                                                                   "sent to " +
                                                                                   "customer!\n$3" +
                                                                                   " reward email" +
                                                                                   " successfully" +
                                                                                   " sent!\nVIP " +
                                                                                   "status email " +
                                                                                   "successfully " +
                                                                                   "sent!\n")));

    }
}
