package yfwang.bluetooth.eventbean;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/7/17 9:54
 */
public class DisConnectedEvent {
    private String msg;

    public DisConnectedEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
