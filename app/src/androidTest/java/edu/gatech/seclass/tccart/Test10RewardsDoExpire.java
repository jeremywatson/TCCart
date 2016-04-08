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
 * Automated test for test case 10.0 - Verify rewards expire
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test10RewardsDoExpire {

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

        /**
         * Set serviceState to success, since we expect all services to return success for this
         * test. Also set customerIdForLookup to Betty Monroe, who has an active reward of $3.
         */
        TCCartApplication.serviceState = "success";
        TCCartApplication.customerIdForLookup = "b59441af";

        // Go to the customer scan screen and scan a customer
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());

        // Expect Betty
        onView(withId(R.id.firstNameField)).check(matches(withText("Betty")));
        onView(withId(R.id.lastNameField)).check(matches(withText("Monroe")));
        onView(withId(R.id.emailField)).check(matches(withText("betty.monroe@gmail.com")));
        onView(withId(R.id.customerIdTextView)).check(matches(withText("b59441af")));

        // Start a transaction
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());

        // Make sure we've got our reward and do a transaction that's large enough to earn another
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("33"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$3.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$30.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$30.00")));

        // Advance time evaluator a year and go back to transactions screen
        TCCartApplication.timeToAddToCalculationForTesting = DateUtils.YEAR_IN_MILLIS;
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());

        // Make sure our reward's gone
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("33"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$33.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$33.00")));

        // Bring it back, advancing time 29 days
        TCCartApplication.timeToAddToCalculationForTesting = DateUtils.DAY_IN_MILLIS * 29;
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());

        // Verify reward is back and do a transaction that's large enough to earn another
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("64.35"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$3.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$61.35")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$61.35")));

        // Advancing time 31 days
        TCCartApplication.timeToAddToCalculationForTesting = DateUtils.DAY_IN_MILLIS * 31;
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());
        onView(withId(R.id.startTransaction)).perform(click());

        // Make sure our reward's gone
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("33"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$33.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessAmountText)).check(matches(withText("$33.00")));

    }
}
