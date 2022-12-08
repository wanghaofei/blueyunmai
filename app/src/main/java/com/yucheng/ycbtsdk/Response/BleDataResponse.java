package com.yucheng.ycbtsdk.Response;


import java.util.HashMap;

public interface BleDataResponse {
    void onDataResponse(int code, float ratio, HashMap resultMap);
}

    
