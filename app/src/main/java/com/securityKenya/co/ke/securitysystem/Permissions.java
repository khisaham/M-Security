package com.securityKenya.co.ke.securitysystem;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 *
 */
public class Permissions {
    public static final int REQUEST_CODE_FINE_LOCATION = 231;
    public static final int REQUEST_CODE_READ_PHONE_STATE = 232;
    public static final int REQUEST_CODE_CALL_PHONE = 233;
    public static final int REQUEST_CODE_VIBRATE = 234;

    public static boolean check(Context context, String permission) {
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static void request(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
}