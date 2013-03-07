package com.frc2410.scoutingapplication;

import java.io.IOException;
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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UploadLink extends Activity
{	
	//State variables
	public static final int MESSAGE_CONNECTED = 1;
	public static final int MESSAGE_FAILED_CONNECTION = 2;
	public static final int MESSAGE_FAILED_SOCKET_CLOSE = 3;
	public static final int MESSAGE_FAILED_OUTPUT_CREATE = 4;
	public static final int MESSAGE_FAILED_INPUT_CREATE = 5;
	public static final int MESSAGE_FAILED_WRITE_DATA = 6;
	public static final int MESSAGE_SERVER_DATA = 7;
	public static final int MESSAGE_STREAMS_CREATED = 8;
	public static final int MESSAGE_SERVER_WAIT = 9;
	public static final int MESSAGE_SENT_READY = 10;
	public static final int MESSAGE_CLIENT_DATA = 11;
	public static final int MESSAGE_START_DATA_COLLECTION = 12;
	public static final int MESSAGE_SEND_COMPLETE = 13;
	
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
    
    //Upload type
    String uploadType;
    
    //Upload String
    String uploadData;
    
    public static boolean inActivity = true;
    
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_uploadlink);
        
        //Setup TextView for Debug Data
        out = (TextView) findViewById(R.id.bluetoothDebugTextViewUpload);
        
        //Make Text View Scrollable
        out.setMovementMethod(new ScrollingMovementMethod());
        
        //Get Extra for Upload Type
        Intent intent = getIntent();
        uploadType = intent.getStringExtra("EXTRA_UPLOAD_TYPE");
        out.append("Upload Type: " + uploadType + "\n");
        
        //Get Extra for Upload Data
        uploadData = intent.getStringExtra("EXTRA_UPLOAD_DATA");
        out.append("Upload Data: " + uploadData + "\n");
        
        //Setup Abandon Button
        Button quitButton = (Button) findViewById(R.id.quitConnectionButtonUpload);
        quitButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Close Activity
            	finish();
            }
        });
        
        // Get the local Bluetooth Adapter
        BtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        //Say we have created the activity
        out.append("Activity Create\n");
        
        //Get Server Address from Intent
        address = intent.getStringExtra(ServerSettings.EXTRA_MAC);
        
        //Write out Server Address
        out.append("Server's MAC Address: " + address + "\n");
        
        //Create A device with the received MAC address
  		BluetoothDevice device = BtAdapter.getRemoteDevice(address);
  		
  	    try 
  	    {
  	  		//Create the socket for the Connection
  	        BtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
  	    } 
  	    catch (IOException e) 
  	    {
  	        AlertBox("Fatal Error", "Socket Create Failure: Online Mode Cannot Proceed" + e.getMessage() + ".");
  	    }
  	    
  	    //Make Sure discovery is off
  	    BtAdapter.cancelDiscovery();
  	    
  	    //Create Thread to Deal with Connection
  	    new Thread(new UploadConnectionThread(BtSocket, mHandler, uploadType, uploadData)).start();
	}
	
	protected void onStart()
	{
		//Superclass Mandatory Start call
		super.onStart();
		
		//Say we have started the onStart
		out.append("Activity Started\n");
    	inActivity = true;
	}
	
	
	public void onPause() 
	{
	    super.onPause();
    	inActivity = false;
	    out.append("Pausing the Activity\n");
	}
	
	public void onDestroy()
	{
	    super.onDestroy();
		 
	    out.append("Destroying the Activity\n");
	 
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
	            		if(inActivity)
	            		{
	            			AlertBox("Fatal Error","Failed to Establish A Connection");
	            		}
	            		break;
	            	case MESSAGE_FAILED_SOCKET_CLOSE:
	            		out.append("Failed to Close the Socket\n");
	            		if(inActivity)
	            		{
	            			AlertBox("Fatal Error","Failed to Close the Socket");
	            		}
	            		break;
	            	case MESSAGE_SERVER_DATA:
	            		String sD = String.valueOf(msg.obj);
	            		out.append("From Server: " + sD +"\n");
	            		break;
	            	case MESSAGE_FAILED_OUTPUT_CREATE:
	            		out.append("Failed to Create the Output Stream\n");
	            		if(inActivity)
	            		{
	            			AlertBox("Fatal Error","Failed to Create an Output Socket");
	            		}
	            		break;	
	            	case MESSAGE_FAILED_INPUT_CREATE:
	            		out.append("Failed to Create the Input Stream\n");
	            		if(inActivity)
	            		{
	            			AlertBox("Fatal Error","Failed to Create an Input Socket");
	            		}
	            		break;
	            	case MESSAGE_FAILED_WRITE_DATA:
	            		out.append("Failed to Write Data\n");
	            		if(inActivity)
	            		{
	            			AlertBox("Fatal Error","Failed to Write Data to Server");
	            		}
	            		break;
	            	case MESSAGE_STREAMS_CREATED:
	            		out.append("Input/Output Streams Created Sucesfully\n");
	            		break;
	            	case MESSAGE_SENT_READY:
	            		out.append("Sent Ready Command to Server\n");
	            		break;
	            	case MESSAGE_CLIENT_DATA:
	            		String cD = String.valueOf(msg.obj);
	            		out.append("To Server: " + cD +"\n");
	            		break;
	            	case MESSAGE_SEND_COMPLETE:
	            		out.append("Send Complete\n");
	            		//Create Toast to Inform User
	            		Toast toast = Toast.makeText(getApplicationContext(), "Uploaded Data was Recieved By the Server!", Toast.LENGTH_LONG);
	            		toast.show();
	            		
						//Return to Main Screen
						Intent startMain = new Intent(UploadLink.this, MainScreen.class);
						startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(startMain);
	            		break;
	            }
	        }
	    };
}
