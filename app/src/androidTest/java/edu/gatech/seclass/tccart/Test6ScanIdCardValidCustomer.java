package edu.gatech.seclass.tccart;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
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
public class Test6ScanIdCardValidCustomer {

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
         * test.
         */
        TCCartApplication.serviceState = "success";

        //Go to the customer scan screen and scan a customer
        onView(withId(R.id.lookupCustomerButton)).perform(click());
        onView(withId(R.id.scanCustomerCardButton)).perform(click());

        //expect the stubbed info to be returned
        onView(withId(R.id.firstNameField)).check(matches(withText("Ralph")));
        onView(withId(R.id.lastNameField)).check(matches(withText("Hapschatt")));
        onView(withId(R.id.emailField)).check(matches(withText("ralph.hapschatt@gmail.com")));
        onView(withId(R.id.customerIdTextView)).check(matches(withText("7c86ffee")));

        //try it again
        onView(withId(R.id.scanCustomerCardButton)).perform(click());
        onView(withId(R.id.firstNameField)).check(matches(withText("Ralph")));
        onView(withId(R.id.lastNameField)).check(matches(withText("Hapschatt")));
        onView(withId(R.id.emailField)).check(matches(withText("ralph.hapschatt@gmail.com")));
        onView(withId(R.id.customerIdTextView)).check(matches(withText("7c86ffee")));

        //go back to the customer menu and verify that
        onView(withId(R.id.selectCustomerButton)).perform(click());
        onView(withId(R.id.customerTextView)).check(matches(withText("Ralph Hapschatt")));

    }
}
