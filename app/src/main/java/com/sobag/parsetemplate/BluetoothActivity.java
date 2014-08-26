package com.sobag.parsetemplate;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.sobag.parsetemplate.R;

public class BluetoothActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        System.out.println("Adapter: " + mBluetoothAdapter);

        BTScanStart();
    }

    // Start the Bluetooth Scan
    private void BTScanStart() {
        if (mBluetoothAdapter == null) {
            System.out.println("Bluetooth NOT supported. Aborting.");
            return;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                System.out.println("Bluetooth is enabled...");

                // Starting the device discovery
                mBluetoothAdapter.startLeScan(mLeScanCallback);

            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            count++;
            System.out.println("Found " + count + ":" + device + " " + rssi + "db");
            device.connectGatt(null, false, mGattCallback);
            if (count > 5) mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    };

    // Gatt Callback
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                System.out.println(gatt.getDevice() + ": Connected.. ");
                gatt.readRemoteRssi();
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                System.out.println(gatt.getDevice() + ": Disconnected.. ");
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            System.out.println(gatt.getDevice() + " RSSI:" + rssi + "db ");
            try
            {
                Thread.sleep(300);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            gatt.readRemoteRssi();

        }
    };
}
