package yfwang.bluetooth.eventbean;

/**
 * Created by HIMan on 2016/11/5.
 */

public class BluetoothScanResultEvent {

    private String msg;

    public BluetoothScanResultEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
