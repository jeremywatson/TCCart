package edu.gatech.seclass.tccart;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
 * Automated test for test case 28.0 - Empty transaction history
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
public class Test28ViewEmptyTransactionHistoryForCustomer {
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
        TCCartApplication.serviceState = "success";

        /**
         * We use a randomEmail to avoid collisions with existing addresses
         */
        String randomEmail = UUID.randomUUID().toString().substring(1, 20).replace("-", "") +
                "@gatech.edu";

        String randomFirstName = UUID.randomUUID().toString().substring(1, 20);
        String randomLastName = UUID.randomUUID().toString().substring(1, 20);

        onView(withId(R.id.addNewCustomerButton)).perform(click());

        //Enter data
        onView(withId(R.id.addCustomerFirstNameField)).perform(click());
        onView(withId(R.id.addCustomerFirstNameField)).perform(clearText(), typeText
                (randomFirstName));
        onView(withId(R.id.addCustomerLastNameField)).perform(clearText(), typeText
                (randomLastName));
        onView(withId(R.id.addCustomerEmailAddressField)).perform(click());
        onView(withId(R.id.addCustomerEmailAddressField)).perform(clearText(), typeText
                (randomEmail));

        //Verify that labels and results are on the screen
        onView(withId(R.id.firstNameLabelTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.firstNameLabelTextView)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.firstNameLabelTextView)).check(matches(isEnabled()));
        onView(withId(R.id.firstNameLabelTextView)).check(matches(withText("First Name")));
        onView(withId(R.id.lastNameLabelTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.lastNameLabelTextView)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.lastNameLabelTextView)).check(matches(isEnabled()));
        onView(withId(R.id.lastNameLabelTextView)).check(matches(withText("Last Name")));
        onView(withId(R.id.addCustomerEmailAddressText)).check(matches(isDisplayed()));
        onView(withId(R.id.addCustomerEmailAddressText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.addCustomerEmailAddressText)).check(matches(isEnabled()));
        onView(withId(R.id.addCustomerEmailAddressText)).check(matches(withText("Email")));
        onView(withId(R.id.addCustomerEmailAddressText)).perform(closeSoftKeyboard());
        onView(withId(R.id.addCustomerCreateCustomerButton)).perform(click());
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isDisplayed()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(isEnabled()));
        onView(withId(R.id.createCustomerSuccessNameText)).check(matches(withText(randomFirstName
                                                                                          + " " +
                                                                                          randomLastName)));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isDisplayed()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(isEnabled()));
        onView(withId(R.id.createCustomerSuccessEmailText)).check(matches(withText(randomEmail)));

        //click to print the card and verify success
        onView(withId(R.id.createCustomerSuccessPrintCardButton)).perform(click());
        onView(withId(R.id.createCustomerSuccessErrorText)).check(matches(isDisplayed()));
        onView(withId(R.id.createCustomerSuccessErrorText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.createCustomerSuccessErrorText)).check(matches(isEnabled()));
        onView(withId(R.id.createCustomerSuccessErrorText)).check(matches(withText("Printing successful!")));

        //go back to customer actions and verify success
        onView(withId(R.id.createCustomerSuccessToActionsButton)).perform(click());
        onView(withId(R.id.customerTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.customerTextView)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.customerTextView)).check(matches(isEnabled()));
        onView(withId(R.id.customerTextView)).check(matches(withText(randomFirstName + " " +
                                                                             randomLastName)));

        onView(withId(R.id.viewCustomerTransactions)).perform(click());
        onView(withId(R.id.customerActionsErrorText)).check(matches(withText("No previous " +
                                                                                     "transactions to view.")));

    }

}
