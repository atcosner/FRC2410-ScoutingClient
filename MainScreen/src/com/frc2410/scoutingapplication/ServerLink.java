package com.frc2410.scoutingapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ServerLink extends Activity
{
	//State variables
	public static final int MESSAGE_CONNECTED = 1;
	public static final int MESSAGE_FAILED_CONNECTION = 2;
	public static final int MESSAGE_FAILED_SOCKET_CLOSE = 3;
	public static final int MESSAGE_FAILED_OUTPUT_CREATE = 4;
	public static final int MESSAGE_FAILED_INPUT_CREATE = 5;
	public static final int MESSAGE_FAILED_WRITE_DATA = 6;
	public static final int MESSAGE_SERVER_DATA = 7;
	
	//BlueTooth Adapter
    private BluetoothAdapter BtAdapter;
    
    //Bluetooth Socket
    private BluetoothSocket BtSocket = null;
    
    //SPP UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    //Server Mac Address
    private static String address;
    
    //Text View for output Data
    TextView out;
    
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_serverlink);
        
        // Get the local Bluetooth Adapter
        BtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        //Setup TextView for Debug Data
        out = (TextView) findViewById(R.id.bluetoothDebugTextView);
        
        //Say we have created the activity
        out.append("Activity Create\n");
        
        //Get Server Address from Intent
        Intent intent = getIntent();
        address = intent.getStringExtra(ServerSettings.EXTRA_MAC);
        
        //Write out Server Address
        out.append("Server's MAC Address: " + address + "\n");
	}
	
	protected void onStart()
	{
		//Superclass Mandatory call
		super.onStart();
		
		//Say we have started the onStart
		out.append("Activity Started\n");
		
		//Create A device with the received MAC address
		BluetoothDevice device = BtAdapter.getRemoteDevice(address);
		
		//Create the socket for the Connection
	    try 
	    {
	        BtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
	    } 
	    catch (IOException e) 
	    {
	        AlertBox("Fatal Error", "Socket Create Failure: Online Mode Cannot Proceed" + e.getMessage() + ".");
	    }
	    
	    //Make Sure discovery is off
	    BtAdapter.cancelDiscovery();
	    
	    //Create Thread to Deal with Connection
	    new ConnectionThread(BtSocket,mHandler).run();
	}
	
	
	public void onPause() 
	{
	    super.onPause();
	 
	    out.append("Pausing the Activity\n");
	 
	    try     
	    {
	      BtSocket.close();
	    } 
	    catch (IOException e2) 
	    {
	      AlertBox("Fatal Error", "Failed to Close the Socket on Pause" + e2.getMessage() + ".");
	    }
	  }
	 
	
	  public void AlertBox( String title, String message )
	  {
		    new AlertDialog.Builder(this)
		    .setTitle(title)
		    .setMessage( message + " Press OK to exit" )
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() 
		    {
		        public void onClick(DialogInterface arg0, int arg1) 
		        {
		          finish();
		        }
		    }).show();
	  }
	  
	  // The Handler that gets information back from the Connected thread
	    private final Handler mHandler = new Handler() 
	    {
	        @Override
	        public void handleMessage(Message msg) 
	        {
	            switch (msg.what) 
	            {
	            	case MESSAGE_CONNECTED:
	            		out.append("Connected to Server\n");
	            		out.append("Attempting to Open Streams\n");
	            		break;
	            	case MESSAGE_FAILED_CONNECTION:
	            		out.append("Connection to Server Failed\n");
	            		AlertBox("Fatal Error","Failed to Establish A Connection");
	            		break;
	            	case MESSAGE_FAILED_SOCKET_CLOSE:
	            		out.append("Failed to Close the Socket\n");
	            		AlertBox("Fatal Error","Failed to Close the Socket");
	            		break;
	            	case MESSAGE_SERVER_DATA:
	            		String sD = String.valueOf(msg.obj);
	            		out.append("From Server: " + sD +"\n");
	            		break;
	            	case MESSAGE_FAILED_OUTPUT_CREATE:
	            		out.append("Failed to Create the Output Stream\n");
	            		AlertBox("Fatal Error","Failed to Create an Output Socket");
	            		break;	
	            	case MESSAGE_FAILED_INPUT_CREATE:
	            		out.append("Failed to Create the Input Stream\n");
	            		AlertBox("Fatal Error","Failed to Create an Input Socket");
	            		break;
	            	case MESSAGE_FAILED_WRITE_DATA:
	            		out.append("Failed to Write Data\n");
	            		AlertBox("Fatal Error","Failed to Write Data to Server");
	            		break;
	            }
	        }
	    };
}
