package com.securityKenya.co.ke.securitysystem;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Device {
    private static final String TAG = "SECURITY.Device";
    public static String getSimCardSN(Context context) {
        if(Permissions.check(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String serial  = telephonyManager.getSimSerialNumber();
            return  serial;
        }
        Log.w(TAG, "User hasn't granted application access to phone state, cannot determine sim card serial number");
        return null;
    }

    public static String getPhoneNumber(Context context) {
        if(Permissions.check(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        }
        return null;
    }

    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

    public static String generateTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());

        return timestamp;
    }
}
