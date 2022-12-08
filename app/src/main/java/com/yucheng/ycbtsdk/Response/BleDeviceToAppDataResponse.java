package com.yucheng.ycbtsdk.Response;

import java.util.HashMap;

/**
 * @author zengchong
 * @date 2020/5/9
 * @desc 实时数据回调(设备实时状态上传)
 */
public interface BleDeviceToAppDataResponse {
    public void onDataResponse(int code, HashMap dataMap);
}
