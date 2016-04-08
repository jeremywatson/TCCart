package edu.gatech.seclass.tccart.model;

import io.realm.RealmObject;

/**
 * Class that represents a year that a VIP discount could have been applied.
 * <p/>
 * Created by jeremyw on 3/20/16.
 */
public class VipDiscountYear extends RealmObject {

    /**
     * 4-digit year, in String format
     */
    private String vipYearQualifiedFor;

    /**
     * Gets vip year qualified for.
     *
     * @return the vip year qualified for
     */
    public String getVipYearQualifiedFor() {
        return vipYearQualifiedFor;
    }

    /**
     * Sets vip year qualified for.
     *
     * @param vipYearQualifiedFor the vip year qualified for
     */
    public void setVipYearQualifiedFor(String vipYearQualifiedFor) {
        this.vipYearQualifiedFor = vipYearQualifiedFor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VipDiscountYear that = (VipDiscountYear) o;

        return vipYearQualifiedFor != null ? vipYearQualifiedFor.equals(that.vipYearQualifiedFor)
                : that.vipYearQualifiedFor == null;

    }

    @Override
    public int hashCode() {
        return vipYearQualifiedFor != null ? vipYearQualifiedFor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VipDiscountYear{" +
                "vipYearQualifiedFor='" + vipYearQualifiedFor + '\'' +
                '}';
    }
}
