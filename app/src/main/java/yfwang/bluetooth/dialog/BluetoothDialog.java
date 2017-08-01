package yfwang.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import yfwang.bluetooth.R;
import yfwang.bluetooth.adapter.DevicesListAdapter;
import yfwang.bluetooth.bean.BluetoothBean;
import yfwang.bluetooth.eventbean.CancelScanEvent;

/**
 * Description: 蓝牙连接列表
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/7/14 10:22
 */
public class BluetoothDialog extends Dialog {
    private Context context;
    private List<BluetoothBean> mScanData;
    private ListView mDevices;
    private DevicesListAdapter devicesListAdapter;
    private OnItemClickListener onItemClickListener;

    public BluetoothDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public BluetoothDialog(@NonNull Context context) {
        super(context, R.style.commmon_window_style);
        this.context = context;
        this.mScanData = new ArrayList<>();
        setContentView(R.layout.dialog_bluetooth);
        init();
    }


    public void addResult(BluetoothBean result) {
        if (mScanData != null) {
            mScanData.add(result);
            devicesListAdapter.notifyDataSetChanged();
        }
    }


    private void init() {
        mDevices = (ListView) findViewById(R.id.list_device);
        devicesListAdapter = new DevicesListAdapter(context);
        devicesListAdapter.setData(mScanData);
        mDevices.setAdapter(devicesListAdapter);
        mDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(devicesListAdapter.getItem(position));
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothBean result);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBackPressed() {
        if (isShowing()) {
            EventBus.getDefault().post(new CancelScanEvent("Cancle"));
        }
        super.onBackPressed();
    }

}
