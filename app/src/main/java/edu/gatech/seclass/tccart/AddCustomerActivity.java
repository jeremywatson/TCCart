package edu.gatech.seclass.tccart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Reward;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.util.CustomerUtil;
import edu.gatech.seclass.tccart.util.RealmUtil;
import edu.gatech.seclass.tccart.util.RewardUtil;
import io.realm.Realm;

/**
 * The type Add customer activity.
 */
public class AddCustomerActivity extends AppCompatActivity {

    /**
     * Realm database
     */
    private Realm realm;

    private EditText newCustomerFirstName;
    private EditText newCustomerLastName;
    private EditText newCustomerEmail;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        setTitle(Constants.ACTIVITY_ADD_CUSTOMER_TITLE);

        realm = RealmUtil.buildDatabase(this);

        newCustomerFirstName = (EditText) findViewById(R.id.addCustomerFirstNameField);
        newCustomerLastName = (EditText) findViewById(R.id.addCustomerLastNameField);
        newCustomerEmail = (EditText) findViewById(R.id.addCustomerEmailAddressField);
        errorText = (TextView) findViewById(R.id.addCustomerErrorText);

        final Button addCustomer = (Button) findViewById(R.id.addCustomerCreateCustomerButton);

        errorText.setText("");

        View.OnClickListener handler = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view == addCustomer) {
                    Log.i("TCCart", "Request to add customer");

                    // Close the keyboard if it's open
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    //Clear any previous error messages
                    errorText.setText("");

                    String firstName = newCustomerFirstName.getText().toString();
                    String lastName = newCustomerLastName.getText().toString();
                    String emailAddress = newCustomerEmail.getText().toString();

                    // do some input validation
                    if (TextUtils.isEmpty(firstName) ||
                            TextUtils.isEmpty(lastName) ||
                            !CustomerUtil.isEmailValid(emailAddress)) {

                        Log.w("TCCart", "Bad inputs on attempting to add customer.");
                        errorText.setText("Invalid customer info.");
                        return;
                    }

                    try {
                        // add a new Customer to Realm if they don't already exist from email lookup
                        Customer customer = realm.where(Customer.class).
                                equalTo("email", emailAddress).findFirst();

                        if (customer == null) {
                            realm.beginTransaction();
                            customer = realm.createObject(Customer.class);

                            String customerId;

                            //keep generating ids until we find one that doesn't conflict
                            do {
                                customerId = CustomerUtil.generateID();
                            }
                            while (realm.where(Customer.class).equalTo("id", customerId).count()
                                    != 0L);

                            customer.setId(customerId);
                            customer.setFirstName(firstName);
                            customer.setLastName(lastName);
                            customer.setEmail(emailAddress);

                            Reward reward = realm.createObject(Reward.class);
                            reward.setId(RewardUtil.generateRewardId());
                            customer.setReward(reward);

                            VipDiscount vipDiscount = realm.createObject(VipDiscount.class);
                            customer.setVipDiscount(vipDiscount);

                            realm.commitTransaction();
                            Log.i("TCCart", "Created customer" + customer);

                        } else {
                            Log.w("TCCart", "Email address found in system. Please use another.");
                            errorText.setText("Email exists. Please use another address.");
                            return;
                        }

                        // send to success activity
                        Intent intent = new Intent(view.getContext(), AddCustomerSuccessActivity
                                .class);
                        intent.putExtra("CustomerId", customer.getId());
                        startActivity(intent);
                    }
                    /**
                     * In the interest of simple error handling, if something goes wrong creating
                     * the customer,
                     * no matter what it is, catch the Exception and return an error message to
                     * the user.
                     */
                    catch (Exception e) {
                        Log.w("TCCart", "Error.", e);
                        errorText.setText("Error.  Please try again.");
                    }
                }
            }
        };

        addCustomer.setOnClickListener(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }
}
