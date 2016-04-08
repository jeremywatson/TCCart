package edu.gatech.seclass.tccart.util;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.gatech.seclass.tccart.exception.CardScanFailedException;
import edu.gatech.seclass.tccart.model.CreditCard;
import edu.gatech.seclass.tccart.proxy.CreditCardServiceProxy;

/**
 * Various utilities for working with Credit Cards
 * <p/>
 * Created by jeremyw on 3/23/16.
 */
public class CreditCardUtil {

    private static final SimpleDateFormat creditCardScanFormatter = new SimpleDateFormat
            ("MMddyyyy");

    /**
     * Scan credit card and return details as a CreditCard object
     *
     * @return CreditCard object, populated with card data
     */
    public static CreditCard scanCreditCard() throws CardScanFailedException {

        String fromCreditCardService = CreditCardServiceProxy.readCreditCard();

        if ("ERR".equals(fromCreditCardService) || fromCreditCardService == null) {
            throw new CardScanFailedException("Card failed to scan");
        }

        String[] creditCardData = fromCreditCardService.split("#");

        if (creditCardData.length != 5) {
            throw new CardScanFailedException("Card not recognized.");
        }

        Log.i("TCCart", "Credit Card Info Received" + ArrayUtils.toString(creditCardData));

        try {
            String firstName = creditCardData[0];
            String lastName = creditCardData[1];
            String ccNumber = creditCardData[2];
            String expDate = creditCardData[3];
            String cvc = creditCardData[4];

            Date date = DateUtils.truncate(creditCardScanFormatter.parse(expDate), Calendar.DATE);

            CreditCard creditCard = new CreditCard();
            creditCard.setExpirationDate(date);
            creditCard.setSecurityCode(cvc);
            creditCard.setFirstName(firstName);
            creditCard.setLastName(lastName);
            creditCard.setNumber(ccNumber);

            return creditCard;

        }
        catch (IndexOutOfBoundsException e) {
            throw new CardScanFailedException("Card failed to scan");

        }
        catch (ParseException pe) {
            throw new RuntimeException("Credit card could not be processed");
        }
    }

    /**
     * Check to see if credit card is valid or not to proceed with trying to process the payment
     *
     * @param creditCard credit card to check
     * @return true if valid, false if not
     */
    public static boolean creditCardIsValid(CreditCard creditCard) {

        return !(null == creditCard ||
                StringUtils.isBlank(creditCard.getLastName()) ||
                StringUtils.isBlank(creditCard.getFirstName()) ||
                StringUtils.isBlank(creditCard.getNumber()) ||
                StringUtils.isBlank(creditCard.getSecurityCode()) ||
                null == creditCard.getExpirationDate());

    }
}
