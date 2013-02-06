package com.frc2410.scoutingapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;


public class CommunicationThread 
{
	//Variable For Handler
    private final Handler CMHandler;
	
    //Variable For streams
    private OutputStream outStream = null;
    private InputStream inStream = null;
    
    //Variables for Bluetooth
    private BluetoothSocket BtSocket;

	public CommunicationThread(BluetoothSocket btSocket, Handler handler)
	{
		BtSocket = btSocket;
		CMHandler = handler;
	}
	
	public void run()
	{
		//Attempt to Create an Output Stream
	    try 
	    {
	        outStream = BtSocket.getOutputStream();
	    } 
	    catch (IOException e) 
	    {
	        //Fatal Error
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_FAILED_OUTPUT_CREATE).sendToTarget();
	    }
	    
	    //Attempt to Create an Input Stream
	    try 
	    {
	        inStream = BtSocket.getInputStream();
	    } 
	    catch (IOException e) 
	    {
	    	//Fatal Error
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_FAILED_INPUT_CREATE).sendToTarget();
	    }
	    
	    
	    //Alert User to Action
	    
	    //Setup for Message
	    String message = "ready";
	    byte[] msgBuffer = message.getBytes();
	    
	    //Attempt to Send the Message
	    try 
	    {
	        outStream.write(msgBuffer);
	    } 
	    catch (IOException e) 
	    {
	    	//Fatal Error
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_FAILED_WRITE_DATA).sendToTarget();
	    }
	    
        try 
        {
            byte[] buffer = new byte[1024];
            int bytes;
        	
        	// Read from the InputStream
        	bytes = inStream.read(buffer);
        	
        	//Create String and Send to UI
        	String serverData = new String(buffer,0,bytes);
        	CMHandler.obtainMessage(ServerLink.MESSAGE_SERVER_DATA,serverData).sendToTarget();
        } 
        catch (IOException e) 
        {
                //connectionLost();
                //break;
        }
        }
	    
	}
