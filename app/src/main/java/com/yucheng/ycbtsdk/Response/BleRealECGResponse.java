package com.yucheng.ycbtsdk.Response;

import java.util.HashMap;

/**
 * @author zengchong
 * @date 2020/5/9
 * @desc 实时数据回调(设备心电上传)
 */
public interface BleRealECGResponse {
    void onRealECGResponse(int dataType, HashMap dataMap);
}
