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
 * Automated test for test case 14.0 - Credit Card Scanner Displays CC Info Properly
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test14CreditCardScannerDisplaysCcInfoProperly {

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

        /**
         * Set serviceState to success, since we expect all services to initially return success
         * for this
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

        // Go to the customer scan screen and scan a customer
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());

        // Start a transaction to get to the scan screen
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("4"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());

        // Scan and validate fields
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.cardholderName)).check(matches(withText("Ralph Hapschatt")));
        onView(withId(R.id.cardNumber)).check(matches(withText("9328895960019440")));
        onView(withId(R.id.expDate)).check(matches(withText("12/31/2016")));
        onView(withId(R.id.securityCode)).check(matches(withText("111")));
        onView(withId(R.id.transactionFailure)).check(matches(withText("")));

        // Simulate a scan failure
        TCCartApplication.serviceState = "fail";
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.cardholderName)).check(matches(withText("")));
        onView(withId(R.id.cardNumber)).check(matches(withText("")));
        onView(withId(R.id.expDate)).check(matches(withText("")));
        onView(withId(R.id.securityCode)).check(matches(withText("")));
        onView(withId(R.id.transactionFailure)).check(matches(withText("Error scanning card.  " +
                                                                               "Please try again" +
                                                                               ".")));

    }
}
