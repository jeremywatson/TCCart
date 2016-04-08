package edu.gatech.seclass.tccart.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Reward;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.model.VipDiscount;
import edu.gatech.seclass.tccart.model.VipDiscountYear;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Utility method used to load initial data into the system.
 * <p/>
 * Created by jeremyw on 3/19/16.
 */
public class LoadInitialCustomerDataUtil {

    /**
     * Check to see if test customers currently exist in Realm database
     *
     * @return true if Ralph Hapschatt, Betty Monroe, and Everett Scott found, false if not
     */
    public static boolean checkTestData(Realm realm) {

        // Customer ids to check for. Add to this list if desired
        List<String> idsToCheckIfExist = Arrays.asList("7c86ffee", "b59441af", "cd0f0e05");

        RealmQuery<Customer> query = realm.where(Customer.class);
        query.equalTo("id", idsToCheckIfExist.get(0));

        for (int i = 1; i < idsToCheckIfExist.size(); i++) {
            query.or().equalTo("id", idsToCheckIfExist.get(i));
        }

        int resultsFound = query.findAll().size();

        return resultsFound == idsToCheckIfExist.size();
    }

    /**
     * Load initial "demo" customer data
     *
     * @param realm handle to Realm database
     */
    public static void loadInitialData(Realm realm) {

        realm.beginTransaction();
        realm.deleteAll();

        /**
         * Ralph Hapschatt
         *
         * No special characteristics
         */
        Customer customer = realm.createObject(Customer.class);
        customer.setId("7c86ffee");
        customer.setFirstName("Ralph");
        customer.setLastName("Hapschatt");
        customer.setEmail("ralph.hapschatt@gmail.com");
        Reward reward = realm.createObject(Reward.class);
        reward.setId(RewardUtil.generateRewardId());
        customer.setReward(reward);

        /**
         * Betty Monroe
         *
         * Has a $3 reward
         */
        customer = realm.createObject(Customer.class);
        customer.setId("b59441af");
        customer.setFirstName("Betty");
        customer.setLastName("Monroe");
        customer.setEmail("betty.monroe@gmail.com");
        reward = realm.createObject(Reward.class);
        reward.setId(RewardUtil.generateRewardId());
        reward.setObtainedDate(new Date());
        reward.setRemainingAmount(3.00);
        customer.setReward(reward);

        /**
         * Scott Everett
         *
         * Has VIP Discount for the current year
         */
        customer = realm.createObject(Customer.class);
        customer.setId("cd0f0e05");
        customer.setFirstName("Everett");
        customer.setLastName("Scott");
        customer.setEmail("everett.scott@gmail.com");
        reward = realm.createObject(Reward.class);
        reward.setId(RewardUtil.generateRewardId());
        customer.setReward(reward);

        VipDiscountYear vipDiscountYear = realm.createObject(VipDiscountYear.class);
        vipDiscountYear.setVipYearQualifiedFor(new SimpleDateFormat("yyyy").format(new Date()));

        VipDiscount vipDiscount = realm.createObject(VipDiscount.class);
        vipDiscount.getYearsVipDiscountQualifiedFor().add(vipDiscountYear);
        customer.setVipDiscount(vipDiscount);

        // Transactions added to Scott Everett for transaction history
        Transaction transaction = realm.createObject(Transaction.class);
        transaction.setId("c9f1448d-0f57-4785-b4f3-2b90d5b8ded6");
        transaction.setTransactionDate(new Date());
        transaction.setPriceOfItemsSold(5);
        transaction.setRewardAmountApplied(0);
        transaction.setVipDiscountAmount(0);
        customer.getTransactions().add(transaction);

        transaction = realm.createObject(Transaction.class);
        transaction.setId("ab485721-0f57-4785-b4f3-ffffffffffff");
        transaction.setTransactionDate(new Date());
        transaction.setPriceOfItemsSold(30);
        transaction.setRewardAmountApplied(0);
        transaction.setVipDiscountAmount(0);
        customer.getTransactions().add(transaction);

        transaction = realm.createObject(Transaction.class);
        transaction.setId("ab485721-0f57-4785-b4f3-abc3019401fc");
        transaction.setTransactionDate(new Date());
        transaction.setPriceOfItemsSold(10.50);
        transaction.setRewardAmountApplied(2);
        transaction.setVipDiscountAmount(2);
        customer.getTransactions().add(transaction);

        transaction = realm.createObject(Transaction.class);
        transaction.setId("ab485721-0f57-4785-b4f3-cde203aa2345");
        transaction.setTransactionDate(new Date());
        transaction.setPriceOfItemsSold(8.50);
        transaction.setRewardAmountApplied(4);
        transaction.setVipDiscountAmount(0);
        customer.getTransactions().add(transaction);

        realm.commitTransaction();
    }

}
