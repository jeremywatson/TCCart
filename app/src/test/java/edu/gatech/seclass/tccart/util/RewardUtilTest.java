package edu.gatech.seclass.tccart.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Reward;
import edu.gatech.seclass.tccart.model.Transaction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

/**
 * Test RewardUtil
 */
@RunWith(PowerMockRunner.class)
public class RewardUtilTest {

    /**
     * Test to see if a reward is valid using today's date as the obtained date
     */
    @Test
    public void test_isRewardValid_todaysDate() {

        Reward reward = new Reward();
        reward.setObtainedDate(new Date());
        assertTrue(RewardUtil.isRewardActive(reward));

    }

    /**
     * TTest to see if a reward is valid using a very old date (100 yrs ago) as the obtained date
     */
    @Test
    public void test_isRewardValid_100YearsAgo() {

        Reward reward = new Reward();
        reward.setObtainedDate(DateUtils.addYears(new Date(), -100));
        assertFalse(RewardUtil.isRewardActive(reward));

    }

    /**
     * Test to see if a reward is valid using an obtained date of 1 month ago
     */
    @Test
    public void test_isRewardValid_1MonthAgo() {
        Reward reward = new Reward();
        reward.setObtainedDate(DateUtils.addMonths(new Date(), -1));
        assertTrue(RewardUtil.isRewardActive(reward));

    }

    /**
     * Test to see if a reward is valid using a null obtained date
     */
    @Test
    public void test_isRewardValid_nullObtainedDate() {

        Reward reward = new Reward();
        reward.setObtainedDate(null);
        assertFalse(RewardUtil.isRewardActive(reward));

    }

    /**
     * Test to see if a reward is valid using an obtained date of 29 days ago
     */
    @Test
    public void test_isRewardValid_29DaysAgo() {

        Reward reward = new Reward();
        reward.setObtainedDate(DateUtils.addDays(new Date(), -29));
        assertTrue(RewardUtil.isRewardActive(reward));

    }

    /**
     * Test new customer has no remaining rewards.
     */
    @Test
    public void test_newCustomerHasNoRemainingRewards() {

        Customer customer = new Customer();
        assertEquals(RewardUtil.getRemainingRewardAmount(customer), 0.0);

    }

    /**
     * Test customer with rewards has remaining rewards.
     */
    @Test
    public void test_customerWithRewardsHasRemainingRewards() {

        Customer customer = new Customer();
        Reward reward = new Reward();
        reward.setRemainingAmount(Reward.INITIAL_REWARD_AMOUNT);
        reward.setObtainedDate(new Date());
        customer.setReward(reward);
        assertEquals(RewardUtil.getRemainingRewardAmount(customer), 3.0);

    }

    /**
     * Test customer with expired reward has no remaining rewards.
     */
    @Test
    public void test_customerWithExpiredRewardHasNoRemainingRewards() {

        Customer customer = new Customer();
        Reward reward = new Reward();
        reward.setRemainingAmount(Reward.INITIAL_REWARD_AMOUNT);
        reward.setObtainedDate(DateUtils.addDays(new Date(), -60));
        customer.setReward(reward);
        assertEquals(RewardUtil.getRemainingRewardAmount(customer), 0.0);
    }

    /**
     * Test transaction under threshold does not trigger reward.
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    public void test_transactionUnderThresholdDoesNotTriggerReward() throws
            EmailNotSentException {

        Customer customer = new Customer();
        Reward reward = new Reward();
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(29.99);
        transaction.setRewardAmountApplied(0.0);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
        assertEquals(customer.getReward().getRemainingAmount(), 0.0);
    }

    /**
     * Basic test to make sure this method works.
     * <p>
     * Since this method ist just making a Java API call, we don't need to do too much here.
     */
    @Test
    public void test_generateId() {
        String rewardId = RewardUtil.generateRewardId();
        assertNotNull(rewardId);
        assertEquals(36, rewardId.length());
    }

    /**
     * Test to verify a transaction at the threshold does trigger reward
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_transactionAtThresholdDoesTriggerReward() throws EmailNotSentException {

        String emailExpected = "customer@customer.net";
        String subjectExpected = "TCCart Reward Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned a $3.00 reward " +
                "towards your next purchase. We hope to see you back again soon.";
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(true);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        Reward reward = new Reward();
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(30.00);
        transaction.setRewardAmountApplied(0.0);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
        assertEquals(customer.getReward().getRemainingAmount(), 3.0);

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);
    }

    /**
     * Test to verify a transaction at the threshold does trigger reward, but the e-mail fails
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_transactionAtThresholdDoesTriggerReward_emailFails() {

        String emailExpected = "customer@customer.net";
        String subjectExpected = "TCCart Reward Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned a $3.00 reward " +
                "towards your next purchase. We hope to see you back again soon.";
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(false);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        Reward reward = new Reward();
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(30.00);
        transaction.setRewardAmountApplied(0.0);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        try {
            RewardUtil.adjustRewardBalance(customer, transaction);
            fail("We expect an exception");
        }
        catch (EmailNotSentException e) {
            //we expect this
        }
        //reward should still be updated
        assertEquals(customer.getReward().getRemainingAmount(), 3.0);

        PowerMockito.verifyStatic(times(Constants.SERVICE_ATTEMPTS));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);
    }

    /**
     * Test to verify a transaction that's using a reward does not earn a reward if under limit
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_transactionWithRewardDoesNotEarnReward() throws EmailNotSentException {

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(any(String.class), any(String.class), any(String.class))).thenReturn(true);

        Customer customer = new Customer();
        Reward reward = new Reward();
        reward.setRemainingAmount(3.0);
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(32.00);
        transaction.setRewardAmountApplied(3.0);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
        assertEquals(customer.getReward().getRemainingAmount(), 0.0);

        //verify that Email service is not called
        PowerMockito.verifyStatic(times(0));
        EmailService.sendEMail(any(String.class), any(String.class), any(String.class));
    }

    /**
     * Test to verify a transaction that's using a reward earns reward if over limit
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_transactionWithRewardStillEarnsRewardOverLimit() throws EmailNotSentException {

        String emailExpected = "customer@customer.net";
        String subjectExpected = "TCCart Reward Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned a $3.00 reward " +
                "towards your next purchase. We hope to see you back again soon.";
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected)).thenReturn(true);


        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        Reward reward = new Reward();
        reward.setRemainingAmount(3.0);
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(33.00);
        transaction.setRewardAmountApplied(3.0);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
        assertEquals(customer.getReward().getRemainingAmount(), 3.0);

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);
    }

    /**
     * Test to verify a transaction that's using a reward earns reward if over limit
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test
    @PrepareForTest({EmailService.class})
    public void test_transactionSmallReducesRewardBalance() throws EmailNotSentException {

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(any(String.class), any(String.class), any(String.class))).thenReturn(true);

        Customer customer = new Customer();
        Reward reward = new Reward();
        reward.setRemainingAmount(3.0);
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(0.50);
        transaction.setRewardAmountApplied(0.50);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
        assertEquals(customer.getReward().getRemainingAmount(), 2.5);

        //verify that Email service is not called
        PowerMockito.verifyStatic(times(0));
        EmailService.sendEMail(any(String.class), any(String.class), any(String.class));
    }

    /**
     * Test to verify an exception if we try to add a reward with an existing non-zero reward
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test(expected = IllegalStateException.class)
    public void test_cannotAddRewardIfRewardExists() throws EmailNotSentException {

        Customer customer = new Customer();
        Reward reward = new Reward();
        reward.setRemainingAmount(3.0);
        reward.setObtainedDate(new Date());
        customer.setReward(reward);

        Transaction transaction = new Transaction();
        transaction.setId(TransactionUtil.generateTransactionId());
        transaction.setPriceOfItemsSold(33.00);
        transaction.setRewardAmountApplied(0.00);
        transaction.setTransactionDate(new Date());
        transaction.setPaymentSuccessfullyProcessed(false);
        transaction.setVipDiscountAmount(0.0);

        RewardUtil.adjustRewardBalance(customer, transaction);
    }
}