package com.yucheng.ycbtsdk.Protocol;

import android.util.Log;

import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.Utils.YCBTLog;
import com.yucheng.ycbtsdk.YCBTClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

public class DataUnpack {

    public static HashMap unpackUIFileBreakInfo(byte[] databytes) {
        int tOffset = 0;
        int devFileTotalLen = 0;
        int devFileOffset = 0;
        int devFileCheckSum = 0;

        HashMap tRetMap = new HashMap();

        if (databytes.length > 8) {
            devFileTotalLen = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8) + ((databytes[tOffset++] & 0xff) << 16) + ((databytes[tOffset++] & 0xff) << 24);
            devFileOffset = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8) + ((databytes[tOffset++] & 0xff) << 16) + ((databytes[tOffset++] & 0xff) << 24);
            devFileCheckSum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);

            YCBTLog.e("总长度 " + devFileTotalLen + " 已升级偏移量 " + devFileOffset + " 检验码 " + devFileCheckSum);

            tRetMap.put("code", Constants.CODE.Code_OK);
            tRetMap.put("dataType", Constants.DATATYPE.OtaUI_GetFileBreak);
            tRetMap.put("uiFileTotalLen", devFileTotalLen);
            tRetMap.put("uiFileOffset", devFileOffset);
            tRetMap.put("uiFileCheckSum", devFileCheckSum);
        } else {
            tRetMap.put("code", Constants.CODE.Code_Failed);
        }

        return tRetMap;
    }


    public static HashMap unpackAppEcgPpgStatus(byte[] databytes) {
        int tOffset = 0;

        int tEcgStatus = (databytes[tOffset++] & 0xff);
        int tPPGStatus = (databytes[tOffset++] & 0xff);

        YCBTLog.e("心电电极状态: " + tEcgStatus + "光电传感器状态: " + tPPGStatus);

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.AppECGPPGStatus);
        tRetMap.put("EcgStatus", tEcgStatus);
        tRetMap.put("PPGStatus", tPPGStatus);

        return tRetMap;
    }

    public static HashMap unpackCollectSummaryInfo(byte[] databytes) {
        int tOffset = 0;
        int millisFromGMT = TimeZone.getDefault().getOffset(System.currentTimeMillis());

        int tCollectType = (databytes[tOffset++] & 0xff);
        int tCollectSN = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
        long tStartTime = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8) + ((databytes[tOffset++] & 0xff) << 16) + ((databytes[tOffset++] & 0xff) << 24);
        long tStartTime2 = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
//        tOffset += 3;
        tOffset += 2;
        int tCollectDigits = databytes[tOffset++] & 0xff;
        int tDataTotalLen = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8) + ((databytes[tOffset++] & 0xff) << 16) + ((databytes[tOffset++] & 0xff) << 24);
        int tBlockNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);

        YCBTLog.e("SN " + tCollectSN + " tStartTime " + tStartTime + " realTime " + tStartTime2 + " tDataTotalLen " + tDataTotalLen);

        HashMap tRetMap = new HashMap();
        tRetMap.put("collectType", tCollectType);
        tRetMap.put("collectSN", tCollectSN);
        tRetMap.put("collectSendTime", tStartTime);
        tRetMap.put("collectStartTime", tStartTime2 - millisFromGMT);
        tRetMap.put("collectTotalLen", tDataTotalLen);
        tRetMap.put("collectBlockNum", tBlockNum);
        tRetMap.put("collectDigits", tCollectDigits);
        return tRetMap;
    }

    public static HashMap unpackRealSportData(byte[] databytes) {
        int tOffset = 0;
        int tStep = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
        int tDis = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
        int tCal = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);

        Log.e("yc-ble","实时步数 " + tStep + " Dis " + tDis + " Cal " + tCal);

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.Real_UploadSport);
        tRetMap.put("sportStep", tStep);
        tRetMap.put("sportCalorie", tCal);
        tRetMap.put("sportDistance", tDis);

        return tRetMap;
    }

    public static HashMap unpackRealHeartData(byte[] databytes) {
        int tOffset = 0;
        int tHeartNum = (databytes[tOffset++] & 0xff);

        Log.e("yc-ble","实时心率 " + tHeartNum);

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.Real_UploadHeart);
        tRetMap.put("heartValue", tHeartNum);

        return tRetMap;
    }

    public static HashMap unpackRealBloodData(byte[] databytes) {
        int tOffset = 0;
        if (databytes.length < 3)
            return null;
        int tDBP = (databytes[tOffset++] & 0xff);
        int tSBP = (databytes[tOffset++] & 0xff);
        int tHeartNum = (databytes[tOffset++] & 0xff);

        Log.e("yc-ble","实时血压 DBP " + tDBP + " SBP " + tSBP + " Heart " + tHeartNum);

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.Real_UploadBlood);
        tRetMap.put("heartValue", tHeartNum);
        tRetMap.put("bloodDBP", tDBP);
        tRetMap.put("bloodSBP", tSBP);
        if (databytes.length > 3) {
            int hrv = (databytes[tOffset++] & 0xff);
            tRetMap.put("hrv", hrv);
        }
        if (databytes.length > 4) {
            int bloodOxygen = (databytes[tOffset++] & 0xff);
            tRetMap.put("bloodOxygen", bloodOxygen);
        }
        if (databytes.length > 6) {
            int tempInteger = (databytes[tOffset++] & 0xff);
            int tempFloat = (databytes[tOffset++] & 0xff);
            tRetMap.put("tempInteger", tempInteger);
            tRetMap.put("tempFloat", tempFloat);
        }
        return tRetMap;
    }

    public static HashMap unpackRealPPGData(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.Real_UploadPPG);
        tRetMap.put("data", databytes);
        return tRetMap;
    }

    public static HashMap unpackRealECGData(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.Real_UploadECG);
        tRetMap.put("data", databytes);//松果项目需要原始数据,调用此方法
//        tRetMap.put("data", AITools.getInstance().ecgRealWaveFiltering(databytes));
        return tRetMap;
    }

    public static HashMap unpackGetHistoryOutline(byte[] databytes) {
        int tOffset = 0;

        int tSleepNum = 0;
        int tSleepTotalTimeMin = 0;
        int tHeartNum = 0;
        int tSportNum = 0;
        int tBloodNum = 0;
        int tBloodOxygenNum = 0;
        int tTempHumidNum = 0;
        int tTempNum = 0;
        int tAmbientLightNum = 0;

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);

        if (databytes.length > 8) {
            tSleepNum = (databytes[tOffset++] & 0xff);
            tSleepTotalTimeMin = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            tHeartNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            tSportNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            tBloodNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            if (databytes.length > 16) {//血氧条数 环境温湿度条数 体温条数 环境光条数 预留
                tBloodOxygenNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
                tTempHumidNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
                tTempNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
                tAmbientLightNum = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            }
            tRetMap.put("supportOk", 1);
        } else {
            tRetMap.put("supportOk", 0);
        }
        tRetMap.put("SleepNum", tSleepNum);
        tRetMap.put("SleepTotalTime", tSleepTotalTimeMin);
        tRetMap.put("HeartNum", tHeartNum);
        tRetMap.put("SportNum", tSportNum);
        tRetMap.put("BloodNum", tBloodNum);
        tRetMap.put("BloodOxygenNum", tBloodOxygenNum);
        tRetMap.put("TempHumidNum", tTempHumidNum);
        tRetMap.put("TempNum", tTempNum);
        tRetMap.put("AmbientLightNum", tAmbientLightNum);
        tRetMap.put("dataType", Constants.DATATYPE.GetHistoryOutline);
        return tRetMap;
    }

    public static HashMap unpackGetRealTemp(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length > 1)
            tRetMap.put("tempValue", (databytes[0] & 0xff) + "." + (databytes[1] & 0xff));
        tRetMap.put("dataType", Constants.DATATYPE.GetRealTemp);
        return tRetMap;
    }

    //获取屏幕显示信息
    public static HashMap unpackGetScreenInfo(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 4) {
            tRetMap.put("currentScreenDisplayLevel", databytes[0] & 0xff);//当前屏幕显示等级
            tRetMap.put("currentScreenOffTime", databytes[1] & 0xff);//当前屏幕息屏时间
            tRetMap.put("currentLanguageSettings", databytes[2] & 0xff);//当前语言设置
            tRetMap.put("CurrentWorkingMode", databytes[3] & 0xff);//当前工作模式
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetScreenInfo);
        return tRetMap;
    }

    //获取天地五行的数据
    public static HashMap unpackGetHeavenEarthAndFiveElement(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1) {
            try {
                tRetMap.put("data", new String(databytes, "utf-8"));//UTF8 编码的字符串
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetHeavenEarthAndFiveElement);
        return tRetMap;
    }

    //获取设备实时血氧
    public static HashMap unpackGetRealBloodOxygen(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 2) {
            tRetMap.put("bloodOxygenIsTest", databytes[0] & 0xff);//0x00: 未测心氧 0x01: 正在测试心氧
            tRetMap.put("bloodOxygenValue", databytes[1] & 0xff);//血氧值 0-100
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetRealBloodOxygen);
        return tRetMap;
    }

    //获取当前环境光强度
    public static HashMap unpackGetCurrentAmbientLightIntensity(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 3) {
            tRetMap.put("ambientLightIntensityIsTest", databytes[0] & 0xff);//0x00: 未测环境光 0x01: 正在测试环境光强度
            tRetMap.put("ambientLightIntensityValue", (databytes[1] & 0xff) + ((databytes[2] & 0xff) << 8));//环境光值 0-65535
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetCurrentAmbientLightIntensity);
        return tRetMap;
    }

    ////获取当前环境温湿度
    public static HashMap unpackGetCurrentAmbientTempAndHumidity(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 5) {
            tRetMap.put("ambientTempAndHumidityIsTest", databytes[0] & 0xff);//0x00: 未测环境温湿度 0x01: 正在测试环境温湿度
            tRetMap.put("ambientTempValue", (databytes[1] & 0xff) + "." + (databytes[2] & 0xff));//温度整数 温度小数
            tRetMap.put("ambientHumidityValue", (databytes[3] & 0xff) + "." + (databytes[4] & 0xff));//湿度整数 湿度小数
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetCurrentAmbientTempAndHumidity);
        return tRetMap;
    }

    //获取查询日程信息
    public static HashMap unpackGetScheduleInfo(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 9) {
            tRetMap.put("scheduleIndex", databytes[0] & 0xff);//日程索引
            tRetMap.put("scheduleEnable", databytes[1] & 0xff);//日程使能
            tRetMap.put("incidentIndex", databytes[2] & 0xff);//事件索引
            tRetMap.put("incidentEnable", databytes[3] & 0xff);//事件使能
            tRetMap.put("incidentTime", ((databytes[4] & 0xff) + ((databytes[5] & 0xff) << 8) + ((databytes[6] & 0xff) << 16) + ((databytes[7] & 0xff) << 24) + YCBTClient.SecFrom30Year) * 1000l - TimeZone.getDefault().getOffset(System.currentTimeMillis()));//事件时间戳
            tRetMap.put("incidentID", (databytes[8] & 0xff));//事件 ID
            if (databytes.length > 9) {
                byte[] datas = new byte[databytes.length - 9];
                System.arraycopy(databytes, 9, datas, 0, databytes.length - 9);
                try {
                    tRetMap.put("incidentName", new String(datas, "utf-8"));//事件类型名称
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tRetMap.put("incidentName", "");//事件类型名称
            }
        }
        return tRetMap;
    }

    //事件提醒数据上传
    public static HashMap unpackGetEventReminder(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 7) {
            tRetMap.put("eventReminderIndex", databytes[0] & 0xff);//事件下标
            tRetMap.put("eventReminderSwitch", databytes[1] & 0xff);//开关  0x00：关   0x01：开
            tRetMap.put("eventReminderType", databytes[2] & 0xff);//事件类型  0x00：闹钟    0x01：自定义事件
            tRetMap.put("eventReminderHour", databytes[3] & 0xff);//时间   小时
            tRetMap.put("eventReminderMin", databytes[4] & 0xff);//时间   分钟
            tRetMap.put("eventReminderRepeat", databytes[5] & 0xff);//重复 周 一 到 周 天 是 否 需 要重复，参 考闹钟
            tRetMap.put("eventReminderInterval", databytes[6] & 0xff);//间隔时长 0x00:单次  0x01:10min  0x02:20min  0x03:30min
            if ((databytes[2] & 0xff) == 1 && databytes.length > 7) {
                byte[] bytes = new byte[databytes.length - 7];
                System.arraycopy(databytes, 7, bytes, 0, databytes.length - 7);
                try {
                    tRetMap.put("incidentName", new String(bytes, "utf-8"));//自定义事件数据 最大支持 4 个 中文字符 (N ≤ 12)
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tRetMap.put("incidentName", "");//事件类型名称
            }
        }
        return tRetMap;
    }

    //获取传感器采样信息
    public static HashMap unpackGetSensorSamplingInfo(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 5) {
            tRetMap.put("sensorSamplingInfoState", databytes[0] & 0xff);//0x00: 关闭 0x01: 开启
            tRetMap.put("sensorSamplingInfoDuration", (databytes[1] & 0xff) + ((databytes[2] & 0xff) << 8));//单次采集时长 2 bytes 单位：s
            tRetMap.put("sensorSamplingInfoInterval", (databytes[3] & 0xff) + ((databytes[4] & 0xff) << 8));//采集间隔 2bytes 单位：min
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetSensorSamplingInfo);
        return tRetMap;
    }

    //获取当前系统工作模式
    public static HashMap unpackGetCurrentSystemWorkingMode(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1) {
            //0x00：正常工作模式 0x01: 关怀工作模式 0x02：省电工作模式 0x03: 自定义工作模式
            tRetMap.put("currentSystemWorkingMode", databytes[0] & 0xff);
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetCurrentSystemWorkingMode);
        return tRetMap;
    }

    //获取保险相关信息
    public static HashMap unpackGetInsuranceRelatedInfo(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 2) {
            switch (databytes[0] & 0xff) {
                case 0x00://推送保险名称
                    byte[] datas = new byte[databytes.length - 1];
                    System.arraycopy(databytes, 1, datas, 0, databytes.length - 1);
                    try {
                        tRetMap.put("data", new String(datas, "utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x01://推送健康基金数额
                case 0x02://推送动态保额数额
                case 0x03://推送次月保费数额
                case 0x04://推送次年保费数额
                    if (databytes.length >= 5)
                        tRetMap.put("data", (databytes[1] & 0xff) + ((databytes[2] & 0xff) << 8) + ((databytes[3] & 0xff) << 16) + ((databytes[4] & 0xff) << 24));
                    break;
                case 0x05://推送保险状态 0x01：上涨 0x00：下跌
                case 0x06://设置该保险展示文案 0x02 展示第二个文案
                    tRetMap.put("data", databytes[1] & 0xff);
                    break;
                case 0x07://推送保险更新日期
                    tRetMap.put("data", ((databytes[1] & 0xff) + ((databytes[2] & 0xff) << 8) + ((databytes[3] & 0xff) << 16) + ((databytes[4] & 0xff) << 24) + YCBTClient.SecFrom30Year) * 1000l);
                    break;
            }
            tRetMap.put("type", databytes[0] & 0xff);
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetInsuranceRelatedInfo);
        return tRetMap;
    }

    //获取上传提醒的配置信息
    public static HashMap unpackGetUploadConfigurationInfoOfReminder(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 2) {
            tRetMap.put("UploadConfigurationInfoOfReminderEnable", databytes[0] & 0xff);//0x00：关 0x01：开
            tRetMap.put("UploadConfigurationInfoOfReminderValue", databytes[1] & 0xff);//存储阈值范围 0-100
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetUploadConfigurationInfoOfReminder);
        return tRetMap;
    }

    //获取手动模式的状态/
    public static HashMap unpackGetStatusOfManualMode(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1)
            tRetMap.put("statusOfManualMode", databytes[0] & 0xff);//0x00：饮酒模式开启 0x01: 运动模式开启
        tRetMap.put("dataType", Constants.DATATYPE.GetStatusOfManualMode);
        return tRetMap;
    }

    //获取手动模式的状态/
    public static HashMap unpackGetChipScheme(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1)
            tRetMap.put("chipScheme", databytes[0] & 0xff);//0x00：NRF52832  0x01: RTK8762C   0x02: RTK8762D
        tRetMap.put("dataType", Constants.DATATYPE.GetChipScheme);
        return tRetMap;
    }


    //获取手环提醒设置信息
    public static HashMap unpackGetDeviceRemindInfo(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1)
            tRetMap.put("deviceRemindInfo", databytes[0] & 0xff);//0x00:关闭 0x01:打开
        tRetMap.put("dataType", Constants.DATATYPE.GetDeviceRemindInfo);
        return tRetMap;
    }


    public static HashMap unpackGetNowSport(byte[] databytes) {
        int tOffset = 0;

        int tStep = 0;
        int tCal = 0;
        int tDis = 0;

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);

        if (databytes.length > 6) {
            tStep = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8) + ((databytes[tOffset++] & 0xff) << 16);
            tCal = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            tDis = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);

            YCBTLog.e("tStep " + tStep + " tCal " + tCal + " tDis " + tDis);

            tRetMap.put("supportOk", 1);
        } else {
            tRetMap.put("supportOk", 0);
        }
        tRetMap.put("nowStep", tStep);
        tRetMap.put("nowCalorie", tCal);
        tRetMap.put("nowDistance", tDis);
        tRetMap.put("dataType", Constants.DATATYPE.GetNowStep);

        return tRetMap;
    }

    public static HashMap unpackEcgLocation(byte[] databytes) {
        int tOffset = 0;

        int tEcgLocation = 0;

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);

        if (databytes.length > 0) {
            tEcgLocation = (databytes[tOffset++] & 0xff);
        }
        tRetMap.put("ecgLocation", tEcgLocation);
        tRetMap.put("dataType", Constants.DATATYPE.GetElectrodeLocation);

        return tRetMap;
    }

    public static HashMap unpackDeviceScreenInfo(byte[] databytes) {
        int tOffset = 0;
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        if (databytes.length >= 8) {
            int screenWidth = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            int screenHeight = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            int fontWidth = (databytes[tOffset++] & 0xff) + ((databytes[tOffset++] & 0xff) << 8);
            int fontHeight = (databytes[tOffset++] & 0xff) + ((databytes[tOffset] & 0xff) << 8);
            int count = (int) ((screenHeight / fontHeight) * (screenWidth / fontWidth) * 0.8);
            tRetMap.put("count", count);
        }
        tRetMap.put("dataType", Constants.DATATYPE.GetDeviceScreenInfo);
        return tRetMap;
    }

    public static HashMap unpackHomeTheme(byte[] databytes) {

        int tOffset = 0;

        int tTotalIndex = 0;
        int tCurIndex = 0;

        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);

        if (databytes.length > 1) {
            tTotalIndex = (databytes[tOffset++] & 0xff);
            tCurIndex = (databytes[tOffset++] & 0xff);
        }
        tRetMap.put("themeTotal", tTotalIndex);
        tRetMap.put("themeCurrentIndex", tCurIndex);
        tRetMap.put("dataType", Constants.DATATYPE.GetThemeInfo);

        return tRetMap;
    }

    public static HashMap unpackAlarmData(byte[] alarmData) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);

        int tOffset = 0;
        int tOptType = alarmData[tOffset++];

        if (tOptType >= 1 && tOptType <= 3) { //修改,删除,添加
            int tSettingCode = alarmData[tOffset++];

            tRetMap.put("optType", tOptType);
            tRetMap.put("code", tSettingCode);

            tRetMap.put("dataType", Constants.DATATYPE.SettingAlarm);
        } else {
            int tSupportAlarmNum = alarmData[tOffset++];
            ;
            int tSettedAlarmNum = alarmData[tOffset++];
            ;

            YCBTLog.e("支持闹钟数量" + tSupportAlarmNum + "已设置闹钟数据:" + tSettedAlarmNum);
            ArrayList tSettedAlarms = new ArrayList();

            if (tSettedAlarmNum > 0) {

                int tAlarmType = 0;
                int tAlarmHour = 0;
                int tAlarmMin = 0;
                int tAlarmRepeat = 0;
                int tAlarmDelayTime = 0;

                for (int i = 0; i < tSettedAlarmNum; ++i) {
                    tAlarmType = (alarmData[tOffset++] & 0xff);
                    tAlarmHour = (alarmData[tOffset++] & 0xff);
                    tAlarmMin = (alarmData[tOffset++] & 0xff);
                    tAlarmRepeat = (alarmData[tOffset++] & 0xff);
                    tAlarmDelayTime = (alarmData[tOffset++] & 0xff);

                    HashMap tAlarmMap = new HashMap();
                    tAlarmMap.put("alarmType", tAlarmType);//闹钟类型0x00:起床0x01:睡觉0x02:锻炼0x03:吃药0x04:约会 0x05:聚会 0x06:会议 0x07:自定义
                    tAlarmMap.put("alarmHour", tAlarmHour);//小时
                    tAlarmMap.put("alarmMin", tAlarmMin);//分钟
                    tAlarmMap.put("alarmRepeat", tAlarmRepeat);//重复
                    tAlarmMap.put("alarmDelayTime", tAlarmDelayTime);//贪睡时长（单位：分钟）
                    tSettedAlarms.add(tAlarmMap);
                }
            }
            tRetMap.put("data", tSettedAlarms);
            tRetMap.put("tSupportAlarmNum", tSupportAlarmNum);//支持闹钟数量
            tRetMap.put("tSettedAlarmNum", tSettedAlarmNum);//已设置闹钟数据
            tRetMap.put("optType", tOptType);
            tRetMap.put("dataType", Constants.DATATYPE.SettingAlarm);
        }

        return tRetMap;
    }


    public static HashMap unpackDeviceInfoData(byte[] dataBytes) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);


        int tOffset = 0;
        int tDeviceId = (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8);
        int tVersionL = dataBytes[tOffset++];
        int tVersionH = dataBytes[tOffset++];
        int tBatteryState = dataBytes[tOffset++];
        int tBatteryNum = dataBytes[tOffset++];
        YCBTLog.e("设备ID " + tDeviceId + " 版本号 " + tVersionH + "." + tVersionL + " 电量 " + tBatteryNum);

        HashMap tDataMap = new HashMap();
        tDataMap.put("deviceId", tDeviceId);
        tDataMap.put("deviceVersion", tVersionH + "." + tVersionL);
        tDataMap.put("deviceBatteryState", tBatteryState);
        tDataMap.put("deviceBatteryValue", tBatteryNum);

        tRetMap.put("dataType", Constants.DATATYPE.GetDeviceInfo);
        tRetMap.put("data", tDataMap);

        return tRetMap;
    }


    /**
     * 处理运动健康数据
     *
     * @param healthData
     * @param healthType
     */
    public static HashMap unpackHealthData(byte[] healthData, int healthType) {

        int tOffset = 0;
        int millisFromGMT = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        switch (healthType) {

            case CMD.KEY_Health.HistorySport: {
                long tStartTime = 0;
                long tEndTime = 0;
                int tStep = 0;
                int tCal = 0;
                int tDis = 0;
                ArrayList tRetStepArr = new ArrayList();
                while (tOffset + 13 < healthData.length) {
                    tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    tEndTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tEndTime = (tEndTime + YCBTClient.SecFrom30Year) * 1000;
                    tStep = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tDis = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tCal = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);

                    YCBTLog.e("开始时间 " + (tStartTime - millisFromGMT) + " 步数 " + tStep + " 卡路里 " + tCal + " 距离 " + tDis);

                    HashMap tStepMap = new HashMap();
                    tStepMap.put("sportStartTime", tStartTime - millisFromGMT);
                    tStepMap.put("sportEndTime", tEndTime - millisFromGMT);
                    tStepMap.put("sportStep", tStep);
                    tStepMap.put("sportCalorie", tCal);
                    tStepMap.put("sportDistance", tDis);
                    tRetStepArr.add(tStepMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistorySport);
                tRetMap.put("data", tRetStepArr);
                break;
            }
            case CMD.KEY_Health.HistorySleep: {
                int tSleepHead = 0;
                int tAllLen = 0;
                long tStartTime = 0;
                long tEndTime = 0;
                int tDeepSleepNum = 0;
                int tLightSleepNum = 0;
                int tDeepSleepTotalMin = 0;
                int tLightSleepTotalMin = 0;
                int tSleepType = 0;
                long tSleepStartTime = 0;
                int tSleepLenSec = 0;

                ArrayList tAllSleepArr = new ArrayList();

                while (tOffset + 20 <= healthData.length) {

                    tSleepHead = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tAllLen = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    tEndTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tEndTime = (tEndTime + YCBTClient.SecFrom30Year) * 1000;
                    tDeepSleepNum = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tLightSleepNum = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tDeepSleepTotalMin = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    tLightSleepTotalMin = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);


                    ArrayList tSleepArr = new ArrayList();

                    int offsetSleep = tOffset;
                    while (tOffset - offsetSleep + 8 <= tAllLen - 20) {

                        tSleepType = (healthData[tOffset++] & 0xff);
                        tSleepStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                        tSleepStartTime = (tSleepStartTime + YCBTClient.SecFrom30Year) * 1000;
                        tSleepLenSec = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16);

                        HashMap tSleepTypeMap = new HashMap();
                        tSleepTypeMap.put("sleepType", tSleepType);//0xF1:深睡  0xF2:浅睡
                        tSleepTypeMap.put("sleepStartTime", tSleepStartTime - millisFromGMT);//开始时间戳
                        tSleepTypeMap.put("sleepLen", tSleepLenSec);//睡眠时长  单位秒

                        tSleepArr.add(tSleepTypeMap);
                    }

                    HashMap tASleepMap = new HashMap();
                    tASleepMap.put("startTime", tStartTime - millisFromGMT);//睡眠开始时间
                    tASleepMap.put("endTime", tEndTime - millisFromGMT);//睡眠结束时间
                    tASleepMap.put("deepSleepCount", tDeepSleepNum);//深睡次数
                    tASleepMap.put("lightSleepCount", tLightSleepNum);//浅睡次数
                    tASleepMap.put("deepSleepTotal", tDeepSleepTotalMin);//深睡总时长 单位分钟
                    tASleepMap.put("lightSleepTotal", tLightSleepTotalMin);//浅睡总时长  单位分钟
                    tASleepMap.put("sleepData", tSleepArr);
                    tAllSleepArr.add(tASleepMap);


                    tRetMap.put("dataType", Constants.DATATYPE.Health_HistorySleep);
                    tRetMap.put("data", tAllSleepArr);

                }

                break;
            }
            case CMD.KEY_Health.HistoryHeart: {

                long tStartTime = 0;
                int tHeartNum = 0;

                ArrayList tRetHeartArr = new ArrayList();


                while (tOffset + 6 <= healthData.length) {

                    tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;

                    tOffset += 1;

                    tHeartNum = (healthData[tOffset++] & 0xff);

                    HashMap tHeartMap = new HashMap();
                    tHeartMap.put("heartStartTime", tStartTime - millisFromGMT);
                    tHeartMap.put("heartValue", tHeartNum);

                    tRetHeartArr.add(tHeartMap);
                }

                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryHeart);
                tRetMap.put("data", tRetHeartArr);


                break;
            }
            case CMD.KEY_Health.HistoryBlood: {

                long tStartTime = 0;
                int tDBP = 0;  //收缩压
                int tSBP = 0;  //舒张压

                ArrayList tRetBloodArr = new ArrayList();

                while (tOffset + 8 <= healthData.length) {

                    tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;


                    tOffset += 1;

                    tDBP = (healthData[tOffset++] & 0xff);
                    tSBP = (healthData[tOffset++] & 0xff);

                    tOffset += 1;

                    HashMap tBloodMap = new HashMap();
                    tBloodMap.put("bloodStartTime", tStartTime - millisFromGMT);
                    tBloodMap.put("bloodDBP", tDBP);
                    tBloodMap.put("bloodSBP", tSBP);

                    tRetBloodArr.add(tBloodMap);
                }

                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryBlood);
                tRetMap.put("data", tRetBloodArr);


                break;
            }
            case CMD.KEY_Health.HistoryAll: {

                long tStartTime = 0;
                int tStep = 0;
                int tHeartNum = 0;
                int tDBP = 0;  //收缩压
                int tSBP = 0;  //舒张压
                int tOO = 0;
                int tHuXiRate = 0;
                int tHrv = 0;
                int tCVRR = 0;
                int tTempInt = 0;
                int tTempFloat = 0;


                ArrayList tRetMultArr = new ArrayList();

                while (tOffset + 20 <= healthData.length) {

                    tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;

                    tStep = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);

                    tHeartNum = (healthData[tOffset++] & 0xff);
                    tDBP = (healthData[tOffset++] & 0xff);
                    tSBP = (healthData[tOffset++] & 0xff);
                    tOO = (healthData[tOffset++] & 0xff);
                    tHuXiRate = (healthData[tOffset++] & 0xff);
                    tHrv = (healthData[tOffset++] & 0xff);
                    tCVRR = (healthData[tOffset++] & 0xff);
                    tTempInt = (healthData[tOffset++] & 0xff);
                    tTempFloat = (healthData[tOffset++] & 0xff);

                    tOffset += 5;

                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("stepValue", tStep);
                    tMultMap.put("heartValue", tHeartNum);
                    tMultMap.put("DBPValue", tDBP);
                    tMultMap.put("SBPValue", tSBP);
                    tMultMap.put("OOValue", tOO);
                    tMultMap.put("respiratoryRateValue", tHuXiRate);
                    tMultMap.put("hrvValue", tHrv);
                    tMultMap.put("cvrrValue", tCVRR);
                    tMultMap.put("tempIntValue", tTempInt);
                    tMultMap.put("tempFloatValue", tTempFloat);

                    tRetMultArr.add(tMultMap);
                }

                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryAll);
                tRetMap.put("data", tRetMultArr);

                break;
            }
            case CMD.KEY_Health.HistoryFall: {//同步历史手环佩戴脱落数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 5 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    int state = (healthData[tOffset++] & 0xff);//0x00: 佩戴状态  0x01：脱落状态
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("state", state);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryFall);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistoryBloodOxygen: {//同步历史的血氧数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 6 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    int type = (healthData[tOffset++] & 0xff);//0x00: 单次模式 0x01：监测模式
                    int value = (healthData[tOffset++] & 0xff);//血氧值
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("type", type);
                    tMultMap.put("value", value);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryBloodOxygen);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistoryTempAndHumidity: {//同步历史的温湿度数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 9 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    int type = (healthData[tOffset++] & 0xff);//0x00: 单次模式 0x01：监测模式
                    float tempValue = Float.parseFloat((healthData[tOffset++] & 0xff) + "." + (healthData[tOffset++] & 0xff));//温度值
                    float humidValue = Float.parseFloat((healthData[tOffset++] & 0xff) + "." + (healthData[tOffset++] & 0xff));//湿度值
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("type", type);
                    tMultMap.put("tempValue", tempValue);
                    tMultMap.put("humidValue", humidValue);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryTempAndHumidity);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistoryTemp: {//同步历史的体温数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 5 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    int type = (healthData[tOffset++] & 0xff);//0x00: 单次模式 0x01：监测模式
                    float tempValue = Float.parseFloat((healthData[tOffset++] & 0xff) + "." + (healthData[tOffset++] & 0xff));//温度值
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("type", type);
                    tMultMap.put("tempValue", tempValue);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryTemp);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistoryAmbientLight: {//同步历史的环境光数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 6 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    int type = (healthData[tOffset++] & 0xff);//0x00: 单次模式 0x01：监测模式
                    int value = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("type", type);
                    tMultMap.put("value", value);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryAmbientLight);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistoryHealthMonitoring: {//同步历史健康监测数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 30 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    long steps = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);//步数
                    int heart = (healthData[tOffset++] & 0xff);//心率
                    int tSBP = (healthData[tOffset++] & 0xff);//舒张压
                    int tDBP = (healthData[tOffset++] & 0xff);  //收缩压
                    int tOO = (healthData[tOffset++] & 0xff);  //血氧
                    int tHuXiRate = (healthData[tOffset++] & 0xff);  //呼吸率
                    int tHrv = (healthData[tOffset++] & 0xff);  //hrv
                    int tCVRR = (healthData[tOffset++] & 0xff);  //cvrr
                    int tTempInt = (healthData[tOffset++] & 0xff);  //温度int
                    int tTempFloat = (healthData[tOffset++] & 0xff);  //温度float
                    int humidInt = (healthData[tOffset++] & 0xff);  //湿度int
                    int humidFloat = (healthData[tOffset++] & 0xff);  //湿度float
                    int tAmbientLight = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);//环境光
                    int isSprotMode = (healthData[tOffset++] & 0xff);//0：普通模式 1：运动模式
                    int tCal = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);//卡路里
                    int tDis = (healthData[tOffset++] & 0xff);//距离
                    tOffset += 4;//最后4个字节保留
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("stepValue", steps);
                    tMultMap.put("heartValue", heart);
                    tMultMap.put("DBPValue", tDBP);
                    tMultMap.put("SBPValue", tSBP);
                    tMultMap.put("OOValue", tOO);
                    tMultMap.put("respiratoryRateValue", tHuXiRate);
                    tMultMap.put("hrvValue", tHrv);
                    tMultMap.put("cvrrValue", tCVRR);
                    tMultMap.put("tempIntValue", tTempInt);
                    tMultMap.put("tempFloatValue", tTempFloat);
                    tMultMap.put("humidIntValue", humidInt);
                    tMultMap.put("humidFloatValue", humidFloat);
                    tMultMap.put("ambientLightValue", tAmbientLight);
                    tMultMap.put("isSprotMode", isSprotMode);
                    tMultMap.put("sportCalorie", tCal);
                    tMultMap.put("sportDistance", tDis);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistoryHealthMonitoring);
                tRetMap.put("data", tRetMultArr);
                break;
            }
            case CMD.KEY_Health.HistorySportMode: {//同步历史运动模式数据
                ArrayList tRetMultArr = new ArrayList();
                while (tOffset + 15 <= healthData.length) {
                    long tStartTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    long tEndTime = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8) + ((healthData[tOffset++] & 0xff) << 16) + ((healthData[tOffset++] & 0xff) << 24);
                    tStartTime = (tStartTime + YCBTClient.SecFrom30Year) * 1000;
                    tEndTime = (tEndTime + YCBTClient.SecFrom30Year) * 1000;
                    long steps = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);//步数
                    int tDis = (healthData[tOffset++] & 0xff)+ ((healthData[tOffset++] & 0xff) << 8);//距离
//                    int tDis = (healthData[tOffset++] & 0xff);//距离
                    int tCal = (healthData[tOffset++] & 0xff) + ((healthData[tOffset++] & 0xff) << 8);//卡路里
                    int isSprotMode = (healthData[tOffset++] & 0xff);//0：普通模式 1：运动模式
//                    tOffset += 5;//最后5个字节保留
                    HashMap tMultMap = new HashMap();
                    tMultMap.put("startTime", tStartTime - millisFromGMT);
                    tMultMap.put("endTime", tEndTime - millisFromGMT);
                    tMultMap.put("stepValue", steps);
                    tMultMap.put("sportDistance", tDis);
                    tMultMap.put("sportCalorie", tCal);
                    tMultMap.put("isSprotMode", isSprotMode);
                    tRetMultArr.add(tMultMap);
                }
                tRetMap.put("dataType", Constants.DATATYPE.Health_HistorySportMode);
                tRetMap.put("data", tRetMultArr);
                break;
            }
        }
        return tRetMap;
    }

    //封装简单的回调内容, 成功和失败
    public static HashMap unpackParseData(byte[] databytes, int dataType) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1)
            tRetMap.put("data", databytes[0] & 0xff);//0x00：成功 0x01: 失败
        tRetMap.put("dataType", dataType);
        return tRetMap;
    }

    //保险消息推送
    public static HashMap unpackInsuranceNews(byte[] databytes) {
        HashMap tRetMap = new HashMap();
        if (databytes != null && databytes.length >= 1) {
            //0x00: 推送保险名称  0x01: 推送健康基金数额 0x02: 推送动态保额数额 0x03: 推送次月保费数额
            // 0x04: 推送次年保费数额 0x05：推送保险状态 0x06：设置该保险展示文案 0x07:推送保险更新日期
//            tRetMap.put("insurance_type", databytes[0] & 0xff);
            tRetMap.put("result", databytes[0] & 0xff);//0x00：成功 0x01: 失败
        }
        tRetMap.put("dataType", Constants.DATATYPE.AppInsuranceNews);
        return tRetMap;
    }


    public static HashMap unpackDeviceUserConfigData(byte[] dataBytes) {
        HashMap tRetMap = new HashMap();
        tRetMap.put("code", Constants.CODE.Code_OK);
        tRetMap.put("dataType", Constants.DATATYPE.GetDeviceUserConfig);
        int tOffset = 0;
        if (dataBytes.length >= 65) {
            HashMap tDataMap = new HashMap();
            tDataMap.put("stepTarget", (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16));//步数目标
            tDataMap.put("calorTarget", (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16));//目标千卡
            tDataMap.put("distanceTarget", (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8) + ((dataBytes[tOffset++] & 0xff) << 16));//距离目标
            tDataMap.put("sleepTarget", (dataBytes[tOffset++] & 0xff) + ((dataBytes[tOffset++] & 0xff) << 8));//睡眠目标
            tDataMap.put("userHeight", dataBytes[tOffset++] & 0xff);//用户身高
            tDataMap.put("userWeight", dataBytes[tOffset++] & 0xff);//用户体重
            tDataMap.put("userSex", dataBytes[tOffset++] & 0xff);//用户性别
            tDataMap.put("userAge", dataBytes[tOffset++] & 0xff);//用户年龄
            tDataMap.put("distanceUnit", dataBytes[tOffset++] & 0xff);//距离单位
            tDataMap.put("weightUnit", dataBytes[tOffset++] & 0xff);//体重单位
            tDataMap.put("tempUnit", dataBytes[tOffset++] & 0xff);//温度单位
            tDataMap.put("timeUnit", dataBytes[tOffset++] & 0xff);//时间单位  12或24小时制
            tDataMap.put("longSitStartHour1", dataBytes[tOffset++] & 0xff);//久坐提醒开始时间1(小时)
            tDataMap.put("longSitStartMin1", dataBytes[tOffset++] & 0xff);//久坐提醒开始时间1(分钟)
            tDataMap.put("longSitEndHour1", dataBytes[tOffset++] & 0xff);//久坐提醒结束时间1(小时)
            tDataMap.put("longSitEndMin1", dataBytes[tOffset++] & 0xff);//久坐提醒结束时间1(分钟)
            tDataMap.put("longSitStartHour2", dataBytes[tOffset++] & 0xff);//久坐提醒开始时间2(小时)
            tDataMap.put("longSitStartMin2", dataBytes[tOffset++] & 0xff);//久坐提醒开始时间2(分钟)
            tDataMap.put("longSitEndHour2", dataBytes[tOffset++] & 0xff);//久坐提醒结束时间2(小时)
            tDataMap.put("longSitEndMin2", dataBytes[tOffset++] & 0xff);//久坐提醒结束时间2(分钟)
            tDataMap.put("longSitInterval", dataBytes[tOffset++] & 0xff);//久坐提醒间隔时间(分钟)
            tDataMap.put("longSitRepeat", dataBytes[tOffset++] & 0xff);//久坐提醒 重复&开关
            tDataMap.put("antiLostType", dataBytes[tOffset++] & 0xff);//防丢模式 0x00: 不防丢 0x01: 近距离防丢 0x02: 中距离防丢 0x03: 远距离防丢
            tDataMap.put("antiLostRssi", dataBytes[tOffset++] & 0xff);//RSSI -100 ~ 0dB
            tDataMap.put("antiLostDelay", dataBytes[tOffset++] & 0xff);//防丢延时（秒） 1-10
            tDataMap.put("antiLostDisDelay", dataBytes[tOffset++] & 0xff);//是否断线延时 0x00:不支持 0x01:支持
            tDataMap.put("antiLostRepeat", dataBytes[tOffset++] & 0xff);//重复开关 0x00:不重复提醒 0x01:重复提醒
            tDataMap.put("messageTotalSwitch", dataBytes[tOffset++] & 0xff);//通知提醒总开关 0x00: 关 0x01: 开
            tDataMap.put("messageSwitch0", dataBytes[tOffset++] & 0xff);//提醒项子开关0  具体的看消息推送功能
            tDataMap.put("messageSwitch1", dataBytes[tOffset++] & 0xff);//提醒项子开关1
            tDataMap.put("heartHand", dataBytes[tOffset++] & 0xff);//左右手 0x00: 左手0x01: 右手
            tDataMap.put("heartAlarmSwitch", dataBytes[tOffset++] & 0xff);//心率报警开关 0x00:关 0x01:开
            tDataMap.put("heartAlarmValue", dataBytes[tOffset++] & 0xff);//心率报警阈值100-240
            tDataMap.put("heartMonitorTye", dataBytes[tOffset++] & 0xff);//心率监测模式 0x00: 手动  0x01: 自动
            tDataMap.put("heartMonitorInterval", dataBytes[tOffset++] & 0xff);//自动模式监测间隔 1-60分钟

            tDataMap.put("language", dataBytes[tOffset++] & 0xff);//手环语言
            tDataMap.put("handupswitch", dataBytes[tOffset++] & 0xff);//抬腕亮屏开关
            tDataMap.put("screenval", dataBytes[tOffset++] & 0xff);//显示屏亮度
            tDataMap.put("skincolour", dataBytes[tOffset++] & 0xff);//肤色设置
            tDataMap.put("screendown", dataBytes[tOffset++] & 0xff);//息屏时间
            tDataMap.put("bluebreakswitch", dataBytes[tOffset++] & 0xff);//蓝牙断开提醒
            tDataMap.put("datauploadswitch", dataBytes[tOffset++] & 0xff);//数据上传提醒

            tDataMap.put("disturbswitch", dataBytes[tOffset++] & 0xff);//勿扰模式，开关
            tDataMap.put("disturbbegintimehour", dataBytes[tOffset++] & 0xff);//勿扰模式，开始时间，时
            tDataMap.put("disturbbegintimemin", dataBytes[tOffset++] & 0xff);//勿扰模式，开始时间，分
            tDataMap.put("disturbendtimehour", dataBytes[tOffset++] & 0xff);//勿扰模式，结束时间，时
            tDataMap.put("disturbendtimemin", dataBytes[tOffset++] & 0xff);//勿扰模式，结束时间，分


            tDataMap.put("sleepswitch", dataBytes[tOffset++] & 0xff);//睡眠提醒，开关
            tDataMap.put("sleeptimehour", dataBytes[tOffset++] & 0xff);//睡眠提醒，开始时间，时
            tDataMap.put("sleeptimemin", dataBytes[tOffset++] & 0xff);//睡眠提醒，开始时间，分

            tDataMap.put("scheduleswitch", dataBytes[tOffset++] & 0xff);//日程，开关
            tDataMap.put("eventswitch", dataBytes[tOffset++] & 0xff);//事件提醒，开关
            tDataMap.put("accidentswitch", dataBytes[tOffset++] & 0xff);//意外检测，开关
            tDataMap.put("tempswitch", dataBytes[tOffset++] & 0xff);//体温报警，开关
//            tDataMap.put("longSitInterval", dataBytes[tOffset++] & 0xff);//预留使用，暂不解析，5个字节

            tRetMap.put("data", tDataMap);
        }
        return tRetMap;
    }

}
