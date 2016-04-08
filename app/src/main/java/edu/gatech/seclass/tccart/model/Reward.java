package edu.gatech.seclass.tccart.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Reward - a credit that can be earned by a customer after a certain amount of purchases are made.
 * The credit can be redeemed on future purchases.
 * <p/>
 * Created by jeremyw on 3/15/16.
 */
public class Reward extends RealmObject {

    /**
     * The amount of purchase required to earn a reward.  Note that this is the amount the customer
     * actually pays, in other words, the total before any discounts are applied.
     */
    public static final double AMOUNT_OF_PURCHASE_NEEDED_TO_EARN_REWARD = 30;

    /**
     * The amount each reward is initialized to when it is awarded
     */
    public static final double INITIAL_REWARD_AMOUNT = 3;

    /**
     * The number of months after a reward is earned where it is still available for use
     */
    public static final int VALID_MONTHS_DURATION_FOR_REWARD_TO_BE_AVAILABLE = 1;

    @PrimaryKey
    private String id;

    /**
     * Date reward obtained
     */
    private Date obtainedDate;

    /**
     * Remaining amount of reward
     */
    private double remainingAmount;

    /**
     * Gets obtained date.
     *
     * @return the obtained date
     */
    public Date getObtainedDate() {
        return obtainedDate;
    }

    /**
     * Sets obtained date.
     *
     * @param obtainedDate the obtained date
     */
    public void setObtainedDate(Date obtainedDate) {
        this.obtainedDate = obtainedDate;
    }

    /**
     * Gets remaining amount.
     *
     * @return the remaining amount
     */
    public double getRemainingAmount() {
        return remainingAmount;
    }

    /**
     * Sets remaining amount.
     *
     * @param remainingAmount the remaining amount
     */
    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    // --Commented out by Inspection START (4/8/16, 9:11 AM):
    //    /**
    //     * Gets id.
    //     *
    //     * @return the id
    //     */
    //    public String getId() {
    //        return id;
    //    }
    // --Commented out by Inspection STOP (4/8/16, 9:11 AM)

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "id='" + id + '\'' +
                ", obtainedDate=" + obtainedDate +
                ", remainingAmount=" + remainingAmount +
                '}';
    }
}
