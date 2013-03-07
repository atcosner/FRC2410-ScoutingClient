package com.frc2410.scoutingapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class UploadCommunicationThread implements Runnable
{
	//Variable For Handler
    private final Handler CMHandler;
	
    //Variable For streams
    private OutputStream outStream = null;
    private InputStream inStream = null;
    
    //Variables for Bluetooth
    private BluetoothSocket BtSocket;

    boolean hasConnection = true;
    
    BufferedReader bReader;
    
    public String matchData = "";
    
    String uploadType;
    
    String uploadData;
    
	public UploadCommunicationThread(BluetoothSocket btSocket, Handler handler, String uploadT, String uploadD)
	{
		//Set Local variables to Passed Parameters
		BtSocket = btSocket;
		CMHandler = handler;
		uploadType = uploadT;
		uploadData = uploadD;
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
	    	CMHandler.obtainMessage(UploadLink.MESSAGE_FAILED_OUTPUT_CREATE).sendToTarget();
	    	hasConnection = false;
	    }
	    
	    //Attempt to Create an Input Stream
	    try 
	    {
	        inStream = BtSocket.getInputStream();
	        bReader = new BufferedReader(new InputStreamReader(inStream));
	    } 
	    catch (IOException e) 
	    {
	    	//Fatal Error
	    	CMHandler.obtainMessage(UploadLink.MESSAGE_FAILED_INPUT_CREATE).sendToTarget();
	    	hasConnection = false;
	    }
	    
	    
	    //Inform UploadLink that Streams were Created Successfully
	    CMHandler.obtainMessage(UploadLink.MESSAGE_STREAMS_CREATED).sendToTarget();
	     
	    try 
	    {
	    	//Create String and Send to UI
	    	String serverData = bReader.readLine();
	    	CMHandler.obtainMessage(UploadLink.MESSAGE_SERVER_DATA,serverData).sendToTarget();

	    	//Analyze Server Command
	    	if(serverData.equals("hello"))
	    	{
	    		//Send Ready
	    		sendData("ready");

	    		//Respond To Pings Until Server Sends Transmit
	    		String serverDataP = "";
	    		do
	    		{
	    			//Read Data From Server
	    			try 
	    			{
	    				serverDataP = bReader.readLine();
	    		        CMHandler.obtainMessage(UploadLink.MESSAGE_SERVER_DATA,serverDataP).sendToTarget();
	    			} 
	    			catch (IOException e) 
	    			{
	    				//Server Disconnected
	    				hasConnection = false;
	    			}

	    			if(hasConnection)
	    			{
	    				//Process Server Command
	    				processCommand(serverDataP);
	    			}
	    		Log.i("CommThread","Checking For Transmit");
	    		}
	    		while(!serverDataP.equals("transmit"));
	    		
	    		Log.i("CommThread","Sent Transmit");
	    		
	    		//Send Upload Type to Server
	    		sendData(uploadType);
		        CMHandler.obtainMessage(UploadLink.MESSAGE_CLIENT_DATA,uploadType).sendToTarget();
		        Log.i("CommThread",uploadType);
		        
	    		//Send Upload Data to Server
	    		sendData(uploadData);
		        CMHandler.obtainMessage(UploadLink.MESSAGE_CLIENT_DATA,uploadData).sendToTarget();
		        Log.i("CommThread",uploadData);
		        
	    		do
	    		{
	    			//Wait for Server to Send Confirmation
	    			try 
	    			{
	    				serverDataP = bReader.readLine();
	    				CMHandler.obtainMessage(UploadLink.MESSAGE_SERVER_DATA,serverDataP).sendToTarget();
	    			} 
	    			catch (IOException e) 
	    			{
	    				//Server Disconnected
	    				hasConnection = false;
	    			}
	    		}
	    		while(!serverDataP.equals("4"));
    			
	    		//Server Sent 4
	    		//Send Confirmation through Handler
	    		CMHandler.obtainMessage(UploadLink.MESSAGE_SEND_COMPLETE).sendToTarget();
	    	}
	    }
	    catch (IOException e) 
	    {
	    	//Server Disconnected
	    	hasConnection = false;
	    }
	}

	public void processCommand(String sC)
	{
		//1 is Server Ping
		if(sC.equals("1"))
		{
			//Respond to Ping with 2
			sendData("2");
		}
		else
		{
			//Unknown Command
			//Do Nothing
		}
	}
	
	private void sendData(String data)
	{
	    //Attempt to Send the Message
		//Create StringBuilder
		StringBuilder sendData = new StringBuilder();
		sendData.append(data);
		//Add Newline because Server uses ReadLine
		sendData.append("\n");
		byte[] msgBuffer = sendData.toString().getBytes();
		
	    try 
	    {
	    	//Write Message to OutStream
	        outStream.write(msgBuffer);
	        
	        //Flush Stream to Ensure Sending
	        outStream.flush();
	        
	        //Alert UI to what we have Send
	        CMHandler.obtainMessage(UploadLink.MESSAGE_CLIENT_DATA,data).sendToTarget();
	    } 
	    catch (IOException e) 
	    {
	    	//Fatal Error
	    	CMHandler.obtainMessage(UploadLink.MESSAGE_FAILED_WRITE_DATA).sendToTarget();
	    }
	}
	    
}
