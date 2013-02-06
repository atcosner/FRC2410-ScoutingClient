package com.frc2410.scoutingapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ViewSavedPit extends Activity
{
	private String fileName;
	private PitScoutData pd;
	
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Set View to the Data Screen
        setContentView(R.layout.activity_savedpit);
        
        //Get File Name from Intent
        Intent intent = getIntent();
        fileName = intent.getStringExtra(OfflineData.FILE_NAME);
        
        //Setup Back Button
        Button goBack = (Button) findViewById(R.id.goBackButtonPitReview);
        goBack.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });
        
        //Create Helper Object
        pd = new PitScoutData();
	}
	
	protected void onStart()
	{
		super.onStart();
		
		//Populate Object with Supplies File
		pd.populateFromFile(fileName);
		
		//Variables For Header Data
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberPitReview);
    	EditText teamName = (EditText) findViewById(R.id.teamNamePitReview);
    	EditText strategy = (EditText) findViewById(R.id.strategyPitReview);
    	
    	//Set Header Data
    	teamNumber.setText(String.valueOf(pd.getTeamNumber()));
    	teamName.setText(pd.getTeamName());
    	strategy.setText(pd.getStrategy());
    	
    	//Variables For Shooter Data
    	CheckBox canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPitReview);
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPitReview);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPitReview);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePitReview);
    	CheckBox shooterAngleAdjustable = (CheckBox) findViewById(R.id.adjustableShooterPitReview);
    	CheckBox shooterFloorPickup = (CheckBox) findViewById(R.id.shooterFloorPickUpPitReview);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPitReview);
    	
    	//Set Shooter Data
    	canShoot.setChecked(pd.getShooterStatus());
    	shooterDescription.setText(pd.getShooterDescription());
    	shooterFinalSpeed.setText(String.valueOf(pd.getShooterFinalSpeed()));
    	shooterAngle.setText(String.valueOf(pd.getShooterAngle()));
    	shooterAngleAdjustable.setChecked(pd.getShooterAdjustable());
    	shooterFloorPickup.setChecked(pd.getShooterPickUp());
    	shooterSystemSpeed.setText(String.valueOf(pd.getShooterSystemSpeed()));
    	
    	//Variables For Drivetrain Data
    	EditText drivetrainDescription = (EditText) findViewById(R.id.drivetrainDesciptionPitReview);
    	EditText drivetrainWheels = (EditText) findViewById(R.id.drivetrainWheelsPitReview);
    	EditText drivetrainSpeed = (EditText) findViewById(R.id.drivetrainSpeedPitReview);
    	EditText drivetrainSpecial = (EditText) findViewById(R.id.drivetrainSpecialPitReview);
    	
    	//Set Drivetrain Data
    	drivetrainDescription.setText(pd.getDrivetrainDescription());
    	drivetrainWheels.setText(pd.getDrivetrainWheels());
    	drivetrainSpeed.setText(String.valueOf(pd.getDrivetrainSpeed()));
    	drivetrainSpecial.setText(pd.getDrivetrainSpecial());
    	
    	//Variables for Climbing Data
    	CheckBox canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPitReview);
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPitReview);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevelReview);
    	CheckBox sideClimb = (CheckBox) findViewById(R.id.climbingSidePitReview);
    	CheckBox cornerClimb = (CheckBox) findViewById(R.id.climbingCornerPitReview);
    	
    	//Set Climbing Data
    	canClimb.setChecked(pd.getClimbingStatus());
    	climbingSpeed.setText(String.valueOf(pd.getClimbingSpeed()));
    	climbingHighestLevel.setText(String.valueOf(pd.getClimbingLevel()));
    	sideClimb.setChecked(pd.getSideClimb());
    	cornerClimb.setChecked(pd.getCornerClimb());
    	
    	//Variables for Autonomous Data
    	CheckBox hasAutonomous = (CheckBox) findViewById(R.id.autonomousPitReview);
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPitReview);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPitReview);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPitReview);
    	CheckBox autonomousFloorPickup = (CheckBox) findViewById(R.id.autonomousFloorPickupPitReview);
    	
    	//Set Autonomous Data
    	hasAutonomous.setChecked(pd.getAutonomousStatus());
    	autonomousPlacement.setText(String.valueOf(pd.getAutonomousPlacement()));
    	autonomousNumPreloaded.setText(String.valueOf(pd.getAutonomousNumPreloaded()));
    	autonomousLevelAimed.setText(String.valueOf(pd.getAutonomousLevelAimed()));
    	autonomousFloorPickup.setChecked(pd.getAutonomousFloorPickup());
    	
    	//Variables For Misc Data
    	EditText miscMaintTime = (EditText) findViewById(R.id.miscMaintenanceTimePitReview);
    	EditText miscTripTime = (EditText) findViewById(R.id.miscTripTimePitReview);
    	CheckBox miscAbleToDefend = (CheckBox) findViewById(R.id.miscAbleToDefendPitReview);
    	CheckBox miscColoredDiscs = (CheckBox) findViewById(R.id.miscColoredDiscsPitReview);
    	CheckBox miscVisionTargetting = (CheckBox) findViewById(R.id.miscVisionTargetingReview);
    	EditText miscEstimatedScore = (EditText) findViewById(R.id.miscEstimatedScoreReview);
    	
    	//Set Misc Data
    	miscMaintTime.setText(String.valueOf(pd.getMaintenanceTime()));
    	miscTripTime.setText(String.valueOf(pd.getTripTime()));
    	miscAbleToDefend.setChecked(pd.getAbleToDefend());
    	miscColoredDiscs.setChecked(pd.getColoredDiscs());
    	miscVisionTargetting.setChecked(pd.getVisionTargeting());
    	miscEstimatedScore.setText(String.valueOf(pd.getEstimatedScore()));
	}
}