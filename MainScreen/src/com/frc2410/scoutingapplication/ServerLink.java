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

public class ServerLink extends Activity
{
	//Variable to Pass As Extra
	public final static String EXTRA_TEAM_NUMBER = "com.frc2410.scoutingapplication.teamnumber";
	public final static String EXTRA_TEAM_COLOR = "com.frc2410.scoutingapplication.teamcolor";
	public final static String EXTRA_MATCH_NUMBER = "com.frc2410.scoutingapplication.matchnumber";
	
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
    
    public static boolean inActivity = true;
    
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_serverlink);
        
        //Setup Abandon Button
        Button quitButton = (Button) findViewById(R.id.quitConnectionButton);
        quitButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Send disconnect signal to Server
            	
            	
            	//Close Activity
            	finish();
            }
        });
        
        // Get the local Bluetooth Adapter
        BtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        //Setup TextView for Debug Data
        out = (TextView) findViewById(R.id.bluetoothDebugTextView);
        
        //Make Text View Scrollable
        out.setMovementMethod(new ScrollingMovementMethod());
        
        //Say we have created the activity
        out.append("Activity Create\n");
        
        //Get Server Address from Intent
        Intent intent = getIntent();
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
  	    new Thread(new ConnectionThread(BtSocket, mHandler)).start();
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
	            	case MESSAGE_START_DATA_COLLECTION:
	            		out.append("Got Message to Start Data Collection\n");
	            		Bundle b = msg.getData();
	            		int teamNumber = b.getInt("tN");
	            		int matchNumber = b.getInt("mN");
	            		String teamColor = b.getString("tC");
	            		out.append("Team Number: " + teamNumber + "\n");
	            		out.append("Match Number: " + matchNumber + "\n");
	            		out.append("Team Color: " + teamColor + "\n");
	            		out.append("Starting Fields Form\n");
	                	Intent intent = new Intent(ServerLink.this, FieldsForm.class);
	                	intent.putExtra("EXTRA_MODE", "Online");
	                	intent.putExtra(ServerLink.EXTRA_MATCH_NUMBER,matchNumber);
	                	intent.putExtra(ServerLink.EXTRA_TEAM_NUMBER,teamNumber);
	                	intent.putExtra(ServerLink.EXTRA_TEAM_COLOR,teamColor);
	                	startActivity(intent);
	            		break;
	            }
	        }
	    };
}
