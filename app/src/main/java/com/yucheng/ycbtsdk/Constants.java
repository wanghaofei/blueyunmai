package com.yucheng.ycbtsdk;

public class Constants {

    public static class DATATYPE {

        public static final int SettingTime = 0x0100; //设置时间
        public static final int SettingAlarm = 0x0101; //闹钟提醒设置
        public static final int SettingGoal = 0x0102; //目标设置
        public static final int SettingUserInfo = 0x0103; //用户信息设置
        public static final int SettingUnit = 0x0104; //单位设置
        public static final int SettingLongsite = 0x0105;
        public static final int SettingAntiLose = 0x0106;
        public static final int SettingHandWear = 0x0108;
        public static final int SettingNotify = 0x010A;//通知提醒开关设置
        public static final int SettingHeartAlarm = 0x010b;
        public static final int SettingHeartMonitor = 0x010c;
        public static final int SettingFindPhone = 0x010d;
        public static final int SettingRestoreFactory = 0x010e;
        public static final int SettingNotDisturb = 0x010f;
        public static final int SettingLanguage = 0x0112;
        public static final int SettingRaiseScreen = 0x0113;
        public static final int SettingDisplayBrightness = 0x0114;
        public static final int SettingSkin = 0x0115;
        public static final int SettingBloodRange = 0x0116;
        public static final int SettingMainTheme = 0x0119;
        public static final int SettingSleepRemind = 0x011a;
        public static final int SettingDataCollect = 0x011b;
        public static final int SettingTemperatureAlarm = 0x011f;//温度报警
        public static final int SettingTemperatureMonitor = 0x0120;//温度监测
        public static final int SettingScreenTime = 0x0121;//息屏时间设置
        public static final int SettingAmbientLight = 0x0122;//环境光检测设置
        public static final int SettingWorkingMode = 0x0123;//工作模式切换设置
        public static final int SettingAccidentMode = 0x0124;//意外监测模式设置
        public static final int SettingBraceletStatusAlert = 0x0125;//手环状态提醒设置
        public static final int SettingBloodOxygenModeMonitor = 0x0126;//血氧监测模式设置
        public static final int SettingScheduleModification = 0x0127;//日程修改设置
        public static final int SettingAmbientTemperatureAndHumidity = 0x0128;//环境温湿度检测模式设置
        public static final int SettingScheduleSwitch = 0x0129;//日程开关设置
        public static final int SettingStepCountingStateTime = 0x012A;//计步状态时间设置
        public static final int SettingUploadReminder = 0x012B;//上传提醒设置
        public static final int SettingBluetoothBroadcastInterval = 0x012C;//设置蓝牙广播间隔
        public static final int SettingBluetoothTransmittingPower = 0x012D;//设置蓝牙发射功率
        public static final int SettingExerciseHeartRateZone = 0x012E;//运动心率区间设置
        public static final int SettingEventReminder = 0x012F;//事件提醒设置
        public static final int SettingEventReminderSwitch = 0x0130;//事件提醒开关控制
//        public static final int SettingInsurance = 0x0321;//保险设置

        public static final int GetDeviceInfo = 0x0200; //获取设备基本信息
        public static final int GetDeviceUserConfig = 0x0207; //获取用户配置
        public static final int GetDeviceLog = 0x0208; //获取设备Log
        public static final int GetThemeInfo = 0x0209; //获取设备主界面样式配置
        public static final int GetElectrodeLocation = 0x020a; //获取心电右电极位置
        public static final int GetDeviceScreenInfo = 0x020b; //获取屏幕分辨率和字体分辨率
        public static final int GetNowStep = 0x020c;
        public static final int GetHistoryOutline = 0x020d; //获取历史记录概要信息
        public static final int GetRealTemp = 0x020e; //获取实时温度
        public static final int GetScreenInfo = 0x020F;//获取屏幕显示信息
        public static final int GetHeavenEarthAndFiveElement = 0x0210;//获取天地五行的数据
        public static final int GetRealBloodOxygen = 0x0211;//获取设备实时血氧
        public static final int GetCurrentAmbientLightIntensity = 0x0212;//获取当前环境光强度
        public static final int GetCurrentAmbientTempAndHumidity = 0x0213;//获取当前环境温湿度
        public static final int GetScheduleInfo = 0x0214;//获取查询日程信息
        public static final int GetSensorSamplingInfo = 0x0215;//获取传感器采样信息
        public static final int GetCurrentSystemWorkingMode = 0x0216;//获取当前系统工作模式
        public static final int GetInsuranceRelatedInfo = 0x0217;//获取保险相关信息
        public static final int GetUploadConfigurationInfoOfReminder = 0x0218;//获取上传提醒的配置信息
        public static final int GetStatusOfManualMode = 0x0219;//获取手动模式的状态
        public static final int GetEventReminderInfo = 0x021A;//获取当前手环事件提醒信息
        public static final int GetChipScheme = 0x021B;//获取当前手环芯片方案

        public static final int AppFindDevice = 0x0300; //寻找手环
        public static final int AppRunMode = 0x030C; //运动模式
        public static final int AppHeartSwitch = 0x0301; //心率测试开关控制
        public static final int AppBloodSwitch = 0x0302; //血压测试开关控制
        public static final int AppMessageControl = 0x0308;//消息提醒控制
        public static final int AppBloodCalibration = 0x0303; //血压校准

        public static final int GetDeviceRemindInfo = 0x021F;//获取手环提醒设置信息

        public static final int AppControlReal = 0x0309;
        public static final int AppControlWave = 0x030b;
        public static final int AppTodayWeather = 0x0312;//今日天气预报
        public static final int AppTomorrowWeather = 0x0313;//明日天气预报
        public static final int AppECGPPGStatus = 0x0314;  //设备实时状态上传
        public static final int AppHealthArg = 0x0315; //健康参数、预警信息发送
        public static final int AppShutDown = 0x0316;//关机、进入运输模式控制
        public static final int AppTemperatureCorrect = 0x0317;//温度校准
        public static final int AppTemperatureMeasure = 0x0318;//温度测量控制
        public static final int AppTemperatureCode = 0x031f;//体温红绿码设置
        public static final int AppEmoticonIndex = 0x0319;//表情包显示
        public static final int AppHealthWriteBack = 0x031A;//健康值回写到手环
        public static final int AppSleepWriteBack = 0x031B;//睡眠数据回写到手环
        public static final int AppUserInfoWriteBack = 0x031C;//用户个人信息回写到手环
        public static final int AppUpgradeReminder = 0x031D;//升级提醒
        public static final int AppAmbientLightMeasurementControl = 0x031E;//环境光测量控制
        public static final int AppAmbientTempHumidityMeasurementControl = 0x0320;//环境温湿度测量控制
        public static final int AppInsuranceNews = 0x0321;//保险消息推送
        public static final int AppSensorSwitchControl = 0x0322;//传感器数据存储开关控制
        public static final int AppMobileModel = 0x0323;//当前手机型号推送
        public static final int AppEffectiveStep = 0x0324;//有效步数同步
        public static final int AppEffectiveHeart = 0x0325;//计算心率同步
        public static final int AppEarlyWarning = 0x0326;//app预警推送
        public static final int AppPushMessage = 0x0327;//app信息推送
        public static final int AppOpenOrCloseTesting = 0x0328;//app一键启动/关闭检测

        public static final int DeviceFindMobile = 0x0400;//寻找手机
        public static final int DeviceLostReminder = 0x0401;//防丢提醒
        public static final int DeviceAnswerAndClosePhone = 0x0402;//接听/拒接电话
        public static final int DeviceTakePhoto = 0x0403;//相机拍照控制
        public static final int DeviceStartMusic = 0x0404;//音乐控制
        public static final int DeviceSos = 0x0405;//一键呼救控制命令
        public static final int DeviceDrinkingPatterns = 0x0406;//饮酒模式控制命令
        public static final int DeviceConnectOrDisconnect = 0x0407;//手环蓝牙  连接/拒连
        public static final int DeviceSportMode = 0x0408;//手环运动模式控制

        public static final int Health_HistorySport = 0x0502; //同步历史运动数据
        public static final int Health_HistorySleep = 0x0504; //同步历史睡眠数据
        public static final int Health_HistoryHeart = 0x0506; //同步历史心率数据
        public static final int Health_HistoryBlood = 0x0508; //同步历史血压数据
        public static final int Health_HistoryAll = 0x0509; //同步历史记录数据
        public static final int Health_HistoryBloodOxygen = 0x051A; //同步历史的血氧数据
        public static final int Health_HistoryTempAndHumidity = 0x051C; //同步历史的温湿度数据
        public static final int Health_HistoryTemp = 0x051E; //同步历史的体温数据
        public static final int Health_HistoryAmbientLight = 0x0520; //同步历史的环境光数据
        public static final int Health_HistoryFall = 0x0529;//同步历史手环佩戴脱落数据
        public static final int Health_HistoryHealthMonitoring = 0x052B; //同步历史健康监测数据
        public static final int Health_HistorySportMode = 0x052D; //同步历史运动模式数据
        public static final int Health_DeleteSport = 0x0540;
        public static final int Health_DeleteSleep = 0x0541;
        public static final int Health_DeleteHeart = 0x0542;
        public static final int Health_DeleteBlood = 0x0543;
        public static final int Health_DeleteAll = 0x0544;
        public static final int Health_DeleteBloodOxygen = 0x0545; //删除历史的血氧数据
        public static final int Health_DeleteTempAndHumidity = 0x0546; //删除历史的温湿度数据
        public static final int Health_DeleteTemp = 0x0547; //删除历史的体温数据
        public static final int Health_DeleteAmbientLight = 0x0548; //删除历史的环境光数据
        public static final int Health_DeleteFall = 0x0549;//删除手环脱落记录
        public static final int Health_DeleteHealthMonitoring = 0x054A;//删除历史健康监测数据
        public static final int Health_DeleteSportMode = 0x054B;//删除历史运动模式数据
        public static final int Health_HistoryBlock = 0x0580;

        public static final int Real_UploadSport = 0x0600;
        public static final int Real_UploadHeart = 0x0601;
        public static final int Real_UploadOO = 0x0602;
        public static final int Real_UploadBlood = 0x0603;
        public static final int Real_UploadPPG = 0x0604;
        public static final int Real_UploadECG = 0x0605;
        public static final int Real_UploadRun = 0x0606;
        public static final int Real_UploadRespiratoryRate = 0x0607;
        public static final int Real_UploadSchedule = 0x060B;
        public static final int Real_UploadECGHrv = 0x06f0;
        public static final int Real_UploadECGRR = 0x06f1;


        public static final int Collect_QueryNum = 0x0700;
        public static final int Collect_GetWithIndex = 0x0701;
        public static final int Collect_GetWithTimestamp = 0x0702;
        public static final int Collect_SyncData = 0x0710;
        public static final int Collect_SyncCheck = 0x0720;
        public static final int Collect_Delete = 0x0730;
        public static final int Collect_DeleteTimestamp = 0x0731;


        public static final int OtaUI_GetFileBreak = 0x7e00;
        public static final int OtaUI_SyncFileInfo = 0x7e01;
        public static final int OtaUI_SyncBlock = 0x7e02;
        public static final int OtaUI_SyncBlockCheck = 0x7e03;

    }

    public static class CODE {
        public static final int Code_OK = 0;
        public static final int Code_Failed = 1;
        public static final int Code_TimeOut = 2;
    }

    public static class BLEState {
        public static int TimeOut = 0x01;     //超时
        public static int NotOpen = 0x02;
        public static int Disconnect = 0x03;    //未连接
        public static int Disconnecting = 0x04; //断开连接中
        public static int Connecting = 0x05;    //连接中
        public static int Connected = 0x06;     //已连接
        public static int ServicesDiscovered = 0x07; //
        public static int CharacteristicDiscovered = 0x08;
        public static int CharacteristicNotification = 0x09;
        public static int ReadWriteOK = 0x0a;     //读写成功
    }
}
