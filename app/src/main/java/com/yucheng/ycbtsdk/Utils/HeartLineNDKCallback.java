package com.yucheng.ycbtsdk.Utils;

import android.os.Message;
import android.util.Log;

import com.yucheng.ycbtsdk.Core.YCBTClientImpl;

/**
 * @author StevenLiu
 * @date 2019/12/24
 * @desc one word for this class
 */
public class HeartLineNDKCallback {

    public HeartLineNDKCallback(){

    }

    public void hrv_evt_handle(int evt_type, float params) {

//        Log.e("qob", "hrv_evt_handle " + evt_type);
        YCBTLog.e("hrv_evt_handle " + evt_type + " params " + params);
        YCBTClientImpl.getInstance().hrv_evt_handle(evt_type, params);
    }


}
