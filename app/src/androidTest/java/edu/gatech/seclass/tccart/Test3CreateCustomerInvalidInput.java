package edu.gatech.seclass.tccart;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Automated test for test case 3.0 - Test Creating a new customer with invalid input
 * <p/>
 * Created by jeremyw on 3/31/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class Test3CreateCustomerInvalidInput {
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

        /**
         * We use a randomEmail to avoid collisions with existing addresses
         */
        String randomEmail = UUID.randomUUID().toString().substring(1, 20).replace("-", "") +
                "@gatech.edu";

        /**
         * First, create with blank fields
         */
        onView(withId(R.id.addNewCustomerButton)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(clearText(), typeText(""));
        onView(withId(R.id.addCustomerLastNameField)).perform(clearText(), typeText(""));
        onView(withId(R.id.addCustomerEmailAddressField)).perform(click());
        onView(withId(R.id.addCustomerEmailAddressField)).perform(clearText(), typeText(""));
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());
        onView(withId(R.id.addCustomerErrorText)).check(matches(withText("Invalid customer info"
                                                                                 + ".")));

        /**
         * Add first name, then expect an error again
         */
        onView(withId(R.id.addCustomerFirstNameField)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(clearText(), typeText("Jane"));
        onView(withId(R.id.addCustomerEmailAddressField)).perform(click());
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());
        onView(withId(R.id.addCustomerErrorText)).check(matches(withText("Invalid customer info"
                                                                                 + ".")));

        /**
         * Add last name, then expect an error again
         */
        onView(withId(R.id.addCustomerLastNameField)).perform(clearText(), typeText("Doe"));
        onView(withId(R.id.addCustomerEmailAddressField)).perform(click());
        onView(withId(R.id.addCustomerEmailAddressField)).perform(clearText(), typeText(""));
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());
        onView(withId(R.id.addCustomerErrorText)).check(matches(withText("Invalid customer info"
                                                                                 + ".")));

        /**
         * Add invalid e-mail, then expect an error again
         */
        onView(withId(R.id.addCustomerEmailAddressField)).perform(clearText(), typeText("bogus"));
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());
        onView(withId(R.id.addCustomerErrorText)).check(matches(withText("Invalid customer info"
                                                                                 + ".")));

        /**
         * Add valid e-mail, then expect success
         */
        onView(withId(R.id.addCustomerEmailAddressField)).perform(clearText(), typeText
                (randomEmail));
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());

        /**
         * Now we should have everything we need, we should see things be successful
         */
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isDisplayed()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isEnabled()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(withText("Jane Doe")));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isDisplayed()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isEnabled()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(withText(randomEmail)));

    }
}
