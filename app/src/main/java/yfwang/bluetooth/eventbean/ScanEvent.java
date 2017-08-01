package yfwang.bluetooth.eventbean;


import yfwang.bluetooth.bean.BluetoothBean;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/7/17 9:54
 */
public class ScanEvent {
    private BluetoothBean bluetoothBean;

    public ScanEvent(BluetoothBean bluetoothBean) {
        this.bluetoothBean = bluetoothBean;
    }

    public BluetoothBean getResult() {
        return bluetoothBean;
    }

    public void setResult(BluetoothBean bluetoothBean) {
        this.bluetoothBean = bluetoothBean;
    }
}
