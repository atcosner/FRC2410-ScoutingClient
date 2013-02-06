package com.frc2410.scoutingapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
		
		//Populate Object with Supplies File
		md.populateFromFile(fileName);
		
		//Create Variables For Text Fields
    	EditText matchNumber = (EditText) findViewById(R.id.matchNumberField1Review);
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberField1Review);
    	EditText allianceColor = (EditText) findViewById(R.id.allianceColorFieldReview);
    	EditText pointsScored = (EditText) findViewById(R.id.teamScoreFieldReview);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreFieldReview);
    	EditText penalities = (EditText) findViewById(R.id.penaltiesFieldReview);
    	EditText describeMovement = (EditText) findViewById(R.id.movementFieldReview);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsFieldReview);
    	
    	//Populate Text Fields
    	matchNumber.setText(String.valueOf(md.getMatchNumber()));
    	teamNumber.setText(String.valueOf(md.getTeamNumber()));
    	allianceColor.setText(md.getAllianceColor());
    	pointsScored.setText(String.valueOf(md.getPointsScored()));
    	allianceScore.setText(String.valueOf(md.getAllianceScore()));
    	penalities.setText(String.valueOf(md.getPenalties()));
    	describeMovement.setText(md.getMovementDescription());
    	additionalComments.setText(md.getComments());
    	
    	//Create Variables For Scoring
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBoxReview);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBoxReview);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBoxReview);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBoxReview);
    	RadioButton canScore = (RadioButton) findViewById(R.id.canScoreReview);
    	RadioButton cantScore = (RadioButton) findViewById(R.id.cannotScoreReview);
    	
    	//Populate Scoring Fields
    	boolean[] scoringData = md.getScoringStatus();
    	if(scoringData[0])
    	{
    		canScore.setChecked(true);
    		onePointGoal.setChecked(scoringData[1]);
    		twoPointGoal.setChecked(scoringData[2]);
    		threePointGoal.setChecked(scoringData[3]);
    		fivePointGoal.setChecked(scoringData[4]);
    	}
    	else
    	{
    		cantScore.setChecked(true);
    	}
    	
    	//Create Variables For Climbing
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBoxReview);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBoxReview);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBoxReview);
    	RadioButton canClimb = (RadioButton) findViewById(R.id.canClimbReview);
    	RadioButton cantClimb = (RadioButton) findViewById(R.id.cannotClimbReview);
    	
    	//Populate Climbing Fields
    	boolean[] climbingData = md.getClimbingStatus();
    	if(climbingData[0])
    	{
    		canClimb.setChecked(true);
    		climb1.setChecked(climbingData[1]);
    		climb2.setChecked(climbingData[2]);
    		climb3.setChecked(climbingData[3]);
    	}
    	else
    	{
    		cantClimb.setChecked(true);
    	}
    	
    	//Create Variables For Special Features
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBoxReview);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBoxReview);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBoxReview);
    	
    	//Populate Special Features Fields
    	boolean[] specialData = md.getSpecialFeatures();
    	defense.setChecked(specialData[0]);
    	climbAssist.setChecked(specialData[1]);
    	humanPlayer.setChecked(specialData[2]);
	}
}