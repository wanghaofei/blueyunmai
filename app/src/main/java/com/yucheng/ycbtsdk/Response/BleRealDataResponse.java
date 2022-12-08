package com.yucheng.ycbtsdk.Response;

import java.util.HashMap;

/**
 * @author StevenLiu
 * @date 2020/2/29
 * @desc 实时数据回调(实时计步,实时心率,实时血压,实时心电等)
 */
public interface BleRealDataResponse {
    void onRealDataResponse(int dataType, HashMap dataMap);
}

    
