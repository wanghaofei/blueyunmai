package com.yucheng.ycbtsdk.Bean;

public class ScanDeviceBean implements Comparable<ScanDeviceBean> {
    private String deviceMac;
    private String deviceName;
    private int deviceRssi;

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceRssi() {
        return deviceRssi;
    }

    public void setDeviceRssi(int deviceRssi) {
        this.deviceRssi = deviceRssi;
    }

    @Override
    public int compareTo(ScanDeviceBean scanDeviceBean) {
        int i = -(this.deviceRssi - scanDeviceBean.getDeviceRssi());
        return i;
    }
}
