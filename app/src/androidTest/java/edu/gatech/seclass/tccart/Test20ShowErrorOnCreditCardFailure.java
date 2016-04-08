package edu.gatech.seclass.tccart;

import android.support.test.rule.ActivityTestRule;
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
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Automated test for test case 20.0 - SHow errors on credit card failure
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test20ShowErrorOnCreditCardFailure {

    /**
     * The Main.
     */
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    /**
     * Sets .
     */
    @Before
    public void setup() {
        closeSoftKeyboard();

        /**
         * Set serviceState to success, since we expect all services to (initially) return
         * success for this
         * test.
         */
        TCCartApplication.serviceState = "success";
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

        //first, try to continue without scanning a card, and fail
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.transactionFailure)).check(matches(withText("Card not valid.  " +
                                                                               "Please try again" +
                                                                               ".")));
        //then, scan a card, but get a failure
        TCCartApplication.serviceState = "fail";
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.transactionFailure)).check(matches(withText("Error scanning card.  " +
                                                                               "Please try again" +
                                                                               ".")));
        //then, try to continue with the failed scan
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.transactionFailure)).check(matches(withText("Card not valid.  " +
                                                                               "Please try again" +
                                                                               ".")));
        //now set the state to success, scan a card and process
        TCCartApplication.serviceState = "success";
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessText)).check(matches(withText("Charge Successful for")));
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(isCompletelyDisplayed()));

    }
}
