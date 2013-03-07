package com.frc2410.scoutingapplication;

import java.io.IOException;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class UploadConnectionThread implements Runnable
{ 
    //Bluetooth Socket
    private BluetoothSocket BtSocket = null;
	
    //Private Handler
    private final Handler ULHandler;
    
    //Upload Type
    String uploadType;
    
    //Upload Data
    String uploadData;
    
	public UploadConnectionThread(BluetoothSocket btSocket, Handler handler, String uploadT, String uploadD)
	{
		//Assign Parameters to Local Variables
		BtSocket = btSocket;
		ULHandler = handler;
		uploadType = uploadT;
		uploadData = uploadD;
	}
	
	public void run()
	{
		//Attempt to Establish the connection
	    try 
	    {
	    	//Connect to Device
	        BtSocket.connect();
	        
	        //Start Communication Thread
	        Thread connection = new Thread(new UploadCommunicationThread(BtSocket, ULHandler, uploadType, uploadData));
	        connection.start();
	    } 
	    catch (IOException e) 
	    {
	    	ULHandler.obtainMessage(ServerLink.MESSAGE_FAILED_CONNECTION).sendToTarget();
	        try 
	        {
	          BtSocket.close();
	        } 
	        catch (IOException e2) 
	        {
	        	ULHandler.obtainMessage(ServerLink.MESSAGE_FAILED_SOCKET_CLOSE).sendToTarget();
	        }
	    }
	    
	}
}
