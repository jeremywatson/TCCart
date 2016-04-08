package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

/**
 * The type Custom activity test rule.
 * <p/>
 * This is used to reset data in automated tests.
 *
 * @param <A> the type parameter
 */
public class CustomActivityTestRule<A extends Activity> extends ActivityTestRule<A> {

    /**
     * Instantiates a new Custom activity test rule.
     *
     * @param activityClass the activity class
     */
    public CustomActivityTestRule(Class<A> activityClass) {

        super(activityClass);
    }

    @Override
    public void beforeActivityLaunched() {
        TCCartApplication.resetRealmOnMainActivity = true;
    }

}
