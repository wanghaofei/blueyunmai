package com.yucheng.ycbtsdk.Protocol;

public class BleState {

  public static int Ble_PermissionForbid = 0x01;
  public static int Ble_NotOpen = 0x02;       //蓝牙未打开
  public static int Ble_Disconnect = 0x03;    //未连接
  public static int Ble_Disconnecting = 0x04; //断开连接中
  public static int Ble_Connecting = 0x05;    //连接中
  public static int Ble_Connected = 0x06;     //已连接
  public static int Ble_ServicesDiscovered = 0x07;             //发现服务
  public static int Ble_CharacteristicDiscovered = 0x08;       //发现特征
  public static int Ble_CharacteristicNotification = 0x09;     //使能读特征成功
  public static int Ble_ReadOk = 0x0a;                         //读写OK

}

    
