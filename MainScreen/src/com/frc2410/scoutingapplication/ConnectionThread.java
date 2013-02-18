package com.frc2410.scoutingapplication;

import java.io.IOException;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectionThread implements Runnable
{
    
    //Bluetooth Socket
    private BluetoothSocket BtSocket = null;
	
    //Private Handler
    private final Handler SLHandler;
    
	public ConnectionThread(BluetoothSocket btSocket, Handler handler)
	{
		BtSocket = btSocket;
		SLHandler = handler;
	}
	
	public void run()
	{
		//Attempt to Establish the connection
	    try 
	    {
	    	//Connect to Device
	        BtSocket.connect();
	        
	        //Start Communication Thread
	        Thread connection = new Thread(new CommunicationThread(BtSocket, SLHandler));
	        connection.start();
	    } 
	    catch (IOException e) 
	    {
	    	SLHandler.obtainMessage(ServerLink.MESSAGE_FAILED_CONNECTION).sendToTarget();
	        try 
	        {
	          BtSocket.close();
	        } 
	        catch (IOException e2) 
	        {
	        	SLHandler.obtainMessage(ServerLink.MESSAGE_FAILED_SOCKET_CLOSE).sendToTarget();
	        }
	    }
	    
	}
}
