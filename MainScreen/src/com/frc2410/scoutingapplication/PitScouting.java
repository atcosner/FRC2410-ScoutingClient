package com.frc2410.scoutingapplication;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class PitScouting extends Activity
{
	//Global buttons
    private Button submitButton;
    private Button resetButton;
    private Button backButton;
    
	//Create Variable to set during Validation
	boolean dataToProceed = false;

	//Object to Store Pit Data
	PitScoutData md = new PitScoutData();
	
	CheckBox canShoot;
	CheckBox canClimb;
	CheckBox hasAutonomous;
	
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_pitscouting);
        
        //Initialize Buttons For Use
        submitButton = (Button) findViewById(R.id.saveButtonPit);
        resetButton = (Button) findViewById(R.id.resetButtonPit);
        backButton = (Button) findViewById(R.id.goBackButtonPit);
        
        //Setup Shooter Group Enable Buttons
        canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPit);
        canShoot.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)	
				{
					enableShooterFields();
				}
				else
				{
					disableShooterFields();
				}
			}
        });
        
        //Setup Climbing Group Enable Buttons
        canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPit);
        canClimb.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)	
				{
					enableClimbingFields();
				}
				else
				{
					disableClimbingFields();
				}
			}
        });
        
        //Setup Autonomous Group Enable Buttons
        hasAutonomous = (CheckBox) findViewById(R.id.autonomousPit);
        hasAutonomous.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(isChecked)	
				{
					enableAutonomousFields();
				}
				else
				{
					disableAutonomousFields();
				}
			}
        });
        
        //Setup Button to go back to the previous Activity
        backButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });
        
    	//Setup Reset Button to Clear Data
        resetButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
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
    }
    
    
    protected void onStop()
    {
    	super.onStop();
    	
    	//Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStop(this);
    }
    
    @Override
    protected void onStart() 
    {
    	super.onStart();
    	
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStart(this);
    	
    	//Setup Button For Saving
    	submitButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Validate Data
            	if(goodTextEntryData())
            	{
            		if(goodShooterData())
            		{
                		if(goodClimbingData())
                		{
                    		if(goodDrivetrainData())
                    		{
                        		if(goodAutonomousData())
                        		{
                            		if(goodMiscData())
                            		{
                            			dataToProceed = true;
                            		}
                            		else
                            		{
                            			dataToProceed = false;
                                    	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                                    	builder.setCancelable(false);
                                    	builder.setTitle("Data Error");
                                    	builder.setMessage("Please Enter Data in all of the Misc Fields");
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
                                	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                                	builder.setCancelable(false);
                                	builder.setTitle("Data Error");
                                	builder.setMessage("Please Enter Data in all of the Autonomous Fields");
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
                            	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                            	builder.setCancelable(false);
                            	builder.setTitle("Data Error");
                            	builder.setMessage("Please Enter Data in all of the DriveTrain Fields");
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
                        	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                        	builder.setCancelable(false);
                        	builder.setTitle("Data Error");
                        	builder.setMessage("Please Enter Data in all of the Climbing Fields");
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
                    	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                    	builder.setCancelable(false);
                    	builder.setTitle("Data Error");
                    	builder.setMessage("Please Enter Data in all of the Shooter Fields");
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
                	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                	builder.setCancelable(false);
                	builder.setTitle("Data Error");
                	builder.setMessage("Please Enter Data in all of the Required Text Fields!");
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
            		confirmUserSave();
            	}
            }
        });
    }
    
    private void enableShooterFields()
    {
    	//Shooter Data
    	CheckBox canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPit);
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPit);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPit);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePit);
    	CheckBox shooterAngleAdjustable = (CheckBox) findViewById(R.id.adjustableShooterPit);
    	CheckBox shooterFloorPickup = (CheckBox) findViewById(R.id.shooterFloorPickUpPit);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPit);
    	//Enable Controls
    	canShoot.setEnabled(true);
    	shooterDescription.setEnabled(true);
    	shooterFinalSpeed.setEnabled(true);
    	shooterAngle.setEnabled(true);
    	shooterAngleAdjustable.setEnabled(true);
    	shooterFloorPickup.setEnabled(true);
    	shooterSystemSpeed.setEnabled(true);
    }
    
    private void enableClimbingFields()
    {
    	//Climbing Data
    	CheckBox canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPit);
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPit);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevel);
    	CheckBox sideClimb = (CheckBox) findViewById(R.id.climbingSidePit);
    	CheckBox cornerClimb = (CheckBox) findViewById(R.id.climbingCornerPit);
    	//Enable Controls
    	canClimb.setEnabled(true);
    	climbingSpeed.setEnabled(true);
    	climbingHighestLevel.setEnabled(true);
    	sideClimb.setEnabled(true);
    	cornerClimb.setEnabled(true);
    }
    
    private void disableClimbingFields()
    {
    	//Climbing Data
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPit);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevel);
    	CheckBox sideClimb = (CheckBox) findViewById(R.id.climbingSidePit);
    	CheckBox cornerClimb = (CheckBox) findViewById(R.id.climbingCornerPit);
    	//Disable Controls
    	climbingSpeed.setEnabled(false);
    	climbingHighestLevel.setEnabled(false);
    	sideClimb.setEnabled(false);
    	cornerClimb.setEnabled(false);
    }
    
    private void disableShooterFields()
    {
    	//Shooter Data
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPit);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPit);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePit);
    	CheckBox shooterAngleAdjustable = (CheckBox) findViewById(R.id.adjustableShooterPit);
    	CheckBox shooterFloorPickup = (CheckBox) findViewById(R.id.shooterFloorPickUpPit);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPit);
    	//Disable Controls
    	shooterDescription.setEnabled(false);
    	shooterFinalSpeed.setEnabled(false);
    	shooterAngle.setEnabled(false);
    	shooterAngleAdjustable.setEnabled(false);
    	shooterFloorPickup.setEnabled(false);
    	shooterSystemSpeed.setEnabled(false);
    }
    
    private void disableAutonomousFields()
    {
    	//Autonomous Data
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPit);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPit);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPit);
    	CheckBox autonomousFloorPickup = (CheckBox) findViewById(R.id.autonomousFloorPickupPit);
    	//Disable Controls
    	autonomousPlacement.setEnabled(false);
    	autonomousNumPreloaded.setEnabled(false);
    	autonomousLevelAimed.setEnabled(false);
    	autonomousFloorPickup.setEnabled(false);
    }
    
    private void enableAutonomousFields()
    {
    	//Autonomous Data
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPit);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPit);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPit);
    	CheckBox autonomousFloorPickup = (CheckBox) findViewById(R.id.autonomousFloorPickupPit);
    	//Disable Controls
    	autonomousPlacement.setEnabled(true);
    	autonomousNumPreloaded.setEnabled(true);
    	autonomousLevelAimed.setEnabled(true);
    	autonomousFloorPickup.setEnabled(true);
    }
    
    public boolean goodTextEntryData()
    {
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberPit);
    	EditText teamName = (EditText) findViewById(R.id.teamNamePit);
    	EditText strategy = (EditText) findViewById(R.id.strategyPit);
		
    	if(!teamNumber.getText().toString().equals(""))
    	{
        	if(!teamName.getText().toString().equals(""))
        	{
            	if(!strategy.getText().toString().equals(""))
            	{
            		return true;
            	}
            	else
            	{
            		return false;
            	}
        	}
        	else
        	{
        		return false;
        	}
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean goodShooterData()
    {
    	CheckBox canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPit);
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPit);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPit);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePit);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPit);
		
    	if(canShoot.isChecked())
    	{
        	if(!shooterDescription.getText().toString().equals(""))
        	{
            	if(!shooterFinalSpeed.getText().toString().equals(""))
            	{
                	if(!shooterAngle.getText().toString().equals(""))
                	{
                    	if(!shooterSystemSpeed.getText().toString().equals(""))
                    	{
                    		return true;
                    	}
                    	else
                    	{
                    		return false;
                    	}
                	}
                	else
                	{
                		return false;
                	}
            	}
            	else
            	{
            		return false;
            	}
        	}
        	else
        	{
        		return false;
        	}
    	}
    	else
    	{
    		return true;
    	}
    }
    
    public boolean goodClimbingData()
    {
    	CheckBox canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPit);
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPit);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevel);
		
    	if(canClimb.isChecked())
    	{
        	if(!climbingSpeed.getText().toString().equals(""))
        	{
            	if(!climbingHighestLevel.getText().toString().equals(""))
            	{
            		return true;
            	}
            	else
            	{
            		return false;
            	}
        	}
        	else
        	{
        		return false;
        	}
    	}
    	else
    	{
    		return true;
    	}
    }
    
    public boolean goodDrivetrainData()
    {
    	EditText drivetrainDescription = (EditText) findViewById(R.id.drivetrainDesciptionPit);
    	EditText drivetrainWheels = (EditText) findViewById(R.id.drivetrainWheelsPit);
    	EditText drivetrainSpeed = (EditText) findViewById(R.id.drivetrainSpeedPit);
		
    	if(!drivetrainDescription.getText().toString().equals(""))
    	{
        	if(!drivetrainWheels.getText().toString().equals(""))
        	{
            	if(!drivetrainSpeed.getText().toString().equals(""))
            	{
                	if(!drivetrainSpeed.getText().toString().equals(""))
                	{
                		return true;
                	}
                	else
                	{
                		return false;
                	}
            	}
            	else
            	{
            		return false;
            	}
        	}
        	else
        	{
        		return false;
        	}
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean goodAutonomousData()
    {
    	CheckBox autonomous = (CheckBox) findViewById(R.id.autonomousPit);
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPit);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPit);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPit);
		
    	if(autonomous.isChecked())
    	{
    		if(!autonomousPlacement.getText().toString().equals(""))
    		{
    			if(!autonomousNumPreloaded.getText().toString().equals(""))
    			{
    				if(!autonomousLevelAimed.getText().toString().equals(""))
    				{
    					return true;
    				}
    	    		else
    	    		{
    	    			return false;
    	    		}
    			}
        		else
        		{
        			return false;
        		}
    		}
    		else
    		{
    			return false;
    		}
    	}
    	else
    	{
    		return true;
    	}
    }
    
    public boolean goodMiscData()
    {
    	EditText miscTripTime = (EditText) findViewById(R.id.miscTripTimePit);
    	EditText miscEstimatedScore = (EditText) findViewById(R.id.miscEstimatedScore);
    	EditText miscMaintenanceTime = (EditText) findViewById(R.id.miscMaintenanceTimePit);
		
    	if(!miscTripTime.getText().toString().equals(""))
    	{
        	if(!miscEstimatedScore.getText().toString().equals(""))
        	{
            	if(!miscMaintenanceTime.getText().toString().equals(""))
            	{
            		return true;
            	}
            	else
            	{
            		return false;
            	}
        	}
        	else
        	{
        		return false;
        	}
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private void clearEnteredData()
    {
    	//Top Data
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberPit);
    	EditText teamName = (EditText) findViewById(R.id.teamNamePit);
    	EditText strategy = (EditText) findViewById(R.id.strategyPit);
    	//Shooter Data
    	CheckBox canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPit);
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPit);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPit);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePit);
    	CheckBox shooterAngleAdjustable = (CheckBox) findViewById(R.id.adjustableShooterPit);
    	CheckBox shooterFloorPickup = (CheckBox) findViewById(R.id.shooterFloorPickUpPit);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPit);
    	//DriveTrain Data
    	EditText drivetrainDescription = (EditText) findViewById(R.id.drivetrainDesciptionPit);
    	EditText drivetrainWheels = (EditText) findViewById(R.id.drivetrainWheelsPit);
    	EditText drivetrainSpeed = (EditText) findViewById(R.id.drivetrainSpeedPit);
    	EditText drivetrainSpecial = (EditText) findViewById(R.id.drivetrainSpecialPit);
    	//Climbing Data
    	CheckBox canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPit);
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPit);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevel);
    	CheckBox sideClimb = (CheckBox) findViewById(R.id.climbingSidePit);
    	CheckBox cornerClimb = (CheckBox) findViewById(R.id.climbingCornerPit);
    	//Autonomous Data
    	CheckBox hasAutonomous = (CheckBox) findViewById(R.id.autonomousPit);
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPit);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPit);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPit);
    	CheckBox autonomousFloorPickup = (CheckBox) findViewById(R.id.autonomousFloorPickupPit);
    	//Misc. Data
    	EditText miscMaintTime = (EditText) findViewById(R.id.miscMaintenanceTimePit);
    	EditText miscTripTime = (EditText) findViewById(R.id.miscTripTimePit);
    	CheckBox miscAbleToDefend = (CheckBox) findViewById(R.id.miscAbleToDefendPit);
    	CheckBox miscColoredDiscs = (CheckBox) findViewById(R.id.miscColoredDiscsPit);
    	CheckBox miscVisionTargetting = (CheckBox) findViewById(R.id.miscVisionTargeting);
    	EditText miscEstimatedScore = (EditText) findViewById(R.id.miscEstimatedScore);
    	
    	//Reset All Componets
    	teamNumber.setText("");
    	teamName.setText("");
    	strategy.setText("");
    	//Shooter
    	canShoot.setChecked(true);
    	shooterDescription.setText("");
    	shooterFinalSpeed.setText("");
    	shooterAngle.setText("");
    	shooterAngleAdjustable.setChecked(false);
    	shooterFloorPickup.setChecked(false);
    	shooterSystemSpeed.setText("");
    	//DriveTrain
    	drivetrainDescription.setText("");
    	drivetrainWheels.setText("");
    	drivetrainSpeed.setText("");
    	drivetrainSpecial.setText("");
    	//Climbing
    	canClimb.setChecked(false);
    	climbingSpeed.setText("");
    	climbingHighestLevel.setText("");
    	sideClimb.setChecked(false);
    	cornerClimb.setChecked(false);
    	//Autonomous
    	hasAutonomous.setChecked(true);
    	autonomousPlacement.setText("");
    	autonomousNumPreloaded.setText("");
    	autonomousLevelAimed.setText("");
    	autonomousFloorPickup.setChecked(false);
    	//Misc
    	miscMaintTime.setText("");
    	miscTripTime.setText("");
    	miscAbleToDefend.setChecked(false);
    	miscColoredDiscs.setChecked(false);
    	miscVisionTargetting.setChecked(false);
    	miscEstimatedScore.setText("");
    }
    
    public void confirmUserSave()
    {
    	//Top Data
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberPit);
    	EditText teamName = (EditText) findViewById(R.id.teamNamePit);
    	EditText strategy = (EditText) findViewById(R.id.strategyPit);
    	//Shooter Data
    	CheckBox canShoot = (CheckBox) findViewById(R.id.shooterCheckBoxPit);
    	EditText shooterDescription = (EditText) findViewById(R.id.shooterDescriptionPit);
    	EditText shooterFinalSpeed = (EditText) findViewById(R.id.shooterSpeedPit);
    	EditText shooterAngle = (EditText) findViewById(R.id.shooterAnglePit);
    	CheckBox shooterAngleAdjustable = (CheckBox) findViewById(R.id.adjustableShooterPit);
    	CheckBox shooterFloorPickup = (CheckBox) findViewById(R.id.shooterFloorPickUpPit);
    	EditText shooterSystemSpeed = (EditText) findViewById(R.id.shooterSystemSpeedPit);
    	//DriveTrain Data
    	EditText drivetrainDescription = (EditText) findViewById(R.id.drivetrainDesciptionPit);
    	EditText drivetrainWheels = (EditText) findViewById(R.id.drivetrainWheelsPit);
    	EditText drivetrainSpeed = (EditText) findViewById(R.id.drivetrainSpeedPit);
    	EditText drivetrainSpecial = (EditText) findViewById(R.id.drivetrainSpecialPit);
    	//Climbing Data
    	CheckBox canClimb = (CheckBox) findViewById(R.id.climbingCheckBoxPit);
    	EditText climbingSpeed = (EditText) findViewById(R.id.climbingSpeedPit);
    	EditText climbingHighestLevel = (EditText) findViewById(R.id.climbingHighestLevel);
    	CheckBox sideClimb = (CheckBox) findViewById(R.id.climbingSidePit);
    	CheckBox cornerClimb = (CheckBox) findViewById(R.id.climbingCornerPit);
    	//Autonomous Data
    	CheckBox hasAutonomous = (CheckBox) findViewById(R.id.autonomousPit);
    	EditText autonomousPlacement = (EditText) findViewById(R.id.autonomousPlacementPit);
    	EditText autonomousNumPreloaded = (EditText) findViewById(R.id.autonomousNumPreloadedPit);
    	EditText autonomousLevelAimed = (EditText) findViewById(R.id.autonomousLevelAimedPit);
    	CheckBox autonomousFloorPickup = (CheckBox) findViewById(R.id.autonomousFloorPickupPit);
    	//Misc. Data
    	EditText miscTripTime = (EditText) findViewById(R.id.miscTripTimePit);
    	EditText miscMaintTime = (EditText) findViewById(R.id.miscMaintenanceTimePit);
    	CheckBox miscAbleToDefend = (CheckBox) findViewById(R.id.miscAbleToDefendPit);
    	CheckBox miscColoredDiscs = (CheckBox) findViewById(R.id.miscColoredDiscsPit);
    	CheckBox miscVisionTargetting = (CheckBox) findViewById(R.id.miscVisionTargeting);
    	EditText miscEstimatedScore = (EditText) findViewById(R.id.miscEstimatedScore);
    	
    	//Build The Strings for the Alert Box
    	StringBuilder mes = new StringBuilder();
    	mes.append("Team Number: " + teamNumber.getText() + "\n");
    	mes.append("Team Name: " + teamName.getText() + "\n");
    	mes.append("Strategy: " + strategy.getText() + "\n");
    	mes.append("\n");
    	if(canShoot.isChecked())
    	{
        	mes.append("Shooter: Can Shoot" + "\n");
        	mes.append("Description: " + shooterDescription.getText() + "\n");
        	mes.append("Speed (Final): " + shooterFinalSpeed.getText() + "\n");
        	mes.append("Angle: " + shooterAngle.getText() + "\n");
        	mes.append("Adjustable:" + shooterAngleAdjustable.isChecked() + "\n");
        	mes.append("Floor Pick Up: " + shooterFloorPickup.isChecked() + "\n");
        	mes.append("System Speed: " + shooterSystemSpeed.getText() + "\n");
    	}
    	else
    	{
    		mes.append("Shooter: Cannot Shoot" + "\n");
    	}
    	mes.append("\n");
    	
    	mes.append("Drivetrain: " + "\n");
    	mes.append("Description: " + drivetrainDescription.getText() + "\n");
    	mes.append("Wheels: " + drivetrainWheels.getText() + "\n");
    	mes.append("Speed: " + drivetrainSpeed.getText() + "\n");
    	mes.append("Special: " + drivetrainSpecial.getText() + "\n");
    	mes.append("\n");
    	
    	if(canClimb.isChecked())
    	{
        	mes.append("Climbing: Can Climb" + "\n");
        	mes.append("Speed: " + climbingSpeed.getText() + "\n");
        	mes.append("Highest Level: " + climbingHighestLevel.getText() + "\n");
        	mes.append("Climbs Up Side: " + sideClimb.isChecked() + "\n");
        	mes.append("Climbs Up Corner: " + cornerClimb.isChecked() + "\n");
    	}
    	else
    	{
    		mes.append("Climbing: Cannot Climb" + "\n");
    	}
    	mes.append("\n");
    	
    	if(hasAutonomous.isChecked())
    	{
        	mes.append("Autonomous: " + "\n");
        	mes.append("Placement: " + autonomousPlacement.getText() + "\n");
        	mes.append("# Preloaded: " + autonomousNumPreloaded.getText() + "\n");
        	mes.append("Level Aimed: " + autonomousLevelAimed.getText() + "\n");
        	mes.append("Floor Pickup: " + autonomousFloorPickup.isChecked() + "\n");
    	}
    	else
    	{
    		mes.append("Autonomous: None" + "\n");
    	}
    	mes.append("\n");
    	
    	mes.append("Misc. Data: " + "\n");
    	mes.append("Trip Time: " + miscTripTime.getText() + "\n");
    	mes.append("Able To Defend: " + miscAbleToDefend.isChecked() + "\n");
    	mes.append("Colored Discs: " + miscColoredDiscs.isChecked() + "\n");
    	mes.append("Vision Targeting: " + miscVisionTargetting.isChecked() + "\n");
    	mes.append("Estimated Score: " + miscEstimatedScore.getText() + "\n"); 
    	mes.append("Mainenance Time: " + miscMaintTime.getText() + "\n");
		
    	//Set Data Values for Disabled Fields
    	if(!canShoot.isChecked())
    	{
    		shooterDescription.setText("None");
    		shooterFinalSpeed.setText("0");
    		shooterAngle.setText("0");
    		shooterSystemSpeed.setText("0");
    	}
    	
    	if(!canClimb.isChecked())
    	{
        	climbingSpeed.setText("0");
        	climbingHighestLevel.setText("0");
    	}
    	
    	if(!hasAutonomous.isChecked())
    	{
        	autonomousPlacement.setText("0");
        	autonomousNumPreloaded.setText("0");
        	autonomousLevelAimed.setText("0");
    	}
    	
		//Add Data to Object
		md.setHeaderData(Integer.parseInt(teamNumber.getText().toString()), teamName.getText().toString(), strategy.getText().toString());
		md.setShooterData(canShoot.isChecked(), shooterDescription.getText().toString(), Integer.parseInt(shooterFinalSpeed.getText().toString()), Integer.parseInt(shooterAngle.getText().toString()), shooterAngleAdjustable.isChecked(), shooterFloorPickup.isChecked(), Integer.parseInt(shooterSystemSpeed.getText().toString()));
		md.setDrivetrainData(drivetrainDescription.getText().toString(), drivetrainWheels.getText().toString(), Integer.parseInt(drivetrainSpeed.getText().toString()), drivetrainSpecial.getText().toString());
		md.setClimbingData(canClimb.isChecked(), Integer.parseInt(climbingSpeed.getText().toString()), Integer.parseInt(climbingHighestLevel.getText().toString()), sideClimb.isChecked(), cornerClimb.isChecked());
		md.setMiscData(Integer.parseInt(miscMaintTime.getText().toString()), miscAbleToDefend.isChecked(), miscColoredDiscs.isChecked(), miscVisionTargetting.isChecked(), Integer.parseInt(miscEstimatedScore.getText().toString()),Integer.parseInt(miscTripTime.getText().toString()));
		md.setAutonomousData(hasAutonomous.isChecked(), Integer.parseInt(autonomousPlacement.getText().toString()), Integer.parseInt(autonomousNumPreloaded.getText().toString()), Integer.parseInt(autonomousLevelAimed.getText().toString()), autonomousFloorPickup.isChecked());
		
    	//Construct The Alert Box
    	AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
    	builder.setCancelable(false);
    	builder.setTitle("Would you like to save the following data?");
    	builder.setMessage(mes.toString());
    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
    	{
			public void onClick(DialogInterface dialog, int which) 
			{
				//Create Helper Object
				FileStorageHelper fh = new FileStorageHelper(md);
				if(fh.canWeWrite())
				{
					//Save Data and Get Save Result
					if(fh.writePitFile("PS"))
					{
						//Display Success Toast to User
						Toast toast = Toast.makeText(getApplicationContext(), "Pit Data was Stored Sucessfully!", Toast.LENGTH_LONG);
						toast.show();
						//Return to Main Screen
						Intent startMain = new Intent(PitScouting.this, MainScreen.class);
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
				dialog.cancel();
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
}
