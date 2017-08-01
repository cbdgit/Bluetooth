package yfwang.bluetooth.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import yfwang.bluetooth.R;
import yfwang.bluetooth.bean.BluetoothBean;
import yfwang.bluetooth.dialog.BluetoothDialog;
import yfwang.bluetooth.eventbean.BluetoothScanResultEvent;
import yfwang.bluetooth.eventbean.ConnectEvent;
import yfwang.bluetooth.eventbean.ConnectSuccessEvent;
import yfwang.bluetooth.eventbean.DisConnectedEvent;
import yfwang.bluetooth.eventbean.ScanEvent;
import yfwang.bluetooth.service.BluetoothService;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mData;
    private Button mContact;
    private BluetoothDialog bluetoothDialog;
    private ProgressDialog progressDialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        mData = (TextView) findViewById(R.id.tv_data);
        mContact = (Button) findViewById(R.id.btn_contact);
        progressDialog = new ProgressDialog(this);
        mContact.setOnClickListener(this);
        bluetoothDialog = new BluetoothDialog(BluetoothActivity.this);
        bluetoothDialog.setOnItemClickListener(new BluetoothDialog.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothBean result) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
                if (bluetoothDialog.isShowing()) {
                    bluetoothDialog.dismiss();
                }
                EventBus.getDefault().post(new ConnectEvent(result.getAddress()));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (intent != null)
            stopService(intent);
    }

    public void onEvent(final Object event) {
        if (event != null && event instanceof BluetoothScanResultEvent) {
            mData.setText("");
            mData.setText(((BluetoothScanResultEvent) event).getMsg());

        } else if (event != null && event instanceof ScanEvent) {
            showDialog();
            bluetoothDialog.addResult(((ScanEvent) event).getResult());

        }  else if (event != null && event instanceof ConnectSuccessEvent) {
            dismissDialog();
            Toast.makeText(BluetoothActivity.this, R.string.connected, Toast.LENGTH_SHORT).show();

        }  else if (event != null && event instanceof DisConnectedEvent) {
            dismissDialog();
            Toast.makeText(BluetoothActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (bluetoothDialog.isShowing()) {
            bluetoothDialog.dismiss();
        }
    }

    private void showDialog() {
        if (!bluetoothDialog.isShowing()) {
            bluetoothDialog.show();
        }
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_contact) {
            checkPermissions();
        }

    }


    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                startService();
                break;
        }
    }


    private void startService() {
        intent = new Intent(this, BluetoothService.class);
        startService(intent);
    }


}
