package com.yucheng.ycbtsdk.Response;

import java.util.HashMap;

/**
 * @author zengchong
 * @date 2020/5/9
 * @desc 实时数据回调(心率实时数据上传)
 */
public interface BleRealRunResponse {
    void onRealRunResponse(int dataType, HashMap dataMap);
}
