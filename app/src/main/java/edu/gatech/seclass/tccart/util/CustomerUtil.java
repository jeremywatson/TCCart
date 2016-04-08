package edu.gatech.seclass.tccart.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.proxy.PrintingServiceProxy;

/**
 * Various utilities for working with customers.
 * <p/>
 * Created by jeremyw on 3/20/16.
 */
public class CustomerUtil {

    /**
     * Generate ID to be used for a customer, and guarantee that it is unique by checking realm to
     * see if it is unique
     *
     * @return generatedId
     */
    public static String generateID() {

        Random r = new Random();

        StringBuilder sb = new StringBuilder();

        while (sb.length() < Customer.CUSTOMER_ID_LENGTH) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, Customer.CUSTOMER_ID_LENGTH);
    }

    /**
     * Check to see if email address is valid
     *
     * @param email email to check
     * @return true if valid, false if not
     */
    public static boolean isEmailValid(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        } else {
            Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher emailMatcher = emailPattern.matcher(email);
            return emailMatcher.matches();
        }
    }

    /**
     * Print customer card, featuring a QR code
     *
     * @param customer to print card for
     * @return true if card printed, false if it didn't
     */
    public static boolean printCard(Customer customer) {
        return PrintingServiceProxy.printCard(customer.getFirstName(), customer.getLastName(),
                                              customer.getId());
    }

}
