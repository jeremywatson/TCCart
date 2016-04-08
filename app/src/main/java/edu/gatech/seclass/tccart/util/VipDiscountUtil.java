package edu.gatech.seclass.tccart.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.TCCartApplication;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.model.VipDiscountYear;
import edu.gatech.seclass.tccart.proxy.EmailServiceProxy;

/**
 * Utilities used to manipulate/edit/view VipDiscounts
 * <p/>
 * Created by jeremyw on 3/20/16.
 */
public class VipDiscountUtil {

    /**
     * Determine if a customer is a VIP now, qualifying them for VIP discounts
     *
     * @param customer customer to check
     * @return true if customer is VIP, false otherwise
     */
    public static boolean isCustomerVipNow(Customer customer) {

        String currentYear = new SimpleDateFormat("yyyy").format(new Date(new Date().getTime() +
                                                                                  TCCartApplication.timeToAddToCalculationForTesting));

        // if anything is null that needs to be there to determine if a vipDiscountYear exists
        // return false
        if (null == customer || null == customer.getVipDiscount() || null == customer
                .getVipDiscount().getYearsVipDiscountQualifiedFor()) {
            return false;
        }

        //normally we would have done something like use Collection.contains, but because of this
        // being a RealmList,
        // we need to compare the inner contents
        for (VipDiscountYear vipDiscountYearFromCustomer : customer.getVipDiscount()
                .getYearsVipDiscountQualifiedFor()) {
            if (vipDiscountYearFromCustomer.getVipYearQualifiedFor().equals(currentYear)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Process customer vip status.  If the customer newly qualifies for VIP status for next year,
     * return true
     *
     * @param customer to check and see if they are newly qualified for VIP
     */
    public static boolean isCustomerNewlyQualifiedForVip(Customer customer) {

        if (null == customer) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        VipDiscountYear vipDiscountNextYear = new VipDiscountYear();
        vipDiscountNextYear.setVipYearQualifiedFor(Constants.NEXT_YEAR);

        //if customer is null or customer is already a VIP for next year, return, we don't need
        // to calculate their status
        if (null != customer.getVipDiscount() &&
                null != customer.getVipDiscount().getYearsVipDiscountQualifiedFor() &&
                customer.getVipDiscount().getYearsVipDiscountQualifiedFor().contains
                        (vipDiscountNextYear)) {
            return false;
        }

        return TransactionUtil.getSumOfAllYearlyTransactions(customer) >= VipDiscount.VIP_THRESHOLD;

    }

    /**
     * Send customer message that their VIP status has been achieved
     *
     * @param customer   customer that has earned VIP status
     * @param yearEarned year that they earned it for
     */
    public static void sendVipStatusAchievedEmail(Customer customer, String yearEarned) throws
            EmailNotSentException {

        if (null == customer) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        String emailSubject = "TCCart VIP Status Earned";
        String emailBody = "Thank you for your purchase.  You have earned VIP status for " +
                yearEarned + ". We hope to see you " +
                "back again soon.";

        boolean emailSent;

        //Number of times to retry
        int retryAttempts = Constants.SERVICE_ATTEMPTS;

        //Send e-mail, with retries upon failure
        do {

            Log.i("TCCard", "Trying to send email: " + customer.getEmail() + emailSubject +
                    emailBody);
            emailSent = EmailServiceProxy.sendEMail(customer.getEmail(), emailSubject, emailBody);
            Log.i("TCCard", "Email send successful? " + emailSent);

            //Retry up to the specified number of times.
            retryAttempts--;
        } while (!emailSent && retryAttempts > 0);

        if (!emailSent) {
            //If we fail after attempts, we give up, log a message and allow the transaction to
            // continue on.
            // In a later version of this application this could be made more sophisticated.
            Log.w("TCCard", "Email send failed " + customer.getEmail() + emailSubject + emailBody);
            throw new EmailNotSentException("VIP notification e-mail unable to be sent");
        }

    }
}
