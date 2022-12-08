package com.yucheng.ycbtsdk.Utils;

import android.util.Log;

import com.yucheng.ycbtsdk.YCBTClient;

public class YCBTLog {

    private static final String LOG_TAG = "yc-ble";

    public static void e(String msg) {

//        if (YCBTClient.OpenLogSwitch){
//            LogToFileUtils.write(msg);
//        }
        Log.e(LOG_TAG, msg);
    }

}
