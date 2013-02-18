package com.frc2410.scoutingapplication;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ServerSettings extends Activity
{
	//Extra For MAC Address
	public final static String EXTRA_MAC = "com.frc2410.scoutingapplication.MAC";
	
	//Intent Request Codes
    private static final int REQUEST_ENABLE_BT = 2;
    
	//BlueTooth Adapter
    private BluetoothAdapter mBtAdapter;
    
    //Arrays for the List Views
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_serversettings);
        
        // Get the local Bluetooth Adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //Make Sure Bluetooth Is Supported
        if (mBtAdapter == null) 
        {
            Toast.makeText(this, "Bluetooth is not available, Online mode cannot be used, Please use offline Mode", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        //Make Sure Bluetooth Is Enabled
        if (!mBtAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } 
        
        // Initialize array adapters
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bluetooth_device);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.pairedDevicesView);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);
        
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) 
        {
            for (BluetoothDevice device : pairedDevices) 
            {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } 
        else 
        {
            mPairedDevicesArrayAdapter.add("No Paried Devices!");
        }
    }
    
    protected void onStart()
    {
    	super.onStart();
    }
    
    protected void onDestroy() 
    {
        super.onDestroy();
        //Stop Discovery
        if (mBtAdapter != null) 
        {
            mBtAdapter.cancelDiscovery();
        }
    }
    
    //Click Listener For List View Items
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
        {
            // Cancel discovery because its computing intensive
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Create New Intent For the Server Link
        	Intent intent = new Intent(ServerSettings.this, ServerLink.class);
        	intent.putExtra(EXTRA_MAC, address);
        	startActivity(intent);
        }
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if (resultCode == Activity.RESULT_OK) 
        {
            // Bluetooth is now enabled continue with execution
        }
        else 
        {
            // User did not enable Bluetooth or an error occured
            Toast.makeText(this, "Bluetooth was not enabled, Online Mode cannot be used", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
