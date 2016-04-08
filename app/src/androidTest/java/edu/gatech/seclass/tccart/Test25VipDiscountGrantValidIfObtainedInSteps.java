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
 * Automated test for test case 24.0 - Check that VIP Discount Grant is Valid
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test25VipDiscountGrantValidIfObtainedInSteps {

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
        TCCartApplication.customerIdForLookup = "7c86ffee"; // Ralph, no special characteristics
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

        // Scan Ralph and verify he's not a VIP (yet)
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.viewEditCustomer)).perform(click());
        onView(withId(R.id.vipYearsTextView)).check(matches(withText("")));
        onView(withId(R.id.saveCustomerButton)).perform(click());

        // Get VIP through 4 transactions
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("150"));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("53"));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("102"));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("4"));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.chargeCreditCardButton)).perform(click());
        onView(withId(R.id.scanCreditCard)).perform(click());
        onView(withId(R.id.processPayment)).perform(click());
        onView(withId(R.id.chargeSuccessToActionsButton)).perform(click());

        // Verify we're a VIP for next year
        onView(withId(R.id.viewEditCustomer)).perform(click());
        onView(withId(R.id.vipYearsTextView)).check(matches(withText(Constants.NEXT_YEAR)));
        onView(withId(R.id.saveCustomerButton)).perform(click());

        // Let's advance time a year
        TCCartApplication.timeToAddToCalculationForTesting = DateUtils.YEAR_IN_MILLIS;

        // Verify we're getting a VIP discount; we received an award before, but it expired
        onView(withId(R.id.startTransaction)).perform(click());
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("5"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.50")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$4.50")));

    }
}
