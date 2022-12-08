package com.yucheng.ycbtsdk.Gatt;

import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;

public interface GattBleResponse {

  void bleStateResponse(int state);

  void bleOnCharacteristicWrite(int status, byte[] writeBytes);

  void bleDataResponse(int code, byte[] recvValue);

  void bleScanResponse(int code, ScanDeviceBean scanBean);
}

    
