package com.yucheng.ycbtsdk.Gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.Protocol.BleState;
import com.yucheng.ycbtsdk.Protocol.CMD;
import com.yucheng.ycbtsdk.Utils.ByteUtil;
import com.yucheng.ycbtsdk.Utils.SPUtil;
import com.yucheng.ycbtsdk.Utils.YCBTLog;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author StevenLiu
 * @date 2020/1/13
 * @desc one word for this class
 */
public class BleHelper {
    private static BleHelper mGatt;
    private Context bleContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChar;
    private boolean isEnableWriteChar;  //是否已使能读特征
    private boolean isScaning;  //是否正在搜索
    private int mBleState;
    private GattBleResponse mBleResponse;

    public boolean getScanState() {
        return isScaning;
    }

    public static BleHelper getHelper() {
        if (mGatt == null) {
            synchronized (BleHelper.class) {
                if (mGatt == null) {
                    mGatt = new BleHelper();
                }
            }
        }
        return mGatt;
    }

    public void initContext(Context context) {
        bleContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBleState = BleState.Ble_Disconnect;
    }

    public void registerGattResponse(GattBleResponse bleResponse) {
        mBleResponse = bleResponse;
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            YCBTLog.e("onConnectionStateChange " + " status " + status + " newState " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                YCBTLog.e("开始发现服务");
                mBleState = BleState.Ble_Connected;
                mBleResponse.bleStateResponse(BleState.Ble_Connected);
                mBluetoothGatt.discoverServices();
            } else {
                mBleState = BleState.Ble_Disconnect;
                mBleResponse.bleStateResponse(BleState.Ble_Disconnect);
                refreshDeviceCache();
                closeGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mBleState = BleState.Ble_ServicesDiscovered;
            mBleResponse.bleStateResponse(BleState.Ble_ServicesDiscovered);
            YCBTLog.e("onServicesDiscovered " + " status " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                YCBTLog.e("开始获取服务里的特征");
                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : gattServices) {
                    // 判断当前服务的UUID是否和预定的服务的UUID相等
                    if (gattService.getUuid().toString().equals(CMD.UUID_S)) {
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                            if (gattCharacteristic.getUuid().toString().equals(CMD.UUID_C_1)) {
                                isEnableWriteChar = false;
                                mWriteChar = gattCharacteristic;
                                YCBTLog.e("开始使能读特征 " + gattCharacteristic.getUuid().toString());
                            } else if (gattCharacteristic.getUuid().toString().equals(CMD.UUID_C_3)) {
                                mBleState = BleState.Ble_CharacteristicDiscovered;
                                mBleResponse.bleStateResponse(BleState.Ble_CharacteristicDiscovered);
                                setNotificationForCharacteristic(gattCharacteristic, true);
                            }
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            YCBTLog.e("onCharacteristicRead " + " status " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            YCBTLog.e("onCharacteristicWrite " + " status " + status + " " + ByteUtil.byteToString(characteristic.getValue()) + " " + Thread.currentThread().toString());
            mBleResponse.bleOnCharacteristicWrite(status, characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic.getValue().length > 60) {
                byte[] tLogByte = new byte[60];
                System.arraycopy(characteristic.getValue(), 0, tLogByte, 0, 60);
                Log.e("yc-ble","BLE Data(60):" + ByteUtil.byteToString(tLogByte));
            } else {
                YCBTLog.e("BLE Data:" + ByteUtil.byteToString(characteristic.getValue()));
            }
            mBleResponse.bleDataResponse(0, characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e(CMD.BLETAG, "onDescriptorRead " + " status " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            YCBTLog.e("onDescriptorWrite " + " status " + status + " descriptor " + descriptor.getUuid().toString() + " " + Thread.currentThread().toString());
            if (!isEnableWriteChar) {
                setNotificationForCharacteristic(mWriteChar, true);
                isEnableWriteChar = true;
            } else { //第二个读特征使能成功
                mBleState = BleState.Ble_CharacteristicNotification;
                mBleResponse.bleStateResponse(BleState.Ble_CharacteristicNotification);
                //连接成功后, 保存设备地址到本地
                SPUtil.saveBindedDeviceMac(gatt.getDevice().getAddress());
                SPUtil.saveBindedDeviceName(gatt.getDevice().getName());
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            YCBTLog.e("onReliableWriteCompleted " + " status " + status);
        }
    };

    /**
     * Clears the internal cache and forces a refresh of the services from the
     * remote device.
     */
    private boolean refreshDeviceCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    YCBTLog.e("Refreshing device " + bool);
                    return bool;
                }
            } catch (Exception localException) {
                YCBTLog.e("An exception occured while refreshing device");
            }
        }
        return false;
    }


    /* 为characteristic启用/禁用通知 */
    private void setNotificationForCharacteristic(BluetoothGattCharacteristic ch, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;
        boolean success = mBluetoothGatt.setCharacteristicNotification(ch, enabled);
        if (!success) {
            YCBTLog.e("----- Seting proper notification status for characteristic failed!");
        }
        // This is also sometimes required (e.g. for heart rate monitors) to enable notifications/indications
        // see: https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorViewer.aspx?u=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
        BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (descriptor != null) {
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            descriptor.setValue(val);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void connectGatt(String devMac) {
        if (mBleState != BleState.Ble_Disconnect) {
            disconnectGatt();
        }
        if (mBleState == BleState.Ble_Disconnect) {
            BluetoothDevice tBlueDev = mBluetoothAdapter.getRemoteDevice(devMac);
            if (tBlueDev != null) {
                mBleState = BleState.Ble_Connecting;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBluetoothGatt = tBlueDev.connectGatt(bleContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                } else {
                    mBluetoothGatt = tBlueDev.connectGatt(bleContext, false, mGattCallback);
                }
            }
        }
    }

    public void disconnectGatt() {
        if (mBluetoothGatt != null) {
            mBleState = BleState.Ble_Disconnecting;
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBleState = BleState.Ble_Disconnect;
        mBleResponse.bleStateResponse(BleState.Ble_Disconnect);
        SPUtil.saveBindedDeviceMac("");
    }

    public void startScan() {
        if (isScaning) {
            YCBTLog.e("正在搜索 isScaning " + isScaning);
            return;
        }
        isScaning = true;
        mBluetoothAdapter.startLeScan(leScanCallback);
    }

    public void stopScan() {
        if (!isScaning) {
            return;
        }
        isScaning = false;
        mBluetoothAdapter.stopLeScan(leScanCallback);
    }

    public void gattWriteData(byte[] bleData) {
        if (mWriteChar != null && mBluetoothGatt != null) {
            mWriteChar.setValue(bleData);
            boolean isOk = mBluetoothGatt.writeCharacteristic(mWriteChar);
            YCBTLog.e("发送数据 " + ByteUtil.byteToString(bleData) + " 写结果 " + isOk + " " + Thread.currentThread().toString());
        }
    }

    private void closeGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            String bytesStr = ByteUtil.byteToString(bytes);
            //0x10, 0x78, 0x03, 0xe8 厂商ID
            if (bytes.length > 4 && bytesStr.contains("1078") && mBleResponse != null) {//03E8
                YCBTLog.e("onLeScan " + bluetoothDevice.getAddress() + " name " + bluetoothDevice.getName() + " " + Thread.currentThread().getName());
                ScanDeviceBean tBean = new ScanDeviceBean();
                tBean.setDeviceMac(bluetoothDevice.getAddress());
                tBean.setDeviceName(bluetoothDevice.getName());
                tBean.setDeviceRssi(i);
                mBleResponse.bleScanResponse(0, tBean);
            }
        }
    };

}