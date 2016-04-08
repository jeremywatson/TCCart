package edu.gatech.seclass.tccart.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.gatech.seclass.services.PrintingService;
import edu.gatech.seclass.tccart.model.Customer;

import static edu.gatech.seclass.tccart.util.CustomerUtil.isEmailValid;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Test RewardUtil
 */
@RunWith(PowerMockRunner.class)
public class CustomerUtilTest {

    /**
     * Test email validation(isEmailValid) method in CustomerUtil will return correct result for
     * valid email address
     */
    @Test
    public void test_isEmailValid_validEmail() {
        Customer customer = new Customer();
        customer.setEmail("validEmail@gmail.com");
        assertTrue(isEmailValid(customer.getEmail()));

    }

    /**
     * Test email validation(isEmailValid) method in CustomerUtil will return correct result of invalid/false
     * for blank email address
     */
    @Test
    public void test_isEmailValid_blankEmail() {
        Customer customer = new Customer();
        customer.setEmail("");
        assertFalse(isEmailValid(customer.getEmail()));

    }

    /**
     * Test email validation(isEmailValid) method in CustomerUtil will return correct result
     * for invalid email address
     */
    @Test
    public void test_isEmailValid_invalidEmail() {
        Customer customer = new Customer();
        customer.setEmail("invalidEmailgmail.com");
        assertFalse(isEmailValid(customer.getEmail()));
    }

    /**
     * Test print card service method (with mocks to avoid actual printing), using null customer
     * attributes, with a failure
     */
    @Test
    @PrepareForTest({PrintingService.class})
    public void test_printCard_nullCustomerAttributes() {
        Customer customer = new Customer();
        mockStatic(PrintingService.class);
        PowerMockito.when(PrintingService.printCard(customer.getFirstName(), customer.getLastName
                (), customer.getId())).thenReturn(false);

        assertFalse(CustomerUtil.printCard(customer));

        PowerMockito.verifyStatic(times(1));
        PrintingService.printCard(customer.getFirstName(), customer.getLastName(), customer.getId
                ());

    }

    /**
     * Test print card service method (with mocks to avoid actual printing), using actual customer
     * attributes, with a failure
     */
    @Test
    @PrepareForTest({PrintingService.class})
    public void test_printCard_actualCustomerAttributes_Failure() {
        Customer customer = new Customer();
        customer.setFirstName("Firstname");
        customer.setLastName("Lastname");
        customer.setId("ABC");
        mockStatic(PrintingService.class);
        PowerMockito.when(PrintingService.printCard(customer.getFirstName(), customer.getLastName
                (), customer.getId())).thenReturn(false);

        assertFalse(CustomerUtil.printCard(customer));

        PowerMockito.verifyStatic(times(1));
        PrintingService.printCard(customer.getFirstName(), customer.getLastName(), customer.getId
                ());

    }

    /**
     * Test print card service method (with mocks to avoid actual printing), using actual customer
     * attributes, with success
     */
    @Test
    @PrepareForTest({PrintingService.class})
    public void test_printCard_actualCustomerAttributes_Success() {
        Customer customer = new Customer();
        customer.setFirstName("Firstname");
        customer.setLastName("Lastname");
        customer.setId("ABC");
        mockStatic(PrintingService.class);
        PowerMockito.when(PrintingService.printCard(customer.getFirstName(), customer.getLastName
                (), customer.getId())).thenReturn(true);

        assertTrue(CustomerUtil.printCard(customer));

        PowerMockito.verifyStatic(times(1));
        PrintingService.printCard(customer.getFirstName(), customer.getLastName(), customer.getId
                ());

    }

    /**
     * Test customer id generator; verify it produces id's of specified length and type (hex)
     */
    @Test
    public void test_generateID() {
        String generatedId = CustomerUtil.generateID();
        assertEquals(Customer.CUSTOMER_ID_LENGTH, generatedId.length());
        //verify this does not throw an exception.  If it does, the test will fail, proving this
        // is not hex
        assertTrue(Long.parseLong(generatedId, 16) > -2);
    }

}