package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;

/**
 * The type Customer actions activity.
 */
public class CustomerActionsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_customer_actions);
        setTitle(Constants.ACTIVITY_CUSTOMER_ACTIONS_TITLE);

        realm = RealmUtil.buildDatabase(this);

        final Button startTransaction = (Button) findViewById(R.id.startTransaction);
        final Button viewEditCustomer = (Button) findViewById(R.id.viewEditCustomer);
        final Button viewTransactions = (Button) findViewById(R.id.viewCustomerTransactions);
        final Button toMain = (Button) findViewById(R.id.goToMain);

        final TextView customerTextView = (TextView) findViewById(R.id.customerTextView);
        errorText = (TextView) findViewById(R.id.customerActionsErrorText);

        errorText.setText("");

        /**
         *Customer id populated by carried over customerId
         */
        String customerId = (String) getIntent().getSerializableExtra("CustomerId");
        customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();
        customerTextView.setText(customer.getFirstName() + " " + customer.getLastName());

        View.OnClickListener handler = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorText.setText("");

                if (v == startTransaction) {
                    Intent intentToStartTransaction = new Intent(v.getContext(),
                                                                 BuildTransactionActivity.class);
                    intentToStartTransaction.putExtra("CustomerId", customer.getId());
                    startActivity(intentToStartTransaction);
                }

                if (v == viewEditCustomer) {
                    Intent intentToViewEditCustomer = new Intent(v.getContext(),
                                                                 CustomerInfoActivity.class);
                    intentToViewEditCustomer.putExtra("CustomerId", customer.getId());
                    startActivity(intentToViewEditCustomer);
                }

                if (v == viewTransactions) {
                    if (customer.getTransactions().size() == 0) {
                        Log.w("TCCart", "User tried to view transactions, but there were none to " +
                                "" + "view.");
                        errorText.setText("No previous transactions to view.");
                        return;
                    }

                    Intent intentToViewTransactions = new Intent(v.getContext(),
                                                                 CustomerTransactionsActivity
                                                                         .class);
                    intentToViewTransactions.putExtra("CustomerId", customer.getId());
                    startActivity(intentToViewTransactions);
                }

                if (v == toMain) {
                    Intent intentToMain = new Intent(v.getContext(), MainActivity.class);
                    startActivity(intentToMain);
                }
            }
        };

        startTransaction.setOnClickListener(handler);
        viewEditCustomer.setOnClickListener(handler);
        viewTransactions.setOnClickListener(handler);
        toMain.setOnClickListener(handler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }
}
