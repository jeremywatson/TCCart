package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.util.CustomerUtil;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;

/**
 * The type Add customer success activity.
 */
public class AddCustomerSuccessActivity extends AppCompatActivity {

    /**
     * Realm database
     */
    private Realm realm;

    /**
     * Customer populated by database lookup based on carried over customerId
     */
    private Customer customer;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create_success);
        setTitle(Constants.ACTIVITY_CUSTOMER_CREATE_SUCCESS_TITLE);

        realm = RealmUtil.buildDatabase(this);

        TextView createdCustomerName = (TextView) findViewById(R.id.createCustomerSuccessNameText);
        TextView createdCustomerEmail = (TextView) findViewById(R.id.createCustomerSuccessEmailText);
        errorText = (TextView) findViewById(R.id.createCustomerSuccessErrorText);

        final Button toActionsButton = (Button) findViewById(R.id.createCustomerSuccessToActionsButton);
        final Button toMainButton = (Button) findViewById(R.id.createCustomerSuccessToMainButton);
        final Button printCardButton = (Button) findViewById(R.id.createCustomerSuccessPrintCardButton);

        toActionsButton.setVisibility(View.INVISIBLE);
        toMainButton.setVisibility(View.INVISIBLE);

        errorText.setText("");

        /**
         * Customer id populated by carried over customerId
         */
        String customerId = (String) getIntent().getSerializableExtra("CustomerId");
        customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

        createdCustomerName.setText(customer.getFirstName() + " " + customer.getLastName());
        createdCustomerEmail.setText(customer.getEmail());

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {
                if (view == printCardButton) {

                    errorText.setText("");
                    Log.i("TCCart", "Sending customer to printer");

                    boolean resultFromPrinter = CustomerUtil.printCard(customer);

                    if (!resultFromPrinter) {
                        Log.w("TCCart", "Print job failed.");
                        errorText.setText("Printing failed.");
                        return;
                    }

                    Log.w("TCCart", "Print job successful.");
                    errorText.setText("Printing successful!");

                    toActionsButton.setVisibility(View.VISIBLE);
                    toMainButton.setVisibility(View.VISIBLE);
                    printCardButton.setVisibility(View.INVISIBLE);

                }

                if (view == toActionsButton) {
                    Intent intentToActions = new Intent(view.getContext(),
                                                        CustomerActionsActivity.class);
                    intentToActions.putExtra("CustomerId", customer.getId());
                    startActivity(intentToActions);

                }

                if (view == toMainButton) {
                    Intent intentToMain = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intentToMain);

                }
            }
        };

        toActionsButton.setOnClickListener(handler);
        toMainButton.setOnClickListener(handler);
        printCardButton.setOnClickListener(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }

}
