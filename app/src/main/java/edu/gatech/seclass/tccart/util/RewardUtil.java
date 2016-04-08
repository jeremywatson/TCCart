package edu.gatech.seclass.tccart.util;

import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.TCCartApplication;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Reward;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.proxy.EmailServiceProxy;

/**
 * Utility class with various methods to process Rewards
 * <p/>
 * Created by jeremyw on 3/20/16.
 */
public class RewardUtil {

    /**
     * Generate unique id to be used for a transaction.
     *
     * @return unique id
     */
    public static String generateRewardId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns pending (unexpired) reward amounts for a customer.
     *
     * @param customer to retrieve current reward data for
     * @return amount of pending reward to be applied to transactions as of current date
     */
    public static double getRemainingRewardAmount(Customer customer) {
        Reward reward = customer.getReward();
        if (null != reward && isRewardActive(reward)) {
            return reward.getRemainingAmount();
        }
        return 0;
    }

    /**
     * Is the reward active and not expired?
     *
     * @param reward reward to check if active
     * @return true if active, false if expired
     */
    public static boolean isRewardActive(Reward reward) {
        final Date todayDate = new Date(new Date().getTime() + TCCartApplication
                .timeToAddToCalculationForTesting);

        //if today's date is less than or equal to the date the reward was obtained + number of
        // months a reward lasts for, the reward is still good
        return reward.getObtainedDate() != null && 1 > DateUtils.truncatedCompareTo(todayDate,
                                                                                    DateUtils
                                                                                            .addMonths(reward.getObtainedDate(), Reward.VALID_MONTHS_DURATION_FOR_REWARD_TO_BE_AVAILABLE), Calendar.DATE);
    }

    /**
     * Adjust customer reward balance based on how much of the reward is applied in the transaction
     * <p/>
     * In order to facilitate persistence, calls to this method must be wrapped in a transaction
     *
     * @param customer    to reduce the reward
     * @param transaction where a reward amount may have been applied
     * @throws EmailNotSentException if reward balance is adjusted successfully but email fails
     */
    public static boolean adjustRewardBalance(Customer customer, Transaction transaction) throws
            EmailNotSentException {

        boolean rewardWasAdded = false;

        //First, remove any rewards that have been used
        if (null != transaction && 0 != transaction.getRewardAmountApplied()) {
            double rewardAmountRemaining = customer.getReward().getRemainingAmount() -
                    transaction.getRewardAmountApplied();

            customer.getReward().setRemainingAmount(rewardAmountRemaining);
        }

        //Second, increment any earned rewards
        if (null != transaction && TransactionUtil.calculateTotalPriceAmount(transaction) >=
                Reward.AMOUNT_OF_PURCHASE_NEEDED_TO_EARN_REWARD) {
            //First, verify that the reward is now zero
            if (customer.getReward().getRemainingAmount() != 0 && isRewardActive(customer.getReward())) {
                throw new IllegalStateException("Reward is not zero, but it should be before a "
                                                        + "new reward can be earned.");
            }
            customer.getReward().setRemainingAmount(Reward.INITIAL_REWARD_AMOUNT);
            customer.getReward().setObtainedDate(new Date());

            sendRewardEarnedEmail(customer);
            rewardWasAdded = true;

        }
        return rewardWasAdded;

    }

    /**
     * Send email send request when customer has earned a reward.
     *
     * @param customer to send outbound email reward acknowledgement to.
     */
    private static void sendRewardEarnedEmail(Customer customer) throws EmailNotSentException {

        String emailSubject = "TCCart Reward Earned";
        String emailBody = "Thank you for your purchase.  You have earned a " + Constants
                .CURRENCY_FORMAT.format(Reward.INITIAL_REWARD_AMOUNT) + " reward towards your " +
                "next purchase. We hope to see you " +
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
            if (retryAttempts > 0) {
                retryAttempts--;
            }

        } while (!emailSent && retryAttempts > 0);

        if (!emailSent) {
            //If we fail after 20 attempts, we give up, log a message and allow the
            // transaction to continue on.
            // In a later version of this application this could be made more sophisticated.
            Log.w("TCCard", "Email send failed " + customer.getEmail() + emailSubject +
                    emailBody);
            throw new EmailNotSentException("Reward Email Not Sent");
        }

    }

}
