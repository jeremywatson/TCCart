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
 * Automated test for test case 11.0 - Build transaction total with VIP status
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test11BuildTransactionTotalWithVIP {

    /**
     * The Main.
     */
    @Rule
    public final CustomActivityTestRule<MainActivity> main = new CustomActivityTestRule<>
            (MainActivity.class);

    /**
     * Setup
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

        // Check $1
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("1.0"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.10")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$0.90")));

        // Check $500
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("500"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$50.00")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$450.00")));

        // Check $4294.59
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText("4294.59"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$429.46")));
        onView(withId(R.id.totalPriceTextView)).check(matches(withText("$3,865.13")));

        // Check bad input .00
        onView(withId(R.id.priceOfItemsSoldEditText)).perform(clearText(), typeText(".00"));
        onView(withId(R.id.rewardsAppliedPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.vipDiscountPriceTextView)).check(matches(withText("$0.00")));
        onView(withId(R.id.buildTransactionErrorTextView)).check(matches(withText("Total Price " +
                                                                                          "For " +
                                                                                          "Items " +
                                                                                          "Not " +
                                                                                          "Valid")));

    }
}
