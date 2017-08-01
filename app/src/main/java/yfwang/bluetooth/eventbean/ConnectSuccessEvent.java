package yfwang.bluetooth.eventbean;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/7/17 9:54
 */
public class ConnectSuccessEvent {
    private String msg;

    public ConnectSuccessEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
