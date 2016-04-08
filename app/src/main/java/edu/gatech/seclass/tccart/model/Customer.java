package edu.gatech.seclass.tccart.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Customer object
 */
public class Customer extends RealmObject {

    /**
     * Length for customer id
     */
    public static final int CUSTOMER_ID_LENGTH = 8;

    /**
     * String id which stores customer identification number; currently mandated to be an 8
     * character hexidecimal string
     */
    @PrimaryKey
    private String id;

    /**
     * Customer first name
     */
    private String firstName;

    /**
     * Customer lastName
     */
    private String lastName;

    /**
     * Customer email
     */
    private String email;

    /**
     * Customer transactions
     */
    private RealmList<Transaction> transactions;

    /**
     * Customer reward
     */
    private Reward reward;

    /**
     * Customer VipDiscount and Status
     */
    private VipDiscount vipDiscount;

    /**
     * Gets reward.
     *
     * @return the reward
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * Sets reward.
     *
     * @param reward the reward
     */
    public void setReward(Reward reward) {
        this.reward = reward;
    }

    /**
     * Gets vip discount.
     *
     * @return the vip discount
     */
    public VipDiscount getVipDiscount() {
        return vipDiscount;
    }

    /**
     * Sets vip discount.
     *
     * @param vipDiscount the vip discount
     */
    public void setVipDiscount(VipDiscount vipDiscount) {
        this.vipDiscount = vipDiscount;
    }

    /**
     * Gets transactions.
     *
     * @return the transactions
     */
    public RealmList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Sets transactions.
     *
     * @param transactions the transactions
     */
    public void setTransactions(RealmList<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
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
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
