package edu.gatech.seclass.tccart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.util.RealmUtil;
import edu.gatech.seclass.tccart.util.TransactionUtil;
import io.realm.Realm;

import static edu.gatech.seclass.tccart.Constants.CURRENCY_FORMAT;

/**
 * The type Build transaction activity.
 */
public class BuildTransactionActivity extends AppCompatActivity {

    private static final String TOTAL_PRICE_FOR_ITEMS_NOT_VALID = "Total Price For Items Not Valid";
    /**
     * Realm database
     */
    private Realm realm;

    private TextView errorText;

    private Customer customer;

    private TextView vipDiscountPriceTextView;

    private TextView rewardsAppliedPriceTextView;

    private TextView totalPriceTextView;

    private EditText priceOfItemsSoldEditText;
    private final TextWatcher priceOfItemsSoldEditTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Do nothing
        }

        //When text is changed, recalculate totals
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (isPriceValid()) {
                double vipDiscountAmount = TransactionUtil.calculateVipDiscountAmount(customer,
                                                                                      getPriceOfItemsSold());
                double calculateRewardAmount = TransactionUtil.calculateRewardAmount(customer,
                                                                                     getPriceOfItemsSold());
                double calculateTotalPrice = TransactionUtil.calculateTotalPriceAmount
                        (getPriceOfItemsSold(), vipDiscountAmount, calculateRewardAmount);

                errorText.setText("");
                vipDiscountPriceTextView.setText(CURRENCY_FORMAT.format(vipDiscountAmount));
                rewardsAppliedPriceTextView.setText(CURRENCY_FORMAT.format(calculateRewardAmount));
                totalPriceTextView.setText(CURRENCY_FORMAT.format(calculateTotalPrice));
            } else {
                errorText.setText(TOTAL_PRICE_FOR_ITEMS_NOT_VALID);
                vipDiscountPriceTextView.setText(CURRENCY_FORMAT.format(0));
                rewardsAppliedPriceTextView.setText(CURRENCY_FORMAT.format(0));
                totalPriceTextView.setText(CURRENCY_FORMAT.format(0));
            }

        }

        public void afterTextChanged(Editable s) {
            //Do nothing
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_transaction);
        setTitle(Constants.ACTIVITY_BUILD_TRANSACTION_TITLE);

        realm = RealmUtil.buildDatabase(this);

        final Button chargeCard = (Button) findViewById(R.id.chargeCreditCardButton);
        errorText = (TextView) findViewById(R.id.buildTransactionErrorTextView);

        priceOfItemsSoldEditText = (EditText) findViewById(R.id.priceOfItemsSoldEditText);
        vipDiscountPriceTextView = (TextView) findViewById(R.id.vipDiscountPriceTextView);
        rewardsAppliedPriceTextView = (TextView) findViewById(R.id.rewardsAppliedPriceTextView);
        totalPriceTextView = (TextView) findViewById(R.id.totalPriceTextView);

        String customerId = (String) getIntent().getSerializableExtra("CustomerId");
        customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {

                if (view == chargeCard) {

                    // Close the keyboard if it's open
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String totalPriceEnteredByUser = priceOfItemsSoldEditText.getText().toString();

                    if (isPriceValid()) {

                        double totalPrice = Double.parseDouble(totalPriceEnteredByUser);

                        double vipDiscountAmount = TransactionUtil.calculateVipDiscountAmount
                                (customer, getPriceOfItemsSold());
                        double calculateRewardAmount = TransactionUtil.calculateRewardAmount
                                (customer, getPriceOfItemsSold());

                        //Create Transaction object, persist it, and link Customer to the
                        // Transaction
                        realm.beginTransaction();
                        Transaction transaction = realm.createObject(Transaction.class);
                        transaction.setId(TransactionUtil.generateTransactionId());
                        transaction.setVipDiscountAmount(vipDiscountAmount);
                        transaction.setRewardAmountApplied(calculateRewardAmount);
                        transaction.setPriceOfItemsSold(totalPrice);
                        transaction.setTransactionDate(new Date());
                        transaction.setPaymentSuccessfullyProcessed(false);

                        customer.getTransactions().add(transaction);
                        realm.commitTransaction();

                        Intent intentToCharge = new Intent(BuildTransactionActivity.this,
                                                           ChargeCreditCardActivity.class);
                        intentToCharge.putExtra("CustomerId", customer.getId());
                        intentToCharge.putExtra("TransactionId", transaction.getId());
                        startActivity(intentToCharge);
                    } else {
                        errorText.setText(TOTAL_PRICE_FOR_ITEMS_NOT_VALID);
                    }

                }
            }
        };

        chargeCard.setOnClickListener(handler);
        priceOfItemsSoldEditText.addTextChangedListener(priceOfItemsSoldEditTextWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Close Realm when done.
    }

    private double getPriceOfItemsSold() {
        if (isPriceValid()) {
            return Double.valueOf(priceOfItemsSoldEditText.getText().toString());
        } else {
            return 0;
        }
    }

    private boolean isPriceValid() {
        if (NumberUtils.isNumber(priceOfItemsSoldEditText.getText().toString()) && Double.valueOf
                (priceOfItemsSoldEditText.getText().toString()) > 0) {
            Log.d("TCCart", "Price valid" + priceOfItemsSoldEditText.getText().toString());
            return true;
        }
        Log.d("TCCart", "Price NOT valid" + priceOfItemsSoldEditText.getText().toString());
        return false;
    }
}
