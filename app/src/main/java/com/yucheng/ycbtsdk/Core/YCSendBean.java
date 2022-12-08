package com.yucheng.ycbtsdk.Core;

import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.Utils.YCBTLog;

/**
 * @author StevenLiu
 * @date 2020/1/13
 * @desc one word for this class
 */
public class YCSendBean implements Comparable<YCSendBean> {

    public int collectDigits=16;

    private static final int YCMAXLEN = 176;
    private static int SENDSN = 1;

    public int groupType;
    public int groupSize;

    public byte[] willData;
    public int sendPriority;
    private int sendSN;  //发送序列号
    private int currentSendPos;

    public int dataType;
    public boolean dataSendFinish; //此元素处理完成, 只等待出队列

    public  BleDataResponse mDataResponse;

    public YCSendBean(byte[] willData, int priority, BleDataResponse dataResponse){

        this.willData = willData;
        sendPriority = priority;

        mDataResponse = dataResponse;



        sendSN = SENDSN++;

        YCBTLog.e("sendSN=========================="+sendSN);

        dataSendFinish = false;
        currentSendPos = 0;
    }

    public byte[] willSendFrame(){
        int tTotalLen = willData.length;

        if (tTotalLen - currentSendPos > YCMAXLEN){
            byte[] retBytes = new byte[YCMAXLEN];
            System.arraycopy(willData, currentSendPos, retBytes, 0, YCMAXLEN);
            currentSendPos += YCMAXLEN;

            return retBytes;
        }
        else if (tTotalLen <= currentSendPos){
            if (tTotalLen == 0 && currentSendPos == 0){
                byte[] retBytes = {};
                currentSendPos = 1;
                return retBytes;
            }
            return null;
        }
        else {
            int tLastLen = tTotalLen - currentSendPos;
            byte[] retBytes = new byte[tLastLen];
            System.arraycopy(willData, currentSendPos, retBytes, 0, tLastLen);
            currentSendPos += tLastLen;

            return retBytes;
        }
    }

    public void resetGroup(int data_type, byte[] cmd_data){
        currentSendPos = 0;
        dataType = data_type;
        willData = cmd_data;
    }

    public void collectStopReset(){
        currentSendPos = 0;
    }

    @Override
    public String toString() {
        return String.format("%04X-%d-%04d-%04X", dataType, sendPriority, sendSN, groupType);
    }

    @Override
    public int compareTo(YCSendBean ycSendBean) {
        if (this.sendPriority == ycSendBean.sendPriority){
            return this.sendSN - ycSendBean.sendSN;
        }
        return ycSendBean.sendPriority - this.sendPriority;
    }
}
