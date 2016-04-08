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
 * Automated test for test case 6.0 - Scan Id Card for Valid Customer
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test17TotalCorrectOnPaymentSuccessScreen {

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
         * Set serviceState to success, since we expect all services to return success for this
         * test. Also set customerIdForLookup to Everett Scott, who has a current VIP reward.
         * Set database reset to clear out any previous tests/data.
         */
        TCCartApplication.serviceState = "success";
        TCCartApplication.resetRealmOnMainActivity = true;
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
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("30"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());

        // Make sure the total == $30
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$30.00")));
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        // We should've earned a reward, roll another txn
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("300"));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());

        // Make sure the total == $297
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$297.00")));

    }
}
