package com.frc2410.scoutingapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientSettings extends Activity
{
	//Variable to Pass As Network Mode
	public final static String EXTRA_MODE = "EXTRA_MODE";
	
	//Button Declarations
    private Button onlineButton;
    private Button offlineButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_clientsettings);
    }
    
    protected void onStart()
    {
    	super.onStart();
    	
    	//Setup Online Button With a Click Listener
        onlineButton = (Button) findViewById(R.id.onlineButton);
        onlineButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Start Server Config Activity
            	Intent intent = new Intent(ClientSettings.this, ServerSettings.class);
            	startActivity(intent);
            }
        });
        
        //Setup Offline Button With a Click Listener
        offlineButton = (Button) findViewById(R.id.offlineButton);
        offlineButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Start Fields Form Activity in Offline Mode
            	Intent intent = new Intent(ClientSettings.this, FieldsForm.class);
            	intent.putExtra(EXTRA_MODE, "Offline");
            	startActivity(intent);
            }
        });
    }
}