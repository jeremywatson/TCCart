package edu.gatech.seclass.tccart;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.util.RealmUtil;
import io.realm.Realm;
import io.realm.RealmList;

import static edu.gatech.seclass.tccart.Constants.CURRENCY_FORMAT;

/**
 * The type Customer transactions activity.
 */
public class CustomerTransactionsActivity extends AppCompatActivity {

    /**
     * @param transaction that is used to build the VIP status string
     * @return String used to inform user about VIP status
     */
    private static String buildVipStatusAppliedString(Transaction transaction) {
        if (transaction.getVipDiscountAmount() == 0) {
            return "No VIP discount applied";
        } else {
            return "VIP discount of " + CURRENCY_FORMAT.format(transaction.getVipDiscountAmount()
            ) + " applied";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_transactions);
        setTitle(Constants.ACTIVITY_CUSTOMER_TRANSACTIONS_TITLE);

        Realm realm = RealmUtil.buildDatabase(this);

        String customerId = (String) getIntent().getSerializableExtra("CustomerId");
        Customer customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

        RealmList<Transaction> allTransactions = customer.getTransactions();
        Log.i("TCCart", allTransactions.toString());

        Log.i("TCCart", "Attempting to show " + allTransactions.size() + " transactions");

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout transactionParentLayout = (LinearLayout) findViewById(R.id.transactionLayoutParent);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        for (Transaction transaction : allTransactions) {
            Log.i("TCCart", transaction.toString());
            View childView = inflater.inflate(R.layout.activity_transaction, null);

            TextView transactionAmount = (TextView) childView.findViewById(R.id.transactionAmountText);
            TextView transactionId = (TextView) childView.findViewById(R.id.transactionIdText);
            TextView transactionDate = (TextView) childView.findViewById(R.id.transactionDateText);
            TextView transactionWasVip = (TextView) childView.findViewById(R.id.transactionWasVipText);
            TextView transactionDiscount = (TextView) childView.findViewById(R.id.transactionDiscountText);

            transactionId.setText(transaction.getId());
            transactionDate.setText(formatter.format(transaction.getTransactionDate()));
            transactionWasVip.setText(buildVipStatusAppliedString(transaction));
            transactionDiscount.setText(CURRENCY_FORMAT.format(transaction.getRewardAmountApplied
                    ()) + " Discount");
            transactionAmount.setText(CURRENCY_FORMAT.format(transaction.getPriceOfItemsSold()));

            transactionParentLayout.addView(childView);
        }
    }
}
