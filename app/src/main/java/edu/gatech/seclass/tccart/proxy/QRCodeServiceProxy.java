package edu.gatech.seclass.tccart.proxy;

import edu.gatech.seclass.services.QRCodeService;
import edu.gatech.seclass.tccart.TCCartApplication;

/**
 * Proxy class for QRCodeService that allows "mock" responses when the application is in a test
 * mode.
 * <p/>
 * Created by jeremyw on 4/3/16.
 */
public class QRCodeServiceProxy {

    /**
     * Scans a QR code and returns the corresponding hexadecimal ID String
     * <p/>
     * If "fail".equals(serviceState); always return failure ("ERR" string) If
     * "success".equals(serviceState); always return Ralph Hapschatt (ID: 7c86ffee) If serviceState
     * is anything else, call QRCodeService.scanQRCode
     *
     * @return String hexidecimalId from the QRCode
     */
    public static String scanQRCode() {

        String serviceState = TCCartApplication.serviceState;

        if ("fail".equals(serviceState)) {
            return "ERR";

        } else if ("success".equals(serviceState)) {
            return TCCartApplication.customerIdForLookup;

        }
        return QRCodeService.scanQRCode();

    }
}
