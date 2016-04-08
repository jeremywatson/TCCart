package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.util.RealmUtil;
import edu.gatech.seclass.tccart.util.TransactionUtil;
import io.realm.Realm;

import static edu.gatech.seclass.tccart.Constants.CURRENCY_FORMAT;

/**
 * The type Charge success activity.
 */
public class ChargeSuccessActivity extends AppCompatActivity {

    private String customerId;
    private TextView emailSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_success);
        setTitle(Constants.ACTIVITY_CHARGE_SUCCESS_TITLE);

        Realm realm = RealmUtil.buildDatabase(this);

        TextView chargeAmount = (TextView) findViewById(R.id.chargeSuccessAmountText);
        emailSent = (TextView) findViewById(R.id.chargeSuccessEmailText);

        final Button toActionsButton = (Button) findViewById(R.id.chargeSuccessToActionsButton);
        final Button toMainButton = (Button) findViewById(R.id.chargeSuccessToMainButton);

        Bundle extras = getIntent().getExtras();

        customerId = extras.getString("CustomerId");
        String transactionId = extras.getString("TransactionId");
        String receiptEmailWasSent = extras.getString("ReceiptEmailSent");
        String rewardEmailWasSent = extras.getString("RewardEmailSent");
        String vipEmailWasSent = extras.getString("VipEmailSent");

        Transaction transaction = realm.where(Transaction.class).equalTo("id", transactionId)
                .findFirst();

        chargeAmount.setText(CURRENCY_FORMAT.format(TransactionUtil.calculateTotalPriceAmount
                (transaction)));
        emailSent.setText("");

        presentSentEmails(receiptEmailWasSent, rewardEmailWasSent, vipEmailWasSent);

        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View view) {
                if (view == toActionsButton) {
                    Intent intentToActions = new Intent(ChargeSuccessActivity.this,
                                                        CustomerActionsActivity.class);
                    intentToActions.putExtra("CustomerId", customerId);
                    startActivity(intentToActions);
                }

                if (view == toMainButton) {
                    Intent intentToMain = new Intent(ChargeSuccessActivity.this, MainActivity
                            .class);
                    startActivity(intentToMain);
                }
            }
        };

        toActionsButton.setOnClickListener(handler);
        toMainButton.setOnClickListener(handler);

    }

    private void presentSentEmails(String receiptEmail, String rewardEmail, String vipEmail) {
        if (receiptEmail.equals(ChargeCreditCardActivity.EMAIL_SUCCESS)) {
            emailSent.append("Receipt email successfully sent to customer!\n");

        } else if (receiptEmail.equals(ChargeCreditCardActivity.EMAIL_FAILURE)) {
            emailSent.append("Receipt email failed to send.\n");

        }

        if (rewardEmail != null && rewardEmail.equals(ChargeCreditCardActivity.EMAIL_SUCCESS)) {
            emailSent.append("$3 reward email successfully sent!\n");

        } else if (rewardEmail != null && rewardEmail.equals(ChargeCreditCardActivity.EMAIL_FAILURE)) {
            emailSent.append("Reward email failed to send.\n");

        }

        if (vipEmail != null && vipEmail.equals(ChargeCreditCardActivity.EMAIL_SUCCESS)) {
            emailSent.append("VIP status email successfully sent!\n");

        } else if (vipEmail != null && vipEmail.equals(ChargeCreditCardActivity.EMAIL_FAILURE)) {
            emailSent.append("VIP status email failed to send.\n");
        }
    }

}
