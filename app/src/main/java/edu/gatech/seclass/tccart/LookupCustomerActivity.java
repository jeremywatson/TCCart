package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.proxy.QRCodeServiceProxy;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;

/**
 * The type Lookup customer activity.
 */
public class LookupCustomerActivity extends AppCompatActivity {

    /**
     * Realm database
     */
    private Realm realm;

    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_customer);
        setTitle(Constants.ACTIVITY_LOOKUP_CUSTOMER_TITLE);

        realm = RealmUtil.buildDatabase(this);

        final Button scanCustomerCardButton = (Button) findViewById(R.id.scanCustomerCardButton);
        final Button selectCustomerButton = (Button) findViewById(R.id.selectCustomerButton);

        final TextView firstNameTextView = (TextView) findViewById(R.id.firstNameField);
        final TextView lastNameTextView = (TextView) findViewById(R.id.lastNameField);
        final TextView emailTextView = (TextView) findViewById(R.id.emailField);
        final TextView customerIdTextView = (TextView) findViewById(R.id.customerIdTextView);
        final TextView errorText = (TextView) findViewById(R.id.errorMessageTextView);

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {
                if (view == scanCustomerCardButton) {
                    errorText.setText("");

                    String customerId = QRCodeServiceProxy.scanQRCode();

                    if ("ERR".equals(customerId)) {
                        setErrorScanningCardText(errorText, firstNameTextView, lastNameTextView,
                                                 emailTextView, customerIdTextView);

                    } else {
                        //We just "findFirst" here because id is a primary key; there will not be
                        // more than one of them
                        customer = realm.where(Customer.class).equalTo("id", customerId)
                                .findFirst();
                        if (null != customer && null != customer.getId()) {
                            firstNameTextView.setText(customer.getFirstName());
                            lastNameTextView.setText(customer.getLastName());
                            emailTextView.setText(customer.getEmail());
                            customerIdTextView.setText(customer.getId());
                        } else {
                            setErrorScanningCardText(errorText, firstNameTextView,
                                                     lastNameTextView, emailTextView,
                                                     customerIdTextView);
                        }

                    }

                }
                if (view == selectCustomerButton) {

                    if (customerIdTextView.getText().length() == 0L) {
                        Log.w("TCCart", "Customer ID not found. Did you forget to scan in a card?");
                        errorText.setText("Error. Please scan in a valid customer card.");

                    } else {
                        Intent intentToActions = new Intent(view.getContext(),
                                                            CustomerActionsActivity.class);
                        intentToActions.putExtra("CustomerId", customer.getId());
                        startActivity(intentToActions);
                    }

                }
            }
        };

        selectCustomerButton.setOnClickListener(handler);
        scanCustomerCardButton.setOnClickListener(handler);
    }

    private void setErrorScanningCardText(TextView errorText, TextView firstNameTextView,
                                          TextView lastNameTextView, TextView emailTextView,
                                          TextView customerIdTextView) {
        errorText.setText("Error scanning card. Try again.");

        firstNameTextView.setText("");
        lastNameTextView.setText("");
        emailTextView.setText("");
        customerIdTextView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }
}