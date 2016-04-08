package edu.gatech.seclass.tccart.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Credit Card object
 */
public class CreditCard implements Serializable {

    /**
     * First name
     */
    private String firstName;

    /**
     * Last name
     */
    private String lastName;

    /**
     * Expiration date of card
     */
    private Date expirationDate;

    /**
     * Security code of card
     */
    private String securityCode;

    /**
     * Number of card
     */
    private String number;

    /**
     * Gets number.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets number.
     *
     * @param number the number
     */
    public void setNumber(String number) {
        this.number = number;
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
     * Gets expiration date.
     *
     * @return the expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets expiration date.
     *
     * @param expirationDate the expiration date
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets security code.
     *
     * @return the security code
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * Sets security code.
     *
     * @param securityCode the security code
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", expirationDate=" + expirationDate +
                ", securityCode='" + securityCode + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
