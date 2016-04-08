package edu.gatech.seclass.tccart.util;

import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.util.Precision;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.exception.PaymentNotProcessedException;
import edu.gatech.seclass.tccart.model.CreditCard;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.proxy.EmailServiceProxy;
import edu.gatech.seclass.tccart.proxy.PaymentServiceProxy;
import io.realm.RealmList;

/**
 * Business logic and utility methods related to creating, modifying and processing customer
 * transactions
 * <p/>
 * Created by jeremyw on 3/21/16.
 */
public class TransactionUtil {

    /**
     * Generate unique id to be used for a transaction.
     *
     * @return unique id
     */
    public static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Calculate the VipDiscountAmount to be applied to a transaction based on the customer's VIP
     * discount status and the total price for the items on the order.
     *
     * @param customer         customer to check
     * @param priceOfItemsSold total price for the items (not including discounts)
     * @return vip discount amount that can be applied to the transaction based on the
     * priceOfItemsSold passed in
     */
    public static double calculateVipDiscountAmount(Customer customer, double priceOfItemsSold) {
        if (VipDiscountUtil.isCustomerVipNow(customer)) {
            Log.i("TCCart", "Customer" + customer + "is VIP for year");
            return Precision.round(priceOfItemsSold * VipDiscount.PERCENT_OFF, 2);
        } else {
            Log.i("TCCart", "Customer" + customer + "is NOT VIP for year");
            return 0;
        }
    }

    /**
     * Calculate the amount of reward that can be applied to a transaction.
     *
     * @param customer         customer to calculate reward for
     * @param priceOfItemsSold total price for items (not including discounts)
     * @return reward amount to be returned
     */
    public static double calculateRewardAmount(Customer customer, double priceOfItemsSold) {

        double rewardAmount = 0;
        double remainingRewardAmount = RewardUtil.getRemainingRewardAmount(customer);
        double vipDiscountAmount = calculateVipDiscountAmount(customer, priceOfItemsSold);

        //we only look at rewards that are greater than zero
        if (remainingRewardAmount > 0) {
            //if the reward amounts is greater than or equal to the total price for items, we use
            // that
            if (remainingRewardAmount >= priceOfItemsSold - vipDiscountAmount) {
                return priceOfItemsSold - vipDiscountAmount;
            }
            //otherwise we use the full reward
            return remainingRewardAmount;
        }
        return rewardAmount;
    }

    /**
     * Calculates total price amount of a Transaction
     *
     * @param priceOfItemsSold    price of items sold
     * @param vipDiscountAmount   vip discount amount
     * @param rewardAmountApplied reward amount applied to this purchase
     * @return totalPrice total price after discounts
     */
    public static double calculateTotalPriceAmount(double priceOfItemsSold, double
            vipDiscountAmount, double rewardAmountApplied) {
        return priceOfItemsSold - vipDiscountAmount - rewardAmountApplied;
    }

    /**
     * Calculates total price amount of a Transaction
     *
     * @param transaction transaction to calculate price for
     * @return totalPrice calculated price for the transaction
     */
    public static double calculateTotalPriceAmount(Transaction transaction) {
        return calculateTotalPriceAmount(transaction.getPriceOfItemsSold(), transaction
                .getVipDiscountAmount(), transaction.getRewardAmountApplied());
    }

    /**
     * Determines if a vipDiscount is applied to a transaction, meaning that a deduction has been
     * made for vip
     *
     * @param transaction to check and see if a VIP discount is applied
     * @return true of it is applied, false otherwise
     */
    public static boolean isVipDiscountApplied(Transaction transaction) {
        return null != transaction && transaction.getVipDiscountAmount() > 0;
    }

    /**
     * Send transaction email to customer for transaction
     *
     * @param customer    customer checking out
     * @param transaction transaction being checked out
     */
    static void sendTransactionProcessedEmail(Customer customer, Transaction transaction) throws
            EmailNotSentException {

        if (null == customer || null == transaction) {
            throw new IllegalArgumentException("Customer and/or Transaction are null, and cannot " +
                                                       "" + "be");
        }

        String emailSubject = "TCCart Purchase Processed";
        String emailBody = "Thank you for your purchase of " + Constants.CURRENCY_FORMAT.format
                (TransactionUtil.calculateTotalPriceAmount(transaction)) + ".  We hope to see you" +
                " back again soon.";

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
            if (retryAttempts > 0) {
                retryAttempts--;
            }
        } while (!emailSent && retryAttempts > 0);

        if (!emailSent) {
            Log.w("TCCard", "Email send failed " + customer.getEmail() + emailSubject + emailBody);
            throw new EmailNotSentException("Transaction e-mail unable to be sent");
        }

    }

    /**
     * Process transaction payment, a 3 step process:
     * <p/>
     * (1) call the external Payment Service to process the payment
     * <p/>
     * (2) mark the transaction as successfully processed
     * <p/>
     * (3) e-mail customer with confirmation e-mail
     *
     * @param creditCard  creditCard used for check out
     * @param transaction transaction being checked out
     * @param customer    customer being checked out
     * @throws EmailNotSentException if all else succeeds but email is not sent
     */
    public static void checkOutTransaction(CreditCard creditCard, Transaction transaction,
                                           Customer customer) throws EmailNotSentException,
            PaymentNotProcessedException {

        boolean paymentProcessed;

        paymentProcessed = PaymentServiceProxy.processPayment(creditCard.getFirstName(),
                                                              creditCard.getLastName(),
                                                              creditCard.getNumber(), creditCard
                                                                      .getExpirationDate(),
                                                              creditCard.getSecurityCode(),
                                                              calculateTotalPriceAmount
                                                                      (transaction));
        if (!paymentProcessed) {
            throw new PaymentNotProcessedException("Transaction unable to be processed; " +
                                                           "PaymentService is" + " unable to " +
                                                           "process payment");
        }

        //Mark transaction as processed
        transaction.setPaymentSuccessfullyProcessed(true);
        sendTransactionProcessedEmail(customer, transaction);

    }

    /**
     * Get sum of all transactions for the current year
     *
     * @param customer to get all transactions for
     * @return sum of yearly transactions
     */
    public static double getSumOfAllYearlyTransactions(Customer customer) {

        RealmList<Transaction> allTransactions = customer.getTransactions();

        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(currentYear));
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date startDayOfYear = cal.getTime();

        cal.set(Calendar.YEAR, Integer.valueOf(currentYear));
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        Date endDayOfYear = cal.getTime();

        double sumOfYearlyTransactions = 0;

        if (null != allTransactions) {
            for (Transaction transaction : allTransactions) {
                if (transaction.isPaymentSuccessfullyProcessed() &&
                        0 <= DateUtils.truncatedCompareTo(transaction.getTransactionDate(),
                                                          startDayOfYear, Calendar.DATE) &&

                        0 >= DateUtils.truncatedCompareTo(transaction.getTransactionDate(),
                                                          endDayOfYear, Calendar.DATE)) {
                    sumOfYearlyTransactions += TransactionUtil.calculateTotalPriceAmount
                            (transaction);
                }
            }
        }

        return sumOfYearlyTransactions;

    }
}
