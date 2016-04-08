package edu.gatech.seclass.tccart;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.format.DateUtils;

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
 * Automated test for test case 26.0 - Test that VIP Discount doesn't apply after it expires
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test26VIPDiscountDoesNotApplyAfterExpiration {

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
         * test. Also set customerIdForLookup to Everett Scott, who is a current VIP.
         * Set database reset to clear out any previous tests/data.
         */
        TCCartApplication.serviceState = "success";
        TCCartApplication.customerIdForLookup = "cd0f0e05";
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

        // Go to the customer scan screen and scan a customer
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());

        // Expect Everett
        onView(withId(R.id.firstNameField)).check(matches(withText("Everett")));
        onView(withId(R.id.lastNameField)).check(matches(withText("Scott")));
        onView(withId(R.id.emailField)).check(matches(withText("everett.scott@gmail.com")));
        onView(withId(R.id.customerIdTextView)).check(matches(withText("cd0f0e05")));

        // Start a transaction
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());

        // Check $5
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("5"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.50")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$4.50")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$4.50")));
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        // Add a year to the current time
        TCCartApplication.timeToAddToCalculationForTesting = DateUtils.YEAR_IN_MILLIS;

        // Check $5
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("5"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$5.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$5.00")));

    }
}
