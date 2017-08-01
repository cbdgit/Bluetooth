package yfwang.bluetooth.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import yfwang.bluetooth.R;
import yfwang.bluetooth.bean.BluetoothBean;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Author     : yfwang
 * Date       : 2017/7/14 13:57
 */
public class DevicesListAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothBean> mScanData;

    public DevicesListAdapter(Context context) {
        this.context = context;
    }
    public void setData(List<BluetoothBean> mScanData) {
        this.mScanData = mScanData;
    }

    public void clear() {
        mScanData.clear();
    }

    @Override
    public int getCount() {
        return mScanData.size();
    }

    @Override
    public BluetoothBean getItem(int position) {
        if (position > mScanData.size())
            return null;
        return mScanData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.item_scan_result, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
        }

        BluetoothBean result = mScanData.get(position);
        holder.txt_name.setText(result.getName());
        holder.txt_mac.setText(result.getAddress());
        holder.txt_rssi.setText(result.getRssi());
        return convertView;
    }



    class ViewHolder {
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
    }
}
