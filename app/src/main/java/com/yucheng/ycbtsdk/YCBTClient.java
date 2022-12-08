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

    /***
     * 初始化
     * @param context
     */
    public static void initClient(Context context, boolean btDebug) {

        YCBTClientImpl.getInstance().init(context);
//        LogToFileUtils.init(context);
        YCBTClient.OpenLogSwitch = btDebug;

        YCBTLog.e("YCBTClient initClient");
    }


    /****
     * 血压校准
     * @param dbp 收缩压  sbp 舒张压
     *            0x00校准成功，0x01校准失败，0x02校准失败，
     * @param dataResponse
     */
    public static void appBloodCalibration(int dbp, int sbp, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) dbp, (byte) sbp};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppBloodCalibration, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 注册蓝牙连接状态回调
     * @param connectResponse
     */
    public static void registerBleStateChange(BleConnectResponse connectResponse) {
        YCBTClientImpl.getInstance().registerBleStateChangeCallBack(connectResponse);
    }

    /***
     * 移除蓝牙连接状态回调
     * @param connectResponse
     */
    public static void unRegisterBleStateChange(BleConnectResponse connectResponse) {
        YCBTClientImpl.getInstance().unregisterBleStateChangeCallBack(connectResponse);
    }

    /***
     * 开始蓝牙搜索设备, 5s停止搜索
     * 因根据广播包过滤,只会搜索到公司产品
     * @param scanResponse
     * @param timeoutSec 停止时间(秒)
     */
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

    //同步运动健康数据

    /***
     * 同步健康历史数据
     * @param dataType (0x0502步数,0x0504睡眠,0x0506心率,0x0508血压,0x0509同步所有的包括步数睡眠心率血压血氧hrvcvrr温度,
     *                 0x051A血氧 0x051C温湿度 0x051E体温数 0x0520环境光 0x0529手环佩戴脱落记录)
     * @param dataResponse
     */
    public static void healthHistoryData(int dataType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendDataType2Device(dataType, CMD.Group.Group_Health, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    /***
     * 获取设备配置信息
     * @param dataResponse
     */
    public static void getDeviceUserConfig(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x43, 0x46};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceUserConfig, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 删除健康历史数据
     * @param dataType (步数,睡眠,心率,血压)
     * @param dataResponse
     */
    public static void deleteHealthHistoryData(int dataType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(dataType, CMD.Group.Group_Health, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    //同步ECG与PPG数据
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

    //设置命令

    /**
     * 查询闹钟
     *
     * @param dataResponse
     */
    public static void settingGetAllAlarm(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x00};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    /***
     * 设置闹钟
     * @param type 0x00:起床 0x01:睡觉 0x02:锻炼 0x03:吃药 0x04:约会 0x05:聚会 0x06:会议 0x07:自定义
     * @param startHour 开始时间小时
     * @param startMin  开始时间分钟
     * @param weekRepeat 重复&开关
     * bit7  bit6 bit5 bit4 bit3 bit2 bit1 bit0
     * 总开关 周日 周六  周五 周四  周三  周二  周一
     * 0关1开
     * @param delayTime 贪睡时长(单位:分钟) 0-59
     * @param dataResponse 数据返回结果
     */

    public static void settingAddAlarm(int type, int startHour, int startMin, int weekRepeat, int delayTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01, (byte) type, (byte) startHour, (byte) startMin, (byte) weekRepeat, (byte) delayTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 删除闹钟
     * @param startHour 闹钟开始小时
     * @param startMin 闹钟开始分钟
     * @param dataResponse
     */
    public static void settingDeleteAlarm(int startHour, int startMin, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x02, (byte) startHour, (byte) startMin};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 修改闹钟
     * @param startHour 以前闹钟小时
     * @param startMin  以前闹钟分钟
     * @param newType
     * @param newStartHour
     * @param newStartMin
     * @param newWeekRepeat
     * @param newDelayTime
     * @param dataResponse
     */
    public static void settingModfiyAlarm(int startHour, int startMin,
                                          int newType,
                                          int newStartHour, int newStartMin,
                                          int newWeekRepeat, int newDelayTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x03, (byte) startHour, (byte) startMin, (byte) newType, (byte) newStartHour, (byte) newStartMin, (byte) newWeekRepeat, (byte) newDelayTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 目标设置
     * @param goalType 0x00:步数 0x01:卡路里 0x02:距离 0x03:睡眠
     * @param target 目标值(类型为 睡眠时，此处填充 0x00)
     * @param sleepHour 睡眠目标:时 (类型为非睡眠时，此处填充 0x00)
     * @param sleepMinute 睡眠目标:分 (类型为非睡眠 时，此处填充 0x00)
     * @param dataResponse
     */
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


    /***
     * 用户信息设置
     * @param height 身高cm 50 ~ 255
     * @param weight 体重(kg)
     * @param sex 性别 0男 1女
     * @param age 年龄 1~150
     * @param dataResponse
     */
    public static void settingUserInfo(int height, int weight, int sex, int age, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) height, (byte) weight, (byte) sex, (byte) age};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUserInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 单位设置 (不需要的设置项置 0)
     * @param distanceUnit 0x00:km 0x01:mile
     * @param weightUnit  0x00:kg 0x01:lb 0x02:st
     * @param temperatureUnit  0x00: °C 0x01: °F
     * @param timeFormat  0x00:24 小时 0x01:12 小时
     * @param dataResponse
     */
    public static void settingUnit(int distanceUnit, int weightUnit, int temperatureUnit, int timeFormat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) distanceUnit, (byte) weightUnit, (byte) temperatureUnit, (byte) timeFormat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUnit, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 设置久座提醒
     * @param time1StartHour 时间段1开始小时
     * @param time1StartMin 时间段1开始分
     * @param time1EndHour 时间段1结束小时
     * @param time1EndMin 时间段1结束分
     * @param time2StartHour 时间段2开始小时
     * @param time2StartMin 时间段2开始分
     * @param time2EndHour 时间段2结束小时
     * @param time2EndMin 时间段2结束分
     * @param intervalTime  间隔时间(分钟)
     * @param repeat 周重复
     * @param dataResponse
     */
    public static void settingLongsite(int time1StartHour, int time1StartMin, int time1EndHour, int time1EndMin,
                                       int time2StartHour, int time2StartMin, int time2EndHour, int time2EndMin,
                                       int intervalTime, int repeat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) time1StartHour, (byte) time1StartMin, (byte) time1EndHour, (byte) time1EndMin,
                (byte) time2StartHour, (byte) time2StartMin, (byte) time2EndHour, (byte) time2EndMin, (byte) intervalTime, (byte) repeat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingLongsite, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    /***
     * 防丢提醒设置
     * @param type  0x00: 不防丢 0x01: 近距离防丢 0x02: 中距离防丢(默认) 0x03: 远距离防丢
     * @param dataResponse
     */
    public static void settingAntiLose(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAntiLose, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 左右手佩戴设置
     * @param leftOrRight 0x00: 左手 0x01: 右手
     * @param dataResponse
     */
    public static void settingHandWear(int leftOrRight, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) leftOrRight};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHandWear, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 通知提醒开关设置
     * @param on 通知提醒总开关 0x00: 关 0x01: 开
     * @param sub1 提醒项子开关 Bit7:来电 Bit6:信短 Bit5:邮件 Bit4:微信 Bit3:QQ Bit2:新浪微博 Bit1:facebook Bit0:twitter   0:关1：开
     * @param sub2 提醒项子开关 Bit7:Messenger Bit6:WhatsAPP Bit5:Linked in Bit4:Instagram Bit3:Skype Bit2:Line Bit1:Snapchat Bit0:APP 提醒  0: 关 1：开
     * @param dataResponse
     */
    public static void settingNotify(int on, int sub1, int sub2, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) sub1, (byte) sub2};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingNotify, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 心率报警设置
     * @param on 报警开关 0x00: 关
     * @param highHeart 最高心率报警阈值 100 – 240
     * @param lowHeart 最低心率报警阈值 30 - 60
     * @param dataResponse
     */
    public static void settingHeartAlarm(int on, int highHeart, int lowHeart, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) highHeart, (byte) lowHeart};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHeartAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /****
     * 心率监测模式设置
     * @param mode 模式 0x00: 手动模式 0x01: 自动模式
     * @param intervalTime 自动模式下心率监测间隔(分)
     * @param dataResponse
     */
    public static void settingHeartMonitor(int mode, int intervalTime, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) mode, (byte) intervalTime};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingHeartMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 找手机开关设置
     * @param on 0x00: 关 0x01: 开
     * @param dataResponse
     */
    public static void settingFindPhone(int on, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingFindPhone, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 恢复出厂设置
     * @param dataResponse
     */
    public static void settingRestoreFactory(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x52, 0x53, 0x59, 0x53};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingRestoreFactory, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 勿扰模式设置
     *
     * @param on            0x00:关 0x01:开
     * @param startTimeHour
     * @param startTimeMin
     * @param endTimeHour
     * @param endTimeMin
     * @param dataResponse
     */
    public static void settingNotDisturb(int on,
                                         int startTimeHour, int startTimeMin,
                                         int endTimeHour, int endTimeMin, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on, (byte) startTimeHour, (byte) startTimeMin, (byte) endTimeHour, (byte) endTimeMin};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingNotDisturb, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 语言设置
     * @param langType 0x00:英语 0x01: 中文 0x02: 俄语 0x03: 德语 0x04: 法语
     * 0x05: 日语 0x06: 西班牙语 0x07: 意大利语 0x08: 葡萄牙文
     * 0x09: 韩文 0x0A: 波兰文 0x0B: 马来文 0x0C: 繁体中文 0xFF:其它
     * @param dataResponse
     */
    public static void settingLanguage(int langType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) langType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingLanguage, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取屏幕分辨率和字体分辨率
     * @param dataResponse
     */
    public static void getDeviceScreenInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceScreenInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 抬腕亮屏开关设置
     * @param on 0x00:关闭 0x01: 打开
     * @param dataResponse
     */
    public static void settingRaiseScreen(int on, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) on};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingRaiseScreen, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 显示屏亮度设置
     * @param level 0x00:低  0x01: 中  0x02: 高  0x03：自动  0x04: 较低  0x05：较高
     * @param dataResponse
     */
    public static void settingDisplayBrightness(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingDisplayBrightness, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 肤色设置
     * @param skinColor  0x00:白 0x01: 白间黄
     * 0x02: 黄 0x03: 棕色 0x04: 褐色 0x05: 黑 0x07: 其它
     * @param dataResponse
     */
    public static void settingSkin(int skinColor, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) skinColor};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingSkin, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 血压范围设置
     * @param level 0x00:偏低 0x01: 正常 0x02: 轻微偏高 0x03: 中度偏高 0x04: 重度高
     * @param dataResponse
     */
    public static void settingBloodRange(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBloodRange, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 设备主界面样式配置
     * @param themeType  样式(0-(N-1)), N 代表设备支持的主界面数量，由获取指令查询
     * @param dataResponse
     */
    public static void settingMainTheme(int themeType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) themeType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingMainTheme, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 设置睡眠提醒时间
     * @param startHour 时
     * @param startMin 分
     * @param repeat 周重复
     * @param dataResponse
     */
    public static void settingSleepRemind(int startHour, int startMin, int repeat, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) startHour, (byte) startMin, (byte) repeat};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingSleepRemind, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * PPG数据采集配置
     * @param on 0x01: 开启 0x00: 关闭
     * @param collectLong 每次采集时长(单位:秒) (关闭时填 0)
     * @param collectInterval 采集间隔(单位:分钟) (关闭时填 0)
     * @param dataResponse
     */
    public static void settingPpgCollect(int on, int collectLong, int collectInterval, BleDataResponse dataResponse) {
        settingDataCollect(on, 0x00, collectLong, collectInterval, dataResponse);
    }

    /***
     * 数据采集配置
     * @param on 0x01: 开启 0x00: 关闭
     * @param type 0x00: PPG 0x01: 加速度数据 0x02：ECG 0x03：温湿度 0x04：环境光 0x05：体温
     * @param collectLong 每次采集时长(单位:秒) (关闭时填 0)
     * @param collectInterval 采集间隔(单位:分钟) (关闭时填 0)
     * @param dataResponse
     */
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







    /**
     * 温度报警
     *
     * @param on_off       温度报警开关
     * @param temp         温度报警上限值(-127 - 127)
     * @param dataResponse
     */
    public static void settingTemperatureAlarm(boolean on_off, int temp, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[3];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) temp;
        tDataBytes[2] = (byte) 0x00;//暂时没有低温报警  默认为0
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 温度报警  新增了小数部分, 新手环才有这功能
     *
     * @param on_off         温度报警开关
     * @param maxTempInteger 高度报警整数部分(36 -- 100)
     * @param minTempInteger 低温报警整数部分(-127 -- 36)
     * @param maxTempFloat   高度报警小数部分(1 -- 9)
     * @param minTempFloat   低温报警小数部分(1 -- 9)
     * @param dataResponse
     */
    public static void newSettingTemperatureAlarm(boolean on_off, int maxTempInteger, int minTempInteger, int maxTempFloat, int minTempFloat, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[5];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) maxTempInteger;
        tDataBytes[2] = (byte) minTempInteger;
        tDataBytes[3] = (byte) maxTempFloat;
        tDataBytes[4] = (byte) minTempFloat;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureAlarm, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 温度监测
     *
     * @param on_off       温度监测开关
     * @param interval     间隔时间 分钟
     * @param dataResponse
     */
    public static void settingTemperatureMonitor(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingTemperatureMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 息屏时间设置
     *
     * @param level        0x00：5s 0x01: 10s 0x02: 15s 0x03：30s
     * @param dataResponse
     */
    public static void settingScreenTime(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[]{(byte) level};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingScreenTime, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 环境光检测设置
     *
     * @param on_off       环境光检测开关
     * @param interval     环境光监测间隔（分）
     * @param dataResponse
     */
    public static void settingAmbientLight(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAmbientLight, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 工作模式切换设置
     *
     * @param level        0x00：设置为正常工作模式 0x01: 设置为关怀工作模式 0x02：设置为省电工作模式 0x03: 设置为自定义工作模式
     * @param dataResponse
     */
    public static void settingWorkingMode(int level, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) level;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingWorkingMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 意外监测模式设置
     *
     * @param on_off       意外监测模式开关
     * @param dataResponse
     */
    public static void settingAccidentMode(boolean on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00：关闭意外监测模式 0x01: 打开意外监测模式
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAccidentMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 手环状态提醒设置
     *
     * @param on_off       手环状态提醒开关
     * @param type         提醒类型 0x00: 蓝牙断开提醒 0x01：运动达标提醒
     * @param dataResponse
     */
    public static void settingBraceletStatusAlert(boolean on_off, int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 打开
        tDataBytes[1] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBraceletStatusAlert, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 血氧监测模式设置
     *
     * @param on_off       血氧监测模式开关
     * @param interval     间隔时间 分钟
     * @param dataResponse
     */
    public static void settingBloodOxygenModeMonitor(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBloodOxygenModeMonitor, tDataBytes, CMD.Priority_normal, dataResponse);
    }



    /**
     * 设置保险
     * @param type  保险类型，0x00  当前保额,0x01 预计保额,0x02 次月保费,0x03次年保费
     * @param month  月，数字 1-12
     * @param day    日，1-31
     * @param status 保费状态 0x00 减少,0x01 增加
     * @param money 金额
     * @param content  保险名称<=18 byte，字符串长度，不能大于6个汉字
     */
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





    /**
     * 日程修改设置
     *
     * @param type           0x00:修改日程  0x01:增加日程  0x02:删除日程
     * @param scheduleIndex  修改的日程索引 1-20
     * @param scheduleEnable 0x00：禁止 0x01：使能
     * @param eventIndex     修改的事件 索引
     * @param eventEnable    0x00：禁止 0x01：使能
     * @param time           修改事件的时间 格式为yyyy-MM-dd HH:mm:ss
     * @param eventType      事件类型  0x00 起床 0x01 早饭 0x02 晒太阳 0x03 午饭 0x04 午休 0x05 运动 0x06 晚饭 0x07 睡觉 0x08 自定义
     * @param content        修改事件类型的名称
     * @param dataResponse
     */
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

    /**
     * 环境温湿度检测模式设置
     *
     * @param on_off       环境温湿度检测模式开关
     * @param interval     间隔时间 分钟
     * @param dataResponse
     */
    public static void settingAmbientTemperatureAndHumidity(boolean on_off, int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        tDataBytes[1] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingAmbientTemperatureAndHumidity, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 日程开关设置
     *
     * @param on_off       日程开关
     * @param dataResponse
     */
    public static void settingScheduleSwitch(boolean on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00: 关闭 0x01: 开启
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingScheduleSwitch, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 计步状态时间设置
     *
     * @param interval     计步状态时间 分钟
     * @param dataResponse
     */
    public static void settingStepCountingStateTime(int interval, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) interval;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingStepCountingStateTime, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 上传提醒设置
     *
     * @param on_off       上传提醒开关
     * @param threshold    存储阈值
     * @param dataResponse
     */
    public static void settingUploadReminder(boolean on_off, int threshold, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (on_off ? 0x01 : 0x00);//0x00：关 0x01：开
        tDataBytes[1] = (byte) threshold;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingUploadReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 设置蓝牙广播间隔
     *
     * @param time         广播间隔时间  单位ms
     * @param dataResponse
     */
    public static void settingBluetoothBroadcastInterval(int time, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) (time & 0xff);
        tDataBytes[1] = (byte) ((time >> 8) & 0xff);
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBluetoothBroadcastInterval, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 设置蓝牙发射功率
     *
     * @param power        发射功率 >= 0 dbm
     * @param dataResponse
     */
    public static void settingBluetoothTransmittingPower(int power, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) power;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingBluetoothTransmittingPower, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 运动心率区间设置
     *
     * @param type         0	休养静歇  1	休闲热身  2	心肺强化  3	减脂塑形  4	运动极限  5	空状态
     * @param minHeart     该模式下最小心率
     * @param maxHeart     该模式下最大心率
     * @param dataResponse
     */
    public static void settingExerciseHeartRateZone(int type, int minHeart, int maxHeart, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[3];
        tDataBytes[0] = (byte) type;
        tDataBytes[1] = (byte) minHeart;
        tDataBytes[2] = (byte) maxHeart;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingExerciseHeartRateZone, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 事件提醒设置
     *
     * @param code         0x01：添加事件提醒  0x02：删除事件提醒  0x03：修改事件提醒  最多10个事件
     * @param index        时间id  1-10
     * @param on_off       0x00：关  0x01：开
     * @param type         0x00：闹钟  0x01：自定义事件
     * @param hour         响应的小时
     * @param min          响应的分钟
     * @param weekRepeat   重复
     *                     bit7  bit6 bit5 bit4 bit3 bit2 bit1 bit0
     *                     保留 周日 周六  周五 周四  周三  周二  周一
     * @param interval     间隔时间  0x00: 单次
     * 0x01: 10分钟 0x02: 20分钟 0x03: 30分钟
     * @param name         事件的名字 最大4个中文
     * @param dataResponse
     */
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

    /**
     * 事件提醒开关控制
     *
     * @param on_off       0x00：关闭   0x01:打开
     * @param dataResponse
     */
    public static void settingEventReminderSwitch(int on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) on_off;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.SettingEventReminderSwitch, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    //获取命令

    /***
     * 获取设备基本信息
     * 设备ID, 固件版本号,电池状态,电池电量,绑定状态
     * @param dataResponse
     */
    public static void getDeviceInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x47, 0x43};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取设备Log
     * @param logType 0x00当天LOG 0x01所有LOG
     * @param dataResponse
     */
    public static void getDeviceLog(int logType, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) logType};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceLog, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取设备主界面样式配置
     * @param dataResponse
     */
    public static void getThemeInfo(BleDataResponse dataResponse) {

        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetThemeInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取心电右电极位置
     * @param dataResponse
     */
    public static void getElectrodeLocationInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetElectrodeLocation, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取手环当前步数
     * @param dataResponse
     */
    public static void getNowStep(BleDataResponse dataResponse) {

        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetNowStep, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取历史记录概要信息
     * @param dataResponse
     */
    public static void getHistoryOutline(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetHistoryOutline, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取实时温度, 一次只返回一个结果
     * @param dataResponse
     */
    public static void getRealTemp(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetRealTemp, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取屏幕显示信息
     * @param dataResponse
     */
    public static void getScreenInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetScreenInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取天地五行的数据
     * @param type 0x00：天干地支 0x01：五运六气 0x02：季节
     * @param dataResponse
     */
    public static void getHeavenEarthAndFiveElement(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetHeavenEarthAndFiveElement, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取设备实时血氧
     * @param dataResponse
     */
    public static void getRealBloodOxygen(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x49, 0x53};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetRealBloodOxygen, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取当前环境光强度
     * @param dataResponse
     */
    public static void getCurrentAmbientLightIntensity(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x4A, 0x54};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentAmbientLightIntensity, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取当前环境温湿度
     * @param dataResponse
     */
    public static void getCurrentAmbientTempAndHumidity(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x4B, 0x55};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentAmbientTempAndHumidity, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取查询日程信息
     * @param dataResponse
     */
    public static void getScheduleInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetScheduleInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取传感器采样信息
     * @param type  0x00: PPG 0x01: 加速度数据 0x02：ECG 0x03：温湿度 0x04：环境光 0x05：体温
     * @param dataResponse
     */
    public static void getSensorSamplingInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetSensorSamplingInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取当前系统工作模式
     * @param dataResponse
     */
    public static void getCurrentSystemWorkingMode(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetCurrentSystemWorkingMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取保险相关信息
     * @param type 0x00: 推送保险名称 0x01: 推送健康基金数额 0x02: 推送动态保额数额 0x03: 推送次月保费数额 0x04: 推送次年保费数额 0x05：推送保险状态 0x06：设置该保险展示文案 0x07:推送保险更新日期
     * @param dataResponse
     */
    public static void getInsuranceRelatedInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetInsuranceRelatedInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取上传提醒的配置信息
     * @param dataResponse
     */
    public static void getUploadConfigurationInfoOfReminder(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetUploadConfigurationInfoOfReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取手动模式的状态
     * @param dataResponse
     */
    public static void getStatusOfManualMode(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetStatusOfManualMode, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取当前手环事件提醒信息
     * @param dataResponse
     */
    public static void getEventReminderInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x01};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetEventReminderInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取当前手环芯片方案
     * @param dataResponse 0x00：NRF52832  0x01: RTK8762C   0x02: RTK8762D
     */
    public static void getChipScheme(BleDataResponse dataResponse) {
        byte[] tDataBytes = {};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetChipScheme, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 获取手环提醒设置信息
     * @param type 0x00:蓝牙断开提醒 0x01:运动达标提醒
     * @param dataResponse 0x00:关闭 0x01:打开
     */
    public static void getDeviceRemindInfo(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) type};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.GetDeviceRemindInfo, tDataBytes, CMD.Priority_normal, dataResponse);
    }


    // APP控制命令

    /**
     * 寻找手环
     *
     * @param mode           0x01: 开始寻找手环 0x00: 结束寻找手环
     * @param remindNum      设备提醒次数
     * @param remindInterval 提醒间隔秒
     * @param dataResponse
     */
    public static void appFindDevice(int mode, int remindNum, int remindInterval, BleDataResponse dataResponse) {
        byte[] tDataBytes = {(byte) mode, (byte) remindNum, (byte) remindInterval};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppFindDevice, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 消息推送 通知手环
     *
     * @param type         0x00: 来电 0x01:短信 0x02:邮件 0x03:其它 0x04:QQ 0x05:微信 0x06:新浪微博 0x07:Twitter 0x08:Facebook 0x09:Messenger
     *                     0x0A:Whatsapp 0x0B:LinkedIn 0x0C:Instagram 0x0D:Skype 0x0E:Line 0x0F:Snapchat
     * @param title        消息的标题
     * @param content      消息的内容 消息内容多少由屏幕大小和字体大小决定
     * @param dataResponse
     */
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

    /**
     * 读取手环实时数据
     *
     * @param type         0x00: 关闭 0x01:开启
     * @param dataResponse
     */
    public static void appRealSportFromDevice(int type, final BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[]{(byte) type, 0x00, 0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppControlReal, CMD.Group.Group_REAL_SPORT, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 开始运动
     * @param sportType 类型选择 0x00: 预留 0x01: 跑步 0x02: 游泳 0x03: 骑行 0x0C: 乒乓球
     * 0x04: 健身 0x08: 健走 0x05: 预留 0x09: 羽毛球 0x06: 跳绳 0x0A: 足球 0x07: 篮球 0x0B: 登山
     * @param dataResponse
     */
    public static void appRunModeStart(int sportType, BleDataResponse dataResponse, BleRealDataResponse realDataResponse) {
        //开启 1. 打开运动模式  2. 打开心率实时数据上传
        //关闭 1. 关闭运动模式  2. 关闭心率实时数据上传
        byte[] tDataBytes = {(byte) 0x01, (byte) sportType};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppRunMode, CMD.Group.Group_StartSport, tDataBytes, CMD.Priority_normal, dataResponse);
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }

    /***
     * 注册监听实时数据
     */
    public static void appRegisterRealDataCallBack(BleRealDataResponse realDataResponse) {
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }

    /***
     * 监听设备发送数据到App
     */
    public static void deviceToApp(BleDeviceToAppDataResponse bleRealTypeResponse) {
        YCBTClientImpl.getInstance().registerRealTypeCallBack(bleRealTypeResponse);
    }

    /**
     * 结束运动
     *
     * @param sportType    运动类型,见开始运动
     * @param dataResponse
     */
    public static void appRunModeEnd(int sportType, BleDataResponse dataResponse) {
        //开启 1. 打开运动模式 2. 打开心率测试开关.  3. 打开心率实时数据上传
        //关闭 1. 关闭运动模式 2. 关闭心率测试开关   3. 关闭心率实时数据上传
        byte[] tDataBytes = {(byte) 0x00, (byte) sportType};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppRunMode, CMD.Group.Group_EndSport, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /****
     * 开始ECG实时测试
     * @param dataResponse
     * @param realDataResponse
     */
    public static void appEcgTestStart(BleDataResponse dataResponse, BleRealDataResponse realDataResponse) {
        //开启 1. 打开血压测试开关  2. 打开波形上传
        //关闭 1. 关闭血压测试开关  2. 关闭波形上传
        byte[] tDataBytes = {(byte) 0x02};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppBloodSwitch, CMD.Group.Group_StartEcgTest, tDataBytes, CMD.Priority_normal, dataResponse);
        YCBTClientImpl.getInstance().registerRealDataCallBack(realDataResponse);
    }

    /***
     * 结束ECG实时测试
     * @param dataResponse
     */
    public static void appEcgTestEnd(BleDataResponse dataResponse) {
        //开启 1. 打开血压测试开关  2. 打开波形上传
        //关闭 1. 关闭血压测试开关  2. 关闭波形上传
        byte[] tDataBytes = {(byte) 0x00};
        YCBTClientImpl.getInstance().sendDataType2Device(Constants.DATATYPE.AppBloodSwitch, CMD.Group.Group_EndEcgTest, tDataBytes, CMD.Priority_high, dataResponse);
    }

    /***
     * 健康参数、预警信息发送
     * @param warnState 预警状态 0x00: 无预警 0x01: 预警生效中
     * @param healthState 健康状态 0x00:未知 0x01:优秀 0x02:良好 0x03:一般 0x04:较差 0x05:生病
     * @param healthIndex 健康指数 0~120
     * @param friendWarn 亲友预警 0x00:无预警 0x01:预警生效 中
     * @param dataResponse
     */
    public static void appHealthArg(int warnState, int healthState, int healthIndex, int friendWarn, BleDataResponse dataResponse) {

        byte[] tDataBytes = new byte[14];
        tDataBytes[0] = (byte) warnState;
        tDataBytes[1] = (byte) healthState;
        tDataBytes[2] = (byte) healthIndex;
        tDataBytes[3] = (byte) friendWarn;

        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppHealthArg, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * 今日天气信息数据传送
     * @param lowTemp 最底温
     * @param highTemp 最高温
     * @param curTemp 当前温度
     * @param type 天气类型 sunny 1 cloudy  2 wind  3 rain  4 Snow  5 foggy 6 unknown 0
     * @param dataResponse
     */
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

    /***
     * 今日天气信息数据传送
     * @param lowTemp 最底温
     * @param highTemp 最高温
     * @param curTemp 当前温度
     * @param type 天气类型 sunny 1 cloudy  2 wind  3 rain  4 Snow  5 foggy 6 unknown 0
     *          0-6为公司保留        公司保留
     * 7        晴天
     * 8        多云
     * 9        雷阵雨
     * 10        小雨
     * 11        中雨
     * 12        大雨
     * 13        雨雪
     * 14        小雪
     * 15        中雪
     * 16        大雪
     * 17        浮沉
     * 18        雾
     * 19        霾
     * 20        风
     * 21        未知
     * @param windDirection 风向
     * @param windPower 风力
     * @param currentGeographicLocation 当前所处的地理位置
     * @param lunarPhaseInfo 天系列月相信息 编号        月相类型
     * 0        新月
     * 1        残月
     * 2        下弦月
     * 3        下凸月
     * 4        满月
     * 5        上凸月
     * 6        上弦月
     * 7        娥眉月
     * 8        未知
     * @param dataResponse
     */
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

    /**
     * 关机、复位、进入运输模式控制
     *
     * @param type         0x01: 关机 0x02: 进入运输模式 0x03: 系统复位重启
     * @param dataResponse 0x00: 设置成功 0x01: 设置失败-参数错误
     */
    public static void appShutDown(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppShutDown, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 表情包显示
     *
     * @param index        表情包的下标 0-4
     * @param hour         时
     * @param min          分
     * @param name         亲友昵称
     * @param dataResponse 0x00: 手环显示震动成功 0x01: 手环未佩戴 0x02：其他错误
     */
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

    /*
     * 截取指定长度
     * @param msg 截取的字符串
     * @param len 截取长度
     * */
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

    /**
     * 关APP 健康值回写到手环
     *
     * @param healthValue  健康值
     * @param healthState  健康状态
     * @param dataResponse
     */
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

    /**
     * APP 睡眠数据回写到手环
     *
     * @param deepSleepTimeHour  深睡(小时)
     * @param deepSleepTimeMin   深睡(分钟)
     * @param lightSleepTimeHour 浅睡(小时)
     * @param lightSleepTimeMin  浅睡(分钟)
     * @param totalSleepTimeHour 总睡眠(小时)
     * @param totalSleepTimeMin  总睡眠(分钟)
     * @param dataResponse       0x00: 手环接收成功 0x01: 设置失败-参数错误
     */
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

    /**
     * APP 用户个人信息回写到手环
     *
     * @param type         0x00: 用户保险类信息，其后内容为字符串，UTF8 编码 0x01: 用户会员状态信息，其后内容为字符串，UTF8 编码
     * @param content      信息内容
     * @param dataResponse 0x00: 用户保险类信息，其后内容为字符串，UTF8 编码 0x01: 用户会员状态信息，其后内容为字符串，UTF8 编码
     */
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

    /**
     * 升级提醒
     *
     * @param on_off       0x00: 关闭提醒 0x01: 打开提醒
     * @param percent      百分比
     * @param dataResponse 0x00: 手环接收成功 0x01: 设置失败-参数错误
     */
    public static void appUpgradeReminder(int on_off, int percent, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) on_off;
        tDataBytes[1] = (byte) percent;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppUpgradeReminder, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 环境光测量控制
     *
     * @param type         0x00: 关闭 0x01: 单次测试 0x02: 监测模式
     * @param dataResponse 0x00: 设置成功 0x01: 设置失败-参数错误
     */
    public static void appAmbientLightMeasurementControl(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppAmbientLightMeasurementControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 环境温湿度测量控制
     *
     * @param type         0x00: 关闭 0x01: 单次测试 0x02: 监测模式
     * @param dataResponse
     */
    public static void appAmbientTempHumidityMeasurementControl(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppAmbientTempHumidityMeasurementControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 保险消息推送
     *
     * @param type         0x00: 推送保险名称 0x01: 推送健康基金数额 0x02: 推送动态保额数额 0x03: 推送次月保费数额
     *                     0x04: 推送次年保费数额 0x05：推送保险状态 0x06：设置该保险展示文案 0x07:推送保险更新日期
     * @param content      保险名称为 4-6 个中文 如：999,四个字节整数 如：999,四个字节整数 如：999,四个字节整数
     *                     如：999,四个字节整数 0x01：上涨 0x00：下跌 ，一个字节 如：0x02 展示第二个文案，一个字节 四个字节时间戳
     * @param dataResponse 第一个字节: 0x00: 推送保险名称 0x01: 推送健康基金数额 0x02: 推送动态保额数额 0x03: 推送次月保费数额 0x04: 推送次年保费数额
     *                     0x05：推送保险状态 0x06：设置该保险展示文案 0x07:推送保险更新日期
     *                     第二个字节  0x00：推送成功  0x01：参数错误
     */
    public static void appInsuranceNews(int type, String content, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppInsuranceNews, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 传感器数据存储开关控制
     *
     * @param type         0x00: PPG 0x01: 加速度数据 0x02：ECG 0x03：温湿度 0x04：环境光 0x05：体温
     * @param on_off       0x00：关闭 0x01：打开
     * @param dataResponse
     */
    public static void appSensorSwitchControl(int type, int on_off, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) type;
        tDataBytes[1] = (byte) on_off;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppSensorSwitchControl, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 发送当前手机型号
     *
     * @param model        手机型号
     * @param dataResponse 0x00: 推送成功 0x01: 推送失败-参数错误
     */
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

    /**
     * APP 有效步数回写
     *
     * @param step         有效步数
     * @param type         运动类型 0x00:休养静歇 0x01:休闲热身 0x02:心肺强化 0x03:减脂塑性 0x04:运动极限 0x05:空状态
     * @param dataResponse 0x00: 手环同步成功 0x01：手环同步失败
     */
    public static void appEffectiveStep(int step, int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[5];
        tDataBytes[0] = (byte) (step & 0xff);
        tDataBytes[1] = (byte) (step >> 8 & 0xff);
        tDataBytes[2] = (byte) (step >> 16 & 0xff);
        tDataBytes[3] = (byte) (step >> 24 & 0xff);
        tDataBytes[4] = (byte) type;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEffectiveStep, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * APP 计算心率同步
     *
     * @param heart        有效心率
     * @param dataResponse 0x00: 手环同步成功 0x01：手环同步失败
     */
    public static void appEffectiveHeart(int heart, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) heart;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppEffectiveHeart, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * APP 预警推送
     *
     * @param type         类型 0x00:预警自己  0x01:预警他人 0x02:运动高风险 0x03:运动非高风险
     * @param content      预警内容
     * @param dataResponse 0x00: 手环同步成功 0x01：手环同步失败
     */
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

    /**
     * APP 信息推送
     *
     * @param type         类型    0x00	有新的周报生成，请到APP上查看。
     *                     0x01	有新的月报生成，请到APP上查看。
     *                     0x02	收到亲友信息，请到APP上查看。
     *                     0x03	很久没测量了，测量一下吧。
     *                     0x04	您已成功预约咨询。
     *                     0x05	您预约的咨询，将在一小时后开始。
     *                     0x06	自定义内容
     * @param message      信息内容  类型为0x06时,才有信息内容
     * @param dataResponse 0x00: 手环同步成功 0x01：手环同步失败
     */
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

    /**
     * 温度校准
     *
     * @param tempInt      温度整数部分(-127 - 127)
     * @param tempFloat    温度小数部分(0-99)
     * @param dataResponse
     */
    public static void appTemperatureCorrect(int tempInt, int tempFloat, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[2];
        tDataBytes[0] = (byte) tempInt;
        tDataBytes[1] = (byte) tempFloat;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureCorrect, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 温度测量控制
     *
     * @param type         温度监测开关 0x00: 关闭  0x01: 单次测试（一般用于腋测模式）0x02: 监测模式
     * @param dataResponse
     */
    public static void appTemperatureMeasure(int type, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) type;//暂时只有单次测试
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureMeasure, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /**
     * 体温红绿码控制
     *
     * @param code         温度颜色 0x00: 绿色  0x01: 红色  0x02: 橙色
     * @param dataResponse
     */
    public static void appTemperatureCode(int code, BleDataResponse dataResponse) {
        byte[] tDataBytes = new byte[1];
        tDataBytes[0] = (byte) code;
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.AppTemperatureCode, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    //UI升级相关API

    /***
     * APP 查询已传输 UI 文件断点信息
     * @param dataResponse
     */
    public static void otaUIGetBreakInfo(BleDataResponse dataResponse) {
        byte[] tDataBytes = {0x00};
        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_GetFileBreak, tDataBytes, CMD.Priority_normal, dataResponse);
    }

    /***
     * APP 发送待传输文件信息
     * @param dataResponse
     */

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


    /***
     * APP 发送 N 包（1 Block）数据包
     * 建议 N=6,前 5 包发送 176 字节，第 6 包发送 144 字节
     * @param blockData
     * @param dataResponse
     */
    public static void otaUIBlock(byte[] blockData, BleDataResponse dataResponse) {

        YCBTClientImpl.getInstance().sendSingleData2Device(Constants.DATATYPE.OtaUI_SyncBlock, blockData, CMD.Priority_normal, dataResponse);
    }

    /***
     * APP 发送 Block 校验信息
     *
     * @param dataResponse
     */
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
