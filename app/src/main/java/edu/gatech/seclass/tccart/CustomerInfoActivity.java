package edu.gatech.seclass.tccart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.VipDiscountYear;
import edu.gatech.seclass.tccart.util.CustomerUtil;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;

/**
 * The type Customer info activity.
 */
public class CustomerInfoActivity extends AppCompatActivity {

    /**
     * Realm database
     */
    private Realm realm;

    /**
     * Customer populated by database lookup based on carried over customerId
     */
    private Customer customer;

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        setTitle(Constants.ACTIVITY_CUSTOMER_INFO_TITLE);

        realm = RealmUtil.buildDatabase(this);

        firstName = (EditText) findViewById(R.id.firstNameField);
        lastName = (EditText) findViewById(R.id.lastNameField);
        email = (EditText) findViewById(R.id.emailField);
        TextView customerIdFromObject = (TextView) findViewById(R.id.customerIdTextView);
        TextView vipYearsTextView = (TextView) findViewById(R.id.vipYearsTextView);
        TextView rewardBalanceTextView = (TextView) findViewById(R.id.rewardBalanceTextView);
        errorText = (TextView) findViewById(R.id.errorMessageTextView);

        final Button saveCustomer = (Button) findViewById(R.id.saveCustomerButton);

        /**
         * Customer id populated by carried over customerId
         */
        String customerId = (String) getIntent().getSerializableExtra("CustomerId");
        customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

        firstName.setText(customer.getFirstName());
        lastName.setText(customer.getLastName());
        email.setText(customer.getEmail());
        customerIdFromObject.setText(customer.getId());

        final String vipDiscountYearsString = getVipDiscountYearsString();

        if (null != customer.getReward()) {
            rewardBalanceTextView.setText(Constants.CURRENCY_FORMAT.format(customer.getReward()
                                                                                   .getRemainingAmount()));
        }

        vipYearsTextView.setText(vipDiscountYearsString);
        errorText.setText("");

        View.OnClickListener handler = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view == saveCustomer) {

                    errorText.setText("");

                    // Close the keyboard if it's open
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String newFirstName = firstName.getText().toString();
                    String newLastName = lastName.getText().toString();
                    String newEmail = email.getText().toString();

                    // validate the TextEdits and restore old values if inputs fail
                    if (TextUtils.isEmpty(newFirstName) ||
                            TextUtils.isEmpty(newLastName) ||
                            !CustomerUtil.isEmailValid(newEmail)) {

                        firstName.setText(customer.getFirstName());
                        lastName.setText(customer.getLastName());
                        email.setText(customer.getEmail());

                        Log.w("TCCart", "Bad or blank inputs on edit customer.");
                        errorText.setText("Error editing customer, old values restored.");
                        return;
                    }

                    // set validated values
                    try {
                        realm.beginTransaction();
                        customer.setFirstName(newFirstName);
                        customer.setLastName(newLastName);
                        customer.setEmail(newEmail);
                        realm.commitTransaction();

                    }
                    catch (Exception e) {
                        Log.w("TCCart", "Error saving to customer record.", e);
                        errorText.setText("Error saving new attributes to record.");
                        return;
                    }

                    Intent intentToSave = new Intent(view.getContext(), CustomerActionsActivity
                            .class);
                    intentToSave.putExtra("CustomerId", customer.getId());
                    startActivity(intentToSave);
                }
            }
        };

        saveCustomer.setOnClickListener(handler);
    }

    @NonNull
    private String getVipDiscountYearsString() {
        StringBuilder vipDiscountYearsString = new StringBuilder();
        if (null != customer.getVipDiscount()) {
            for (VipDiscountYear vipDiscountYear : customer.getVipDiscount()
                    .getYearsVipDiscountQualifiedFor()) {

                if (vipDiscountYearsString.length() > 0) {
                    vipDiscountYearsString.append(",");
                }
                vipDiscountYearsString.append(vipDiscountYear.getVipYearQualifiedFor());
            }
        }
        return vipDiscountYearsString.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }
}
