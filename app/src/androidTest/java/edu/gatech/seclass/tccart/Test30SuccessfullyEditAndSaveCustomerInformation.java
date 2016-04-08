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
 * Automated test case for test case 30.0 - Edit and save customer information
 * <p/>
 * Created by jeremyw on 4/4/16.
 */
public class Test30SuccessfullyEditAndSaveCustomerInformation {

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

        //go to view and edit
        onView(withId(R.id.viewEditCustomer)).perform(click());
        onView(withId(R.id.firstNameField)).check(matches(withText(randomFirstName)));
        onView(withId(R.id.lastNameField)).check(matches(withText(randomLastName)));
        onView(withId(R.id.emailField)).check(matches(withText(randomEmail)));
        onView(withId(R.id.customerIdTextView)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.vipYearsTextView)).check(matches(withText("")));
        onView(withId(R.id.rewardBalanceTextView)).check(matches(withText("$0.00")));

        //go back to customer actions
        onView(withId(R.id.saveCustomerButton)).perform(click());
        onView(withId(R.id.customerTextView)).check(matches(withText(randomFirstName + " " +
                                                                             randomLastName)));

        onView(withId(R.id.viewEditCustomer)).perform(click());
        onView(withId(R.id.firstNameField)).check(matches(withText(randomFirstName)));
        onView(withId(R.id.lastNameField)).check(matches(withText(randomLastName)));
        onView(withId(R.id.emailField)).check(matches(withText(randomEmail)));
        onView(withId(R.id.customerIdTextView)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.vipYearsTextView)).check(matches(withText("")));
        onView(withId(R.id.rewardBalanceTextView)).check(matches(withText("$0.00")));

        onView(withId(R.id.firstNameField)).perform(clearText(), typeText("Beth"));
        onView(withId(R.id.lastNameField)).perform(clearText(), typeText("Lopez"));
        onView(withId(R.id.emailField)).perform(clearText(), typeText("beth@address.com"));
        onView(withId(R.id.saveCustomerButton)).perform(click());

        //go back to customer actions
        onView(withId(R.id.customerTextView)).check(matches(withText("Beth Lopez")));

        //verify the changes are there
        onView(withId(R.id.viewEditCustomer)).perform(click());
        onView(withId(R.id.firstNameField)).check(matches(withText("Beth")));
        onView(withId(R.id.lastNameField)).check(matches(withText("Lopez")));
        onView(withId(R.id.emailField)).check(matches(withText("beth@address.com")));

    }

}
