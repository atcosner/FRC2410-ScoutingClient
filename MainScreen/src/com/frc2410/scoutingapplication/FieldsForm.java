package com.frc2410.scoutingapplication;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class FieldsForm extends Activity implements OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener
{
	private String networkMode;
	private Spinner colorSpinner;
    private Button submitButton;
    private Button resetButton;
    private Button backButton;
    private boolean validateShooterData = true;
    private boolean validateClimbingData = true;
    private boolean validateAutonomousData = true;
    
	//Create MatchScoutData Object
	MatchScoutData md = new MatchScoutData();
    
	//Create Variable to set during Validation
	boolean dataToProceed;
    
	//Thread Variable for Online mode
	public static CommunicationThread onlineConnection;
	
	//Variable for String data
	public static String stringDataToSend;
	
    int matchNumber;
    int teamNumber;
    String teamColor;
    
    public static boolean dataReady = false;
    public static int commThreadStatusCode = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_dataentry);
        
        //Get Mode from Intent
        Intent intent = getIntent();
        networkMode = intent.getStringExtra("EXTRA_MODE");  
        
        if(networkMode.equals("Online"))    	
        {
            matchNumber = intent.getIntExtra(ServerLink.EXTRA_MATCH_NUMBER, 0);
            teamNumber = intent.getIntExtra(ServerLink.EXTRA_TEAM_NUMBER,0);
            teamColor = intent.getStringExtra(ServerLink.EXTRA_TEAM_COLOR);
            Log.i(String.valueOf(matchNumber));
            Log.i(String.valueOf(teamNumber));
            Log.i(teamColor);
        }
        
        //Initialize The Color Spinner
        colorSpinner = (Spinner) findViewById(R.id.allianceColorSpinner);
        
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.colorchoices, R.layout.spinner_item);
        
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Apply the adapter to the spinner
        colorSpinner.setAdapter(adapter);
        
        //Initialize Buttons For Use
        submitButton = (Button) findViewById(R.id.submitDataButton);
        resetButton = (Button) findViewById(R.id.resetDataButton);
        backButton = (Button) findViewById(R.id.goBackButton);
        
        //Initialize Shooter Goal Check Boxes
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	
    	//Add Click Listeners
    	onePointGoal.setOnCheckedChangeListener(this);
    	twoPointGoal.setOnCheckedChangeListener(this);
    	threePointGoal.setOnCheckedChangeListener(this);
    	fivePointGoal.setOnCheckedChangeListener(this);
    	
        //Initialize Autonomous Goal Check Boxes
    	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomous);
    	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomous);
    	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomous);
    	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomous);
    	
    	//Add Click Listeners
    	onePointGoalA.setOnCheckedChangeListener(this);
    	twoPointGoalA.setOnCheckedChangeListener(this);
    	threePointGoalA.setOnCheckedChangeListener(this);
    	fivePointGoalA.setOnCheckedChangeListener(this);
    	
    	//Initialize Special Features Check Boxes
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	
    	//Add Click Listeners
    	defense.setOnCheckedChangeListener(this);
    	humanPlayer.setOnCheckedChangeListener(this);
    }
    
    protected void onStop()
    {
    	super.onStop();
    	
    	//Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStop(this);
    }
    
    protected void onStart()
    {
    	super.onStart();
    	
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStart(this);
    	
    	//Setup Reset Button to Clear Data
        resetButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
            	builder.setCancelable(false);
            	builder.setTitle("Confirm Reset");
            	builder.setMessage("Are you Sure you want to clear all entered Data?");
            	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
            	{
					public void onClick(DialogInterface dialog, int which) 
					{
						clearEnteredData();
					}
				});
				builder.setNegativeButton("Heck No!", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});
            	builder.show();
            }
        });
        
        //Setup Radio Group For Climb and Score
        RadioGroup shootGroup = (RadioGroup) findViewById(R.id.shootGroup);
        shootGroup.setOnCheckedChangeListener(this);
        RadioGroup climbGroup = (RadioGroup) findViewById(R.id.climbGroup);
        climbGroup.setOnCheckedChangeListener(this);
        RadioGroup autonomousGroup = (RadioGroup) findViewById(R.id.autonomousGroup);
        autonomousGroup.setOnCheckedChangeListener(this);
        
        //Setup Buttons for Online/Offline Mode
    	if(networkMode.equals("Offline"))
    	{
            //Setup Button to go back to the previous Activity
            backButton.setOnClickListener(new OnClickListener() 
            {
                public void onClick(View v) 
                {
                	finish();
                }
            });
            
    		//Change Submit to Save
    		submitButton.setText("Save");
    		
    		//Setup Button Listener
            submitButton.setOnClickListener(new OnClickListener() 
            {
                public void onClick(View v) 
                {
                	//Validate Data
                	if(goodTextEntryData())
                	{
                		if(goodShooterData())
                		{
                    		if(goodClimbData())
                    		{
                    			if(goodAutonomousData())
                        		{
                        			if(goodSpecialFeaturesData())
                            		{
                            			dataToProceed = true;
                            		}
                            		else
                            		{
                            			dataToProceed = false;
                                    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                                    	builder.setCancelable(false);
                                    	builder.setTitle("Data Error");
                                    	builder.setMessage("Please Complete the Special Features Section!");
                                    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                                    	{
                        					public void onClick(DialogInterface dialog, int which) 
                        					{
                        						dialog.cancel();
                        					}
                        				});
                                    	builder.show();
                            		}
                        		}
                        		else
                        		{
                        			dataToProceed = false;
                                	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                                	builder.setCancelable(false);
                                	builder.setTitle("Data Error");
                                	builder.setMessage("Please Complete the Autonomous Section!");
                                	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                                	{
                    					public void onClick(DialogInterface dialog, int which) 
                    					{
                    						dialog.cancel();
                    					}
                    				});
                                	builder.show();
                        		}
                    		}
                    		else
                    		{
                    			dataToProceed = false;
                            	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                            	builder.setCancelable(false);
                            	builder.setTitle("Data Error");
                            	builder.setMessage("Please Complete the Climbing Section!");
                            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                            	{
                					public void onClick(DialogInterface dialog, int which) 
                					{
                						dialog.cancel();
                					}
                				});
                            	builder.show();
                    		}
                		}
                		else
                		{
                			dataToProceed = false;
                        	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                        	builder.setCancelable(false);
                        	builder.setTitle("Data Error");
                        	builder.setMessage("Please Complete the Shooting Section!");
                        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                        	{
            					public void onClick(DialogInterface dialog, int which) 
            					{
            						dialog.cancel();
            					}
            				});
                        	builder.show();
                		}
                	}
                	else
                	{
                		dataToProceed = false;
                    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                    	builder.setCancelable(false);
                    	builder.setTitle("Data Error");
                    	builder.setMessage("Please Enter Data in all of the Text Fields!");
                    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                    	{
        					public void onClick(DialogInterface dialog, int which) 
        					{
        						dialog.cancel();
        					}
        				});
                    	builder.show();
                	}
                	if(dataToProceed)
                	{
                		//Show Data To User to confirm Save operation
                		confirmUserSave("Offline");
                	}
                }
            });
    	}
    	else if(networkMode.equals("Online"))
    	{
            
            //Remove Back Button
            backButton.setEnabled(false);
            
            //Add Know Data to Componets
        	EditText matchNumberF = (EditText) findViewById(R.id.matchNumberField);
        	EditText teamNumberF = (EditText) findViewById(R.id.teamNumberField);
        	matchNumberF.setText(String.valueOf(matchNumber));
        	matchNumberF.setEnabled(false);
        	teamNumberF.setText(String.valueOf(teamNumber));
        	teamNumberF.setEnabled(false);
        	
        	//Determine Alliance Color
        	if(teamColor.equals("red"))
        	{
        		colorSpinner.setSelection(1);
        	}
        	else
        	{
        		colorSpinner.setSelection(0);
        	}
        	colorSpinner.setEnabled(false);
        	
        	//Setup Submit Button
        	//Setup Button Listener
            submitButton.setOnClickListener(new OnClickListener() 
            {
                public void onClick(View v) 
                {
                	//Validate Data
                	if(goodTextEntryData())
                	{
                		if(goodShooterData())
                		{
                    		if(goodClimbData())
                    		{
                    			if(goodAutonomousData())
                        		{
                        			if(goodSpecialFeaturesData())
                            		{
                            			dataToProceed = true;
                            		}
                            		else
                            		{
                            			dataToProceed = false;
                                    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                                    	builder.setCancelable(false);
                                    	builder.setTitle("Data Error");
                                    	builder.setMessage("Please Complete the Special Features Section!");
                                    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                                    	{
                        					public void onClick(DialogInterface dialog, int which) 
                        					{
                        						dialog.cancel();
                        					}
                        				});
                                    	builder.show();
                            		}
                        		}
                        		else
                        		{
                        			dataToProceed = false;
                                	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                                	builder.setCancelable(false);
                                	builder.setTitle("Data Error");
                                	builder.setMessage("Please Complete the Autonomous Section!");
                                	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                                	{
                    					public void onClick(DialogInterface dialog, int which) 
                    					{
                    						dialog.cancel();
                    					}
                    				});
                                	builder.show();
                        		}
                    		}
                    		else
                    		{
                    			dataToProceed = false;
                            	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                            	builder.setCancelable(false);
                            	builder.setTitle("Data Error");
                            	builder.setMessage("Please Complete the Climbing Section!");
                            	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                            	{
                					public void onClick(DialogInterface dialog, int which) 
                					{
                						dialog.cancel();
                					}
                				});
                            	builder.show();
                    		}
                		}
                		else
                		{
                			dataToProceed = false;
                        	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                        	builder.setCancelable(false);
                        	builder.setTitle("Data Error");
                        	builder.setMessage("Please Complete the Shooting Section!");
                        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                        	{
            					public void onClick(DialogInterface dialog, int which) 
            					{
            						dialog.cancel();
            					}
            				});
                        	builder.show();
                		}
                	}
                	else
                	{
                		dataToProceed = false;
                    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                    	builder.setCancelable(false);
                    	builder.setTitle("Data Error");
                    	builder.setMessage("Please Enter Data in all of the Text Fields!");
                    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
                    	{
        					public void onClick(DialogInterface dialog, int which) 
        					{
        						dialog.cancel();
        					}
        				});
                    	builder.show();
                	}
                	if(dataToProceed)
                	{
                		//Show Data To User to confirm Save operation
                		confirmUserSave("Online");
                	}
                }
            });
    	}
    	else
    	{
    		
    	}
    }
    
    private void clearEnteredData()
    {
    	//Variables for Header Data
    	EditText matchNumber = (EditText) findViewById(R.id.matchNumberField);
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberField);
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText teamPenalites = (EditText) findViewById(R.id.teamPenaltiesField);
    	EditText alliancePenalites = (EditText) findViewById(R.id.alliancePenaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);
    	
    	//Clear Components
    	matchNumber.setText("");
    	teamNumber.setText("");
    	teamScore.setText("");
    	allianceScore.setText("");
    	teamPenalites.setText("");
    	alliancePenalites.setText("");
    	describeMovement.setText("");
    	additionalComments.setText("");
    	
    	//Variables for Shooting
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	EditText teamPenaltiesShooter = (EditText) findViewById(R.id.shooterTeamPenalties);
    	EditText alliancePenaltiesShooter = (EditText) findViewById(R.id.shooterAlliancePenalties);
    	EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMade);
    	EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMade);
    	EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMade);
    	EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMade);
    	EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTaken);
    	EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTaken);
    	EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTaken);
    	EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTaken);
    	
    	//Clear Components
    	onePointGoal.setChecked(false);
    	twoPointGoal.setChecked(false);
    	threePointGoal.setChecked(false);
    	fivePointGoal.setChecked(false);
    	teamPenaltiesShooter.setText("");
    	alliancePenaltiesShooter.setText("");
    	ShotsMade1Point.setText("");
    	ShotsMade2Point.setText("");
    	ShotsMade3Point.setText("");
    	ShotsMade5Point.setText("");
    	ShotsTaken1Point.setText("");
    	ShotsTaken2Point.setText("");
    	ShotsTaken3Point.setText("");
    	ShotsTaken5Point.setText("");
    	
    	//Variables for Climbing
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
    	EditText sucessfulClimbs = (EditText) findViewById(R.id.sucessfulClimbs);
    	EditText climbAttempts = (EditText) findViewById(R.id.climbingTotalClimbAttempts);
    	EditText climbingTeamPenalties = (EditText) findViewById(R.id.climbingTeamPenalties);
    	EditText climbingAlliancePenalties = (EditText) findViewById(R.id.climbingAlliancePenalties);
    	
    	//Clear Components
    	climb1.setChecked(false);
    	climb2.setChecked(false);
    	climb3.setChecked(false);
    	sucessfulClimbs.setText("");
    	climbAttempts.setText("");
    	climbingTeamPenalties.setText("");
    	climbingAlliancePenalties.setText("");
    	
    	//Components for Autonomous
    	EditText autonomousTeamPenalties = (EditText) findViewById(R.id.teamPenaltiesAutonomous);
    	EditText autonomousAlliancePenalties = (EditText) findViewById(R.id.alliancePenaltiesAutonomous);
    	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomous);
    	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomous);
    	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomous);
    	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomous);
    	EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomous);
    	EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomous);
    	EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomous);
    	EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomous);
    	EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomous);
    	EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomous);
    	
    	//Clear Components
    	autonomousTeamPenalties.setText("");
    	autonomousAlliancePenalties.setText("");
    	onePointGoalA.setChecked(false);
    	twoPointGoalA.setChecked(false);
    	threePointGoalA.setChecked(false);
    	fivePointGoalA.setChecked(false);
    	ShotsMade1PointA.setText("");
    	ShotsMade2PointA.setText("");
    	ShotsMade3PointA.setText("");
    	ShotsMade5PointA.setText("");
    	ShotsTaken1PointA.setText("");
    	ShotsTaken2PointA.setText("");
    	ShotsTaken3PointA.setText("");
    	ShotsTaken5PointA.setText("");
    	
    	//Components for Special Features
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	EditText defenseRank = (EditText) findViewById(R.id.defenseRank);
    	EditText humanPlayerPenalties = (EditText) findViewById(R.id.humanPlayerPenalties);
    	EditText humanPlayerShotsMade = (EditText) findViewById(R.id.humanPlayerShotsMade);
    	EditText humanPlayerShotsTaken = (EditText) findViewById(R.id.humanPlayerShotsTaken);
    	
    	//Clear Components
    	defense.setChecked(false);
    	climbAssist.setChecked(false);
    	humanPlayer.setChecked(false);
    	defenseRank.setText("");
    	humanPlayerPenalties.setText("");
    	humanPlayerShotsMade.setText("");
    	humanPlayerShotsTaken.setText("");
    }
    
    public boolean goodAutonomousData()
    {
    	boolean goodAutoData = true;
    	EditText autonomousTeamPenalties = (EditText) findViewById(R.id.teamPenaltiesAutonomous);
    	EditText autonomousAlliancePenalties = (EditText) findViewById(R.id.alliancePenaltiesAutonomous);
    	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomous);
    	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomous);
    	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomous);
    	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomous);
    	EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomous);
    	EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomous);
    	EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomous);
    	EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomous);
    	EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomous);
    	EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomous);
    	
    	//If Has Autonomous Radio Button Is Checked
    	if(validateAutonomousData)
    	{
        	if(!autonomousTeamPenalties.getText().toString().equals(""))
        	{
            	if(!autonomousAlliancePenalties.getText().toString().equals(""))
            	{
            		if(!onePointGoalA.isChecked()&&!twoPointGoalA.isChecked()&&!threePointGoalA.isChecked()&&!fivePointGoalA.isChecked())
            		{
            			goodAutoData = false;
            		}
            		else
            		{
            			if(onePointGoalA.isChecked())
            			{
            				if(ShotsMade1PointA.getText().toString().equals("")||ShotsTaken1PointA.getText().toString().equals(""))
            				{
            					goodAutoData = false;
            				}
            			}

            			if(twoPointGoalA.isChecked())
            			{
            				if(ShotsMade2PointA.getText().toString().equals("")||ShotsTaken2PointA.getText().toString().equals(""))
            				{
            					goodAutoData = false;
            				}
            			}

            			if(threePointGoalA.isChecked())
            			{
            				if(ShotsMade3PointA.getText().toString().equals("")||ShotsTaken3PointA.getText().toString().equals(""))
            				{
            					goodAutoData = false;
            				}
            			}

            			if(fivePointGoalA.isChecked())
            			{
            				if(ShotsMade5PointA.getText().toString().equals("")||ShotsTaken5PointA.getText().toString().equals(""))
            				{
            					goodAutoData = false;
            				}
            			}
            		}
            	}
            	else
            	{
            		goodAutoData = false;
            	}
        	}
        	else
        	{
        		goodAutoData = false;
        	}
    	}
    	return goodAutoData;
    }
    
    public boolean goodSpecialFeaturesData()
    {
    	boolean goodSFData = true;
    	
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	EditText defenseRank = (EditText) findViewById(R.id.defenseRank);
    	EditText humanPlayerPenalties = (EditText) findViewById(R.id.humanPlayerPenalties);
    	EditText humanPlayerShotsMade = (EditText) findViewById(R.id.humanPlayerShotsMade);
    	EditText humanPlayerShotsTaken = (EditText) findViewById(R.id.humanPlayerShotsTaken);
    	
    	if(defense.isChecked())
    	{
    		if(defenseRank.getText().toString().equals(""))
    		{
    			goodSFData = false;
    		}
    	}
    	
    	if(humanPlayer.isChecked())
    	{
    		if(!humanPlayerPenalties.getText().toString().equals(""))
    		{
        		if(!humanPlayerShotsMade.getText().toString().equals(""))
        		{
            		if(!humanPlayerShotsTaken.getText().toString().equals(""))
            		{
            			goodSFData = true;
            		}
            		else
            		{
            			goodSFData = false;
            		}
        		}
        		else
        		{
        			goodSFData = false;
        		}
    		}
    		else
    		{
    			goodSFData = false;
    		}
    	}
    	
    	return goodSFData;
    }
    
    public boolean goodTextEntryData()
    {
    	boolean goodTextData = false;
    	EditText matchNumber = (EditText) findViewById(R.id.teamScoreField);
    	EditText teamNumber = (EditText) findViewById(R.id.teamScoreField);
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText teamPenalites = (EditText) findViewById(R.id.teamPenaltiesField);
    	EditText alliancePenalites = (EditText) findViewById(R.id.alliancePenaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);
    	
    	if(!matchNumber.getText().toString().equals(""))
    	{
    		if(!teamNumber.getText().toString().equals(""))
    		{
    			if(!teamScore.getText().toString().equals(""))
    			{
    				if(!teamScore.getText().toString().equals(""))
    				{
    					if(!allianceScore.getText().toString().equals(""))
    					{
    						if(!teamPenalites.getText().toString().equals(""))
    						{
    							if(!alliancePenalites.getText().toString().equals(""))
    							{
    								if(!describeMovement.getText().toString().equals(""))
    								{
    									if(!additionalComments.getText().toString().equals(""))
    									{
    										goodTextData = true;
    									}
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return goodTextData;
    }
    
    public boolean goodShooterData()
    {
    	boolean goodShooterData = true;
    	
    	//If Can Shoot Radio Button Is Checked
    	if(validateShooterData)
    	{
        	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
        	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
        	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
        	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
        	EditText teamPenaltiesShooter = (EditText) findViewById(R.id.shooterTeamPenalties);
        	EditText alliancePenaltiesShooter = (EditText) findViewById(R.id.shooterAlliancePenalties);
        	EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMade);
        	EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMade);
        	EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMade);
        	EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMade);
        	EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTaken);
        	EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTaken);
        	EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTaken);
        	EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTaken);
        	
        	//Check for Checked Boxes is Can Shoot is Checked
        	if(!teamPenaltiesShooter.getText().toString().equals(""))
        	{
            	if(!alliancePenaltiesShooter.getText().toString().equals(""))
            	{
            		if(!onePointGoal.isChecked()&&!twoPointGoal.isChecked()&&!threePointGoal.isChecked()&&!fivePointGoal.isChecked())
            		{
            			goodShooterData = false;
            		}
            		else
            		{
            			if(onePointGoal.isChecked())
            			{
            				if(ShotsMade1Point.getText().toString().equals("")||ShotsTaken1Point.getText().toString().equals(""))
            				{
            					goodShooterData = false;
            				}
            			}

            			if(twoPointGoal.isChecked())
            			{
            				if(ShotsMade2Point.getText().toString().equals("")||ShotsTaken2Point.getText().toString().equals(""))
            				{
            					goodShooterData = false;
            				}
            			}

            			if(threePointGoal.isChecked())
            			{
            				if(ShotsMade3Point.getText().toString().equals("")||ShotsTaken3Point.getText().toString().equals(""))
            				{
            					goodShooterData = false;
            				}
            			}

            			if(fivePointGoal.isChecked())
            			{
            				if(ShotsMade5Point.getText().toString().equals("")||ShotsTaken5Point.getText().toString().equals(""))
            				{
            					goodShooterData = false;
            				}
            			}
            		}
            	}
            	else
            	{
            		goodShooterData = false;
            	}
        	}
        	else
        	{
        		goodShooterData = false;
        	}
    	}
    	return goodShooterData;
    }
    
    //Check For Good Climb Data
    public boolean goodClimbData()
    {
    	boolean goodClimbData = false;
    	
    	//If Can Climb Radio Button Is Checked
    	if(validateClimbingData)
    	{
    		//Make Sure all boxes are not empty
        	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
        	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
        	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
        	EditText sucessfulClimbs = (EditText) findViewById(R.id.sucessfulClimbs);
        	EditText climbAttempts = (EditText) findViewById(R.id.climbingTotalClimbAttempts);
        	EditText climbingTeamPenalties = (EditText) findViewById(R.id.climbingTeamPenalties);
        	EditText climbingAlliancePenalties = (EditText) findViewById(R.id.climbingAlliancePenalties);
        	
        	if(!climb1.isChecked()&&!climb2.isChecked()&&!climb3.isChecked())
        	{
        		goodClimbData = false;
        	}
        	else
        	{
        		if(!sucessfulClimbs.getText().toString().equals(""))
        		{
            		if(!climbAttempts.getText().toString().equals(""))
            		{
                		if(!climbingTeamPenalties.getText().toString().equals(""))
                		{
                    		if(!climbingAlliancePenalties.getText().toString().equals(""))
                    		{
                    			goodClimbData = true;
                    		}
                    		else
                    		{
                    			goodClimbData = false;
                    		}
                		}
                		else
                		{
                			goodClimbData = false;
                		}
            		}
            		else
            		{
            			goodClimbData = false;
            		}
        		}
        		else
        		{
        			goodClimbData = false;
        		}
        	}
    	}
    	else
    	{
    		goodClimbData = true;
    	}
    	
    	return goodClimbData;
    }
    
    public void onCheckedChanged(RadioGroup changedGroup, int checkedId)
    {
    	//If Checked Status has Changed on any RadioButtons/CheckBoxes
    	if(changedGroup.getId() == R.id.shootGroup)
    	{
        	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
        	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
        	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
        	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
        	EditText teamPenaltiesShooter = (EditText) findViewById(R.id.shooterTeamPenalties);
        	EditText alliancePenaltiesShooter = (EditText) findViewById(R.id.shooterAlliancePenalties);
        	EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMade);
        	EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMade);
        	EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMade);
        	EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMade);
        	EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTaken);
        	EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTaken);
        	EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTaken);
        	EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTaken);
    	
    		switch(checkedId)
    		{
    			case R.id.canShoot:
    				onePointGoal.setClickable(true);
    				twoPointGoal.setClickable(true);
    				threePointGoal.setClickable(true);
    				fivePointGoal.setClickable(true);
    				ShotsMade1Point.setEnabled(true);
    				ShotsMade2Point.setEnabled(true);
    				ShotsMade3Point.setEnabled(true);
    				ShotsMade5Point.setEnabled(true);
    				ShotsTaken1Point.setEnabled(true);
    				ShotsTaken2Point.setEnabled(true);
    				ShotsTaken3Point.setEnabled(true);
    				ShotsTaken5Point.setEnabled(true);
    				teamPenaltiesShooter.setEnabled(true);
    				alliancePenaltiesShooter.setEnabled(true);
    				validateShooterData = true;
    				break;
    			case R.id.cannotShoot:
    				onePointGoal.setClickable(false);
    				twoPointGoal.setClickable(false);
    				threePointGoal.setClickable(false);
    				fivePointGoal.setClickable(false);
    				onePointGoal.setChecked(false);
    				twoPointGoal.setChecked(false);
    				threePointGoal.setChecked(false);
    				fivePointGoal.setChecked(false);
    				ShotsMade1Point.setEnabled(false);
    				ShotsMade2Point.setEnabled(false);
    				ShotsMade3Point.setEnabled(false);
    				ShotsMade5Point.setEnabled(false);
    				ShotsTaken1Point.setEnabled(false);
    				ShotsTaken2Point.setEnabled(false);
    				ShotsTaken3Point.setEnabled(false);
    				ShotsTaken5Point.setEnabled(false);
    				teamPenaltiesShooter.setEnabled(false);
    				validateShooterData = false;
    				break;
    		}
    	}
    	else if(changedGroup.getId() == R.id.climbGroup)
    	{
        	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
        	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
        	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
        	EditText sucessfulClimbs = (EditText) findViewById(R.id.sucessfulClimbs);
        	EditText climbAttempts = (EditText) findViewById(R.id.climbingTotalClimbAttempts);
        	EditText climbingTeamPenalties = (EditText) findViewById(R.id.climbingTeamPenalties);
        	EditText climbingAlliancePenalties = (EditText) findViewById(R.id.climbingAlliancePenalties);
        	
    		switch(checkedId)
    		{
    			case R.id.canClimb:
    				climb1.setClickable(true);
    				climb2.setClickable(true);
    				climb3.setClickable(true);
    				sucessfulClimbs.setEnabled(true);
    				climbAttempts.setEnabled(true);
    				climbingTeamPenalties.setEnabled(true);
    				climbingAlliancePenalties.setEnabled(true);
    				validateClimbingData = true;
    				break;
    			case R.id.cannotClimb:
    				climb1.setClickable(false);
    				climb2.setClickable(false);
    				climb3.setClickable(false);
    				climb1.setChecked(false);
    				climb2.setChecked(false);
    				climb3.setChecked(false);
    				sucessfulClimbs.setEnabled(false);
    				climbAttempts.setEnabled(false);
    				climbingTeamPenalties.setEnabled(false);
    				validateClimbingData = false;
    				break;
    		}
    	}
    	else if(changedGroup.getId() == R.id.autonomousGroup)
    	{
        	EditText autonomousTeamPenalties = (EditText) findViewById(R.id.teamPenaltiesAutonomous);
        	EditText autonomousAlliancePenalties = (EditText) findViewById(R.id.alliancePenaltiesAutonomous);
        	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomous);
        	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomous);
        	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomous);
        	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomous);
        	EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomous);
        	EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomous);
        	EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomous);
        	EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomous);
        	EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomous);
        	EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomous);
        	EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomous);
        	EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomous);
        	
    		switch(checkedId)
    		{
    			case R.id.hasAutonomous:
    				onePointGoalA.setClickable(true);
    				twoPointGoalA.setClickable(true);
    				threePointGoalA.setClickable(true);
    				fivePointGoalA.setClickable(true);
    				autonomousTeamPenalties.setEnabled(true);
    				autonomousAlliancePenalties.setEnabled(true);
    				ShotsMade1PointA.setEnabled(true);
    				ShotsMade2PointA.setEnabled(true);
    				ShotsMade3PointA.setEnabled(true);
    				ShotsMade5PointA.setEnabled(true);
    				ShotsTaken1PointA.setEnabled(true);
    				ShotsTaken2PointA.setEnabled(true);
    				ShotsTaken3PointA.setEnabled(true);
    				ShotsTaken5PointA.setEnabled(true);
    				validateAutonomousData = true;
    				break;
    			case R.id.noAutonomous:
    				onePointGoalA.setClickable(false);
    				twoPointGoalA.setClickable(false);
    				threePointGoalA.setClickable(false);
    				fivePointGoalA.setClickable(false);
    				onePointGoalA.setChecked(false);
    				twoPointGoalA.setChecked(false);
    				threePointGoalA.setChecked(false);
    				fivePointGoalA.setChecked(false);
    				autonomousTeamPenalties.setEnabled(false);
    				ShotsMade1PointA.setEnabled(false);
    				ShotsMade2PointA.setEnabled(false);
    				ShotsMade3PointA.setEnabled(false);
    				ShotsMade5PointA.setEnabled(false);
    				ShotsTaken1PointA.setEnabled(false);
    				ShotsTaken2PointA.setEnabled(false);
    				ShotsTaken3PointA.setEnabled(false);
    				ShotsTaken5PointA.setEnabled(false);
    				validateAutonomousData = false;
    				break;
    		}
    	}
    }
    
    public void confirmUserSave(String mode)
    {
    	//Mode String
    	final String opMode = mode;
    	
    	//Text Data Components
    	EditText matchNumber = (EditText) findViewById(R.id.matchNumberField);
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberField);
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText teamPenalites = (EditText) findViewById(R.id.teamPenaltiesField);
    	EditText alliancePenalites = (EditText) findViewById(R.id.alliancePenaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);
    	
    	//Shooter Components
    	RadioButton canShoot = (RadioButton) findViewById(R.id.canShoot);
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	EditText teamPenaltiesShooter = (EditText) findViewById(R.id.shooterTeamPenalties);
    	EditText alliancePenaltiesShooter = (EditText) findViewById(R.id.shooterAlliancePenalties);
    	EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMade);
    	EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMade);
    	EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMade);
    	EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMade);
    	EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTaken);
    	EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTaken);
    	EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTaken);
    	EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTaken);
    	
    	//Climbing Components
    	RadioButton canClimb = (RadioButton) findViewById(R.id.canClimb);
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
    	EditText sucessfulClimbs = (EditText) findViewById(R.id.sucessfulClimbs);
    	EditText climbAttempts = (EditText) findViewById(R.id.climbingTotalClimbAttempts);
    	EditText climbingTeamPenalties = (EditText) findViewById(R.id.climbingTeamPenalties);
    	EditText climbingAlliancePenalties = (EditText) findViewById(R.id.climbingAlliancePenalties);
    	
    	//Autonomous Components
    	RadioButton hasAutonomous = (RadioButton) findViewById(R.id.hasAutonomous);
    	EditText autonomousTeamPenalties = (EditText) findViewById(R.id.teamPenaltiesAutonomous);
    	EditText autonomousAlliancePenalties = (EditText) findViewById(R.id.alliancePenaltiesAutonomous);
    	CheckBox onePointGoalA = (CheckBox) findViewById(R.id.onePointGoalAutonomous);
    	CheckBox twoPointGoalA = (CheckBox) findViewById(R.id.twoPointGoalAutonomous);
    	CheckBox threePointGoalA = (CheckBox) findViewById(R.id.threePointGoalAutonomous);
    	CheckBox fivePointGoalA = (CheckBox) findViewById(R.id.fivePointGoalAutonomous);
    	EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomous);
    	EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomous);
    	EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomous);
    	EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomous);
    	EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomous);
    	EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomous);
    	EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomous);
    	
    	//Special Features Components
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	EditText defenseRank = (EditText) findViewById(R.id.defenseRank);
    	EditText humanPlayerPenalties = (EditText) findViewById(R.id.humanPlayerPenalties);
    	EditText humanPlayerShotsMade = (EditText) findViewById(R.id.humanPlayerShotsMade);
    	EditText humanPlayerShotsTaken = (EditText) findViewById(R.id.humanPlayerShotsTaken);
    	
    	//Set Data to Zero if it was disabled
    	if(!canShoot.isChecked())
    	{
    		teamPenaltiesShooter.setText("0");
    		ShotsMade1Point.setText("0");
    		ShotsTaken1Point.setText("0");
    		ShotsMade2Point.setText("0");
    		ShotsTaken2Point.setText("0");
    		ShotsMade3Point.setText("0");
    		ShotsTaken3Point.setText("0");
    		ShotsMade5Point.setText("0");
    		ShotsTaken5Point.setText("0");
    	}
    	if(!canClimb.isChecked())
    	{
    		sucessfulClimbs.setText("0");
    		climbAttempts.setText("0");
    		climbingTeamPenalties.setText("0");
    	}
    	if(!hasAutonomous.isChecked())
    	{
    		autonomousTeamPenalties.setText("0");
    		ShotsMade1PointA.setText("0");
    		ShotsTaken1PointA.setText("0");
    		ShotsMade2PointA.setText("0");
    		ShotsTaken2PointA.setText("0");
    		ShotsMade3PointA.setText("0");
    		ShotsTaken3PointA.setText("0");
    		ShotsMade5PointA.setText("0");
    		ShotsTaken5PointA.setText("0");
    	}
    	if(!defense.isChecked())
    	{
    		defenseRank.setText("0");
    	}
    	if(!humanPlayer.isChecked())
    	{
    		humanPlayerPenalties.setText("0");
    		humanPlayerShotsMade.setText("0");
    		humanPlayerShotsTaken.setText("0");
    	}
    	
    	//Set Shooter Shots Values to Zero if their goal is not checked
    	if(!onePointGoal.isChecked())
    	{
    		ShotsMade1Point.setText("0");
    		ShotsTaken1Point.setText("0");
    	}
    	if(!twoPointGoal.isChecked())
    	{
    		ShotsMade2Point.setText("0");
    		ShotsTaken2Point.setText("0");
    	}
    	if(!threePointGoal.isChecked())
    	{
    		ShotsMade3Point.setText("0");
    		ShotsTaken3Point.setText("0");
    	}
    	if(!fivePointGoal.isChecked())
    	{
    		ShotsMade5Point.setText("0");
    		ShotsTaken5Point.setText("0");
    	}
    	
    	//Set Autonomous Shots Values to Zero if their goal is not checked
    	if(!onePointGoalA.isChecked())
    	{
    		ShotsMade1PointA.setText("0");
    		ShotsTaken1PointA.setText("0");
    	}
    	if(!twoPointGoalA.isChecked())
    	{
    		ShotsMade2PointA.setText("0");
    		ShotsTaken2PointA.setText("0");
    	}
    	if(!threePointGoalA.isChecked())
    	{
    		ShotsMade3PointA.setText("0");
    		ShotsTaken3PointA.setText("0");
    	}
    	if(!fivePointGoalA.isChecked())
    	{
    		ShotsMade5PointA.setText("0");
    		ShotsTaken5PointA.setText("0");
    	}
    	
    	//Set Alliance Penalties to Zero if not added
    	if(alliancePenaltiesShooter.getText().toString().equals(""))
    	{
    		alliancePenaltiesShooter.setText("0");
    	}
    	if(climbingAlliancePenalties.getText().toString().equals(""))
    	{
    		climbingAlliancePenalties.setText("0");
    	}
    	if(autonomousAlliancePenalties.getText().toString().equals(""))
    	{
    		autonomousAlliancePenalties.setText("0");
    	}
    	
    	//Build The Strings for the Alert Box
    	StringBuilder mes = new StringBuilder();
    	mes.append("Match Number: " + matchNumber.getText() + "\n");
    	mes.append("Team Number: " + teamNumber.getText() + "\n");
    	mes.append("Alliance Color: " + colorSpinner.getSelectedItem().toString() + "\n");
    	mes.append("Team Score: " + teamScore.getText() + "\n");
    	mes.append("Alliance Score: " + allianceScore.getText() + "\n");
    	mes.append("Total Team Penalties: " + teamPenalites.getText() + "\n");
    	mes.append("Total Alliance Penalties: " + alliancePenalites.getText() + "\n");
    	
    	mes.append("\n");
    	
    	if(canShoot.isChecked())
    	{
        	mes.append("Shooting: Can Shoot" + "\n");
        	mes.append("Team Shooting Penalties: " + teamPenaltiesShooter.getText() + "\n");
        	mes.append("Alliance Shooting Penalties: " + alliancePenaltiesShooter.getText() + "\n");
        	mes.append("Can Shoot on:" + "\n");
        	mes.append("One Point Goal: " + onePointGoal.isChecked() + "\n");
        	if(onePointGoal.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade1Point.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken1Point.getText() + "\n");
        	}
        	mes.append("Two Point Goal: " + twoPointGoal.isChecked() + "\n");
        	if(twoPointGoal.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade2Point.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken2Point.getText() + "\n");
        	}
        	mes.append("Three Point Goal: " + threePointGoal.isChecked() + "\n");
        	if(threePointGoal.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade3Point.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken3Point.getText() + "\n");
        	}
        	mes.append("Five Point Goal: " + fivePointGoal.isChecked() + "\n");
        	if(fivePointGoal.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade5Point.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken5Point.getText() + "\n");
        	}
    	}
    	else
    	{
    		mes.append("Shooting: Cannot Shoot" + "\n");
    	}
    	
    	mes.append("\n");
    	
    	if(canClimb.isChecked())
    	{
        	mes.append("Climbing: Can Climb" + "\n");
        	mes.append("Level 1: " + climb1.isChecked() + "\n");
        	mes.append("Level 2: " + climb2.isChecked() + "\n");
        	mes.append("Level 3: " + climb3.isChecked() + "\n");
        	mes.append("Sucessful Climbs: " + sucessfulClimbs.getText() + "\n");
        	mes.append("Total Climb Attempts: " + climbAttempts.getText() + "\n");
        	mes.append("Team Climbing Penalties: " + climbingTeamPenalties.getText() + "\n");
        	mes.append("Alliance Climbing Penalties: " + climbingAlliancePenalties.getText() + "\n");
    	}
    	else
    	{
    		mes.append("Climbing: Cannot Climb" + "\n");
    	}
    	
    	mes.append("\n");
    	
    	if(hasAutonomous.isChecked())
    	{
        	mes.append("Team Autonomous Penalties: " + autonomousTeamPenalties.getText() + "\n");
        	mes.append("Alliance Autonomous Penalties: " + autonomousAlliancePenalties.getText() + "\n");
        	mes.append("Can Shoot on:" + "\n");
        	mes.append("One Point Goal: " + onePointGoalA.isChecked() + "\n");
        	if(onePointGoalA.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade1PointA.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken1PointA.getText() + "\n");
        	}
        	mes.append("Two Point Goal: " + twoPointGoalA.isChecked() + "\n");
        	if(twoPointGoalA.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade2PointA.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken2PointA.getText() + "\n");
        	}
        	mes.append("Three Point Goal: " + threePointGoalA.isChecked() + "\n");
        	if(threePointGoalA.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade3PointA.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken3PointA.getText() + "\n");
        	}
        	mes.append("Five Point Goal: " + fivePointGoalA.isChecked() + "\n");
        	if(fivePointGoalA.isChecked())
        	{
        		mes.append("Shots Made: " + ShotsMade5PointA.getText() + "\n");
        		mes.append("Shots Taken: " + ShotsTaken5PointA.getText() + "\n");
        	}
    	}
    	else
    	{
    		mes.append("Autonomous: No Autonomous" + "\n");
    	}
    	
    	mes.append("\n");
    	
    	mes.append("Special Abilities: " + "\n");
    	if(defense.isChecked())
    	{
    		mes.append("Defense: Has Defense Abilites" + "\n");
    		mes.append("Defense Rank: " + defenseRank.getText() + "\n");
    	}
    	else
    	{
    		mes.append("Defense: No Defense" + "\n");
    	}
    	
    	mes.append("Climbing Assist: " + climbAssist.isChecked() + "\n");
    	
    	if(humanPlayer.isChecked())
    	{
    		mes.append("Human Player: Has a Human Player" + "\n");
    		mes.append("Penalties: " + humanPlayerPenalties.getText() + "\n");
    		mes.append("Shots Made: " + humanPlayerShotsMade.getText() + "\n");
    		mes.append("Shots Taken: " + humanPlayerShotsTaken.getText() + "\n");
    	}
    	else
    	{
    		mes.append("Human Player: No Human Player" + "\n");
    	}
    	
    	mes.append("\n");
    	
    	mes.append("Movement Description: " + "\n");
    	mes.append(describeMovement.getText() + "\n");
    	mes.append("Additional Comments: " + "\n");
    	mes.append(additionalComments.getText() + "\n");    
    	
		//Add Data to Object
		md.setMatchNumber(matchNumber.getText().toString());
		md.setTeamNumber(teamNumber.getText().toString());
		md.setAllianceColor(colorSpinner.getSelectedItem().toString());
		md.setPointsScored(teamScore.getText().toString());
		md.setAllianceScore(allianceScore.getText().toString());
		md.setPenalties(teamPenalites.getText().toString(),alliancePenalites.getText().toString());
		md.setShootingData(canShoot.isChecked(), onePointGoal.isChecked(), twoPointGoal.isChecked(), threePointGoal.isChecked(), fivePointGoal.isChecked(),Integer.parseInt(teamPenaltiesShooter.getText().toString()),
				Integer.parseInt(alliancePenaltiesShooter.getText().toString()),Integer.parseInt(ShotsMade1Point.getText().toString()),Integer.parseInt(ShotsMade2Point.getText().toString()),Integer.parseInt(ShotsMade3Point.getText().toString()),Integer.parseInt(ShotsMade5Point.getText().toString()),
				Integer.parseInt(ShotsTaken1Point.getText().toString()),Integer.parseInt(ShotsTaken2Point.getText().toString()),Integer.parseInt(ShotsTaken3Point.getText().toString()),Integer.parseInt(ShotsTaken5Point.getText().toString()));
		md.setClimbingData(canClimb.isChecked(), climb1.isChecked(), climb2.isChecked(), climb3.isChecked(),Integer.parseInt(sucessfulClimbs.getText().toString()),Integer.parseInt(climbAttempts.getText().toString()),Integer.parseInt(climbingTeamPenalties.getText().toString()),
				Integer.parseInt(climbingAlliancePenalties.getText().toString()));
		md.setAutonomousData(hasAutonomous.isChecked(), Integer.parseInt(autonomousTeamPenalties.getText().toString()), Integer.parseInt(autonomousAlliancePenalties.getText().toString()), onePointGoalA.isChecked(), twoPointGoalA.isChecked(), threePointGoalA.isChecked(), fivePointGoalA.isChecked(), 
				Integer.parseInt(ShotsMade1PointA.getText().toString()), Integer.parseInt(ShotsMade2PointA.getText().toString()), Integer.parseInt(ShotsMade3PointA.getText().toString()), Integer.parseInt(ShotsMade5PointA.getText().toString()), 
				Integer.parseInt(ShotsTaken1PointA.getText().toString()), Integer.parseInt(ShotsTaken2PointA.getText().toString()), Integer.parseInt(ShotsTaken3PointA.getText().toString()), Integer.parseInt(ShotsTaken5PointA.getText().toString()));
		md.setSpecialFeaturesData(defense.isChecked(),Integer.parseInt(defenseRank.getText().toString()), climbAssist.isChecked(), humanPlayer.isChecked(),Integer.parseInt(humanPlayerPenalties.getText().toString()),Integer.parseInt(humanPlayerShotsMade.getText().toString()),
				Integer.parseInt(humanPlayerShotsTaken.getText().toString()));
		md.setMovementDescription(describeMovement.getText().toString());
		md.setComments(additionalComments.getText().toString());
		
    	//Construct The Alert Box
    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
    	builder.setCancelable(false);
    	builder.setTitle("Would you like to save the following data?");
    	builder.setMessage(mes.toString());
    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
    	{
			public void onClick(DialogInterface dialog, int which) 
			{
				if(opMode.equals("Offline"))
				{
					//Create Helper Object
					FileStorageHelper fh = new FileStorageHelper(md);
					if(fh.canWeWrite())
					{
						//Save Data and Get Save Result
						if(fh.writeMatchFile("MS"))
						{
							//Display Success Toast to User
							Toast toast = Toast.makeText(getApplicationContext(), "Match Data was Stored Sucessfully!", Toast.LENGTH_LONG);
							toast.show();
							//Return to Main Screen
							Intent startMain = new Intent(FieldsForm.this, MainScreen.class);
							startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(startMain);
						}
						else
						{
							//Display Error Toast to User
							Toast toast = Toast.makeText(getApplicationContext(), "Error Saving File, Data was not saved", Toast.LENGTH_LONG);
							toast.show();
							//Close Dialog
							dialog.cancel();
						}

					}
					else
					{
						//Alert User to the Error
						Toast toast = Toast.makeText(getApplicationContext(), "File Location Check Error, Check that you External Storage is Mounted/Writeable", Toast.LENGTH_LONG);
						toast.show();
						//Close Dialog
						dialog.cancel();
					}
				}
				else
				{
					//Online Mode
					//Create String of All Data to Send
					String dataToSend = md.createStringToSend();
					stringDataToSend = dataToSend;
					Log.i("Done Scouting, Notifying Communication Thread");
					
					//Set boolean to notify Communication Thread
					dataReady = true;
					
					//Wait For Communication Thread to Send Status
					do
					{
						
					}
					while(commThreadStatusCode == 0);
					
					//Analyze Status Code
					switch(commThreadStatusCode)
					{
					case 1:
						//Mark Data Ready As False
						dataReady = false;
						
						//Set Data String to Null
						stringDataToSend = null;
						
						//Set Status Code to Zero
						commThreadStatusCode = 0;
						
						//Data Send Successfully
						commThreadGoToMainScreen();
						break;
					case 2:
						//No Server Connection
						//Offer To Save Offline
				    	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
				    	builder.setCancelable(false);
				    	builder.setTitle("Would you like to save the following data?");
				    	builder.setMessage("The Server Connection was lost during Scouting, would you like to save the data offline?");
				    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				    	{
							public void onClick(DialogInterface dialog, int which) 
							{
								//Saved Match Data Offline
								//Create Helper Object
								FileStorageHelper fh = new FileStorageHelper(md);
								if(fh.canWeWrite())
								{
									//Save Data and Get Save Result
									if(fh.writeMatchFile("MS"))
									{
										//Display Success Toast to User
										Toast toast = Toast.makeText(getApplicationContext(), "Match Data was Stored Sucessfully!", Toast.LENGTH_LONG);
										toast.show();
										
										//Return to Main Screen
										Intent startMain = new Intent(FieldsForm.this, MainScreen.class);
										startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(startMain);
									}
									else
									{
										//Display Error Toast to User
										Toast toast = Toast.makeText(getApplicationContext(), "Error Saving File, Data was not saved", Toast.LENGTH_LONG);
										toast.show();
										
										//Close Dialog
										dialog.cancel();
									}

								}
								else
								{
									//Alert User to the Error
									Toast toast = Toast.makeText(getApplicationContext(), "File Location Check Error, Check that you External Storage is Mounted/Writeable", Toast.LENGTH_LONG);
									toast.show();
									//Close Dialog
									dialog.cancel();
								}
							}
				    	});
				    	builder.setPositiveButton("No", new DialogInterface.OnClickListener() 
				    	{
							public void onClick(DialogInterface dialog, int which) 
							{
								//Return to Main Screen
								Intent startMain = new Intent(FieldsForm.this, MainScreen.class);
								startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(startMain);
							}
				    	});
				    	builder.show();
						break;
					}
				}
			}
		});
    	builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
    	{
			public void onClick(DialogInterface dialog, int which) 
			{
				//Close Window for editing
				dialog.cancel();
			}
		});
    	builder.show();
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		//If Checked Status has Changed For CheckBoxes
		if(buttonView.getId() == R.id.onePointCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade1Point = (EditText) findViewById(R.id.onePointGoalShotsMade);
			EditText ShotsTaken1Point = (EditText) findViewById(R.id.onePointGoalShotsTaken);
			if(!cB.isChecked())
			{
				ShotsMade1Point.setEnabled(false);
				ShotsTaken1Point.setEnabled(false);
			}
			else
			{
				ShotsMade1Point.setEnabled(true);
				ShotsTaken1Point.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.twoPointCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade2Point = (EditText) findViewById(R.id.twoPointGoalShotsMade);
			EditText ShotsTaken2Point = (EditText) findViewById(R.id.twoPointGoalShotsTaken);
			if(!cB.isChecked())
			{
				ShotsMade2Point.setEnabled(false);
				ShotsTaken2Point.setEnabled(false);
			}
			else
			{
				ShotsMade2Point.setEnabled(true);
				ShotsTaken2Point.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.threePointCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade3Point = (EditText) findViewById(R.id.threePointGoalShotsMade);
			EditText ShotsTaken3Point = (EditText) findViewById(R.id.threePointGoalShotsTaken);
			if(!cB.isChecked())
			{
				ShotsMade3Point.setEnabled(false);
				ShotsTaken3Point.setEnabled(false);
			}
			else
			{
				ShotsMade3Point.setEnabled(true);
				ShotsTaken3Point.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.fivePointCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade5Point = (EditText) findViewById(R.id.fivePointGoalShotsMade);
			EditText ShotsTaken5Point = (EditText) findViewById(R.id.fivePointGoalShotsTaken);
			if(!cB.isChecked())
			{
				ShotsMade5Point.setEnabled(false);
				ShotsTaken5Point.setEnabled(false);
			}
			else
			{
				ShotsMade5Point.setEnabled(true);
				ShotsTaken5Point.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.onePointGoalAutonomous)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade1PointA = (EditText) findViewById(R.id.onePointGoalShotsMadeAutonomous);
			EditText ShotsTaken1PointA = (EditText) findViewById(R.id.onePointGoalShotsTakenAutonomous);
			if(!cB.isChecked())
			{
				ShotsMade1PointA.setEnabled(false);
				ShotsTaken1PointA.setEnabled(false);
			}
			else
			{
				ShotsMade1PointA.setEnabled(true);
				ShotsTaken1PointA.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.twoPointGoalAutonomous)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade2PointA = (EditText) findViewById(R.id.twoPointGoalShotsMadeAutonomous);
			EditText ShotsTaken2PointA = (EditText) findViewById(R.id.twoPointGoalShotsTakenAutonomous);
			if(!cB.isChecked())
			{
				ShotsMade2PointA.setEnabled(false);
				ShotsTaken2PointA.setEnabled(false);
			}
			else
			{
				ShotsMade2PointA.setEnabled(true);
				ShotsTaken2PointA.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.threePointGoalAutonomous)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade3PointA = (EditText) findViewById(R.id.threePointGoalShotsMadeAutonomous);
			EditText ShotsTaken3PointA = (EditText) findViewById(R.id.threePointGoalShotsTakenAutonomous);
			if(!cB.isChecked())
			{
				ShotsMade3PointA.setEnabled(false);
				ShotsTaken3PointA.setEnabled(false);
			}
			else
			{
				ShotsMade3PointA.setEnabled(true);
				ShotsTaken3PointA.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.fivePointGoalAutonomous)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText ShotsMade5PointA = (EditText) findViewById(R.id.fivePointGoalShotsMadeAutonomous);
			EditText ShotsTaken5PointA = (EditText) findViewById(R.id.fivePointGoalShotsTakenAutonomous);
			if(!cB.isChecked())
			{
				ShotsMade5PointA.setEnabled(false);
				ShotsTaken5PointA.setEnabled(false);
			}
			else
			{
				ShotsMade5PointA.setEnabled(true);
				ShotsTaken5PointA.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.defenseCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
			EditText defenseRank = (EditText) findViewById(R.id.defenseRank);
			if(!cB.isChecked())
			{
				defenseRank.setEnabled(false);
			}
			else
			{
				defenseRank.setEnabled(true);
			}
		}
		else if(buttonView.getId() == R.id.humanPlayerCheckBox)
		{
			CheckBox cB = (CheckBox) buttonView;
	    	EditText humanPlayerPenalties = (EditText) findViewById(R.id.humanPlayerPenalties);
	    	EditText humanPlayerShotsMade = (EditText) findViewById(R.id.humanPlayerShotsMade);
	    	EditText humanPlayerShotsTaken = (EditText) findViewById(R.id.humanPlayerShotsTaken);
			if(!cB.isChecked())
			{
		    	humanPlayerPenalties.setEnabled(false);
		    	humanPlayerShotsMade.setEnabled(false);
		    	humanPlayerShotsTaken.setEnabled(false);
			}
			else
			{
		    	humanPlayerPenalties.setEnabled(true);
		    	humanPlayerShotsMade.setEnabled(true);
		    	humanPlayerShotsTaken.setEnabled(true);
			}
		}
	}
	
	public void commThreadGoToMainScreen()
	{
		//Display Success Toast to User
		Toast toast = Toast.makeText(getApplicationContext(), "Match Data was Recieved By the Server! Thanks for Scouting!", Toast.LENGTH_LONG);
		toast.show();
		
		//Return to Main Screen
		Intent startMain = new Intent(FieldsForm.this, MainScreen.class);
		startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(startMain);
	}
}
