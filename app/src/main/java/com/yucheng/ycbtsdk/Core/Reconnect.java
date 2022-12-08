package com.yucheng.ycbtsdk.Core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;

import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.Gatt.BleHelper;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Utils.SPUtil;
import com.yucheng.ycbtsdk.Utils.YCBTLog;

public class Reconnect implements BleConnectResponse {

    private static Reconnect gReconnect;
    private Context mContext;

    public static Reconnect getHelper() {

        if (gReconnect == null) {
            synchronized (Reconnect.class) {
                if (gReconnect == null) {
                    gReconnect = new Reconnect();
                }
            }
        }
        return gReconnect;
    }

    public void init(Context context){
        mContext = context;
        mContext.registerReceiver(mReceiver, makeFilter());

        String tSaveMac = SPUtil.getBindedDeviceMac();
        if (tSaveMac.length() > 0){
  //          BleHelper.getHelper().connectGatt(tSaveMac);
        }
    }

    public void startCheck(){

        Intent intent = new Intent("reconnect_dev_action");

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent mmCurrentAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        if (Build.VERSION.SDK_INT < 19) {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, mmCurrentAlarmIntent);
        } else {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, mmCurrentAlarmIntent);
        }
    }

    /**
     * 连接成功后，暂停重连
     */
    public void stopCheck(){

    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter.addAction("reconnect_dev_action");
        return filter;
    }

    //蓝牙状态监听
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context con, Intent intent) {

            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            YCBTLog.e("蓝牙正在开启");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            YCBTLog.e( "蓝牙已开启，开始连接");

                            String tSaveMac = SPUtil.getBindedDeviceMac();
                            if (tSaveMac.length() > 0){
 //                               BleHelper.getHelper().connectGatt(tSaveMac);
                            }

                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            YCBTLog.e("蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            YCBTLog.e( "蓝牙已关闭");
                            stopCheck();
                            break;
                    }
                    break;
                case "reconnect_dev_action":{
                    YCBTLog.e("Receiver reconnect_dev_action");
                    String tSaveMac = SPUtil.getBindedDeviceMac();
                    if (tSaveMac.length() > 0){
//                        BleHelper.getHelper().connectGatt(tSaveMac);
                    }

                    break;
                }
            }
        }
    };


    @Override
    public void onConnectResponse(int code) {
        YCBTLog.e("Reconnect onConnectResponse " + code);
        if (code == Constants.BLEState.Connected){
            stopCheck();
        }
        else if (code == Constants.BLEState.Disconnect){
            startCheck();
        }
        else if (code == Constants.BLEState.ReadWriteOK){

        }
    }
}
