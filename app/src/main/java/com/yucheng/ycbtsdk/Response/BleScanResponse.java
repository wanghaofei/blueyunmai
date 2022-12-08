package com.yucheng.ycbtsdk.Response;

import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;

public interface BleScanResponse {

  void onScanResponse(int code, ScanDeviceBean scanBean);
}

    
