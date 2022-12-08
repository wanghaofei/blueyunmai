package com.yucheng.ycbtsdk.Protocol;

/**
 * @author StevenLiu
 * @date 2020/1/9
 * @desc one word for this class
 */
public class CMD {

    public static String BLETAG = "YCBLE";

    public static final String UUID_S = "be940000-7333-be46-b7ae-689e71722bd5"; //服务UUID
    public static final String UUID_C_1 = "be940001-7333-be46-b7ae-689e71722bd5";  //读写一起
    public static final String UUID_C_3 = "be940003-7333-be46-b7ae-689e71722bd5";  //读


    public static final int Priority_low = 1;    //队列优先级低
    public static final int Priority_normal = 2;  //队列优先级中
    public static final int Priority_high = 3;    //队列优先级高


    public static class Group {
        public static final int Group_Single = 1;  //单一指令
        public static final int Group_OTAUI = 2;  //UI升级（此操作包括 0 获取UI文件断点信息, 1 发送待传输文件信息, 2 发送block数据, 3 发送block校验信息
        public static final int Group_Health = 3;
        public static final int Group_ECGList = 4;
        public static final int Group_ECGData = 5;
        public static final int Group_PPGList = 6;
        public static final int Group_PPGData = 7;
        public static final int Group_StartSport = 8; //启动运动;
        public static final int Group_EndSport = 9; //结束运动;
        public static final int Group_StartEcgTest = 0x0a; //启动ECG测试
        public static final int Group_EndEcgTest = 0x0b; //结束ECG测试;
        public static final int Group_REAL_SPORT = 0x0C; //开启/关闭上传实时步数
    }

    public static final int Setting = 0x01;
    public static final int Get = 0x02;
    public static final int AppControl = 0x03;
    public static final int DevControl = 0x04;
    public static final int Health = 0x05;
    public static final int Real = 0x06;
    public static final int Collect = 0x07;
    public static final int OtaUI = 0x7e;


    public static class KEY_Setting {
        public static final int Time = 0x00;
        public static final int Alarm = 0x01;
        public static final int Goal = 0x02;
        public static final int UserInfo = 0x03;
        public static final int Unit = 0x04;
        public static final int LongSite = 0x05;
        public static final int AntiLose = 0x06;
        public static final int AntiLoseArg = 0x07;
        public static final int HandWear = 0x08;  //左右手佩戴设置
        public static final int PhoneOS = 0x09;
        public static final int Notification = 0x0a;
        public static final int HeartAlarm = 0x0b;
        public static final int HeartMonitor = 0x0c;
        public static final int FindPhone = 0x0d;
        public static final int RestoreFactory = 0x0e;
        public static final int NotDisturb = 0x0f;
        public static final int AncsOn = 0x10;
        public static final int AerobicTrain = 0x11; //有氧教练
        public static final int Language = 0x12;
        public static final int RaiseScreen = 0x13; //抬腕亮屏
        public static final int DisplayBrightness = 0x14; //显示屏亮度
        public static final int Skin = 0x15;
        public static final int BloodRange = 0x16;
        public static final int BTName = 0x17;
        public static final int SensorRate = 0x18;
        public static final int MainTheme = 0x19;
        public static final int SleepRemind = 0x1a;
        public static final int PPGCollect = 0x1b;
        public static final int TemperatureAlarm = 0x1f;//温度报警
        public static final int TemperatureMonitor = 0x20;//温度监测
        public static final int ScreenTime = 0x21;//息屏时间设置
        public static final int AmbientLight = 0x22;//环境光检测设置
        public static final int WorkingMode = 0x23;//工作模式切换设置
        public static final int AccidentMode = 0x24;//意外监测模式设置
        public static final int BraceletStatusAlert = 0x25;//手环状态提醒设置
        public static final int BloodOxygenModeMonitor = 0x26;//血氧监测模式设置
        public static final int ScheduleModification = 0x27;//日程修改设置
        public static final int AmbientTemperatureAndHumidity = 0x28;//环境温湿度检测模式设置
        public static final int ScheduleSwitch = 0x29;//日程开关设置
        public static final int StepCountingStateTime = 0x2A;//计步状态时间设置
        public static final int UploadReminder = 0x2B;//上传提醒设置
        public static final int BluetoothBroadcastInterval = 0x2C;//设置蓝牙广播间隔
        public static final int BluetoothTransmittingPower = 0x2D;//设置蓝牙发射功率
        public static final int ExerciseHeartRateZone = 0x2E;//运动心率区间设置
        public static final int EventReminder = 0x2F;//事件提醒设置
        public static final int EventReminderSwitch = 0x30;//事件提醒开关控制
//        public static final int Insurance = 0x21;//保险设置
    }

    public static class KEY_Get {
        public static final int DeviceInfo = 0x00;
        public static final int SupportFunction = 0x01;
        public static final int MacAddress = 0x02;
        public static final int DevcieName = 0x03;
        public static final int UserConfig = 0x07; //获取用户配置
        public static final int FunctionState = 0x04;
        public static final int DeviceLog = 0x08;
        public static final int MainTheme = 0x09;
        public static final int ElectrodeLocation = 0x0a;
        public static final int DeviceScreenInfo = 0x0b;
        public static final int NowStep = 0x0c;
        public static final int HistoryOutline = 0x0d;
        public static final int RealTemp = 0x0e;
        public static final int ScreenInfo = 0x0F;//获取屏幕显示信息
        public static final int HeavenEarthAndFiveElement = 0x10;//获取天地五行的数据
        public static final int RealBloodOxygen = 0x11;//获取设备实时血氧
        public static final int CurrentAmbientLightIntensity = 0x12;//获取当前环境光强度
        public static final int CurrentAmbientTempAndHumidity = 0x13;//获取当前环境温湿度
        public static final int ScheduleInfo = 0x14;//获取查询日程信息
        public static final int SensorSamplingInfo = 0x15;//获取传感器采样信息
        public static final int CurrentSystemWorkingMode = 0x16;//获取当前系统工作模式
        public static final int InsuranceRelatedInfo = 0x17;//获取保险相关信息
        public static final int UploadConfigurationInfoOfReminder = 0x18;//获取上传提醒的配置信息
        public static final int StatusOfManualMode = 0x19;//获取手动模式的状态
        public static final int EventReminderInfo = 0x1A;//获取当前手环事件提醒信息
        public static final int ChipScheme = 0x1B;//获取当前手环芯片方案
        public static final int DeviceRemindInfo = 0x1F;//获取手环提醒设置信息
    }

    public static class KEY_AppControl {
        public static final int FindDevice = 0x00;
        public static final int HeartTest = 0x01;
        public static final int BloodTest = 0x02;
        public static final int BloodCheck = 0x03;
        public static final int AppExit = 0x04;
        public static final int AerobiCcoachMode = 0x05;  //有氧教练模式
        public static final int BindDevice = 0x06;
        public static final int UnBindDevice = 0x07;
        public static final int NotificationPush = 0x08;
        public static final int RealData = 0x09;
        public static final int QuerySampleRate = 0x0a;
        public static final int WaveUpload = 0x0b; //波形上传控制
        public static final int SportMode = 0x0c; //运动模式启动/停止
        public static final int TakePhoto = 0x0e; //相机拍照控制
        public static final int TodayWeather = 0x12; //今天天气
        public static final int TomorrowWeather = 0x13; //明天天气
        public static final int EcgRealStatus = 0x14; //设备实时状态上报
        public static final int HealthArg = 0x15; //健康参数、预警信息发送
        public static final int ShutDown = 0x16;//关机、进入运输模式控制
        public static final int TemperatureCorrect = 0x17;
        public static final int TempMeasurementControl = 0x18;//温度测量控制
        public static final int EmoticonIndex = 0x19;//表情包显示
        public static final int HealthWriteBack = 0x1A;//健康值回写到手环
        public static final int SleepWriteBack = 0x1B;//睡眠数据回写到手环
        public static final int UserInfoWriteBack = 0x1C;//用户个人信息回写到手环
        public static final int UpgradeReminder = 0x1D;//升级提醒
        public static final int AmbientLightMeasurementControl = 0x1E;//环境光测量控制
        public static final int AmbientTempHumidityMeasurementControl = 0x20;//环境温湿度测量控制
        public static final int InsuranceNews = 0x21;//保险消息推送
        public static final int SensorSwitchControl = 0x22;//传感器数据存储开关控制
        public static final int MobileModel = 0x23;//当前手机型号推送
        public static final int EffectiveStep = 0x24;//有效步数同步
        public static final int EffectiveHeart = 0x25;//计算心率同步
        public static final int EarlyWarning = 0x26;//app预警推送
        public static final int PushMessage = 0x27;//app信息推送
        public static final int OpenOrCloseTesting = 0x28;//app一键启动/关闭检测
    }

    public static class KEY_DeviceControl {
        public static final int FindMobile = 0x00;//寻找手机
        public static final int LostReminder = 0x01;//防丢提醒
        public static final int AnswerAndClosePhone = 0x02;//接听/拒接电话
        public static final int TakePhoto = 0x03;//相机拍照控制
        public static final int StartMusic = 0x04;//音乐控制
        public static final int Sos = 0x05;//一键呼救控制命令
        public static final int DrinkingPatterns = 0x06;//饮酒模式控制命令
        public static final int ConnectOrDisconnect = 0x07;//手环蓝牙  连接/拒连
        public static final int SportMode = 0x08;//手环运动模式控制
    }

    public static class KEY_Health {

        public static final int HistorySport = 0x02;
        public static final int HistorySleep = 0x04;
        public static final int HistoryHeart = 0x06;
        public static final int HistoryBlood = 0x08;
        public static final int HistoryAll = 0x09;
        public static final int HistoryBloodOxygen = 0x1A; //同步历史的血氧数据
        public static final int HistoryTempAndHumidity = 0x1C; //同步历史的温湿度数据
        public static final int HistoryTemp = 0x1E; //同步历史的体温数据
        public static final int HistoryAmbientLight = 0x20; //同步历史的环境光数据
        public static final int HistoryFall = 0x29;//同步历史手环佩戴脱落数据
        public static final int HistoryHealthMonitoring = 0x2B;//同步历史健康监测数据
        public static final int HistorySportMode = 0x2D;//同步历史运动模式数据

        public static final int HistorySportAck = 0x11;
        public static final int HistorySleepAck = 0x13;
        public static final int HistoryHeartAck = 0x15;
        public static final int HistoryBloodAck = 0x17;
        public static final int HistoryAllAck = 0x18;
        public static final int HistoryBloodOxygenAck = 0x22; //发送完一个block 历史的血氧数据
        public static final int HistoryTempAndHumidityAck = 0x24; //发送完一个block 历史的温湿度数据
        public static final int HistoryTempAck = 0x26; //发送完一个block 历史的体温数据
        public static final int HistoryAmbientLightAck = 0x28; //发送完一个block 同步历史的环境光数据
        public static final int HistoryFallAck = 0x2A;//发送完一个block历史手环佩戴脱落数据
        public static final int HistoryHealthMonitoringAck = 0x2C;//发送完一个block历史健康监测数据
        public static final int HistorySportModeAck = 0x2E;//发送完一个block历史运动模式数据

        public static final int DeleteSport = 0x40;
        public static final int DeleteSleep = 0x41;
        public static final int DeleteHeart = 0x42;
        public static final int DeleteBlood = 0x43;
        public static final int DeleteAll = 0x44;
        public static final int DeleteBloodOxygen = 0x45; //删除历史的血氧数据
        public static final int DeleteTempAndHumidity = 0x46; //删除历史的温湿度数据
        public static final int DeleteTemp = 0x47; //删除历史的体温数据
        public static final int DeleteAmbientLight = 0x48; //删除历史的环境光数据
        public static final int DeleteFall = 0x49;//删除手环脱落记录
        public static final int DeleteHealthMonitoring = 0x4A;//删除健康检测记录
        public static final int DeleteSportMode = 0x4B;//删除运动
        public static final int HistoryBlock = 0x80;
    }

    public static class KEY_Real {
        public static final int UploadSport = 0x00;
        public static final int UploadHeart = 0x01;
        public static final int UploadOO = 0x02;
        public static final int UploadBlood = 0x03;
        public static final int UploadPPG = 0x04;
        public static final int UploadECG = 0x05;
        public static final int UploadRun = 0x06;
        public static final int UploadRespiratoryRate = 0x07;
        public static final int UploadSchedule = 0x0B;
        public static final int UploadEventReminder = 0x0C;//上传事件提醒信息
    }

    public static class KEY_Collect {
        public static final int QueryNum = 0x00;
        public static final int GetWithIndex = 0x01;
        public static final int GetWithTimestamp = 0x02;
        public static final int SyncData = 0x10;
        public static final int CheckData = 0x20;
        public static final int DeleteWithIndex = 0x30;
        public static final int DeleteWithTimestamp = 0x31;
    }

    public static class KEY_UI {
        public static final int UI_GetFileBreak = 0x00;
        public static final int UI_SyncFileInfo = 0x01;
        public static final int UI_SyncBlock = 0x02;
        public static final int UI_SyncBlockCheck = 0x03;
    }

}
