package com.yucheng.ycbtsdk.Core;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.Bean.YCStopEvent;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.Gatt.BleHelper;
import com.yucheng.ycbtsdk.Gatt.GattBleResponse;
import com.yucheng.ycbtsdk.Protocol.BleState;
import com.yucheng.ycbtsdk.Protocol.CMD;
import com.yucheng.ycbtsdk.Protocol.DataUnpack;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.Response.BleDeviceToAppDataResponse;
import com.yucheng.ycbtsdk.Response.BleRealDataResponse;
import com.yucheng.ycbtsdk.Response.BleScanResponse;
import com.yucheng.ycbtsdk.Utils.ByteUtil;
import com.yucheng.ycbtsdk.Utils.SPUtil;
import com.yucheng.ycbtsdk.Utils.TimeUtil;
import com.yucheng.ycbtsdk.Utils.YCBTLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class YCBTClientImpl implements GattBleResponse {
    private volatile static YCBTClientImpl sInstance;
    private int mBleStateCode = Constants.BLEState.Disconnect;
    private ArrayList<YCSendBean> mSendQueue;
    private Boolean mQueueSendState;  //0空闲 1正在发送
    private BleDataResponse sendingDataResponse;
    private BleScanResponse mBleScanResponse;
    private BleConnectResponse mBleConnectResponse;
    private BleRealDataResponse mBleRealDataResponse;
    private BleDeviceToAppDataResponse mBleDeviceToAppResponse;
    private boolean isGattWriteCallBackFinish = true;
    private boolean isRecvRealEcging; //正在接收实时ECG
    private ArrayList<BleConnectResponse> mBleStatelistens;
    private ArrayList mBlockArray;
    private int mBlockFrame = 0; //一个Block里幀数, 用于错误数据回退使用
    private HashMap scheduleInfo = new HashMap();
    private List<HashMap> scheduleInfos = new ArrayList<>();

    private Handler mTimeOutHander;
    private Runnable mTimerOutRunnable = new Runnable() {
        @Override
        public void run() {
            YCBTLog.e("TimeOut");
            stopScanBle();
        }
    };

    private int mEndTimeOutCount;
    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBleStateCode == BleState.Ble_CharacteristicNotification) {
                ++mEndTimeOutCount;
                YCBTLog.e("同步时间超时,重发 " + mEndTimeOutCount);
                if (mEndTimeOutCount > 3) {
                    mTimeOutHander.removeCallbacks(mTimeRunnable);
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(1, 0, null);
                            mEndTimeOutCount = 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    disconnectBle();
                    //popQueue();
                } else {
                    mTimeOutHander.removeCallbacks(mTimeRunnable);
                    mTimeOutHander.postDelayed(mTimeRunnable, 1500);
                    frontQueue();
                }
            } else {
                ++mEndTimeOutCount;
                YCBTLog.e("重发 " + mEndTimeOutCount);
                if (mEndTimeOutCount > 3) {
                    mTimeOutHander.removeCallbacks(mTimeRunnable);
                    popQueue();
                    mEndTimeOutCount = 0;
                } else {
                    mTimeOutHander.removeCallbacks(mTimeRunnable);
                    mTimeOutHander.postDelayed(mTimeRunnable, 1500);
                    frontQueue();
                }
            }
        }
    };

    private int mEndEcgTimeOutCount = 0;

    private Runnable mEndEcgTestOut = new Runnable() {
        @Override
        public void run() {

            ++mEndEcgTimeOutCount;

            YCBTLog.e("实时ECG结束超时,重发 " + mEndEcgTimeOutCount);

            if (mEndEcgTimeOutCount > 3) {
                isRecvRealEcging = false;

                mTimeOutHander.removeCallbacks(mEndEcgTestOut);

                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(1, 0, null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
            } else {

                if (mSendQueue.size() > 0) {
                    YCSendBean tTopBean = mSendQueue.get(0);
                    byte[] tDataBytes = {(byte) 0x00};
                    tTopBean.resetGroup(Constants.DATATYPE.AppBloodSwitch, tDataBytes);
                    frontQueue();

                    mTimeOutHander.removeCallbacks(mEndEcgTestOut);
                    mTimeOutHander.postDelayed(mEndEcgTestOut, 1500);
                }

            }
        }
    };

    public static YCBTClientImpl getInstance() {

        if (sInstance == null) {
            synchronized (YCBTClientImpl.class) {
                if (sInstance == null) {
                    sInstance = new YCBTClientImpl();
                }
            }
        }

        return sInstance;
    }

    public void init(Context context) {
        BleHelper.getHelper().initContext(context);
        BleHelper.getHelper().registerGattResponse(this);
        SPUtil.init(context);
        mSendQueue = new ArrayList<>();
        mQueueSendState = false;
        mBlockArray = new ArrayList();
        mBleStatelistens = new ArrayList();
        mTimeOutHander = new Handler();
        Reconnect.getHelper().init(context);
        registerBleStateChangeCallBack(Reconnect.getHelper());
    }

    public void registerBleStateChangeCallBack(BleConnectResponse connectResponseCallback) {
        mBleStatelistens.add(connectResponseCallback);
    }

    public void unregisterBleStateChangeCallBack(BleConnectResponse connectResponseCallback) {
        if (mBleStatelistens.contains(connectResponseCallback)) {
            mBleStatelistens.remove(connectResponseCallback);
        }
    }

    public void registerRealDataCallBack(BleRealDataResponse realDataResponse) {
        mBleRealDataResponse = realDataResponse;
    }

    public void registerRealTypeCallBack(BleDeviceToAppDataResponse realTypeResponse) {
        mBleDeviceToAppResponse = realTypeResponse;
    }

    public void startScanBle(BleScanResponse scanResponse, int timeoutSec) {
        mBleScanResponse = scanResponse;
        mTimeOutHander.removeCallbacks(mTimerOutRunnable);
        mTimeOutHander.postDelayed(mTimerOutRunnable, timeoutSec * 1000); // 搜索, 5s超时
        BleHelper.getHelper().startScan();
    }

    public void stopScanBle() {
        if (mTimeOutHander != null)
            mTimeOutHander.removeCallbacks(mTimerOutRunnable);
        BleHelper.getHelper().stopScan();
        if (mBleScanResponse != null)
            mBleScanResponse.onScanResponse(Constants.CODE.Code_TimeOut, null);
    }

    public void connectBle(String mac, BleConnectResponse connectResponse) {

        mBleConnectResponse = connectResponse;
        BleHelper.getHelper().connectGatt(mac);
    }

    public void reconnectBle(BleConnectResponse connectResponse) {
        mBleConnectResponse = connectResponse;

        String tSaveMac = SPUtil.getBindedDeviceMac();
        if (tSaveMac.length() > 0) {
            BleHelper.getHelper().connectGatt(tSaveMac);
        }
    }

    public void disconnectBle() {
        BleHelper.getHelper().disconnectGatt();
    }

    public int connectState() {
        return mBleStateCode;
    }


    public void sendData2Device(int data_type, byte[] cmd_data) {

        int tDataLen = cmd_data.length + 6;
        byte[] willData = new byte[tDataLen];
        int tOffset = 0;
        willData[tOffset++] = (byte) ((data_type >> 8) & 0xff);
        willData[tOffset++] = (byte) (data_type & 0xff);
        willData[tOffset++] = (byte) (tDataLen & 0xff);
        willData[tOffset++] = (byte) ((tDataLen >> 8) & 0xff);
        System.arraycopy(cmd_data, 0, willData, tOffset, tDataLen - 6);
        tOffset += (tDataLen - 6);
        int tCrc = ByteUtil.crc16_compute(willData, tDataLen - 2);
        willData[tOffset++] = (byte) (tCrc & 0xff);
        willData[tOffset++] = (byte) ((tCrc >> 8) & 0xff);

        isGattWriteCallBackFinish = false;
        BleHelper.getHelper().gattWriteData(willData);
    }

    //发送单一指令到设备
    public void sendSingleData2Device(int data_type, byte[] cmd_data, int cmd_priority, BleDataResponse dataResponse) {

        YCSendBean sendBean = new YCSendBean(cmd_data, cmd_priority, dataResponse);
        sendBean.dataType = data_type;
        sendBean.groupType = CMD.Group.Group_Single;

        pushQueue(sendBean);
    }

    public void sendDataType2Device(int data_type, int group_type, byte[] cmd_data, int cmd_priority, BleDataResponse dataResponse) {


        YCSendBean sendBean = new YCSendBean(cmd_data, cmd_priority, dataResponse);
        sendBean.groupType = group_type;
        sendBean.dataType = data_type;

        pushQueue(sendBean);
    }

    //发送多指令到设备
    public void sendGroupData2Device(int group_type, byte[] cmd_data, int cmd_priority, BleDataResponse dataResponse) {

        YCSendBean sendBean = new YCSendBean(cmd_data, cmd_priority, dataResponse);
        sendBean.groupType = group_type;

        pushQueue(sendBean);
    }


    public void otaUIUpdate(byte[] uiData, BleDataResponse dataResponse) {

        YCBTClientImpl.getInstance().sendGroupData2Device(CMD.Group.Group_OTAUI, uiData, CMD.Priority_normal, dataResponse);
    }

    //队列操作

    private void pushQueue(YCSendBean sendBean) {


        synchronized (this) {

            //===================如果发送关闭指令，把开始指令移除===================

            Log.e("TimeSetActivity2","isRecvRealEcging="+isRecvRealEcging);
            //如果队列里面有关闭指令，同时有开启Ecg指令，需要先将ecg指令移除
//            if (sendBean.groupType == CMD.Group.Group_EndEcgTest && isRecvRealEcging == false) {
            if (sendBean.groupType == CMD.Group.Group_EndEcgTest) {

                boolean findEcg = false;

                for(YCSendBean bean : mSendQueue){
                    if(bean.groupType == CMD.Group.Group_StartEcgTest){
                        mSendQueue.remove(bean);
                        findEcg = true;
                    }
                }
                if(findEcg){
                    return;
                }
            }
            //==========================================


            mSendQueue.add(sendBean);

//            Log.e("TimeSetActivity2","add queue="+mSendQueue);
            YCBTLog.e("pushQueue 队列剩余大小 " + mSendQueue.size() + " " + mSendQueue + " 实时测试 " + isRecvRealEcging + " mQueueSendState " + mQueueSendState);

            if (sendBean.groupType == CMD.Group.Group_EndEcgTest) {
                YCBTLog.e("pushQueue CMD.Group.Group_EndEcgTest");
                YCBTLog.e("pushQueue 排序前 " + mSendQueue);
                Collections.sort(mSendQueue);
                YCBTLog.e("pushQueue 排序后 " + mSendQueue);
                frontQueue();

                mEndEcgTimeOutCount = 0;

                mTimeOutHander.removeCallbacks(mEndEcgTestOut);
                mTimeOutHander.postDelayed(mEndEcgTestOut, 1500);
            } else {
                if (mQueueSendState == false && isRecvRealEcging == false) {

                    YCBTLog.e("pushQueue 排序后 ....end...");

                    //取出第一个开始元素，开始发送
                    frontQueue();
                }
            }
        }
    }

    private void popQueue() {

        synchronized (this) {
            if (mSendQueue.size() > 0) {
                YCBTLog.e("popQueue Gatt写回调 " + isGattWriteCallBackFinish);
                if (isGattWriteCallBackFinish) { //gatt write 回调OK,可出队列，执行后面的.

//                    Log.e("TimeSetActivity2","出队列="+mSendQueue.get(0).toString());

                    mSendQueue.remove(0);
                    mQueueSendState = false;

                    YCBTLog.e("popQueue 排序前 " + mSendQueue);
                    Collections.sort(mSendQueue);
                    YCBTLog.e("popQueue 排序后 " + mSendQueue);

                    YCBTLog.e("popQueue 队列剩余大小 " + mSendQueue.size() + " " + mSendQueue + " 实时测试 " + isRecvRealEcging + " mQueueSendState " + mQueueSendState);
                    if (isRecvRealEcging == false) {
                        frontQueue();
                    }
                } else {
                    YCBTLog.e("popQueue else ");
                    YCSendBean tTopBean = mSendQueue.get(0);
                    Log.e("TimeSetActivity2","popQueue else="+tTopBean.toString()+";isGattWriteCallBackFinish="+isGattWriteCallBackFinish);
                    tTopBean.dataSendFinish = true;
                }
            }
        }
    }

    private void frontQueue() {
        synchronized (this) {
            YCBTLog.e("frontQueue " + isGattWriteCallBackFinish);
            if (mSendQueue.size() > 0) {
                YCSendBean tTopBean = mSendQueue.get(0);
                sendingDataResponse = tTopBean.mDataResponse;
                if (isGattWriteCallBackFinish == true) {
                    byte[] tWillSendFrame = tTopBean.willSendFrame();
                    if (tWillSendFrame != null) {
                        mQueueSendState = true;
                        if (tTopBean.groupType == CMD.Group.Group_Single) {
                            mTimeOutHander.removeCallbacks(mTimeRunnable);
                            mTimeOutHander.postDelayed(mTimeRunnable, 1500);
                        }
                        sendData2Device(tTopBean.dataType, tWillSendFrame);
                    } else if (tTopBean.dataType == Constants.DATATYPE.OtaUI_SyncBlock) {
                        //发送UI Block检验信息
                        byte[] tCurSendUIData = tTopBean.willData;
                        int tBlockLen = tCurSendUIData.length;
                        YCBTLog.e("frontQueue UI升级Block长度: " + tBlockLen);
                        byte[] blockCheckCmd = new byte[8];
                        byte[] tIntByte = ByteUtil.fromInt(tBlockLen);
                        System.arraycopy(tIntByte, 0, blockCheckCmd, 0, 4);
                        int blockFrameNum = 0;
                        if (tBlockLen % 176 == 0) {
                            blockFrameNum = tBlockLen / 176;
                        } else {
                            blockFrameNum = tBlockLen / 176 + 1;
                        }

                        tIntByte = ByteUtil.fromInt(blockFrameNum);
                        System.arraycopy(tIntByte, 0, blockCheckCmd, 4, 2);

                        int blockCrc16 = ByteUtil.crc16_compute(tCurSendUIData, tBlockLen);
                        tIntByte = ByteUtil.fromInt(blockCrc16);
                        System.arraycopy(tIntByte, 0, blockCheckCmd, 6, 2);

                        tTopBean.resetGroup(Constants.DATATYPE.OtaUI_SyncBlockCheck, blockCheckCmd);
                        frontQueue();
                    } else {
                        //出现卡顿，加在这里，调试
//                        popQueue();
//                        resetQueue();
                        YCBTLog.e("frontQueue tWillSendFrame == null");
                    }
                } else {
                    YCBTLog.e("frontQueue isGattWriteCallBackFinish == 0");
                }
            }
        }
    }

    private void resetQueue() {

        mQueueSendState = false;
        isGattWriteCallBackFinish = true;
        mSendQueue.clear();
    }

    private boolean isNeedStopCollect() {

        boolean isNeed = false;
        for (int i = 1; i < mSendQueue.size(); ++i) {
            YCSendBean tSendB = mSendQueue.get(i);
            if (tSendB.sendPriority > CMD.Priority_low) {
                isNeed = true;
                break;
            }
        }

        YCBTLog.e("是否需要停止当前 " + isNeed);

        return isNeed;
    }

    // CMD类型 Key处理
    private void packetSettingHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        int code = 0;
        HashMap resultMap = null;
        if (dataBytes != null) {
            if (dataBytes.length > 0)
                code = dataBytes[0];
            if (dataBytes.length > 1) {
                resultMap = new HashMap();
                resultMap.put("data", dataBytes[1]);
            }
        }
        switch (keyType) {
            case CMD.KEY_Setting.TemperatureAlarm://温度报警
            case CMD.KEY_Setting.TemperatureMonitor://温度监测
            case CMD.KEY_Setting.ScreenTime://息屏时间设置
            case CMD.KEY_Setting.AmbientLight://环境光检测设置
            case CMD.KEY_Setting.WorkingMode://工作模式切换设置
            case CMD.KEY_Setting.AccidentMode://意外监测模式设置
            case CMD.KEY_Setting.BraceletStatusAlert://手环状态提醒设置
            case CMD.KEY_Setting.BloodOxygenModeMonitor://血氧监测模式设置
            case CMD.KEY_Setting.ScheduleModification://日程修改设置
            case CMD.KEY_Setting.AmbientTemperatureAndHumidity://环境温湿度检测模式设置
            case CMD.KEY_Setting.ScheduleSwitch://日程开关设置
            case CMD.KEY_Setting.StepCountingStateTime://计步状态时间设置
            case CMD.KEY_Setting.UploadReminder://上传提醒设置
            case CMD.KEY_Setting.BluetoothBroadcastInterval://设置蓝牙广播间隔
            case CMD.KEY_Setting.BluetoothTransmittingPower://设置蓝牙发射功率
            case CMD.KEY_Setting.ExerciseHeartRateZone://运动心率区间设置
            case CMD.KEY_Setting.EventReminder://事件提醒设置
            case CMD.KEY_Setting.EventReminderSwitch://事件提醒开关控制
            case CMD.KEY_Setting.Notification:
            case CMD.KEY_Setting.PPGCollect:
            case CMD.KEY_Setting.SleepRemind:
            case CMD.KEY_Setting.MainTheme:
            case CMD.KEY_Setting.BloodRange:
            case CMD.KEY_Setting.Skin:
            case CMD.KEY_Setting.DisplayBrightness:
            case CMD.KEY_Setting.RaiseScreen:
            case CMD.KEY_Setting.Language:
            case CMD.KEY_Setting.NotDisturb:
            case CMD.KEY_Setting.RestoreFactory:
            case CMD.KEY_Setting.FindPhone:
            case CMD.KEY_Setting.HeartMonitor:
            case CMD.KEY_Setting.HeartAlarm:
            case CMD.KEY_Setting.HandWear:
            case CMD.KEY_Setting.AntiLose:
            case CMD.KEY_Setting.LongSite:
            case CMD.KEY_Setting.Unit:
            case CMD.KEY_Setting.Goal:
            case CMD.KEY_Setting.UserInfo: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(code, 0, resultMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Setting.Time: {
                mTimeOutHander.removeCallbacks(mTimeRunnable);
                bleStateResponse(BleState.Ble_ReadOk);
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(code, 0, resultMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Setting.Alarm: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(code, 0, DataUnpack.unpackAlarmData(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            default:
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(code, 0, resultMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
        }
    }

    private void packetGetHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {

        switch (keyType) {
            case CMD.KEY_Get.DeviceInfo: {
                if (sendingDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackDeviceInfoData(dataBytes);
                    try {
                        sendingDataResponse.onDataResponse(0, 0, tRetMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Get.UserConfig: {
                if (sendingDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackDeviceUserConfigData(dataBytes);
                    try {
                        sendingDataResponse.onDataResponse(0, 0, tRetMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Get.DeviceLog: {
                if (dataLen > 1) {
                    int tTag = (dataBytes[0] & 0xff);
                    if (tTag == 1) {
                        int tLogNum = (dataBytes[1] & 0xff) + ((dataBytes[2] & 0xff) << 8);
                        YCBTLog.e("设备日志条数 " + tLogNum);
                        mBlockArray.clear();
                    } else if (tTag == 0) {
                        YCBTLog.e("不支持设备日志功能");
                        if (sendingDataResponse != null) {
                            try {
                                HashMap tRetMap = new HashMap();
                                tRetMap.put("code", Constants.CODE.Code_OK);
                                tRetMap.put("dataType", Constants.DATATYPE.GetDeviceLog);
                                tRetMap.put("functionVersion", 97);
                                sendingDataResponse.onDataResponse(0, 0, tRetMap);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        popQueue();
                    } else if (tTag == 2 || tTag == 0xff) {
                        byte[] aLogByte = new byte[dataLen - 1];
                        System.arraycopy(dataBytes, 1, aLogByte, 0, dataLen - 1);
                        String tLogStr = new String(aLogByte);
                        YCBTLog.e("日志内空: " + tLogStr);
                        mBlockArray.add(tLogStr);
                        if (tTag == 0xff) {
                            HashMap tRetMap = new HashMap();
                            tRetMap.put("code", Constants.CODE.Code_OK);
                            tRetMap.put("dataType", Constants.DATATYPE.GetDeviceLog);
                            tRetMap.put("data", mBlockArray);
                            tRetMap.put("functionVersion", 97);
                            if (sendingDataResponse != null) {
                                try {
                                    sendingDataResponse.onDataResponse(0, 0, tRetMap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            popQueue();
                        }
                    }
                } else { //不支持设备日志功能
                    YCBTLog.e("不支持设备日志功能");
                    if (sendingDataResponse != null) {
                        try {
                            HashMap tRetMap = new HashMap();
                            tRetMap.put("code", Constants.CODE.Code_OK);
                            tRetMap.put("dataType", Constants.DATATYPE.GetDeviceLog);
                            tRetMap.put("functionVersion", 97);
                            sendingDataResponse.onDataResponse(0, 0, tRetMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
            }
            case CMD.KEY_Get.MainTheme: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackHomeTheme(dataBytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Get.ElectrodeLocation: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackEcgLocation(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Get.DeviceScreenInfo:
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackDeviceScreenInfo(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.NowStep: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetNowSport(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Get.HistoryOutline:
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetHistoryOutline(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.RealTemp:
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetRealTemp(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.ScreenInfo://获取屏幕显示信息
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetScreenInfo(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.HeavenEarthAndFiveElement://获取天地五行的数据
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetHeavenEarthAndFiveElement(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.RealBloodOxygen://获取设备实时血氧
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetRealBloodOxygen(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.CurrentAmbientLightIntensity://获取当前环境光强度
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetCurrentAmbientLightIntensity(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.CurrentAmbientTempAndHumidity://获取当前环境温湿度
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetCurrentAmbientTempAndHumidity(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.ScheduleInfo://获取查询日程信息
                if (dataBytes.length == 2) {//获取成功
                    scheduleInfos.clear();
                    scheduleInfo.clear();
                    scheduleInfo.put("totalScheduleInfoValue", (dataBytes[0] & 0xff) + ((dataBytes[1] & 0xff) << 8));//日程总条数
                    scheduleInfo.put("dataType", Constants.DATATYPE.GetScheduleInfo);
                } else if (dataBytes.length == 1) {//结束  scheduleInfos.size() == 12
                    if (sendingDataResponse != null) {
                        try {
                            scheduleInfo.put("data", scheduleInfos);//添加事件集合
                            sendingDataResponse.onDataResponse(0, 0, scheduleInfo);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                } else {
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(1, 0, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
            case CMD.KEY_Get.SensorSamplingInfo://获取传感器采样信息
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetSensorSamplingInfo(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.CurrentSystemWorkingMode://获取当前系统工作模式
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetCurrentSystemWorkingMode(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.InsuranceRelatedInfo://获取保险相关信息
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetInsuranceRelatedInfo(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.UploadConfigurationInfoOfReminder://获取上传提醒的配置信息
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetUploadConfigurationInfoOfReminder(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.StatusOfManualMode://获取手动模式的状态
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetStatusOfManualMode(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.ChipScheme://获取当前手环芯片方案
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetChipScheme(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.DeviceRemindInfo://获取手环提醒设置信息
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackGetDeviceRemindInfo(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_Get.EventReminderInfo://获取当前手环事件提醒信息
                if (dataBytes.length == 2) {//获取成功
                    scheduleInfos.clear();
                    scheduleInfo.clear();
                    scheduleInfo.put("totalEventReminderInfoValue", (dataBytes[0] & 0xff) + ((dataBytes[1] & 0xff) << 8));//日程总条数
                    scheduleInfo.put("dataType", Constants.DATATYPE.GetEventReminderInfo);
                } else if (dataBytes.length == 1) {//结束  scheduleInfos.size() == 12
                    if (sendingDataResponse != null) {
                        try {
                            scheduleInfo.put("data", scheduleInfos);//添加事件集合
                            sendingDataResponse.onDataResponse(0, 0, scheduleInfo);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                } else {
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(1, 0, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
        }
    }

    private void packetAppControlHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        int type = -1;
        switch (keyType) {
            case CMD.KEY_AppControl.SportMode: {
                YCSendBean tTopBean = mSendQueue.get(0);
                if (tTopBean.groupType == CMD.Group.Group_StartSport) { //打开心率测试开关
                    tTopBean.resetGroup(Constants.DATATYPE.AppControlReal, new byte[]{0x01, 0x01, 0x00});
                    frontQueue();
                } else if (tTopBean.groupType == CMD.Group.Group_EndSport) {
                    tTopBean.resetGroup(Constants.DATATYPE.AppControlReal, new byte[]{0x00, 0x01, 0x00});
                    frontQueue();
                } else if (tTopBean.groupType == CMD.Group.Group_REAL_SPORT) {
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
            }
            case CMD.KEY_AppControl.BloodTest: {
                //调试临时添加
                if(mSendQueue.size()<= 0){
                    return;
                }

                YCSendBean tTopBean = mSendQueue.get(0);
                if (tTopBean.groupType == CMD.Group.Group_StartEcgTest) {
                    //打开ECG波形上传
                    tTopBean.resetGroup(Constants.DATATYPE.AppControlWave, new byte[]{0x01, 0x01});
                    frontQueue();
                } else if (tTopBean.groupType == CMD.Group.Group_EndEcgTest) {
                    isRecvRealEcging = false;
                    mTimeOutHander.removeCallbacks(mEndEcgTestOut);
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
            }
            case CMD.KEY_AppControl.BloodCheck://血压校准 0x0303
                type = Constants.DATATYPE.AppBloodCalibration;
                break;
            case CMD.KEY_AppControl.RealData: {
                YCSendBean tTopBean = mSendQueue.get(0);
                if (tTopBean.groupType == CMD.Group.Group_StartSport || tTopBean.groupType == CMD.Group.Group_EndSport) {
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    popQueue();
                } else if (tTopBean.groupType == CMD.Group.Group_StartEcgTest) {
                    //发送PPG波形
                    tTopBean.groupSize = 2;
                    tTopBean.resetGroup(Constants.DATATYPE.AppControlWave, new byte[]{0x01, 0x00});
                    frontQueue();
                }
                break;
            }
            case CMD.KEY_AppControl.WaveUpload: {
                YCSendBean tTopBean = mSendQueue.get(0);
                if (tTopBean.groupType == CMD.Group.Group_StartEcgTest) {
                    if (tTopBean.groupSize == 2) {
                        //发送完成,出队列并返回数据
                        if (sendingDataResponse != null) {
                            try {
                                sendingDataResponse.onDataResponse(0, 0, null);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        popQueue();
                    } else {
                        //打开血压上报开关
                        tTopBean.resetGroup(Constants.DATATYPE.AppControlReal, new byte[]{0x01, 0x03, 0x02});
                        frontQueue();
                        isRecvRealEcging = true;
                    }
                }
                break;
            }
            case CMD.KEY_AppControl.EcgRealStatus:
                HashMap tResultMap = DataUnpack.unpackAppEcgPpgStatus(dataBytes);
                int EcgStatus = (int) tResultMap.get("EcgStatus");
                int PPGStatus = (int) tResultMap.get("PPGStatus");
                if (EcgStatus > 0 || PPGStatus > 0) {  //退出了实时测试

                    isRecvRealEcging = false;
                    frontQueue();

                }
                if (mBleRealDataResponse != null) {
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.AppECGPPGStatus, tResultMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (mBleDeviceToAppResponse != null) {
                    try {
                        mBleDeviceToAppResponse.onDataResponse(0, tResultMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CMD.KEY_AppControl.FindDevice://寻找手环 0x0300
                type = Constants.DATATYPE.AppFindDevice;
                break;
            case CMD.KEY_AppControl.HeartTest://心率测试开关控制 0x0301
                type = Constants.DATATYPE.AppHeartSwitch;
                break;
            case CMD.KEY_AppControl.NotificationPush://消息提醒控制 0x0308
                type = Constants.DATATYPE.AppMessageControl;
                break;
            case CMD.KEY_AppControl.TodayWeather://今日天气预报 0x0312
                type = Constants.DATATYPE.AppTodayWeather;
                break;
            case CMD.KEY_AppControl.TomorrowWeather://明日天气预报 0x0313
                type = Constants.DATATYPE.AppTomorrowWeather;
                break;
            case CMD.KEY_AppControl.HealthArg://健康参数、预警信息发送 0x0315
                type = Constants.DATATYPE.AppHealthArg;
                break;
            case CMD.KEY_AppControl.ShutDown://关机、进入运输模式控制 0x0316
                type = Constants.DATATYPE.AppShutDown;
                break;
            case CMD.KEY_AppControl.TemperatureCorrect://温度校准  0x0317
                type = Constants.DATATYPE.AppTemperatureCorrect;
                break;
            case CMD.KEY_AppControl.TempMeasurementControl://温度测量控制 0x0318
                type = Constants.DATATYPE.AppTemperatureMeasure;
                break;
            case CMD.KEY_AppControl.EmoticonIndex://表情包显示 0x0319
                type = Constants.DATATYPE.AppEmoticonIndex;
                break;
            case CMD.KEY_AppControl.HealthWriteBack://健康值回写到手环 0x031A
                type = Constants.DATATYPE.AppHealthWriteBack;
                break;
            case CMD.KEY_AppControl.SleepWriteBack://睡眠数据回写到手环 0x031B
                type = Constants.DATATYPE.AppSleepWriteBack;
                break;
            case CMD.KEY_AppControl.UserInfoWriteBack://用户个人信息回写到手环 0x031C
                type = Constants.DATATYPE.AppUserInfoWriteBack;
                break;
            case CMD.KEY_AppControl.UpgradeReminder://升级提醒 0x031D
                type = Constants.DATATYPE.AppUpgradeReminder;
                break;
            case CMD.KEY_AppControl.AmbientLightMeasurementControl://环境光测量控制 0x031E
                type = Constants.DATATYPE.AppAmbientLightMeasurementControl;
                break;
            case CMD.KEY_AppControl.AmbientTempHumidityMeasurementControl://环境温湿度测量控制 0x0320
                type = Constants.DATATYPE.AppAmbientTempHumidityMeasurementControl;
                break;
            case CMD.KEY_AppControl.InsuranceNews://保险消息推送
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackInsuranceNews(dataBytes));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                popQueue();
                break;
            case CMD.KEY_AppControl.SensorSwitchControl://传感器数据存储开关控制 0x0322
                type = Constants.DATATYPE.AppSensorSwitchControl;
                break;
            case CMD.KEY_AppControl.MobileModel://当前手机型号推送 0x0323
                type = Constants.DATATYPE.AppMobileModel;
                break;
            case CMD.KEY_AppControl.EffectiveStep://有效步数同步 0x0324
                type = Constants.DATATYPE.AppEffectiveStep;
                break;
            case CMD.KEY_AppControl.EffectiveHeart://计算心率同步
                type = Constants.DATATYPE.AppEffectiveHeart;
                break;
            case CMD.KEY_AppControl.EarlyWarning://app预警推送
                type = Constants.DATATYPE.AppEarlyWarning;
                break;
            case CMD.KEY_AppControl.PushMessage://app预警推送
                type = Constants.DATATYPE.AppPushMessage;
                break;
            case CMD.KEY_AppControl.OpenOrCloseTesting://app预警推送
                type = Constants.DATATYPE.AppOpenOrCloseTesting;
                break;
        }
        if (type != -1) {
            if (sendingDataResponse != null) {
                try {
                    sendingDataResponse.onDataResponse(0, 0, DataUnpack.unpackParseData(dataBytes, type));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            popQueue();
        }
    }

    private void packetDevControlHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        int type = -1;
        switch (keyType) {
            case CMD.KEY_DeviceControl.FindMobile://寻找手机
                type = Constants.DATATYPE.DeviceFindMobile;
                break;
            case CMD.KEY_DeviceControl.LostReminder://防丢提醒
                type = Constants.DATATYPE.DeviceLostReminder;
                break;
            case CMD.KEY_DeviceControl.AnswerAndClosePhone://接听/拒接电话
                type = Constants.DATATYPE.DeviceAnswerAndClosePhone;
                break;
            case CMD.KEY_DeviceControl.TakePhoto://相机拍照控制
                type = Constants.DATATYPE.DeviceTakePhoto;
                break;
            case CMD.KEY_DeviceControl.StartMusic://音乐控制
                type = Constants.DATATYPE.DeviceStartMusic;
                break;
            case CMD.KEY_DeviceControl.Sos://一键呼救控制命令
                type = Constants.DATATYPE.DeviceSos;
                break;
            case CMD.KEY_DeviceControl.DrinkingPatterns://饮酒模式控制命令
                type = Constants.DATATYPE.DeviceDrinkingPatterns;
                break;
            case CMD.KEY_DeviceControl.ConnectOrDisconnect://手环蓝牙  连接/拒连
                type = Constants.DATATYPE.DeviceConnectOrDisconnect;
                break;
            case CMD.KEY_DeviceControl.SportMode:
                type = Constants.DATATYPE.DeviceSportMode;
                break;
        }
        if (type != -1) {
            if (mBleDeviceToAppResponse != null) {
                try {
                    //先发再回调
                    sendData2Device(type, new byte[]{0x00});
                    mBleDeviceToAppResponse.onDataResponse(0, DataUnpack.unpackParseData(dataBytes, type));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    sendData2Device(type, new byte[]{0x01});
                }
            }
            popQueue();
        }
    }

    private void packetHealthHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        int tOffset = 0;
        switch (keyType) {
            case CMD.KEY_Health.HistorySportMode://同步历史运动模式数据
            case CMD.KEY_Health.HistoryHealthMonitoring://同步历史健康监测数据
            case CMD.KEY_Health.HistoryAmbientLight://同步历史的环境光数据
            case CMD.KEY_Health.HistoryTemp://同步历史的体温数据
            case CMD.KEY_Health.HistoryTempAndHumidity://同步历史的温湿度数据
            case CMD.KEY_Health.HistoryBloodOxygen://同步历史的血氧数据
            case CMD.KEY_Health.HistoryFall:
            case CMD.KEY_Health.HistoryAll:
            case CMD.KEY_Health.HistoryBlood:
            case CMD.KEY_Health.HistoryHeart:
            case CMD.KEY_Health.HistorySleep:
            case CMD.KEY_Health.HistorySport: {
                if (dataLen > 2) {
                    int tHistoryNum = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8); //历史条数
                    int tHistoryTotalBlock = 0; //所有数据总封包数
                    int tHistoryTotalByte = 0; //所有数据总字节数
                    if (tHistoryNum > 0) {
                        tHistoryTotalBlock = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16) + ((dataBytes[tOffset++] & 0xff) << 24);
                        tHistoryTotalByte = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16) + ((dataBytes[tOffset++] & 0xff) << 24);
                    }
                    YCBTLog.e("历史条数 " + tHistoryNum + " 总包数: " + tHistoryTotalBlock + " 总字节数据 " + tHistoryTotalByte);
                } else {
                    //返回结果,出队列
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    popQueue();
                }
                mBlockArray.clear();
                mBlockArray.add(keyType);
                break;
            }
            case CMD.KEY_Health.HistorySportModeAck://发送完一个block 历史运动模式数据
            case CMD.KEY_Health.HistoryHealthMonitoringAck://发送完一个block 历史健康监测数据
            case CMD.KEY_Health.HistoryAmbientLightAck://发送完一个block 同步历史的环境光数据
            case CMD.KEY_Health.HistoryTempAck://发送完一个block 历史的体温数据
            case CMD.KEY_Health.HistoryTempAndHumidityAck://发送完一个block 历史的温湿度数据
            case CMD.KEY_Health.HistoryBloodOxygenAck://发送完一个block 历史的血氧数据
            case CMD.KEY_Health.HistoryFallAck:
            case CMD.KEY_Health.HistoryAllAck:
            case CMD.KEY_Health.HistoryBloodAck:
            case CMD.KEY_Health.HistoryHeartAck:
            case CMD.KEY_Health.HistorySleepAck:
            case CMD.KEY_Health.HistorySportAck: {
                mBlockArray.add(dataBytes);
                break;
            }
            case CMD.KEY_Health.HistoryBlock: {
                int tHistoryNum = 0; //包数
                int tHistoryTotalCrc = 0; //CRC16 校验码
                int tHistoryTotalByte = 0; //字节数
                tHistoryNum = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
                tHistoryTotalByte = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
                tHistoryTotalCrc = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);

                byte[] allBlockBytes = new byte[tHistoryTotalByte];
                int tLen = 0;
                int tHealthType = (int) mBlockArray.get(0);
                for (int i = 1; i < mBlockArray.size(); ++i) {
                    byte[] tABlock = (byte[]) mBlockArray.get(i);
                    System.arraycopy(tABlock, 0, allBlockBytes, tLen, tABlock.length);
                    tLen += tABlock.length;
                }

                int tCrc16 = ByteUtil.crc16_compute(allBlockBytes, tLen);
                YCBTLog.e("历史条数 " + tHistoryNum + " 字节数据: " + tHistoryTotalByte + " 校验码 " + tHistoryTotalCrc + " 计算出的Crc16 " + tCrc16 + " 接收到的长度 " + tLen);
                if (tCrc16 == tHistoryTotalCrc) {
                    sendData2Device(Constants.DATATYPE.Health_HistoryBlock, new byte[]{0x00});
                    //处理数据
                    HashMap tRetMap = DataUnpack.unpackHealthData(allBlockBytes, tHealthType);
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, tRetMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    sendData2Device(Constants.DATATYPE.Health_HistoryBlock, new byte[]{0x04});
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_Health.DeleteSportMode:
            case CMD.KEY_Health.DeleteHealthMonitoring:
            case CMD.KEY_Health.DeleteFall:
            case CMD.KEY_Health.DeleteAmbientLight:
            case CMD.KEY_Health.DeleteTemp:
            case CMD.KEY_Health.DeleteTempAndHumidity:
            case CMD.KEY_Health.DeleteBloodOxygen:
            case CMD.KEY_Health.DeleteAll:
            case CMD.KEY_Health.DeleteBlood:
            case CMD.KEY_Health.DeleteHeart:
            case CMD.KEY_Health.DeleteSleep:
            case CMD.KEY_Health.DeleteSport: {
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
        }
    }

    private void packetRealHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {

        switch (keyType) {
            case CMD.KEY_Real.UploadEventReminder:
                if (dataBytes.length >= 6) {
                    if (sendingDataResponse != null) {
                        HashMap hashMap = DataUnpack.unpackGetEventReminder(dataBytes);
                        scheduleInfos.add(hashMap);
                    }
                }
                break;
            case CMD.KEY_Real.UploadSchedule:
                if (dataBytes.length >= 9) {
                    if (sendingDataResponse != null) {
                        HashMap hashMap = DataUnpack.unpackGetScheduleInfo(dataBytes);
                        scheduleInfos.add(hashMap);
                    }
                }
                break;
            case CMD.KEY_Real.UploadECG: {
                if (mBleRealDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackRealECGData(dataBytes);
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadECG, tRetMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
            case CMD.KEY_Real.UploadPPG: {
                if (mBleRealDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackRealPPGData(dataBytes);
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadPPG, tRetMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
            case CMD.KEY_Real.UploadSport: {
                if (mBleRealDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackRealSportData(dataBytes);
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadSport, tRetMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
            case CMD.KEY_Real.UploadHeart: {
                if (mBleRealDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackRealHeartData(dataBytes);
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadHeart, tRetMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
            case CMD.KEY_Real.UploadBlood: {
                if (mBleRealDataResponse != null) {
                    HashMap tRetMap = DataUnpack.unpackRealBloodData(dataBytes);
                    try {
                        mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadBlood, tRetMap);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    private void packetCollectHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        int tOffset = 0;
        switch (keyType) {
            case CMD.KEY_Collect.QueryNum: {
                int tHistoryNum = 0; //包数
                int tHistoryType = 0;
                tHistoryType = (dataBytes[tOffset++] & 0xff);
                tHistoryNum = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
                YCBTLog.e("类型 " + tHistoryType + " 数目 " + tHistoryNum);
                if (tHistoryNum > 0) {
                    mBlockArray.clear();
                    YCSendBean tTopEle = mSendQueue.get(0);
                    tTopEle.groupSize = tHistoryNum;
                    tTopEle.resetGroup(Constants.DATATYPE.Collect_GetWithIndex, new byte[]{(byte) tHistoryType, 0x00, 0x00, 0x00});
                    frontQueue();
                } else {
                    //返回数据
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(0, 0, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    popQueue();
                }
                break;
            }
            case CMD.KEY_Collect.GetWithIndex: {
                HashMap tResult = DataUnpack.unpackCollectSummaryInfo(dataBytes);
                int tSN = (int) tResult.get("collectSN");
                int tCollectType = (int) tResult.get("collectType");
                int tCollectTotalLen = (int) tResult.get("collectTotalLen");
                YCSendBean tTopEle = mSendQueue.get(0);
                tTopEle.collectDigits=(int)tResult.get("collectDigits");
                if (tTopEle.groupType == CMD.Group.Group_ECGList || tTopEle.groupType == CMD.Group.Group_PPGList) {
                    if (tCollectTotalLen > 0) {
                        mBlockArray.add(tResult);
                    }
                    Boolean isNeedStopHistory = isNeedStopCollect();
                    if (tSN < tTopEle.groupSize - 1 && isNeedStopHistory == false) {  //停止历史概要可提前退出
                        ++tSN;
                        tTopEle.resetGroup(Constants.DATATYPE.Collect_GetWithIndex, new byte[]{(byte) tCollectType, (byte) (tSN & 0xff), (byte) (tSN >> 8 & 0xff), 0x00});
                        frontQueue();
                    } else {
                        YCBTLog.e("类型 " + tCollectType + " 概要信息同步完成");
                        if (sendingDataResponse != null) {
                            HashMap tRetMap = new HashMap();
                            tRetMap.put("code", Constants.CODE.Code_OK);
                            tRetMap.put("dataType", Constants.DATATYPE.Collect_QueryNum);
                            tRetMap.put("collectType", tCollectType);
                            tRetMap.put("data", mBlockArray);
                            try {
                                sendingDataResponse.onDataResponse(0, 0, tRetMap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        popQueue();
                    }
                } else {
                    mBlockArray.clear();
                    tTopEle.groupSize = tCollectTotalLen;   //把总长度，赋值到groupSize备用
                }
                break;
            }
            case CMD.KEY_Collect.GetWithTimestamp: {
                HashMap tResult = DataUnpack.unpackCollectSummaryInfo(dataBytes);
                int tCollectTotalLen = (int) tResult.get("collectTotalLen");
                YCBTLog.e("GetWithTimestamp " + tResult);
                mBlockFrame = 0;
                if (tCollectTotalLen == 0) { //出队列,数据为0，直接返回
                    if (sendingDataResponse != null) {
                        try {
                            sendingDataResponse.onDataResponse(1, 0, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    popQueue();
                } else {
                    YCSendBean tTopEle = mSendQueue.get(0);
                    tTopEle.collectDigits=(int)tResult.get("collectDigits");
                    tTopEle.groupSize = tCollectTotalLen;   //把总长度，赋值到groupSize备用
                }
                mBlockArray.clear();
                break;
            }
            case CMD.KEY_Collect.SyncData: {
                int tPacketIndex = (dataBytes[1] & 0xff);
                byte[] dataValue = new byte[dataLen - 2];
                System.arraycopy(dataBytes, 2, dataValue, 0, dataLen - 2);
                mBlockArray.add(dataValue);
                ++mBlockFrame;
                Log.e("ycclient","包序号" + tPacketIndex + " Blcok帧数 " + mBlockFrame);
                break;
            }
            case CMD.KEY_Collect.CheckData: {

                if (dataLen < 3) { //设备侧回复 停止历史数据成功

                    //为了调试卡顿，下面临时注销
//                    YCBTLog.e("设备侧回复 停止ECGPPG历史数据");
//                    mQueueSendState = false;
//                    YCSendBean tSendB = mSendQueue.get(0);
//                    tSendB.collectStopReset();
//                    //队列排序,优先级高的先发
//                    YCBTLog.e("stop 排序前 " + mSendQueue);
//                    Collections.sort(mSendQueue);
//                    YCBTLog.e("stop 排序后 " + mSendQueue);
//                    frontQueue();


                    //============调试卡顿，替换为下面一个方法
                    YCBTLog.e("设备侧回复 停止ECGPPG历史数据");
                    EventBus.getDefault().post(new YCStopEvent());

                    popQueue();


                } else {
                    int tCollectType = (dataBytes[tOffset++] & 0xff);
                    int tSendedBlockNum = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
                    int tSendedByteNum = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16) + ((dataBytes[tOffset++] & 0xff) << 24);
                    int tSendedCrc16 = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
                    YCBTLog.e("类型 " + tCollectType + " 已上传字节数 " + tSendedByteNum + " CRC16 " + tSendedCrc16);
                    byte[] allBlockBytes = new byte[tSendedByteNum];
                    int tLen = 0;
                    int tAckCode = 0;
                    try {
                        for (int i = 0; i < mBlockArray.size(); ++i) {
                            byte[] tABlock = (byte[]) mBlockArray.get(i);
                            System.arraycopy(tABlock, 0, allBlockBytes, tLen, tABlock.length);
                            tLen += tABlock.length;
                        }
                    } catch (Exception ex) {
                        YCBTLog.e("Exception " + ex.toString());
                        tAckCode = 2;
                    }
                    int tCrc16 = ByteUtil.crc16_compute(allBlockBytes, tSendedByteNum);
                    if (tCrc16 == tSendedCrc16) {
                        tAckCode = 0;
                    } else {
                        tAckCode = 2;
                    }
                    Boolean isNeedStopHistory = isNeedStopCollect();
                    if (isNeedStopHistory) {
                        tAckCode = 4;
                    }
                    YCBTLog.e("计算出CRC16 " + tCrc16 + " tAckCode " + tAckCode);
                    sendData2Device(Constants.DATATYPE.Collect_SyncCheck, new byte[]{0x00, (byte) tAckCode});
                    YCSendBean tSendB = mSendQueue.get(0);

                    if (tAckCode == 0 && tSendB.groupSize == allBlockBytes.length) {
                        HashMap tRetMap = new HashMap();
                        tRetMap.put("code", Constants.CODE.Code_OK);
                        tRetMap.put("collectType", tCollectType);
                        tRetMap.put("dataType", Constants.DATATYPE.Collect_GetWithTimestamp);
                        if (tCollectType == 0 || tSendB.collectDigits == 24) {
                            tRetMap.put("data", allBlockBytes);
                        } else {
                            int tValueNum = allBlockBytes.length / 2;
                            byte[] tPPGBytes = new byte[tValueNum * 3];
                            int tIndex = 0;
                            while (tIndex < tValueNum) {
                                tPPGBytes[tIndex * 3] = allBlockBytes[tIndex * 2];
                                tPPGBytes[tIndex * 3 + 1] = allBlockBytes[tIndex * 2 + 1];
                                if((tPPGBytes[tIndex * 3 + 1] & 0x80) == 0){
                                    tPPGBytes[tIndex * 3 + 2] = 0x00;
                                }else{
                                    tPPGBytes[tIndex * 3 + 2] = (byte) 0xff;
                                }
                                tIndex += 1;
                            }
                            tRetMap.put("data", tPPGBytes);
                        }
                        if (sendingDataResponse != null) {
                            try {
                                sendingDataResponse.onDataResponse(0, 0, tRetMap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        popQueue();
                    } else if (tAckCode > 0) {
                        YCBTLog.e("移除本Block错误的数据前 " + mBlockArray.size());
                        for (int b = 0; b < mBlockFrame; ++b) {
                            mBlockArray.remove(mBlockArray.size() - 1);
                        }
                        YCBTLog.e("移除本Block错误的数据后 " + mBlockArray.size());
                    }
                    mBlockFrame = 0;
                }

                break;


//                    if (tAckCode == 0 && tSendB.groupSize == allBlockBytes.length) {
//                        HashMap tRetMap = new HashMap();
//                        tRetMap.put("code", Constants.CODE.Code_OK);
//                        tRetMap.put("collectType", tCollectType);
//                        tRetMap.put("dataType", Constants.DATATYPE.Collect_GetWithTimestamp);
//                        if (tCollectType == 0) {
//                            tRetMap.put("data", allBlockBytes);
//                        } else {
//                            int tValueNum = allBlockBytes.length / 2;
//                            byte[] tPPGBytes = new byte[tValueNum * 3];
//                            int tIndex = 0;
//                            while (tIndex < tValueNum) {
//                                tPPGBytes[tIndex * 3] = allBlockBytes[tIndex * 2];
//                                tPPGBytes[tIndex * 3 + 1] = allBlockBytes[tIndex * 2 + 1];
//                                if ((tPPGBytes[tIndex * 3 + 1] & 0x80) == 0) {
//                                    tPPGBytes[tIndex * 3 + 2] = 0x00;
//                                } else {
//                                    tPPGBytes[tIndex * 3 + 2] = (byte) 0xff;
//                                }
//                                tIndex += 1;
//                            }
//                            tRetMap.put("data", tPPGBytes);
//                        }
////20201031替换
////                        else {
////                            int tValueNum = allBlockBytes.length / 2;
////                            byte[] tPPGBytes = new byte[tValueNum * 3];
////                            int tIndex = 0;
////                            while (tIndex < tValueNum) {
////                                tPPGBytes[tIndex * 3] = allBlockBytes[tIndex * 2];
////                                tPPGBytes[tIndex * 3 + 1] = allBlockBytes[tIndex * 2 + 1];
////                                tPPGBytes[tIndex * 3 + 2] = 0x00;
////                                tIndex += 1;
////                            }
////                            tRetMap.put("data", tPPGBytes);
////                        }
//
//
//                        if (sendingDataResponse != null) {
//                            try {
//                                sendingDataResponse.onDataResponse(0, 0, tRetMap);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        popQueue();
//                    } else if (tAckCode > 0) {
//                        YCBTLog.e("移除本Block错误的数据前 " + mBlockArray.size());
//                        for (int b = 0; b < mBlockFrame; ++b) {
//                            mBlockArray.remove(mBlockArray.size() - 1);
//                        }
//                        YCBTLog.e("移除本Block错误的数据后 " + mBlockArray.size());
//                    }
//                    mBlockFrame = 0;
//                }
//
//                break;
            }
            case CMD.KEY_Collect.DeleteWithTimestamp:
            case CMD.KEY_Collect.DeleteWithIndex: {
                YCBTLog.e("删除了 ECG数据接收完成");
                if (sendingDataResponse != null) {
                    try {
                        sendingDataResponse.onDataResponse(0, 0, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }

        }
    }

    private void packetOtaUIHandle(int keyType, int dataLen, byte[] dataBytes, int crcValue) {
        switch (keyType) {
            case CMD.KEY_UI.UI_GetFileBreak: {
                if (sendingDataResponse != null) {
                    try {
                        HashMap tRetMap = DataUnpack.unpackUIFileBreakInfo(dataBytes);
                        sendingDataResponse.onDataResponse(0, 0, tRetMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_UI.UI_SyncFileInfo: {
                if (sendingDataResponse != null) {
                    try {
                        int isOk = dataBytes[0];
                        HashMap tRetMap = new HashMap();
                        tRetMap.put("code", Constants.CODE.Code_OK);
                        tRetMap.put("dataType", Constants.DATATYPE.OtaUI_SyncFileInfo);
                        tRetMap.put("data", isOk);
                        sendingDataResponse.onDataResponse(0, 0, tRetMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
            case CMD.KEY_UI.UI_SyncBlockCheck: {
                if (sendingDataResponse != null) {
                    try {
                        int isOk = dataBytes[0];
                        HashMap tRetMap = new HashMap();
                        tRetMap.put("code", Constants.CODE.Code_OK);
                        tRetMap.put("dataType", Constants.DATATYPE.OtaUI_SyncBlockCheck);
                        tRetMap.put("data", isOk);
                        sendingDataResponse.onDataResponse(0, 0, tRetMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                popQueue();
                break;
            }
        }
    }


    //Gatt 回调

    @Override
    public void bleStateResponse(int state) {

        Log.e("onConnectionStateChange","。。。蓝牙状态。。。。state="+state);

        if (state == BleState.Ble_Connected) {
            isRecvRealEcging = false;
            Log.e("TimeSetActivity2","蓝牙连接重置。。。。");
            resetQueue();
        }

        mBleStateCode = state;
        try {
            for (BleConnectResponse response : mBleStatelistens) {
                response.onConnectResponse(mBleStateCode);
            }
            if (state == BleState.Ble_ReadOk && mBleConnectResponse != null) {
                mBleConnectResponse.onConnectResponse(Constants.CODE.Code_OK);
            }
            if (state <= BleState.Ble_Disconnect && mBleConnectResponse != null) {
                mBleConnectResponse.onConnectResponse(Constants.CODE.Code_Failed);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (state == BleState.Ble_CharacteristicNotification) {
//            //获取设备信息
//            sendSingleData2Device(Constants.DATATYPE.GetDeviceInfo, new byte[]{0x47, 0x43}, CMD.Priority_normal, null);
            //同步时间
            sendSingleData2Device(Constants.DATATYPE.SettingTime, TimeUtil.makeBleTime(), CMD.Priority_normal, null);
            mTimeOutHander.postDelayed(mTimeRunnable, 1500);
        }
    }

    @Override
    public void bleOnCharacteristicWrite(int status, byte[] writeBytes) {
        isGattWriteCallBackFinish = true;
        if (mSendQueue != null && mSendQueue.size() > 0) {
            YCSendBean tTopBean = mSendQueue.get(0);
            if (tTopBean.dataSendFinish) {
                popQueue();
            } else {
                frontQueue();
            }
        }
    }

    @Override
    public void bleDataResponse(int code, byte[] recvValue) {
        int offset = 0;
        int cmdType = recvValue[offset++] & 0xff;
        int cmdKey = recvValue[offset++] & 0xff;
        int cmdLen = (recvValue[offset++] & 0xff) + ((recvValue[offset++] & 0xff) << 8);
        int crc16 = ((recvValue[cmdLen - 2] & 0xff) << 8) + (recvValue[cmdLen - 1] & 0xff);
        int dataLen = cmdLen - 6;
        byte[] dataValue = new byte[dataLen];
        System.arraycopy(recvValue, offset, dataValue, 0, dataLen);

        if (mSendQueue != null && mSendQueue.size() > 0) {
            YCSendBean tTopBean = mSendQueue.get(0);
            if (tTopBean.groupType == CMD.Group.Group_Single) {
                mTimeOutHander.removeCallbacks(mTimeRunnable);
            }
        }

        switch (cmdType) {
            case CMD.Setting: {
                packetSettingHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.Get: {
                packetGetHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.AppControl: {
                packetAppControlHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.DevControl: {
                packetDevControlHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.Health: {
                packetHealthHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.Real: {
                packetRealHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.Collect: {
                packetCollectHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
            case CMD.OtaUI: {
                packetOtaUIHandle(cmdKey, dataLen, dataValue, crc16);
                break;
            }
        }
    }

    @Override
    public void bleScanResponse(int code, ScanDeviceBean scanBean) {
        mBleScanResponse.onScanResponse(code, scanBean);
        if (code != 0) {
            stopScanBle();
        }
    }

    public void hrv_evt_handle(int evt_type, float params) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("data", params);
        switch (evt_type) {
            case 4:  //HRV
                try {
                    tRetMap.put("dataType", Constants.DATATYPE.Real_UploadECGHrv);
                    mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadECGHrv, tRetMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case 3: //RR值
                try {
                    tRetMap.put("dataType", Constants.DATATYPE.Real_UploadECGRR);
                    YCBTLog.e("RR值 " + mBleRealDataResponse + " " + tRetMap);
                    mBleRealDataResponse.onRealDataResponse(Constants.DATATYPE.Real_UploadECGRR, tRetMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
