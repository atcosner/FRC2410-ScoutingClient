package com.frc2410.scoutingapplication;

import com.google.analytics.tracking.android.EasyTracker;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainScreen extends Activity
{
    //Button Variables
    private Button scoutTeamButton;
    private Button pitScoutingButton;
    private Button offlineDataButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the Main Screen
        setContentView(R.layout.activity_mainscreen);
    }

    protected void onStart()
    {
    	super.onStart();
    	
    	//Setup Google Analytics For Data Collection
    	EasyTracker.getInstance().activityStart(this);
    	
    	//Setup Scout Match Button
        scoutTeamButton = (Button) findViewById(R.id.scoutMatchButton);
        scoutTeamButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Start Scouting a Match
            	Intent intent = new Intent(MainScreen.this, ClientSettings.class);
            	startActivity(intent);
            }
        });
        
        //Setup Pit Scouting Button
        pitScoutingButton = (Button) findViewById(R.id.pitScoutButton);
        pitScoutingButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Start Pit Scouting
            	Intent intent = new Intent(MainScreen.this, PitScouting.class);
            	startActivity(intent);
            }
        });
        
        //Setup Offline Data Button
        offlineDataButton = (Button) findViewById(R.id.offlineDataButton);
        offlineDataButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Start Offline Data Viewer
            	Intent intent = new Intent(MainScreen.this, OfflineData.class);
            	startActivity(intent);
            }
        });
    }
    
    public void onStop() 
    {
        super.onStop();
        
        //Setup Google Analytics For Data Collection
        EasyTracker.getInstance().activityStop(this);
      }
}
