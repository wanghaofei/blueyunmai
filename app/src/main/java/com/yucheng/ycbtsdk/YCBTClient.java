package com.yucheng.ycbtsdk;

import android.content.Context;

import com.yucheng.ycbtsdk.Core.YCBTClientImpl;
import com.yucheng.ycbtsdk.Protocol.CMD;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.Response.BleDeviceToAppDataResponse;
import com.yucheng.ycbtsdk.Response.BleRealDataResponse;
import com.yucheng.ycbtsdk.Response.BleScanResponse;
import com.yucheng.ycbtsdk.Utils.ByteUtil;
import com.yucheng.ycbtsdk.Utils.LogToFileUtils;
import com.yucheng.ycbtsdk.Utils.YCBTLog;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

public class YCBTClient {

    public static final int SecFrom30Year = 946684800;
    public static boolean OpenLogSwitch = true;


    public static void initClient(Context context, boolean btDebug) {

        YCBTClientImpl.getInstance().init(context);
//        LogToFileUtils.init(context);
        YCBTClient.OpenLogSwitch = btDebug;

        YCBTLog.e("YCBTClient initClient");
    }



    public static void appBloodCalibration(int dbp, int sbp, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) dbp, (byte) sbp};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppBloodCalibration, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void registerBleStateChange(BleConnectResponse connectResponse) {
        YCBTClientImpl.getInstance().registerBleStateChangeCallBack(connectResponse);
    }


    public static void unRegisterBleStateChange(BleConnectResponse connectResponse) {
        YCBTClientImpl.getInstance().unregisterBleStateChangeCallBack(connectResponse);
    }


    public static void startScanBle(BleScanResponse scanResponse, int timeoutSec) {
        YCBTLog.e("YCBTClient startScanBle");
        YCBTClientImpl.getInstance().startScanBle(scanResponse, timeoutSec);
    }

    public static void stopScanBle() {
        YCBTLog.e("YCBTClient stopScanBle");
        YCBTClientImpl.getInstance().stopScanBle();
    }

    public static void connectBle(String mac, BleConnectResponse connectResponse) {
        YCBTLog.e("YCBTClient connectBle");
        YCBTClientImpl.getInstance().connectBle(mac, connectResponse);
    }

    public static void reconnectBle(BleConnectResponse connectResponse) {
        YCBTClientImpl.getInstance().reconnectBle(connectResponse);
    }

    public static void disconnectBle() {
        YCBTClientImpl.getInstance().disconnectBle();
    }

    public static int connectState() {
        return YCBTClientImpl.getInstance().connectState();
    }


    public static void healthHistoryData(int dataType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendDataType2Device(dataType, CMD.Group.Group_Health, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void getDeviceUserConfig(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x43, 0x46};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceUserConfig, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void deleteHealthHistoryData(int dataType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(dataType, CMD.Group.Group_Health, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    public static void collectEcgList(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x00};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.Collect_QueryNum, CMD.Group.Group_ECGList, tDataBytes, CMD.Priority_low, dataResponse);
    }

    public static void collectEcgDataWithIndex(int ecgIndex, BleDataResponse dataResponse) {

        byte[] tDataBytes = new byte[4];
        tDataBytes[0] = 0x00;
        tDataBytes[1] = (byte) (ecgIndex & 0xff);
        tDataBytes[2] = (byte) ((ecgIndex >> 8) & 0xff);
        tDataBytes[3] = 0x01; //上传数据
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.Collect_GetWithIndex, CMD.Group.Group_ECGData, tDataBytes, CMD.Priority_low, dataResponse);
    }

    public static void collectEcgDataWithTimestamp(int timestamp, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[6];
        tDataBytes[0] = 0x00;
        tDataBytes[1] = (byte) (timestamp & 0xff);
        tDataBytes[2] = (byte) ((timestamp >> 8) & 0xff);
        tDataBytes[3] = (byte) ((timestamp >> 16) & 0xff);
        tDataBytes[4] = (byte) ((timestamp >> 24) & 0xff);
        tDataBytes[5] = 0x01; //上传数据
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.Collect_GetWithTimestamp, CMD.Group.Group_ECGData, tDataBytes, CMD.Priority_low, dataResponse);
    }

    public static void collectPpgList(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.Collect_QueryNum, CMD.Group.Group_PPGList, tDataBytes, CMD.Priority_low, dataResponse);
    }

    public static void collectPpgDataWithTimestamp(int timestamp, BleDataResponse dataResponse) {

        byte[] tDataBytes = new byte[6];
        tDataBytes[0] = 0x01;
        tDataBytes[1] = (byte) (timestamp & 0xff);
        tDataBytes[2] = (byte) ((timestamp >> 8) & 0xff);
        tDataBytes[3] = (byte) ((timestamp >> 16) & 0xff);
        tDataBytes[4] = (byte) ((timestamp >> 24) & 0xff);
        tDataBytes[5] = 0x01; //上传数据
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.Collect_GetWithTimestamp, CMD.Group.Group_PPGData, tDataBytes, CMD.Priority_low, dataResponse);
    }

    public static void collectDeleteEcgPpg(int timestamp, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[5];
        tDataBytes[0] = 0x01;
        tDataBytes[1] = (byte) (timestamp & 0xff);
        tDataBytes[2] = (byte) ((timestamp >> 8) & 0xff);
        tDataBytes[3] = (byte) ((timestamp >> 16) & 0xff);
        tDataBytes[4] = (byte) ((timestamp >> 24) & 0xff);
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.Collect_DeleteTimestamp, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingGetAllAlarm(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x00};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingAddAlarm(int type, int startHour, int startMin, int weekRepeat, int delayTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01, (byte) type, (byte) startHour, (byte) startMin, (byte) weekRepeat, (byte) delayTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingDeleteAlarm(int startHour, int startMin, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x02, (byte) startHour, (byte) startMin};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingModfiyAlarm(int startHour, int startMin,
                                          int newType,
                                          int newStartHour, int newStartMin,
                                          int newWeekRepeat, int newDelayTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x03, (byte) startHour, (byte) startMin, (byte) newType, (byte) newStartHour, (byte) newStartMin, (byte) newWeekRepeat, (byte) newDelayTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingGoal(int goalType, int target, int sleepHour, int sleepMinute, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[7];
        tDataBytes[0] = (byte) goalType;
        tDataBytes[1] = (byte) (target & 0xff);
        tDataBytes[2] = (byte) ((target >> 8) & 0xff);
        tDataBytes[3] = (byte) ((target >> 16) & 0xff);
        tDataBytes[4] = (byte) ((target >> 24) & 0xff);
        tDataBytes[5] = (byte) sleepHour;
        tDataBytes[6] = (byte) sleepMinute;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingGoal, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void settingUserInfo(int height, int weight, int sex, int age, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) height, (byte) weight, (byte) sex, (byte) age};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUserInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingUnit(int distanceUnit, int weightUnit, int temperatureUnit, int timeFormat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) distanceUnit, (byte) weightUnit, (byte) temperatureUnit, (byte) timeFormat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUnit, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingLongsite(int time1StartHour, int time1StartMin, int time1EndHour, int time1EndMin,
                                       int time2StartHour, int time2StartMin, int time2EndHour, int time2EndMin,
                                       int intervalTime, int repeat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) time1StartHour, (byte) time1StartMin, (byte) time1EndHour, (byte) time1EndMin,
                (byte) time2StartHour, (byte) time2StartMin, (byte) time2EndHour, (byte) time2EndMin, (byte) intervalTime, (byte) repeat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingLongsite, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void settingAntiLose(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAntiLose, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingHandWear(int leftOrRight, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) leftOrRight};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHandWear, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingNotify(int on, int sub1, int sub2, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) sub1, (byte) sub2};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingNotify, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingHeartAlarm(int on, int highHeart, int lowHeart, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) highHeart, (byte) lowHeart};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHeartAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingHeartMonitor(int mode, int intervalTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) mode, (byte) intervalTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHeartMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingFindPhone(int on, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingFindPhone, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingRestoreFactory(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x52, 0x53, 0x59, 0x53};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingRestoreFactory, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingNotDisturb(int on,
                                         int startTimeHour, int startTimeMin,
                                         int endTimeHour, int endTimeMin, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) startTimeHour, (byte) startTimeMin, (byte) endTimeHour, (byte) endTimeMin};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingNotDisturb, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingLanguage(int langType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) langType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingLanguage, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getDeviceScreenInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceScreenInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingRaiseScreen(int on, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingRaiseScreen, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingDisplayBrightness(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingDisplayBrightness, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingSkin(int skinColor, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) skinColor};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingSkin, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingBloodRange(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBloodRange, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingMainTheme(int themeType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) themeType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingMainTheme, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingSleepRemind(int startHour, int startMin, int repeat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) startHour, (byte) startMin, (byte) repeat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingSleepRemind, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingPpgCollect(int on, int collectLong, int collectInterval, BleDataResponse dataResponse) {
        settingDataCollect(on, 0x00, collectLong, collectInterval, dataResponse);
    }


    public static void settingDataCollect(int on, int type, int collectLong, int collectInterval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[6];
        tDataBytes[0] = (byte) (on & 0xff);
        tDataBytes[1] = (byte) (type & 0xff);
        tDataBytes[2] = (byte) (collectLong & 0xff);
        tDataBytes[3] = (byte) (collectLong >> 8 & 0xff);
        tDataBytes[4] = (byte) (collectInterval & 0xff);
        tDataBytes[5] = (byte) (collectInterval >> 8 & 0xff);
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingDataCollect, tDataBytes, CMD.Priority_normal, dataResponse);
    }





    public static void settingTemperatureAlarm(boolean on_off, int temp, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[3];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) temp;
        tDataBytes[2] = (byte) 0x00;//暂时没有低温报警  默认为0
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void newSettingTemperatureAlarm(boolean on_off, int maxTempInteger, int minTempInteger, int maxTempFloat, int minTempFloat, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[5];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) maxTempInteger;
        tDataBytes[2] = (byte) minTempInteger;
        tDataBytes[3] = (byte) maxTempFloat;
        tDataBytes[4] = (byte) minTempFloat;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingTemperatureMonitor(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingScreenTime(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[]{(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingScreenTime, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingAmbientLight(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAmbientLight, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingWorkingMode(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) level;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingWorkingMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingAccidentMode(boolean on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00：关闭意外监测模式 0x01: 打开意外监测模式
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAccidentMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingBraceletStatusAlert(boolean on_off, int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 打开
        tDataBytes[1] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBraceletStatusAlert, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingBloodOxygenModeMonitor(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBloodOxygenModeMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }




    public static void setInsurance(int type,int month,int day,int status,int money,String content,BleDataResponse dataResponse){
       try {
           byte[] contents = null;
           if(content != null && content.length() > 0){
               contents = content.getBytes("utf-8");
           }
           byte[] tDataBytes = new byte[contents == null ? 8 : 8 + contents.length];
           tDataBytes[0] = (byte)type;
           tDataBytes[1] = (byte)month;
           tDataBytes[2] = (byte)day;
           tDataBytes[3] = (byte)status;
           tDataBytes[4] = (byte)(money& 0xff);
           tDataBytes[5] = (byte) ((money >> 8) & 0xff);
           tDataBytes[6] = (byte) ((money >> 16) & 0xff);
           tDataBytes[7] = (byte) ((money >> 24) & 0xff);
           if (contents != null) {
               System.arraycopy(contents, 0, tDataBytes, 8, contents.length);
           }
           YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppInsuranceNews, tDataBytes, CMD.Priority_normal, dataResponse);
       }catch (Exception e){
           e.printStackTrace();
       }
    }






    public static void settingScheduleModification(int type, int scheduleIndex, int scheduleEnable, int eventIndex, int eventEnable, String time, int eventType, String content, BleDataResponse dataResponse) {
        try {
            byte[] contents = null;
            if (content != null && content.length() > 0) {
                contents = content.getBytes("utf-8");
            }
//            Long times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() / 1000 - YCBTClient.SecFrom30Year;
            Long times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() / 1000 - YCBTClient.SecFrom30Year + TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000;
            byte[] tDataBytes = new byte[contents == null ? 10 : 10 + contents.length];
            tDataBytes[0] = (byte) type;
            tDataBytes[1] = (byte) scheduleIndex;
            tDataBytes[2] = (byte) scheduleEnable;
            tDataBytes[3] = (byte) eventIndex;
            tDataBytes[4] = (byte) eventEnable;
            tDataBytes[5] = (byte) (times & 0xff);
            tDataBytes[6] = (byte) ((times >> 8) & 0xff);
            tDataBytes[7] = (byte) ((times >> 16) & 0xff);
            tDataBytes[8] = (byte) ((times >> 24) & 0xff);
            tDataBytes[9] = (byte) eventType;
            if (contents != null) {
                System.arraycopy(contents, 0, tDataBytes, 10, contents.length);
            }
            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingScheduleModification, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void settingAmbientTemperatureAndHumidity(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAmbientTemperatureAndHumidity, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingScheduleSwitch(boolean on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingScheduleSwitch, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingStepCountingStateTime(int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingStepCountingStateTime, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingUploadReminder(boolean on_off, int threshold, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00：关 0x01：开
        tDataBytes[1] = (byte) threshold;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUploadReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingBluetoothBroadcastInterval(int time, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (time & 0xff);
        tDataBytes[1] = (byte) ((time >> 8) & 0xff);
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBluetoothBroadcastInterval, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingBluetoothTransmittingPower(int power, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) power;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBluetoothTransmittingPower, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingExerciseHeartRateZone(int type, int minHeart, int maxHeart, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[3];
        tDataBytes[0] = (byte) type;
        tDataBytes[1] = (byte) minHeart;
        tDataBytes[2] = (byte) maxHeart;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingExerciseHeartRateZone, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingEventReminder(int code, int index, int on_off, int type, int hour, int min, int weekRepeat, int interval, String name, BleDataResponse dataResponse) {
        int lenth = 0;
        byte[] bytes = null;
        try {
            if (type == 1 && name != null) {
                bytes = name.getBytes("utf-8");
                lenth = bytes.length;
            }
            if (lenth > 12)
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] tDataBytes = new byte[8 + lenth];
        tDataBytes[0] = (byte) code;
        tDataBytes[1] = (byte) index;
        tDataBytes[2] = (byte) on_off;
        tDataBytes[3] = (byte) type;
        tDataBytes[4] = (byte) hour;
        tDataBytes[5] = (byte) min;
        tDataBytes[6] = (byte) weekRepeat;
        tDataBytes[7] = (byte) interval;
        tDataBytes[0] = (byte) code;
        if (bytes != null)
            System.arraycopy(bytes, 0, tDataBytes, 8, lenth);
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingEventReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void settingEventReminderSwitch(int on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) on_off;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingEventReminderSwitch, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void getDeviceInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x47, 0x43};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getDeviceLog(int logType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) logType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceLog, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getThemeInfo(BleDataResponse dataResponse) {

        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetThemeInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getElectrodeLocationInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetElectrodeLocation, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getNowStep(BleDataResponse dataResponse) {

        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetNowStep, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getHistoryOutline(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetHistoryOutline, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getRealTemp(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetRealTemp, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getScreenInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetScreenInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getHeavenEarthAndFiveElement(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetHeavenEarthAndFiveElement, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getRealBloodOxygen(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x49, 0x53};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetRealBloodOxygen, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getCurrentAmbientLightIntensity(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x4A, 0x54};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentAmbientLightIntensity, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getCurrentAmbientTempAndHumidity(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x4B, 0x55};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentAmbientTempAndHumidity, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getScheduleInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetScheduleInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getSensorSamplingInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetSensorSamplingInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getCurrentSystemWorkingMode(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentSystemWorkingMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getInsuranceRelatedInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetInsuranceRelatedInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getUploadConfigurationInfoOfReminder(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetUploadConfigurationInfoOfReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getStatusOfManualMode(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetStatusOfManualMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getEventReminderInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetEventReminderInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getChipScheme(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetChipScheme, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void getDeviceRemindInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceRemindInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void appFindDevice(int mode, int remindNum, int remindInterval, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) mode, (byte) remindNum, (byte) remindInterval};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppFindDevice, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appSengMessageToDevice(final int type, final String title, final String content, final BleDataResponse dataResponse) {
        if (title == null || title.length() < 1 || content == null || content.length() < 3)
            return;
        getDeviceScreenInfo(new BleDataResponse() {
            @Override
            public void onDataResponse(int code, float ratio, HashMap resultMap) {
                if (resultMap != null) {
                    int count = (int) resultMap.get("count");
                    byte[] tDataBytes = ByteUtil.stringToByte(title, content, count, type);
                    YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppMessageControl, tDataBytes, CMD.Priority_normal, dataResponse);
                }
            }
        });
    }


    public static void appRealSportFromDevice(int type, final BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[]{(byte) type, 0x00, 0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppControlReal, CMD.Group.Group_REAL_SPORT, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appRunModeStart(int sportType, BleDataResponse dataResponse, BleRealDataResponse realDataResponse) {
        byte[] tDataBytes = {(byte) 0x01, (byte) sportType};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppRunMode, CMD.Group.Group_StartSport, tDataBytes, CMD.Priority_normal, dataResponse);
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }


    public static void appRegisterRealDataCallBack(BleRealDataResponse realDataResponse) {
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }


    public static void deviceToApp(BleDeviceToAppDataResponse bleRealTypeResponse) {
        YCBTClientImpl.getInstance().registerRealTypeCallBack(bleRealTypeResponse);
    }


    public static void appRunModeEnd(int sportType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) 0x00, (byte) sportType};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppRunMode, CMD.Group.Group_EndSport, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appEcgTestStart(BleDataResponse dataResponse, BleRealDataResponse realDataResponse) {
        byte[] tDataBytes = {(byte) 0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppBloodSwitch, CMD.Group.Group_StartEcgTest, tDataBytes, CMD.Priority_normal, dataResponse);
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }


    public static void appEcgTestEnd(BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) 0x00};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppBloodSwitch, CMD.Group.Group_EndEcgTest, tDataBytes, CMD.Priority_high, dataResponse);
    }


    public static void appHealthArg(int warnState, int healthState, int healthIndex, int friendWarn, BleDataResponse dataResponse) {

        byte[] tDataBytes = new byte[14];
        tDataBytes[0] = (byte) warnState;
        tDataBytes[1] = (byte) healthState;
        tDataBytes[2] = (byte) healthIndex;
        tDataBytes[3] = (byte) friendWarn;

        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppHealthArg, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appTodayWeather(String lowTemp, String highTemp, String curTemp, int type, BleDataResponse dataResponse) {

        try {
            byte[] tLowTempBytes = lowTemp.getBytes("utf-8");
            byte[] tHighTempBytes = highTemp.getBytes("utf-8");
            byte[] tCurTempBytes = curTemp.getBytes("utf-8");
            int tLen1 = tLowTempBytes.length;
            int tLen2 = tHighTempBytes.length;
            int tLen3 = tCurTempBytes.length;

            byte[] tDataBytes = new byte[3 + tLen1 + 3 + tLen2 + 3 + tLen3 + 3 + 2];
            int tOffset = 0;

            //当前温度
            tDataBytes[tOffset++] = 0x02;
            tDataBytes[tOffset++] = (byte) (tLen3 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen3 >> 8 & 0xff);
            System.arraycopy(tCurTempBytes, 0, tDataBytes, tOffset, tLen3);
            tOffset += tLen3;

            //最底温
            tDataBytes[tOffset++] = 0x00;
            tDataBytes[tOffset++] = (byte) (tLen1 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen1 >> 8 & 0xff);
            System.arraycopy(tLowTempBytes, 0, tDataBytes, tOffset, tLen1);
            tOffset += tLen1;

            //最高温
            tDataBytes[tOffset++] = 0x01;
            tDataBytes[tOffset++] = (byte) (tLen2 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen2 >> 8 & 0xff);
            System.arraycopy(tHighTempBytes, 0, tDataBytes, tOffset, tLen2);
            tOffset += tLen2;

            //天气类型
            tDataBytes[tOffset++] = 0x04;
            tDataBytes[tOffset++] = 0x02;
            tDataBytes[tOffset++] = 0x00;
            tDataBytes[tOffset++] = (byte) (type & 0xff);
            tDataBytes[tOffset++] = (byte) (type >> 8 & 0xff);

            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTodayWeather, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void appTodayWeather(String lowTemp, String highTemp, String curTemp, int type, String windDirection, String windPower, String currentGeographicLocation, int lunarPhaseInfo, BleDataResponse dataResponse) {
        try {
            byte[] tLowTempBytes = lowTemp.getBytes("utf-8");
            byte[] tHighTempBytes = highTemp.getBytes("utf-8");
            byte[] tCurTempBytes = curTemp.getBytes("utf-8");
            byte[] windDirections = null;
            byte[] windPowers = null;
            byte[] currentGeographicLocations = null;
            byte[] lunarPhaseInfos = new byte[]{(byte) lunarPhaseInfo};
            int tLen1 = tLowTempBytes.length;
            int tLen2 = tHighTempBytes.length;
            int tLen3 = tCurTempBytes.length;
            int tLen4 = 0;
            int tLen5 = 0;
            int tLen6 = 0;
            int tLen7 = 1;

            int length = 3 + tLen1 + 3 + tLen2 + 3 + tLen3 + 3 + 2;

            if (windDirection != null) {
                windDirections = windDirection.getBytes("utf-8");
                tLen4 = windDirections.length;
                length += (tLen4 + 3);
            }

            if (windPower != null) {
                windPowers = windPower.getBytes("utf-8");
                tLen5 = windPowers.length;
                length += (tLen5 + 3);
            }

            if (currentGeographicLocation != null) {
                currentGeographicLocations = currentGeographicLocation.getBytes("utf-8");
                tLen6 = currentGeographicLocations.length;
                length += (tLen6 + 3);
            }

            length += (tLen7 + 3);

            byte[] tDataBytes = new byte[length];
            int tOffset = 0;

            //当前温度
            tDataBytes[tOffset++] = 0x02;
            tDataBytes[tOffset++] = (byte) (tLen3 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen3 >> 8 & 0xff);
            System.arraycopy(tCurTempBytes, 0, tDataBytes, tOffset, tLen3);
            tOffset += tLen3;

            //最底温
            tDataBytes[tOffset++] = 0x00;
            tDataBytes[tOffset++] = (byte) (tLen1 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen1 >> 8 & 0xff);
            System.arraycopy(tLowTempBytes, 0, tDataBytes, tOffset, tLen1);
            tOffset += tLen1;

            //最高温
            tDataBytes[tOffset++] = 0x01;
            tDataBytes[tOffset++] = (byte) (tLen2 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen2 >> 8 & 0xff);
            System.arraycopy(tHighTempBytes, 0, tDataBytes, tOffset, tLen2);
            tOffset += tLen2;

            //天气类型
            tDataBytes[tOffset++] = 0x04;
            tDataBytes[tOffset++] = 0x02;
            tDataBytes[tOffset++] = 0x00;
            tDataBytes[tOffset++] = (byte) (type & 0xff);
            tDataBytes[tOffset++] = (byte) (type >> 8 & 0xff);

            if (windDirection != null) {//风向
                tDataBytes[tOffset++] = 0x06;
                tDataBytes[tOffset++] = (byte) (tLen4 & 0xff);
                tDataBytes[tOffset++] = (byte) (tLen4 >> 8 & 0xff);
                System.arraycopy(windDirections, 0, tDataBytes, tOffset, tLen4);
                tOffset += tLen4;
            }

            if (windPower != null) {//风力
                tDataBytes[tOffset++] = 0x07;
                tDataBytes[tOffset++] = (byte) (tLen5 & 0xff);
                tDataBytes[tOffset++] = (byte) (tLen5 >> 8 & 0xff);
                System.arraycopy(windPowers, 0, tDataBytes, tOffset, tLen5);
                tOffset += tLen5;
            }

            if (currentGeographicLocations != null) {//当前所处的地理位置
                tDataBytes[tOffset++] = 0x08;
                tDataBytes[tOffset++] = (byte) (tLen6 & 0xff);
                tDataBytes[tOffset++] = (byte) (tLen6 >> 8 & 0xff);
                System.arraycopy(currentGeographicLocations, 0, tDataBytes, tOffset, tLen6);
                tOffset += tLen6;
            }

            //天系列月相信息
            tDataBytes[tOffset++] = 0x09;
            tDataBytes[tOffset++] = (byte) (tLen7 & 0xff);
            tDataBytes[tOffset++] = (byte) (tLen7 >> 8 & 0xff);
            System.arraycopy(lunarPhaseInfos, 0, tDataBytes, tOffset, tLen7);
            tOffset += tLen7;

            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTodayWeather, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void appShutDown(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppShutDown, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appEmoticonIndex(int index, int hour, int min, String name, BleDataResponse dataResponse) {
        int length = 0;
        byte[] names = null;
        if (name != null && !"".equals(name)) {
            String str = "";
            if (name.length() < 8) {
                str = name;
            } else {
                str = getData(name, 8) + "…";
            }
            names = str.getBytes();
            length = names.length;
        }
        byte[] tDataBytes = new byte[3 + length];
        tDataBytes[0] = (byte) index;
        tDataBytes[1] = (byte) hour;
        tDataBytes[2] = (byte) min;
        if (length != 0) {
            System.arraycopy(names, 0, tDataBytes, 3, length);
        }
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEmoticonIndex, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    private static String getData(String msg, int len) {
        int n = 0;
        try {
            byte[] bytes = msg.substring(0, len).getBytes("utf-8");
            boolean is_flag = true;
            while (is_flag) {
                n++;
                if (bytes.length < (len - 1) * 3 && len + n < msg.length()) {
                    bytes = msg.substring(0, len + n).getBytes("utf-8");
                } else {
                    is_flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg.substring(0, len + n - 2);
    }


    public static void appHealthWriteBack(int healthValue, String healthState, BleDataResponse dataResponse) {
        try {
            byte[] data = null;
            if (healthState != null) {
                data = healthState.getBytes("utf-8");
            }
            int length = data == null ? 0 : data.length;
            byte[] tDataBytes = new byte[length + 1];
            tDataBytes[0] = (byte) healthValue;
            if (data != null) {
                System.arraycopy(data, 0, tDataBytes, 1, length);
            }
            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppHealthWriteBack, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void appSleepWriteBack(int deepSleepTimeHour, int deepSleepTimeMin, int lightSleepTimeHour, int lightSleepTimeMin, int totalSleepTimeHour, int totalSleepTimeMin, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[6];
        tDataBytes[0] = (byte) deepSleepTimeHour;
        tDataBytes[1] = (byte) deepSleepTimeMin;
        tDataBytes[2] = (byte) lightSleepTimeHour;
        tDataBytes[3] = (byte) lightSleepTimeMin;
        tDataBytes[4] = (byte) totalSleepTimeHour;
        tDataBytes[5] = (byte) totalSleepTimeMin;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppSleepWriteBack, tDataBytes, CMD.Priority_normal, dataResponse);

    }


    public static void appUserInfoWriteBack(int type, String content, BleDataResponse dataResponse) {
        try {
            byte[] data = null;
            if (content != null) {
                data = content.getBytes("utf-8");
            }
            int length = data == null ? 0 : data.length;
            byte[] tDataBytes = new byte[length + 3];
            tDataBytes[0] = (byte) type;
            tDataBytes[1] = (byte) (length & 0xff);
            tDataBytes[2] = (byte) ((length >> 8) & 0xff);
            if (data != null) {
                System.arraycopy(data, 0, tDataBytes, 1, length);
            }
            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppUserInfoWriteBack, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void appUpgradeReminder(int on_off, int percent, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) on_off;
        tDataBytes[1] = (byte) percent;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppUpgradeReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appAmbientLightMeasurementControl(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppAmbientLightMeasurementControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appAmbientTempHumidityMeasurementControl(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppAmbientTempHumidityMeasurementControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appInsuranceNews(int type, String content, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppInsuranceNews, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appSensorSwitchControl(int type, int on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) type;
        tDataBytes[1] = (byte) on_off;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppSensorSwitchControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appMobileModel(String model, BleDataResponse dataResponse) {
        if (model == null || model.length() < 1)
            return;
        try {
            byte[] tDataBytes = model.getBytes("utf-8");
            YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppMobileModel, tDataBytes, CMD.Priority_normal, dataResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void appEffectiveStep(int step, int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[5];
        tDataBytes[0] = (byte) (step & 0xff);
        tDataBytes[1] = (byte) (step >> 8 & 0xff);
        tDataBytes[2] = (byte) (step >> 16 & 0xff);
        tDataBytes[3] = (byte) (step >> 24 & 0xff);
        tDataBytes[4] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEffectiveStep, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appEffectiveHeart(int heart, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) heart;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEffectiveHeart, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appEarlyWarning(int type, String content, BleDataResponse dataResponse) {
        if (type == 1 && content == null) {
            return;
        }
        int len = 0;
        byte[] bytes = null;
        try {
            if (content != null) {
                bytes = content.getBytes("utf-8");
                len = bytes.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] tDataBytes = new byte[1 + len];
        tDataBytes[0] = (byte) type;
        if (bytes != null) {
            System.arraycopy(bytes, 0, tDataBytes, 1, len);
        }
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEarlyWarning, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appPushMessage(int type, String message, BleDataResponse dataResponse) {
        if (type == 6 && message == null) {
            return;
        }
        int len = 0;
        byte[] bytes = null;
        try {
            if (message != null) {
                bytes = message.getBytes("utf-8");
                len = bytes.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] tDataBytes = new byte[1 + len];
        tDataBytes[0] = (byte) type;
        if (bytes != null) {
            System.arraycopy(bytes, 0, tDataBytes, 1, len);
        }
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppPushMessage, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appTemperatureCorrect(int tempInt, int tempFloat, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) tempInt;
        tDataBytes[1] = (byte) tempFloat;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureCorrect, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appTemperatureMeasure(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;//暂时只有单次测试
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureMeasure, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void appTemperatureCode(int code, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) code;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureCode, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void otaUIGetBreakInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x00};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_GetFileBreak, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    public static void otaUIFileInfo(int fileTotalLen, int remainFileLen, int remainBlockNum, int fileOffset, int fileCrc, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[18];
        byte[] tIntByte = ByteUtil.fromInt(fileTotalLen);
        System.arraycopy(tIntByte, 0, tDataBytes, 0, 4);

        tIntByte = ByteUtil.fromInt(remainFileLen);
        System.arraycopy(tIntByte, 0, tDataBytes, 4, 4);

        tIntByte = ByteUtil.fromInt(remainBlockNum);
        System.arraycopy(tIntByte, 0, tDataBytes, 8, 4);

        tIntByte = ByteUtil.fromInt(fileOffset);
        System.arraycopy(tIntByte, 0, tDataBytes, 12, 4);

        tIntByte = ByteUtil.fromInt(fileCrc);
        System.arraycopy(tIntByte, 0, tDataBytes, 16, 2);


        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_SyncFileInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    public static void otaUIBlock(byte[] blockData, BleDataResponse dataResponse) {

        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_SyncBlock, blockData, CMD.Priority_normal, dataResponse);
    }


    public static void otaUIBlockCheck(int blockLen, int blockFrameNum, int blockCrc16, BleDataResponse dataResponse) {

        byte[] tDataBytes = new byte[8];
        byte[] tIntByte = ByteUtil.fromInt(blockLen);
        System.arraycopy(tIntByte, 0, tDataBytes, 0, 4);

        tIntByte = ByteUtil.fromInt(blockFrameNum);
        System.arraycopy(tIntByte, 0, tDataBytes, 4, 2);

        tIntByte = ByteUtil.fromInt(blockCrc16);
        System.arraycopy(tIntByte, 0, tDataBytes, 6, 2);

        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_SyncBlockCheck, tDataBytes, CMD.Priority_normal, dataResponse);
    }
}
