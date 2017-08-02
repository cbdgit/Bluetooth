package yfwang.bluetooth.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import yfwang.bluetooth.bean.BluetoothBean;
import yfwang.bluetooth.eventbean.BluetoothScanResultEvent;
import yfwang.bluetooth.eventbean.CancelScanEvent;
import yfwang.bluetooth.eventbean.ConnectEvent;
import yfwang.bluetooth.eventbean.ConnectSuccessEvent;
import yfwang.bluetooth.eventbean.DisConnectedEvent;
import yfwang.bluetooth.eventbean.ScanEvent;

/**
 * Description: 蓝牙扫码枪服务
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/6/21 11:23
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothService extends Service {

    private static final String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String UUID_NOTIFY = "0000fff1-0000-1000-8000-00805f9b34fb";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    public static BluetoothGatt bluetoothGatt;
    private Handler threadHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        //检查是否支持BLE4.0
        if (!isSupportBle()) {
            Toast.makeText(this, "您的设备不支持ble4.0", Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.enable();

    }

    public void onEvent(Object event) {
        if (event != null && event instanceof ConnectEvent) {
            cancelScan();
            connectDevice(((ConnectEvent) event).getMsg());
        } else if (event != null && event instanceof CancelScanEvent) {
            cancelScan();
        }

    }

    private void scanDevice() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                bluetoothLeScanner.startScan(scanCallback);
            } else {
                Toast.makeText(this, "请开启蓝牙!", Toast.LENGTH_SHORT).show();
                bluetoothAdapter.enable();

            }
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.startLeScan(leScanCallback);
            } else {
                Toast.makeText(this, "请开启蓝牙!", Toast.LENGTH_SHORT).show();
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 搜索蓝牙  API 21以下版本
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothBean bluetoothBean = new BluetoothBean();
                    bluetoothBean.setName(device.getName());
                    bluetoothBean.setAddress(device.getAddress());
                    bluetoothBean.setRssi(rssi + "");
                    EventBus.getDefault().post(new ScanEvent(bluetoothBean));
                }
            });


        }
    };

    /**
     * 搜索蓝牙  API 21(包含)以上版本
     */
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothBean bluetoothBean = new BluetoothBean();
                    bluetoothBean.setName(result.getDevice().getName());
                    bluetoothBean.setAddress(result.getDevice().getAddress());
                    bluetoothBean.setRssi(result.getRssi() + "");
                    EventBus.getDefault().post(new ScanEvent(bluetoothBean));
                }
            });


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    /**
     * 判断是否支持ble
     */
    private boolean isSupportBle() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 取消搜索
     */
    public void cancelScan() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.stopLeScan(leScanCallback);
            }
        }
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("info", "bluetooth is connected");
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new ConnectSuccessEvent("ConnectSuccess"));
                    }
                });
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("info", "bluetooth is disconnected");
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new DisConnectedEvent("DisConnected"));
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

          /*  List<BluetoothGattService> supportedGattServices = bluetoothGatt.getServices();

            for (BluetoothGattService supportedGattService : supportedGattServices) {

                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : supportedGattService.getCharacteristics()) {

                    int properties = bluetoothGattCharacteristic.getProperties();
                    if (BluetoothGattCharacteristic.PROPERTY_NOTIFY == properties) {
                        //具备通知属性
                        UUID bluetoothGattCharacteristicUuid = bluetoothGattCharacteristic.getUuid();
                        UUID supportedGattServiceUuid = supportedGattService.getUuid();
                      }

                }


            }*/

            BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(UUID_SERVICE));
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_NOTIFY));
            //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发
            bluetoothGatt.setCharacteristicNotification(characteristic, true);


        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();
            Log.e("info", "读取成功" + new String(data));
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new BluetoothScanResultEvent(new String(data)));
                }
            });
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scanDevice();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean connectDevice(String address) {
        if (bluetoothAdapter == null || address == null) {
            return false;
        }
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        bluetoothGatt = device.connectGatt(this, true, gattCallback);
        return true;


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }

}
