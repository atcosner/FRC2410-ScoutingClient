package com.frc2410.scoutingapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CommunicationThread implements Runnable
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
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_FAILED_INPUT_CREATE).sendToTarget();
	    	hasConnection = false;
	    }
	    
	    
	    //Streams Created Sucessfully
	    CMHandler.obtainMessage(ServerLink.MESSAGE_STREAMS_CREATED).sendToTarget();
	     
	    try 
	    {

	    	//Create String and Send to UI
	    	String serverData = bReader.readLine();
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_SERVER_DATA,serverData).sendToTarget();

	    	//Analyze Server Command
	    	if(serverData.equals("hello"))
	    	{
	    		//Send Ready
	    		sendData("ready");

	    		//Respond to Pings Until Server Sends 3
	    		String serverDataP = "";
	    		do
	    		{
	    			//Read Data From Server
	    			try 
	    			{
	    				Log.i("Comm Thread" ,"Waiting for Ping Message");
	    				serverDataP = bReader.readLine();
	    			} 
	    			catch (IOException e) 
	    			{
	    				//Server Disconnected
	    				hasConnection = false;
	    			}

	    			if(hasConnection)
	    			{
	    				//Process Server Command
	    				Log.i("Comm Thread" ,"Processing Ping");
	    				processCommand(serverDataP);
	    			}
	    		}
	    		while(!serverDataP.equals("3"));

	    		//Wait for Server Information
	    		String matchNumber = bReader.readLine();
	    		CMHandler.obtainMessage(ServerLink.MESSAGE_SERVER_DATA,matchNumber).sendToTarget();
	    		Log.i("Match Number", matchNumber);
	    		
	    		String teamNumber = bReader.readLine();
	    		CMHandler.obtainMessage(ServerLink.MESSAGE_SERVER_DATA,teamNumber).sendToTarget();
	    		Log.i("Team Number", teamNumber);
	    		
	    		String teamColor = bReader.readLine();
	    		CMHandler.obtainMessage(ServerLink.MESSAGE_SERVER_DATA,teamColor).sendToTarget();
	    		Log.i("Team Color", teamColor);
	    		
	    		//Start a Fields Form With Collected Data
	    		Bundle b = new Bundle();
	    		b.putInt("tN", Integer.parseInt(teamNumber));
	    		b.putInt("mN", Integer.parseInt(matchNumber));
	    		b.putString("tC", teamColor);
	    		FieldsForm.onlineConnection = this;
	    		Message m1 = CMHandler.obtainMessage(ServerLink.MESSAGE_START_DATA_COLLECTION);
	    		m1.setData(b);
	    		m1.sendToTarget();
	    	}
	    }
	    catch (IOException e) 
	    {
	    	//Server Disconnected
	    	hasConnection = false;
	    }
	    
	    //Respond to Server Ping while we wait for Scouting Data to be done
	    Log.i("Comm Thread" ,"Initiating Ping Message");
	    
	    String serverData = "";
	    do
	    {
	    	//Read Data From Server
	    	try 
	    	{
	    		Log.i("Comm Thread" ,"Waiting for Ping Message");
				serverData = bReader.readLine();
			} 
	    	catch (IOException e) 
	    	{
				//Server Disconnected
	    		hasConnection = false;
			}
	    	
	    	//Process Server Command
	    	Log.i("Comm Thread" ,"Processing Ping");
	    	processCommand(serverData);
	    	
	    }
	    while(!FieldsForm.dataReady);
	    
	    //Send Scouting Data
	    if(hasConnection)
	    {
	    	//Send 3 to Signify Ready
	    	sendData("3");
	    	
	    	//Send Scouting Data
	    	sendData(FieldsForm.stringDataToSend);
	    }
	    else
	    {
	    	//No Server Connection 
	    	//Notify Fields Form
	    	FieldsForm.commThreadStatusCode = 2;
	    }
	    
	    //Keep Connection Alive Until Server Has Acknowledged Receive
    	try 
    	{
    		Log.i("Comm Thread" ,"Waiting until Server Acknowledged");
			serverData = bReader.readLine();
		} 
    	catch (IOException e) 
    	{
			//Server Disconnected
    		hasConnection = false;
		}
    	
    	if(hasConnection)
    	{
    		Log.i("Comm Thread" ,"Recieved Server Data");
    		Log.i("Comm Thread" ,serverData);
    		if(serverData.equals("4")||serverData.equals("1"))
    		{
    			Log.i("Comm Thread" ,"Recieved Server Ack.");
    			//Server Has Received Scouting Data
    	    	//Notify Fields Form that We are Done
    	    	FieldsForm.commThreadStatusCode = 1;
    		}
    		else
    		{
    			//Unknown Command
    	    	FieldsForm.commThreadStatusCode = 2;
    		}
    	}
	}

	public void processCommand(String sC)
	{
		if(sC.equals("1"))
		{
			sendData("2");
		}
		else
		{
			//Unknown Command
			//Do Nothing
		}
	}
	
	synchronized void sleepThread()
	{
		try 
		{
			wait();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void sendData(String data)
	{
	    //Attempt to Send the Message
		StringBuilder sendData = new StringBuilder();
		sendData.append(data);
		sendData.append("\n");
		byte[] msgBuffer = sendData.toString().getBytes();
		
	    try 
	    {
	        outStream.write(msgBuffer);
	        outStream.flush();
	        CMHandler.obtainMessage(ServerLink.MESSAGE_CLIENT_DATA,data).sendToTarget();
	    } 
	    catch (IOException e) 
	    {
	    	//Fatal Error
	    	CMHandler.obtainMessage(ServerLink.MESSAGE_FAILED_WRITE_DATA).sendToTarget();
	    }
	}
	    
}
