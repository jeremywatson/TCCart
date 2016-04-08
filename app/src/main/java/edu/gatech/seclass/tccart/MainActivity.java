package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.gatech.seclass.tccart.util.LoadInitialCustomerDataUtil;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Realm database
     */
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button addCustomerButton = (Button) findViewById(R.id.addNewCustomerButton);
        final Button lookupCustomerButton = (Button) findViewById(R.id.lookupCustomerButton);

        realm = RealmUtil.buildDatabase(this);

        if (!LoadInitialCustomerDataUtil.checkTestData(realm) || TCCartApplication
                .resetRealmOnMainActivity) {
            Log.i("TCCart", "Starting load of test data");

            try {
                LoadInitialCustomerDataUtil.loadInitialData(realm);
            }
            catch (Exception e) {
                //This is a function used for testing only.  We'll print the stack to the
                // log and swallow the exception
                Log.e("TCCart", "Issue loading test data", e);
            }

            Log.i("TCCart", "Completed load of test data");
            TCCartApplication.resetRealmOnMainActivity = false;

        }

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {
                if (view == addCustomerButton) {
                    Intent intentToAddCustomer = new Intent(MainActivity.this,
                                                            AddCustomerActivity.class);
                    startActivity(intentToAddCustomer);
                }

                if (view == lookupCustomerButton) {
                    Intent intentToLookupCustomer = new Intent(MainActivity.this,
                                                               LookupCustomerActivity.class);
                    startActivity(intentToLookupCustomer);
                }
            }
        };

        addCustomerButton.setOnClickListener(handler);
        lookupCustomerButton.setOnClickListener(handler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }

}


