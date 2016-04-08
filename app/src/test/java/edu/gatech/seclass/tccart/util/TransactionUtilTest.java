package edu.gatech.seclass.tccart.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;
import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.exception.PaymentNotProcessedException;
import edu.gatech.seclass.tccart.model.CreditCard;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;
import io.realm.RealmList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

/**
 * Test TransactionUtil
 */
@RunWith(PowerMockRunner.class)
public class TransactionUtilTest {

    private static Date startDayOfYear;
    private static Date endDayOfYear;

    /**
     * Sets up.
     */
    @BeforeClass
    public static void setUp() {

        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(currentYear));
        cal.set(Calendar.DAY_OF_YEAR, 1);
        startDayOfYear = cal.getTime();

        cal.set(Calendar.YEAR, Integer.valueOf(currentYear));
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        endDayOfYear = cal.getTime();
    }

    /**
     * Test reward calculator with a big transaction and 3.00 reward
     */
    @Test
    @PrepareForTest({RewardUtil.class})
    public void test_rewardAmountApplied_BigTransaction_3Reward() {

        double priceOfItemsSold = 40;
        double remainingRewardAmount = 3.00;
        double expectedResult = 3.00;

        assertRewardAmount(priceOfItemsSold, remainingRewardAmount, expectedResult);
    }

    /**
     * Test reward calculator with a zero transaction and 3.00 reward
     */
    @Test
    @PrepareForTest({RewardUtil.class})
    public void test_rewardAmountApplied_ZeroTransaction_3Reward() {

        double priceOfItemsSold = 0.00;
        double remainingRewardAmount = 3.00;
        double expectedResult = 0.00;

        assertRewardAmount(priceOfItemsSold, remainingRewardAmount, expectedResult);
    }

    /**
     * Test reward calculator with a big transaction and 50 cent reward
     */
    @Test
    @PrepareForTest({RewardUtil.class})
    public void test_rewardAmountApplied_BigTransaction_50centReward() {

        double priceOfItemsSold = 40;
        double remainingRewardAmount = 0.50;
        double expectedResult = 0.50;

        assertRewardAmount(priceOfItemsSold, remainingRewardAmount, expectedResult);
    }

    /**
     * Test reward calculator with a big transaction and 0.00 reward
     */
    @Test
    @PrepareForTest({RewardUtil.class})
    public void test_rewardAmountApplied_BigTransaction_NoReward() {

        double priceOfItemsSold = 40;
        double remainingRewardAmount = 0.00;
        double expectedResult = 0.00;

        assertRewardAmount(priceOfItemsSold, remainingRewardAmount, expectedResult);
    }

    /**
     * Test reward calculator with a 2.00 transaction and 3.00 reward
     */
    @Test
    @PrepareForTest({RewardUtil.class})
    public void test_rewardAmountApplied_Small_MaxReward() {

        double priceOfItemsSold = 2.00;
        double remainingRewardAmount = 3.00;
        double expectedResult = 2.00;

        assertRewardAmount(priceOfItemsSold, remainingRewardAmount, expectedResult);
    }

    private void assertRewardAmount(double priceOfItemsSold, double remainingRewardAmount, double
            expectedResult) {
        PowerMockito.mockStatic(RewardUtil.class);
        PowerMockito.when(RewardUtil.getRemainingRewardAmount(any(Customer.class))).thenReturn
                (remainingRewardAmount);

        assertEquals(expectedResult, TransactionUtil.calculateRewardAmount(new Customer(),
                                                                           priceOfItemsSold));
    }

    /**
     * Get sum of yearly transactions, when there are none
     */
    @Test
    public void test_getSumOfAllYearlyTransactions_NoTransactions() {

        Customer customer = new Customer();
        customer.setTransactions(new RealmList<Transaction>());

        TransactionUtil.getSumOfAllYearlyTransactions(customer);

    }

    /**
     * Get sum of yearly transactions, single $1 trans
     */
    @Test
    public void test_getSumOfAllYearlyTransactions_1TransactionProcessed() {

        Customer customer = new Customer();
        RealmList<Transaction> transactionList = new RealmList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(true);
        transaction.setPriceOfItemsSold(1.00);
        transactionList.add(transaction);
        customer.setTransactions(transactionList);

        assertEquals(1.00, TransactionUtil.getSumOfAllYearlyTransactions(customer));

    }

    /**
     * Get sum of yearly transactions, two $1 trans
     */
    @Test
    public void test_getSumOfAllYearlyTransactions_2_1TransactionProcessed() {

        Customer customer = new Customer();
        RealmList<Transaction> transactionList = new RealmList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(true);
        transaction.setPriceOfItemsSold(1.00);
        transactionList.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate(new Date());
        transaction2.setPaymentSuccessfullyProcessed(true);
        transaction2.setPriceOfItemsSold(1.00);
        transactionList.add(transaction2);

        customer.setTransactions(transactionList);

        assertEquals(2.00, TransactionUtil.getSumOfAllYearlyTransactions(customer));
    }

    /**
     * Get sum of yearly transactions, two $1 trans, begin and end date of current year
     */
    @Test
    public void test_getSumOfAllYearlyTransactions_2_1TransactionProcessed_StartAndEndOfYear() {

        Customer customer = new Customer();
        RealmList<Transaction> transactionList = new RealmList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(startDayOfYear);
        transaction.setPaymentSuccessfullyProcessed(true);
        transaction.setPriceOfItemsSold(1.00);
        transactionList.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate(endDayOfYear);
        transaction2.setPaymentSuccessfullyProcessed(true);
        transaction2.setPriceOfItemsSold(1.00);
        transactionList.add(transaction2);

        customer.setTransactions(transactionList);

        assertEquals(2.00, TransactionUtil.getSumOfAllYearlyTransactions(customer));
    }

    /**
     * Get sum of yearly transactions, two $1 trans, begin and end date of current year
     */
    @Test
    public void
    test_getSumOfAllYearlyTransactions_2_1TransactionProcessed_StartNextAndEndNextYear() {

        Customer customer = new Customer();
        RealmList<Transaction> transactionList = new RealmList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(DateUtils.addDays(startDayOfYear, -1));
        transaction.setPaymentSuccessfullyProcessed(true);
        transaction.setPriceOfItemsSold(1.00);
        transactionList.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate(DateUtils.addDays(endDayOfYear, 1));
        transaction2.setPaymentSuccessfullyProcessed(true);
        transaction2.setPriceOfItemsSold(1.00);
        transactionList.add(transaction2);

        customer.setTransactions(transactionList);

        assertEquals(0.00, TransactionUtil.getSumOfAllYearlyTransactions(customer));
    }

    /**
     * Get sum of yearly transactions, single $1 trans, not processed
     */
    @Test
    public void test_getSumOfAllYearlyTransactions_1TransactionNotProcessed() {

        Customer customer = new Customer();
        RealmList<Transaction> transactionList = new RealmList<>();
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setPriceOfItemsSold(1.00);
        transactionList.add(transaction);
        customer.setTransactions(transactionList);

        assertEquals(0.00, TransactionUtil.getSumOfAllYearlyTransactions(customer));

    }

    /**
     * Basic test to make sure this method works.
     * <p>
     * Since this method ist just making a Java API call, we don't need to do too much here.
     */
    @Test
    public void test_generateId() {
        String rewardId = TransactionUtil.generateTransactionId();
        assertNotNull(rewardId);
        assertEquals(36, rewardId.length());
    }

    /**
     * Test Vip Discount Amount, no vip discount and zero price
     */
    @Test
    @PrepareForTest({VipDiscountUtil.class})
    public void test_calculateVipDiscountAmount_notVip_zeroTotalPrice() {

        double priceOfItemsSold = 0;
        double expectedDiscount = 0;
        boolean isCustomerVipNow = false;

        runAndAssertCalculateVipDiscountAmount(priceOfItemsSold, expectedDiscount,
                                               isCustomerVipNow);
    }

    /**
     * Test Vip Discount Amount, no vip discount and zero price
     */
    @Test
    @PrepareForTest({VipDiscountUtil.class})
    public void test_calculateVipDiscountAmount_notVip_hugeTotalPrice() {

        double priceOfItemsSold = Double.MAX_VALUE;
        double expectedDiscount = 0;
        boolean isCustomerVipNow = false;

        runAndAssertCalculateVipDiscountAmount(priceOfItemsSold, expectedDiscount,
                                               isCustomerVipNow);
    }

    /**
     * Test Vip Discount Amount, vip discount and max value price
     */
    @Test
    @PrepareForTest({VipDiscountUtil.class})
    public void test_calculateVipDiscountAmount_Vip_hugeTotalPrice() {

        double priceOfItemsSold = Double.MAX_VALUE;
        double expectedDiscount = 1.7976931348623158E307;
        boolean isCustomerVipNow = true;

        runAndAssertCalculateVipDiscountAmount(priceOfItemsSold, expectedDiscount,
                                               isCustomerVipNow);
    }

    /**
     * Test Vip Discount Amount, vip discount and regular total price
     */
    @Test
    @PrepareForTest({VipDiscountUtil.class})
    public void test_calculateVipDiscountAmount_Vip_regularTotalPrice() {

        double priceOfItemsSold = 3.00;
        double expectedDiscount = 0.30;
        boolean isCustomerVipNow = true;

        runAndAssertCalculateVipDiscountAmount(priceOfItemsSold, expectedDiscount,
                                               isCustomerVipNow);
    }

    /**
     * Test Vip Discount Amount, vip discount and zero price
     */
    @Test
    @PrepareForTest({VipDiscountUtil.class})
    public void test_calculateVipDiscountAmount_Vip_zeroTotalPrice() {

        double priceOfItemsSold = 0.00;
        double expectedDiscount = 0.00;
        boolean isCustomerVipNow = true;

        runAndAssertCalculateVipDiscountAmount(priceOfItemsSold, expectedDiscount,
                                               isCustomerVipNow);
    }

    private void runAndAssertCalculateVipDiscountAmount(double priceOfItemsSold, double
            expectedDiscount, boolean isCustomerVipNow) {
        Customer customer = new Customer();
        customer.setId("ABC123");

        PowerMockito.mockStatic(VipDiscountUtil.class);
        PowerMockito.when(VipDiscountUtil.isCustomerVipNow(customer)).thenReturn(isCustomerVipNow);

        assertEquals(expectedDiscount, TransactionUtil.calculateVipDiscountAmount(customer,
                                                                                  priceOfItemsSold));
    }

    /**
     * Test calculateTotalPriceAmount with zero inputs
     */
    @Test
    public void test_calculateTotalPriceAmount_zeros() {
        double priceOfItemsSold = 0;
        double vipDiscountAmount = 0;
        double rewardAmountApplied = 0;
        double expectedResult = 0;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));
    }

    /**
     * Test calculateTotalPriceAmount with numerical inputs, version 1
     */
    @Test
    public void test_calculateTotalPriceAmount_numbers1() {
        double priceOfItemsSold = 1.00;
        double vipDiscountAmount = 0.10;
        double rewardAmountApplied = 0;
        double expectedResult = 0.90;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));
    }

    /**
     * Test calculateTotalPriceAmount with numerical inputs, version 2
     */
    @Test
    public void test_calculateTotalPriceAmount_numbers2() {
        double priceOfItemsSold = 10.00;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = 3.00;
        double expectedResult = 6.00;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));
    }

    /**
     * Test calculateTotalPriceAmount with numerical inputs, version 3
     */
    @Test
    public void test_calculateTotalPriceAmount_numbers3() {
        double priceOfItemsSold = 30.00;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = 3.00;
        double expectedResult = 26.00;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));
    }

    /**
     * Test calculateTotalPriceAmount with numerical inputs, version 4
     */
    @Test
    public void test_calculateTotalPriceAmount_numbers4() {
        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 0.00;
        double rewardAmountApplied = Double.MAX_VALUE;
        double expectedResult = 0.00;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));
    }

    /**
     * Test calculateTotalPriceAmount with numerical inputs, version 5
     */
    @Test
    public void test_calculateTotalPriceAmount_numbers5() {
        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;
        double expectedResult = 0.00;

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(priceOfItemsSold,
                                                                               vipDiscountAmount,
                                                                               rewardAmountApplied));

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        assertEquals(expectedResult, TransactionUtil.calculateTotalPriceAmount(transaction));

    }

    /**
     * Test isVipDiscountApplied, null transaction
     */
    @Test
    public void test_isVipDiscountApplied_nullTransaction() {
        assertFalse(TransactionUtil.isVipDiscountApplied(null));
    }

    /**
     * Test isVipDiscountApplied, null transaction
     */
    @Test
    public void test_isVipDiscountApplied_0Discount() {
        Transaction transaction = new Transaction();
        transaction.setVipDiscountAmount(0);
        assertFalse(TransactionUtil.isVipDiscountApplied(transaction));
    }

    /**
     * Test isVipDiscountApplied, null transaction
     */
    @Test
    public void test_isVipDiscountApplied_10Discount() {
        Transaction transaction = new Transaction();
        transaction.setVipDiscountAmount(10);
        assertTrue(TransactionUtil.isVipDiscountApplied(transaction));
    }

    /**
     * Test email service; expect 20 tries because e-mail service is persistently not working
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_sendTransactionProcessedEmail_Failures() {

        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        String emailExpected = "customer@customer.net";
        String subjectExpected = "TCCart Purchase Processed";
        String bodyExpected = "Thank you for your purchase of $0.00.  We hope to see you back " +
                "again soon.";
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(false);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);

        try {
            TransactionUtil.sendTransactionProcessedEmail(customer, transaction);
            fail("Exception was expected");
        }
        catch (EmailNotSentException e) {
            //expected, do nothing
        }

        PowerMockito.verifyStatic(times(Constants.SERVICE_ATTEMPTS));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);

    }

    /**
     * Test email service; expect exception because e-mail service works right away
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_sendTransactionProcessedEmail_SuccessOnFirst_customerabc() throws
            EmailNotSentException {

        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        String emailExpected = "customerabc@customer.net";
        String subjectExpected = "TCCart Purchase Processed";
        String bodyExpected = "Thank you for your purchase of $0.00.  We hope to see you back " +
                "again soon.";

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(true);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);

        TransactionUtil.sendTransactionProcessedEmail(customer, transaction);

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);

    }

    /**
     * Test email service; expect exception because e-mail service works right away
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_sendTransactionProcessedEmail_SuccessOnFirst_customerxyz() throws
            EmailNotSentException {

        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        String emailExpected = "customerxyz@customer.net";
        String subjectExpected = "TCCart Purchase Processed";
        String bodyExpected = "Thank you for your purchase of $0.00.  We hope to see you back " +
                "again soon.";

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(true);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);

        TransactionUtil.sendTransactionProcessedEmail(customer, transaction);

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);

    }

    /**
     * Test email service; expect exception because customer is null
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_sendTransactionProcessedEmail_nullCustomer() throws EmailNotSentException {
        TransactionUtil.sendTransactionProcessedEmail(null, new Transaction());

    }

    /**
     * Test email service; expect exception because customer is null
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_sendTransactionProcessedEmail_nullTransaction() throws EmailNotSentException {
        TransactionUtil.sendTransactionProcessedEmail(new Customer(), null);

    }

    /**
     * Check out transaction, everything works the first time
     *
     * @throws EmailNotSentException        the email not sent exception
     * @throws PaymentNotProcessedException the payment not processed exception
     */
    @Test
    @PrepareForTest({EmailService.class, PaymentService.class})
    public void test_checkOutTransaction_allWorks() throws EmailNotSentException,
            PaymentNotProcessedException {

        CreditCard creditCard = new CreditCard();
        creditCard.setExpirationDate(new Date());
        creditCard.setSecurityCode("XXX");
        creditCard.setFirstName("FIRST NAME");
        creditCard.setLastName("LAST NAME");
        creditCard.setNumber("0000111122224444");

        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        Customer customer = new Customer();

        //We've tested e-mail extensively elsewhere; here's will force success and not exercise it
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(any(String.class), any(String.class), any(String.class))).thenReturn(true);

        //We'll mock the payment service as well
        PowerMockito.mockStatic(PaymentService.class);
        PowerMockito.when(PaymentService.processPayment(eq(creditCard.getFirstName()), eq
                (creditCard.getLastName()), eq(creditCard.getNumber()), eq(creditCard.getExpirationDate()), eq(creditCard.getSecurityCode()), anyDouble())).thenReturn(true);

        TransactionUtil.checkOutTransaction(creditCard, transaction, customer);

        //Verify that the transaction is processed
        assertTrue(transaction.isPaymentSuccessfullyProcessed());

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(any(String.class), any(String.class), any(String.class));

        PowerMockito.verifyStatic(times(1));
        PaymentService.processPayment(eq(creditCard.getFirstName()), eq(creditCard.getLastName())
                , eq(creditCard.getNumber()), eq(creditCard.getExpirationDate()), eq(creditCard
                                                                                             .getSecurityCode()), anyDouble());

    }

    /**
     * Check out transaction, payment does not process, email is not sent
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({PaymentService.class})
    public void test_checkOutTransaction_PaymentDoesNotProcess() throws EmailNotSentException {

        CreditCard creditCard = new CreditCard();
        creditCard.setExpirationDate(new Date());
        creditCard.setSecurityCode("XXX");
        creditCard.setFirstName("FIRST NAME");
        creditCard.setLastName("LAST NAME");
        creditCard.setNumber("0000111122224444");

        double priceOfItemsSold = Double.MAX_VALUE;
        double vipDiscountAmount = 1.00;
        double rewardAmountApplied = Double.MAX_VALUE - 1.00;

        Transaction transaction = new Transaction();
        transaction.setPriceOfItemsSold(priceOfItemsSold);
        transaction.setRewardAmountApplied(rewardAmountApplied);
        transaction.setVipDiscountAmount(vipDiscountAmount);

        Customer customer = new Customer();

        //We'll mock the payment service as well
        PowerMockito.mockStatic(PaymentService.class);
        PowerMockito.when(PaymentService.processPayment(eq(creditCard.getFirstName()), eq
                (creditCard.getLastName()), eq(creditCard.getNumber()), eq(creditCard.getExpirationDate()), eq(creditCard.getSecurityCode()), anyDouble())).thenReturn(false);

        try {
            TransactionUtil.checkOutTransaction(creditCard, transaction, customer);
            fail("We expected an exception.");
        }
        catch (PaymentNotProcessedException e) {
            //we expect this, proceed
        }

        //Verify that the transaction is processed
        assertFalse(transaction.isPaymentSuccessfullyProcessed());

        PowerMockito.verifyStatic(times(1));
        PaymentService.processPayment(eq(creditCard.getFirstName()), eq(creditCard.getLastName())
                , eq(creditCard.getNumber()), eq(creditCard.getExpirationDate()), eq(creditCard
                                                                                             .getSecurityCode()), anyDouble());

    }

}