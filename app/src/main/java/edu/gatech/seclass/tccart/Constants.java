package edu.gatech.seclass.tccart;

import android.text.format.DateUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Constants used through the application
 */
public class Constants {

    /**
     * The constant ACTIVITY_ADD_CUSTOMER_TITLE.
     */
    public static final String ACTIVITY_ADD_CUSTOMER_TITLE = "Add New Customer";
    /**
     * The constant ACTIVITY_BUILD_TRANSACTION_TITLE.
     */
    public static final String ACTIVITY_BUILD_TRANSACTION_TITLE = "New Transaction";
    /**
     * The constant ACTIVITY_CHARGE_CREDIT_CARD_TITLE.
     */
    public static final String ACTIVITY_CHARGE_CREDIT_CARD_TITLE = "Payment Information";
    /**
     * The constant ACTIVITY_CHARGE_SUCCESS_TITLE.
     */
    public static final String ACTIVITY_CHARGE_SUCCESS_TITLE = "Charge Successful";
    /**
     * The constant ACTIVITY_CUSTOMER_ACTIONS_TITLE.
     */
    public static final String ACTIVITY_CUSTOMER_ACTIONS_TITLE = "Customer Actions";
    /**
     * The constant ACTIVITY_CUSTOMER_CREATE_SUCCESS_TITLE.
     */
    public static final String ACTIVITY_CUSTOMER_CREATE_SUCCESS_TITLE = "Customer Created";
    /**
     * The constant ACTIVITY_CUSTOMER_INFO_TITLE.
     */
    public static final String ACTIVITY_CUSTOMER_INFO_TITLE = "View/Edit Customer";
    /**
     * The constant ACTIVITY_CUSTOMER_TRANSACTIONS_TITLE.
     */
    public static final String ACTIVITY_CUSTOMER_TRANSACTIONS_TITLE = "Previous Transactions";
    /**
     * The constant ACTIVITY_LOOKUP_CUSTOMER_TITLE.
     */
    public static final String ACTIVITY_LOOKUP_CUSTOMER_TITLE = "Scan Customer Card";

    /**
     * Currency format to use when displaying currency, $0.00
     */
    public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.getDefault());

    /**
     * Number of times to try system-to-system services like e-mail, payment processing
     * <p/>
     * 1 = no retry Greater than 1 = try more than once on a failure.
     */
    public static final int SERVICE_ATTEMPTS = 1;

    /**
     * The constant NEXT_YEAR, used to represent next year's date in YYYY format.
     */
    public static final String NEXT_YEAR = new SimpleDateFormat("yyyy").format(new Date().getTime
            () + DateUtils.YEAR_IN_MILLIS);
}
