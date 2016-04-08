package edu.gatech.seclass.tccart.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.tccart.exception.CardScanFailedException;
import edu.gatech.seclass.tccart.model.CreditCard;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * The type Credit card util test.
 */
@RunWith(PowerMockRunner.class)
public class CreditCardUtilTest {

    /**
     * Test blank credit card is invalid credit card.
     */
    @Test
    public void test_blankCreditCardIsInvalidCreditCard() {

        CreditCard creditCard = new CreditCard();
        assertFalse(CreditCardUtil.creditCardIsValid(creditCard));

    }

    /**
     * Test null is invalid credit card.
     */
    @Test
    public void test_nullIsInvalidCreditCard() {

        assertFalse(CreditCardUtil.creditCardIsValid(null));
    }

    /**
     * Test valid card is valid credit card.
     */
    @Test
    public void test_validCardIsValidCreditCard() {

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("4242424242424242");
        creditCard.setExpirationDate(DateUtils.addYears(new Date(), 5));
        creditCard.setFirstName("Jane");
        creditCard.setLastName("Doe");
        creditCard.setSecurityCode("222");

        assertTrue(CreditCardUtil.creditCardIsValid(creditCard));

    }

    /**
     * Test err from credit card service throws exception.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test(expected = CardScanFailedException.class)
    @PrepareForTest({CreditCardService.class})
    public void test_errFromCreditCardServiceThrowsException() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).thenReturn("ERR");

        CreditCardUtil.scanCreditCard();
    }

    /**
     * Test null from credit card service throws exception.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test(expected = CardScanFailedException.class)
    @PrepareForTest({CreditCardService.class})
    public void test_nullFromCreditCardServiceThrowsException() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).thenReturn(null);

        CreditCardUtil.scanCreditCard();
    }

    /**
     * Test partial input from credit card service throws exception.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test(expected = CardScanFailedException.class)
    @PrepareForTest({CreditCardService.class})
    public void test_partialInputFromCreditCardServiceThrowsException() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).thenReturn("1991090#1902#3");

        CreditCardUtil.scanCreditCard();
    }

    /**
     * Test bad input from credit card service throws exception.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test(expected = CardScanFailedException.class)
    @PrepareForTest({CreditCardService.class})
    public void test_badInputFromCreditCardServiceThrowsException() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).thenReturn("akslnflnfs03%9s");

        CreditCardUtil.scanCreditCard();
    }

    /**
     * Test too many fields from credit card service throws exception.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test(expected = CardScanFailedException.class)
    @PrepareForTest({CreditCardService.class})
    public void test_tooManyFieldsFromCreditCardServiceThrowsException() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).
                thenReturn("Ralph#Hapschatt#9328895960019440#12312016#111#5039103");

        CreditCardUtil.scanCreditCard();
    }

    /**
     * Test valid scan creates valid credit card.
     *
     * @throws CardScanFailedException the card scan failed exception
     */
    @Test
    @PrepareForTest({CreditCardService.class})
    public void test_validScanCreatesValidCreditCard() throws CardScanFailedException {

        PowerMockito.mockStatic(CreditCardService.class);
        PowerMockito.when(CreditCardService.readCreditCard()).
                thenReturn("Everett#Scott#4224876949325382#12312015#000");

        CreditCard creditCard = CreditCardUtil.scanCreditCard();

        assertEquals(creditCard.getFirstName(), "Everett");
        assertEquals(creditCard.getLastName(), "Scott");
        assertEquals(creditCard.getNumber(), "4224876949325382");
        assertEquals(creditCard.getSecurityCode(), "000");

    }
}
