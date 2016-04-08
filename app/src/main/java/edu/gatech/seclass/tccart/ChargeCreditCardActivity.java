package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.gatech.seclass.tccart.exception.CardScanFailedException;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.exception.PaymentNotProcessedException;
import edu.gatech.seclass.tccart.model.CreditCard;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.model.VipDiscountYear;
import edu.gatech.seclass.tccart.util.CreditCardUtil;
import edu.gatech.seclass.tccart.util.RealmUtil;
import edu.gatech.seclass.tccart.util.RewardUtil;
import edu.gatech.seclass.tccart.util.TransactionUtil;
import edu.gatech.seclass.tccart.util.VipDiscountUtil;
import io.realm.Realm;

/**
 * The type Charge credit card activity.
 */
public class ChargeCreditCardActivity extends AppCompatActivity {

    /**
     * The constant EMAIL_SUCCESS.
     */
    public static final String EMAIL_SUCCESS = "success";
    /**
     * The constant EMAIL_FAILURE.
     */
    public static final String EMAIL_FAILURE = "fail";
    private static final SimpleDateFormat creditCardDateDisplayFormat = new SimpleDateFormat
            ("MM/dd/yyyy");
    /**
     * Realm database
     */
    private Realm realm;
    private TextView errorText;
    private EditText nameField;
    private EditText cardNumberField;
    private EditText expDateField;
    private EditText securityCodeField;
    private String customerId;
    private String transactionId;
    private String receiptEmailSent;
    private String rewardEmailSent;
    private String vipEmailSent;
    private CreditCard creditCard;
    private Customer customer;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_creditcard);
        setTitle(Constants.ACTIVITY_CHARGE_CREDIT_CARD_TITLE);

        realm = RealmUtil.buildDatabase(this);

        final Button scanCreditCardButton = (Button) findViewById(R.id.scanCreditCard);
        final Button processPaymentButton = (Button) findViewById(R.id.processPayment);

        errorText = (TextView) findViewById(R.id.transactionFailure);

        nameField = (EditText) findViewById(R.id.cardholderName);
        cardNumberField = (EditText) findViewById(R.id.cardNumber);
        expDateField = (EditText) findViewById(R.id.expDate);
        securityCodeField = (EditText) findViewById(R.id.securityCode);

        customerId = (String) getIntent().getSerializableExtra("CustomerId");
        transactionId = (String) getIntent().getSerializableExtra("TransactionId");
        customer = realm.where(Customer.class).equalTo("id", customerId).findFirst();
        transaction = realm.where(Transaction.class).equalTo("id", transactionId).findFirst();

        errorText.setText("");
        nameField.setKeyListener(null);
        cardNumberField.setKeyListener(null);
        expDateField.setKeyListener(null);
        securityCodeField.setKeyListener(null);

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {
                if (view == scanCreditCardButton) {

                    Log.i("TCCart", "Attempt to scan card");

                    //Clear any previous error messages
                    errorText.setText("");

                    try {
                        //Scan card and set text
                        creditCard = CreditCardUtil.scanCreditCard();
                        nameField.setText(creditCard.getFirstName() + " " + creditCard
                                .getLastName());
                        cardNumberField.setText(creditCard.getNumber());
                        expDateField.setText(creditCardDateDisplayFormat.format(creditCard
                                                                                        .getExpirationDate()));
                        securityCodeField.setText(creditCard.getSecurityCode());
                    }
                    /**
                     * Generally we don't catch exception, but if anything is wrong, we need to
                     * tell the user about it.
                     */
                    catch (CardScanFailedException e) {
                        creditCard = new CreditCard();
                        handleError(e, "Error scanning card.  Please try again.");
                    }
                    catch (Exception e) {
                        creditCard = new CreditCard();
                        handleError(e, "Error.  Please try again.");
                    }

                }

                /**
                 * If the user chooses to process the payment
                 * then take the steps needed to process the payment and go to the success screen
                 * if successful
                 */
                if (view == processPaymentButton) {

                    if (CreditCardUtil.creditCardIsValid(creditCard)) {
                        try {
                            realm.beginTransaction();

                            //Take care of checking out the transaction; do things like charge
                            // credit
                            // card, update transaction status, send transaction e-mail
                            try {
                                TransactionUtil.checkOutTransaction(creditCard, transaction,
                                                                    customer);
                                receiptEmailSent = EMAIL_SUCCESS;

                            }
                            catch (EmailNotSentException e) {
                                receiptEmailSent = EMAIL_FAILURE;

                            }

                            //Adjust reward balances, subtracting used rewards and awarding new ones
                            // if achieved
                            try {
                                boolean rewardWasAdded = RewardUtil.adjustRewardBalance(customer,
                                                                                        transaction);

                                if (rewardWasAdded) {
                                    rewardEmailSent = EMAIL_SUCCESS;
                                }
                            }
                            catch (EmailNotSentException e) {
                                rewardEmailSent = EMAIL_FAILURE;

                            }

                            //Calculate customer's VIP status based on this purchase and notify
                            // them if they have.  Ideally this could/would be done in an
                            // asynchronous background thread.
                            if (VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer)) {

                                //Add VIP status to customer
                                addNextYearVipToCustomer();

                                //send VIP email
                                try {
                                    VipDiscountUtil.sendVipStatusAchievedEmail(customer,
                                                                               Constants.NEXT_YEAR);
                                    vipEmailSent = EMAIL_SUCCESS;

                                }
                                catch (EmailNotSentException e) {
                                    vipEmailSent = EMAIL_FAILURE;

                                }

                            }

                            realm.commitTransaction();

                            Intent intentToProcessPayment = new Intent(ChargeCreditCardActivity
                                                                               .this,
                                                                       ChargeSuccessActivity.class);
                            intentToProcessPayment.putExtra("CustomerId", customerId);
                            intentToProcessPayment.putExtra("TransactionId", transactionId);
                            intentToProcessPayment.putExtra("CreditCard", creditCard);
                            intentToProcessPayment.putExtra("ReceiptEmailSent", receiptEmailSent);
                            intentToProcessPayment.putExtra("RewardEmailSent", rewardEmailSent);
                            intentToProcessPayment.putExtra("VipEmailSent", vipEmailSent);

                            startActivity(intentToProcessPayment);

                        }
                        catch (PaymentNotProcessedException e) {
                            //This is not OK, as it indicates that things did not work
                            if (realm.isInTransaction()) {
                                realm.cancelTransaction();
                            }
                            handleError(e, "Error processing payment.  Please try again.");
                        }
                        //Catch Exception to handle all other payment processing errors gracefully
                        catch (Exception e) {
                            if (realm.isInTransaction()) {
                                realm.cancelTransaction();
                            }
                            handleError(e, "Error processing.  Please try again.");
                        }
                    } else {
                        errorText.setText("Card not valid.  Please try again.");
                    }
                }
            }
        };

        scanCreditCardButton.setOnClickListener(handler);
        processPaymentButton.setOnClickListener(handler);

    }

    private void handleError(Exception e, String errorMessage) {
        Log.w("TCCart", errorMessage, e);
        nameField.setText("");
        cardNumberField.setText("");
        expDateField.setText("");
        securityCodeField.setText("");
        errorText.setText(errorMessage);
    }

    private void addNextYearVipToCustomer() {
        VipDiscountYear vipDiscountYear = realm.createObject(VipDiscountYear.class);
        vipDiscountYear.setVipYearQualifiedFor(Constants.NEXT_YEAR);

        VipDiscount vipDiscount = realm.createObject(VipDiscount.class);
        vipDiscount.getYearsVipDiscountQualifiedFor().add(vipDiscountYear);

        customer.setVipDiscount(vipDiscount);
    }

}