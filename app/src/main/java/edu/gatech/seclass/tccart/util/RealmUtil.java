package edu.gatech.seclass.tccart.util;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Utility class used to initialize the Realm Database
 * <p/>
 * Created by jeremyw on 3/20/16.
 */
public class RealmUtil {

    /**
     * Initialize the Realm database.  If a RealmMigrationNeededException occurs, delete the Realm
     * and start over with a clean one
     *
     * @return Realm
     */
    public static Realm buildDatabase(Context c) {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(c).build();

        try {
            return Realm.getInstance(realmConfiguration);
        }
        catch (RealmMigrationNeededException e) {
            Realm.deleteRealm(realmConfiguration);
            //Realm file has been deleted.
            Realm realm = Realm.getInstance(realmConfiguration);

            /**
             * When migration occurs, setup sample users.
             */
            LoadInitialCustomerDataUtil.loadInitialData(realm);

            return realm;
        }
    }
}


