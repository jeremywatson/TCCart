package edu.gatech.seclass.tccart.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Customer transactions and their attributes.
 * <p/>
 * Created by jeremyw on 3/15/16.
 */
public class Transaction extends RealmObject {

    @PrimaryKey
    private String id;

    /**
     * Date transaction occurred
     */
    private Date transactionDate;

    /**
     * Total price of all the items sold to the customer, prior to any discounts
     */
    private double priceOfItemsSold;

    /**
     * How much reward was applied to this transaction.
     */
    private double rewardAmountApplied;

    /**
     * The amount of discount applied to this
     */
    private double vipDiscountAmount;

    /**
     * Indicates if payment was successfully processed for this transaction
     */
    private boolean paymentSuccessfullyProcessed;

    /**
     * Is payment successfully processed boolean.
     *
     * @return the boolean
     */
    public boolean isPaymentSuccessfullyProcessed() {
        return paymentSuccessfullyProcessed;
    }

    /**
     * Sets payment successfully processed.
     *
     * @param paymentSuccessfullyProcessed the payment successfully processed
     */
    public void setPaymentSuccessfullyProcessed(boolean paymentSuccessfullyProcessed) {
        this.paymentSuccessfullyProcessed = paymentSuccessfullyProcessed;
    }

    /**
     * Gets vip discount amount.
     *
     * @return the vip discount amount
     */
    public double getVipDiscountAmount() {
        return vipDiscountAmount;
    }

    /**
     * Sets vip discount amount.
     *
     * @param vipDiscountAmount the vip discount amount
     */
    public void setVipDiscountAmount(double vipDiscountAmount) {
        this.vipDiscountAmount = vipDiscountAmount;
    }

    /**
     * Gets price of items sold.
     *
     * @return the price of items sold
     */
    public double getPriceOfItemsSold() {
        return priceOfItemsSold;
    }

    /**
     * Sets price of items sold.
     *
     * @param priceOfItemsSold the price of items sold
     */
    public void setPriceOfItemsSold(double priceOfItemsSold) {
        this.priceOfItemsSold = priceOfItemsSold;
    }

    /**
     * Gets transaction date.
     *
     * @return the transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets transaction date.
     *
     * @param transactionDate the transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets reward amount applied.
     *
     * @return the reward amount applied
     */
    public double getRewardAmountApplied() {
        return rewardAmountApplied;
    }

    /**
     * Sets reward amount applied.
     *
     * @param rewardAmountApplied the reward amount applied
     */
    public void setRewardAmountApplied(double rewardAmountApplied) {
        this.rewardAmountApplied = rewardAmountApplied;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

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
        return "Transaction{" +
                "id='" + id + '\'' +
                ", transactionDate=" + transactionDate +
                ", priceOfItemsSold=" + priceOfItemsSold +
                ", rewardAmountApplied=" + rewardAmountApplied +
                ", vipDiscountAmount=" + vipDiscountAmount +
                '}';
    }
}
