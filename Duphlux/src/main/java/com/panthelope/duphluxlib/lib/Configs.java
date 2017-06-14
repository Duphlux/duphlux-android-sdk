package com.panthelope.duphluxlib.lib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by ikenna iloeje on 08/06/2017.
 */

public class Configs {
    public static String WEBSERVICE_URL = "";
    public static int ACTIVITY_RESULT_CODE = 1386;
    public static String meta_app_token_key = "com.panthelope.duphlux.app.token";

    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // if we can’t find it in the manifest, just return null
        }

        return null;
    }
}
