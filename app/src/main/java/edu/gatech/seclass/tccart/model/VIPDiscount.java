package edu.gatech.seclass.tccart.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * VipDiscount that customer can earn once they reach a certain threshold of purchases for the
 * year.
 * <p/>
 * Created by jeremyw on 3/15/16.
 */
public class VipDiscount extends RealmObject {

    /**
     * Discount percent off once VIP discount amount is reached
     */
    public static final double PERCENT_OFF = 0.1;

    /**
     * Total amount of purchases each year required to qualify for the VIP discount
     */
    public static final double VIP_THRESHOLD = 300;

    /**
     * List of years that the customer has qualified as a VIP
     */
    private RealmList<VipDiscountYear> yearsVipDiscountQualifiedFor;

    /**
     * Gets years vip discount qualified for.
     *
     * @return the years vip discount qualified for
     */
    public RealmList<VipDiscountYear> getYearsVipDiscountQualifiedFor() {
        return yearsVipDiscountQualifiedFor;
    }

    /**
     * Sets years vip discount qualified for.
     *
     * @param yearsVipDiscountQualifiedFor the years vip discount qualified for
     */
    public void setYearsVipDiscountQualifiedFor(RealmList<VipDiscountYear>
                                                        yearsVipDiscountQualifiedFor) {
        this.yearsVipDiscountQualifiedFor = yearsVipDiscountQualifiedFor;
    }

}
