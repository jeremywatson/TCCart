package edu.gatech.seclass.tccart.util;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.tccart.Constants;
import edu.gatech.seclass.tccart.exception.EmailNotSentException;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.model.VipDiscountYear;
import io.realm.RealmList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

/**
 * Test VipDiscountUtil
 */
@RunWith(PowerMockRunner.class)
public class VipDiscountUtilTest {

    /**
     * Test is customer vip now current year.
     */
    @Test
    public void test_isCustomerVipNow_CurrentYear() {

        VipDiscountYear vipDiscountYear = new VipDiscountYear();
        vipDiscountYear.setVipYearQualifiedFor(new SimpleDateFormat("YYYY").format(new Date()));
        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();
        vipDiscountYears.add(vipDiscountYear);

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertTrue(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Test is customer vip now year 1700.
     */
    @Test
    public void test_isCustomerVipNow_Year1700() {

        VipDiscountYear vipDiscountYear = new VipDiscountYear();
        vipDiscountYear.setVipYearQualifiedFor("1700");
        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();
        vipDiscountYears.add(vipDiscountYear);

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertFalse(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Test is customer vip now year 3000.
     */
    @Test
    public void test_isCustomerVipNow_Year3000() {

        VipDiscountYear vipDiscountYear = new VipDiscountYear();
        vipDiscountYear.setVipYearQualifiedFor("3000");
        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();
        vipDiscountYears.add(vipDiscountYear);

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertFalse(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Test is customer vip now year 3000 and 1700.
     */
    @Test
    public void test_isCustomerVipNow_Year3000and1700() {

        VipDiscountYear vipDiscountYear = new VipDiscountYear();
        vipDiscountYear.setVipYearQualifiedFor("3000");

        VipDiscountYear vipDiscountYear1 = new VipDiscountYear();
        vipDiscountYear1.setVipYearQualifiedFor("1700");

        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();
        vipDiscountYears.add(vipDiscountYear);
        vipDiscountYears.add(vipDiscountYear1);

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertFalse(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Test is customer vip now no years.
     */
    @Test
    public void test_isCustomerVipNow_NoYears() {

        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertFalse(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Test is customer vip now empty customer.
     */
    @Test
    public void test_isCustomerVipNow_EmptyCustomer() {

        Customer customer = new Customer();
        assertFalse(VipDiscountUtil.isCustomerVipNow(customer));
    }

    /**
     * Verify that a null customer returns IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_isCustomerNewlyQualifiedForVip_nullCustomer() {
        VipDiscountUtil.isCustomerNewlyQualifiedForVip(null);
    }

    /**
     * Verify that if a customer has null VIP stuff and does not qualify for VIP that the method
     * does not throw an exception, but returns false
     */
    @Test
    public void test_isCustomerNewlyQualifiedForVip_customerNullVipInfo_AndNotQualified() {

        VipDiscount vipDiscount = new VipDiscount();

        Customer customer = new Customer();
        assertFalse(VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer));

        customer.setVipDiscount(vipDiscount);
        assertFalse(VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer));

    }

    /**
     * Verify that if a customer is already a VIP for next year, that checking is not done, as it is
     * not needed.
     */
    @Test
    public void test_isCustomerNewlyQualifiedForVip_customerAlreadyVipForNextYear() {

        VipDiscountYear vipDiscountYear = new VipDiscountYear();
        vipDiscountYear.setVipYearQualifiedFor(new SimpleDateFormat("YYYY").format(DateUtils
                                                                                           .addYears(new Date(), 1)));
        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();
        vipDiscountYears.add(vipDiscountYear);

        VipDiscount vipDiscount = new VipDiscount();
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        Customer customer = new Customer();
        customer.setVipDiscount(vipDiscount);

        assertFalse(VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer));
    }

    /**
     * Test that a customer that is not a VIP and did not qualify for transactions does not get
     * marked as a VIP
     */
    @Test
    @PrepareForTest({TransactionUtil.class})
    public void test_isCustomerNewlyQualifiedForVip_customerNotVipForNextYearAndDidNotObtain() {

        PowerMockito.mockStatic(TransactionUtil.class);
        PowerMockito.when(TransactionUtil.getSumOfAllYearlyTransactions(any(Customer.class)))
                .thenReturn(VipDiscount.VIP_THRESHOLD - 0.01);

        Customer customer = new Customer();

        VipDiscount vipDiscount = new VipDiscount();
        RealmList<VipDiscountYear> vipDiscountYears = new RealmList<>();

        customer.setVipDiscount(vipDiscount);
        vipDiscount.setYearsVipDiscountQualifiedFor(vipDiscountYears);

        assertFalse(VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer));
    }

    /**
     * Test that a customer that is not a VIP that has over the needed limit in transactions does
     * get marked as a VIP
     */
    @Test
    @PrepareForTest({TransactionUtil.class})
    public void test_isCustomerNewlyQualifiedForVip_customerNotVipForNextYearAndDidObtain() {

        PowerMockito.mockStatic(TransactionUtil.class);
        PowerMockito.when(TransactionUtil.getSumOfAllYearlyTransactions(any(Customer.class)))
                .thenReturn(VipDiscount.VIP_THRESHOLD);

        Customer customer = new Customer();

        assertTrue(VipDiscountUtil.isCustomerNewlyQualifiedForVip(customer));

    }

    /**
     * Test email service and expect it to fail
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @PrepareForTest({EmailService.class})
    @Test(expected = EmailNotSentException.class)
    public void test_sendVipStatusAchievedEmail_Failures() throws EmailNotSentException {

        String emailExpected = "customer@customer.net";
        String subjectExpected = "TCCart VIP Status Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned VIP status for 1776" +
                "." + " We hope to see you back again soon.";
        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(false);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        String year = "1776";

        VipDiscountUtil.sendVipStatusAchievedEmail(customer, year);

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
    public void test_sendVipStatusAchievedEmail_SuccessOnFirst_customerabc() throws
            EmailNotSentException {

        String emailExpected = "customerabc@customer.net";
        String subjectExpected = "TCCart VIP Status Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned VIP status for 1881" +
                "." + " We hope to see you back again soon.";

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(true);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        String year = "1881";

        VipDiscountUtil.sendVipStatusAchievedEmail(customer, year);

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
    public void test_sendVipStatusAchievedEmail_SuccessOnFirst_customerxyz() throws EmailNotSentException {

        String emailExpected = "customerxyz@customer.net";
        String subjectExpected = "TCCart VIP Status Earned";
        String bodyExpected = "Thank you for your purchase.  You have earned VIP status for 1882" +
                "." + " We hope to see you back again soon.";

        PowerMockito.mockStatic(EmailService.class);
        PowerMockito.when(EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected))
                .thenReturn(true);

        Customer customer = new Customer();
        customer.setEmail(emailExpected);
        String year = "1882";

        VipDiscountUtil.sendVipStatusAchievedEmail(customer, year);

        PowerMockito.verifyStatic(times(1));
        EmailService.sendEMail(emailExpected, subjectExpected, bodyExpected);

    }

    /**
     * Test email service; expect exception because customer is null
     *
     * @throws EmailNotSentException the email not sent exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_sendVipStatusAchievedEmail_nullCustomer() throws EmailNotSentException {
        VipDiscountUtil.sendVipStatusAchievedEmail(null, null);

    }

}