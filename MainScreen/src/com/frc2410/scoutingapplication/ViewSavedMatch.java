package com.frc2410.scoutingapplication;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

public class ViewSavedMatch extends Activity
{
	private String fileName;
	private MatchScoutData md;
	
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the Data Screen
        setContentView(R.layout.activity_savedmatch);
        
        //Get File Name from Intent
        Intent intent = getIntent();
        fileName = intent.getStringExtra(OfflineData.FILE_NAME);
        
        //Setup Back Button
        Button goBack = (Button) findViewById(R.id.goBackButtonReview);
        goBack.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });
        
        //Create Helper Object
        md = new MatchScoutData();
	}
	
	protected void onStart()
	{
		super.onStart();
		
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStart(this);
    	
		//Populate Object with Supplies File
		md.populateFromFile(fileName);
		
		//Create Variables For Text Fields
    	EditText matchNumber = (EditText) findViewById(R.id.matchNumberFieldReview);
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberFieldReview);
    	EditText allianceColor = (EditText) findViewById(R.id.allianceColorReview);
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreFieldReview);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreFieldReview);
    	EditText teamPenalties = (EditText) findViewById(R.id.teamPenaltiesFieldReview);
    	EditText alliancePenalites = (EditText) findViewById(R.id.alliancePenaltiesFieldReview);
    	EditText describeMovement = (EditText) findViewById(R.id.movementFieldReview);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsFieldReview);
    	
    	//Populate Text Fields
    	matchNumber.setText(String.valueOf(md.getMatchNumber()));
    	teamNumber.setText(String.valueOf(md.getTeamNumber()));
    	allianceColor.setText(md.getAllianceColor());
    	teamScore.setText(String.valueOf(md.getPointsScored()));
    	allianceScore.setText(String.valueOf(md.getAllianceScore()));
    	teamPenalties.setText(String.valueOf(md.getTeamPenalties()));
    	alliancePenalites.setText(String.valueOf(md.getAlliancePenalties()));
    	describeMovement.setText(md.getMovementDescription());
    	additionalComments.setText(md.getComments());
    	
    	//Create Variables For Shooting
    	RadioButton canShoot = (RadioButton) findViewById(R.id.canShootReview);
    	RadioButton cantShoot = (RadioButton) findViewById(R.id.cannotShootReview);
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBoxReview);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBoxReview);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBoxReview);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBoxReview);
    	EditText teamPenaltiesShooter = (EditText) findViewById(R.id.shooterTeamPenaltiesReview);
    	EditText alliancePenaltiesShooter = (EditText) findViewById(R.id.shooterAlliancePenaltiesReview);
    	EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMadeReview);
    	EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMadeReview);
    	EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMadeReview);
    	EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMadeReview);
    	EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTakenReview);
    	EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTakenReview);
    	EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTakenReview);
    	EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTakenReview);
    	
    	//Populate Scoring Fields
    	boolean[] shootingDataB = md.getShootingBooleans();
    	if(shootingDataB[0])
    	{
    		canShoot.setChecked(true);
    		onePointGoal.setChecked(shootingDataB[1]);
    		twoPointGoal.setChecked(shootingDataB[2]);
    		threePointGoal.setChecked(shootingDataB[3]);
    		fivePointGoal.setChecked(shootingDataB[4]);
    	}
    	else
    	{
    		cantShoot.setChecked(true);
    	}
    	int[] shootingDataI = md.getShootingInts();
    	teamPenaltiesShooter.setText(String.valueOf(shootingDataI[0]));
    	alliancePenaltiesShooter.setText(String.valueOf(shootingDataI[1]));
    	ShotsMade1Point.setText(String.valueOf(shootingDataI[2]));
    	ShotsMade2Point.setText(String.valueOf(shootingDataI[3]));
    	ShotsMade3Point.setText(String.valueOf(shootingDataI[4]));
    	ShotsMade5Point.setText(String.valueOf(shootingDataI[5]));
    	ShotsTaken1Point.setText(String.valueOf(shootingDataI[6]));
    	ShotsTaken2Point.setText(String.valueOf(shootingDataI[7]));
    	ShotsTaken3Point.setText(String.valueOf(shootingDataI[8]));
    	ShotsTaken5Point.setText(String.valueOf(shootingDataI[9]));
    	
    	//Create Variables For Climbing
    	RadioButton canClimb = (RadioButton) findViewById(R.id.canClimbReview);
    	RadioButton cantClimb = (RadioButton) findViewById(R.id.cannotClimbReview);
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBoxReview);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBoxReview);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBoxReview);
    	EditText sucessfulClimbs = (EditText) findViewById(R.id.sucessfulClimbsReview);
    	EditText climbAttempts = (EditText) findViewById(R.id.climbingTotalClimbAttemptsReview);
    	EditText climbingTeamPenalties = (EditText) findViewById(R.id.climbingTeamPenaltiesReview);
    	EditText climbingAlliancePenalties = (EditText) findViewById(R.id.climbingAlliancePenaltiesReview);
    	
    	//Populate Climbing Fields
    	boolean[] climbingDataB = md.getClimbingBooleans();
    	if(climbingDataB[0])
    	{
    		canClimb.setChecked(true);
    		climb1.setChecked(climbingDataB[1]);
    		climb2.setChecked(climbingDataB[2]);
    		climb3.setChecked(climbingDataB[3]);
    	}
    	else
    	{
    		cantClimb.setChecked(true);
    	}
    	int[] climbingDataI = md.getClimbingInts();
    	sucessfulClimbs.setText(String.valueOf(climbingDataI[0]));
    	climbAttempts.setText(String.valueOf(climbingDataI[1]));
    	climbingTeamPenalties.setText(String.valueOf(climbingDataI[2]));
    	climbingAlliancePenalties.setText(String.valueOf(climbingDataI[3]));
    	
    	//Create Variables for Autonomous
    	RadioButton hasAutonomous = (RadioButton) findViewById(R.id.hasAutonomousReview);
    	RadioButton noAutonomous = (RadioButton) findViewById(R.id.noAutonomousReview);
    	EditText autonomousTeamPenalties = (EditText) findViewById(R.id.teamPenaltiesAutonomousReview);
    	EditText autonomousAlliancePenalties = (EditText) findViewById(R.id.alliancePenaltiesAutonomousReview);
    	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomousReview);
    	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomousReview);
    	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomousReview);
    	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomousReview);
    	EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomousReview);
    	EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomousReview);
    	EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomousReview);
    	EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomousReview);
    	EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomousReview);
    	EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomousReview);
    	EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomousReview);
    	EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomousReview);
    	
    	boolean[] autonomousDataB = md.getAutonomousBooleans();
    	if(autonomousDataB[0])
    	{
    		hasAutonomous.setChecked(true);
    		onePointGoalA.setChecked(autonomousDataB[1]);
    		twoPointGoalA.setChecked(autonomousDataB[2]);
    		threePointGoalA.setChecked(autonomousDataB[3]);
    		fivePointGoalA.setChecked(autonomousDataB[4]);
    	}
    	else
    	{
    		noAutonomous.setChecked(true);
    	}
    	int[] autonomousDataI = md.getAutonomousInts();
    	autonomousTeamPenalties.setText(String.valueOf(autonomousDataI[0]));
    	autonomousAlliancePenalties.setText(String.valueOf(autonomousDataI[1]));
    	ShotsMade1PointA.setText(String.valueOf(autonomousDataI[2]));
    	ShotsMade2PointA.setText(String.valueOf(autonomousDataI[3]));
    	ShotsMade3PointA.setText(String.valueOf(autonomousDataI[4]));
    	ShotsMade5PointA.setText(String.valueOf(autonomousDataI[5]));
    	ShotsTaken1PointA.setText(String.valueOf(autonomousDataI[6]));
    	ShotsTaken2PointA.setText(String.valueOf(autonomousDataI[7]));
    	ShotsTaken3PointA.setText(String.valueOf(autonomousDataI[8]));
    	ShotsTaken5PointA.setText(String.valueOf(autonomousDataI[9]));
    	
    	//Create Variables For Special Features
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBoxReview);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBoxReview);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBoxReview);
    	EditText defenseRank = (EditText) findViewById(R.id.defenseRankReview);
    	EditText humanPlayerPenalties = (EditText) findViewById(R.id.humanPlayerPenaltiesReview);
    	EditText humanPlayerShotsMade = (EditText) findViewById(R.id.humanPlayerShotsMadeReview);
    	EditText humanPlayerShotsTaken = (EditText) findViewById(R.id.humanPlayerShotsTakenReview);
    	
    	//Populate Special Features Fields
    	boolean[] specialDataB = md.getSpecialFeaturesBooleans();
    	defense.setChecked(specialDataB[0]);
    	climbAssist.setChecked(specialDataB[1]);
    	humanPlayer.setChecked(specialDataB[2]);
    	int[] specialDataI = md.getSpecialFeaturesInts();
    	defenseRank.setText(String.valueOf(specialDataI[0]));
    	humanPlayerPenalties.setText(String.valueOf(specialDataI[1]));
    	humanPlayerShotsMade.setText(String.valueOf(specialDataI[2]));
    	humanPlayerShotsTaken.setText(String.valueOf(specialDataI[3]));
	}
	
	protected void onStop()
	{
		super.onStop();
		
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStop(this);
	}
}