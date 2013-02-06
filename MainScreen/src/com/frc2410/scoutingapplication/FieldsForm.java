package com.frc2410.scoutingapplication;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class FieldsForm extends Activity implements OnCheckedChangeListener
{
	private String networkMode;
	private Spinner colorSpinner;
    private Button submitButton;
    private Button resetButton;
    private Button backButton;
    private boolean validateScoreBoxes = true;
    private boolean validateClimbBoxes = true;
	//Create MatchScoutData Object
	MatchScoutData md = new MatchScoutData();
    
	//Create Variable to set during Validation
	boolean dataToProceed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Set View to the main Screen
        setContentView(R.layout.activity_dataentry);
        
        //Get Mode from Intent
        Intent intent = getIntent();
        networkMode = intent.getStringExtra(ClientSettings.EXTRA_MODE);
        
        //Initialize The Color Spinner
        colorSpinner = (Spinner) findViewById(R.id.allianceColorSpinner12);
        
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
        //Setup Button to go back to the previous Activity
        backButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });
        //Setup Radio Group For Climb and Score
        RadioGroup scoreGroup = (RadioGroup) findViewById(R.id.scoreGroup);
        scoreGroup.setOnCheckedChangeListener(this);
        RadioGroup climbGroup = (RadioGroup) findViewById(R.id.climbGroup);
        climbGroup.setOnCheckedChangeListener(this);
        //Setup Buttons for Online/Offline Mode
    	if(networkMode.equals("Offline"))
    	{
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
                		if(goodScoreData())
                		{
                    		if(goodClimbData())
                    		{
                    			dataToProceed = true;
                    		}
                    		else
                    		{
                    			dataToProceed = false;
                            	AlertDialog.Builder builder = new AlertDialog.Builder(FieldsForm.this);
                            	builder.setCancelable(false);
                            	builder.setTitle("Data Error");
                            	builder.setMessage("Please Check At Least One Climb Level For Climbing!");
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
                        	builder.setMessage("Please Check At Least One Goal For Scoring!");
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
    	else if(networkMode.equals("Online"))
    	{
    		
    	}
    	else
    	{
    		
    	}
    }
    
    private void clearEnteredData()
    {
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText penalites = (EditText) findViewById(R.id.penaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);
    	
    	teamScore.setText("");
    	allianceScore.setText("");
    	penalites.setText("");
    	describeMovement.setText("");
    	additionalComments.setText("");
    	
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	
    	onePointGoal.setChecked(false);
    	twoPointGoal.setChecked(false);
    	threePointGoal.setChecked(false);
    	fivePointGoal.setChecked(false);
    	climb1.setChecked(false);
    	climb2.setChecked(false);
    	climb3.setChecked(false);
    	defense.setChecked(false);
    	climbAssist.setChecked(false);
    	humanPlayer.setChecked(false);
    }
    
    public boolean goodTextEntryData()
    {
    	boolean goodTextData = false;
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText penalites = (EditText) findViewById(R.id.penaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);
		
    	if(!teamScore.getText().toString().equals(""))
    	{
        	if(!teamScore.getText().toString().equals(""))
        	{
            	if(!allianceScore.getText().toString().equals(""))
            	{
                	if(!penalites.getText().toString().equals(""))
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
    	return goodTextData;
    }
    
    public boolean goodScoreData()
    {
    	boolean goodScoreData = false;
    	
    	if(validateScoreBoxes)
    	{
        	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
        	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
        	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
        	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
        	if(!onePointGoal.isChecked()&&!twoPointGoal.isChecked()&&!threePointGoal.isChecked()&&!fivePointGoal.isChecked())
        	{
        		goodScoreData = false;
        	}
        	else
        	{
        		goodScoreData = true;
        	}
    	}
    	else
    	{
    		goodScoreData = true;
    	}
    	
    	return goodScoreData;
    }
    
    //Check For Good Climb Data
    public boolean goodClimbData()
    {
    	boolean goodClimbData = false;
    	
    	//If Can Climb Radio Button Is Checked
    	if(validateClimbBoxes)
    	{
    		//Make Sure all boxes are not empty
        	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
        	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
        	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
        	if(!climb1.isChecked()&&!climb2.isChecked()&&!climb3.isChecked())
        	{
        		goodClimbData = false;
        	}
        	else
        	{
        		goodClimbData = true;
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
    	if(changedGroup.getId() == R.id.scoreGroup)
    	{
    		CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    		CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    		CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    		CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	
    		switch(checkedId)
    		{
    			case R.id.canScore:
    				onePointGoal.setClickable(true);
    				twoPointGoal.setClickable(true);
    				threePointGoal.setClickable(true);
    				fivePointGoal.setClickable(true);
    				validateScoreBoxes = true;
    				break;
    			case R.id.cannotScore:
    				onePointGoal.setClickable(false);
    				twoPointGoal.setClickable(false);
    				threePointGoal.setClickable(false);
    				fivePointGoal.setClickable(false);
    				onePointGoal.setChecked(false);
    				twoPointGoal.setChecked(false);
    				threePointGoal.setChecked(false);
    				fivePointGoal.setChecked(false);
    				validateScoreBoxes = false;
    				break;
    		}
    	}
    	else if(changedGroup.getId() == R.id.climbGroup)
    	{
        	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
        	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
        	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
        	
    		switch(checkedId)
    		{
    			case R.id.canClimb:
    				climb1.setClickable(true);
    				climb2.setClickable(true);
    				climb3.setClickable(true);
    				validateScoreBoxes = false;
    				break;
    			case R.id.cannotClimb:
    				climb1.setClickable(false);
    				climb2.setClickable(false);
    				climb3.setClickable(false);
    				climb1.setChecked(false);
    				climb2.setChecked(false);
    				climb3.setChecked(false);
    				validateClimbBoxes = false;
    				break;
    		}
    	}
    }
    
    public void confirmUserSave()
    {
    	//Variables For all of my View Componets
    	EditText matchNumber = (EditText) findViewById(R.id.matchNumberField1);
    	EditText teamNumber = (EditText) findViewById(R.id.teamNumberField1);
    	EditText teamScore = (EditText) findViewById(R.id.teamScoreField);
    	EditText allianceScore = (EditText) findViewById(R.id.allianceScoreField);
    	EditText penalites = (EditText) findViewById(R.id.penaltiesField);
    	EditText describeMovement = (EditText) findViewById(R.id.movementField);
    	EditText additionalComments = (EditText) findViewById(R.id.additionalCommentsField);	
    	CheckBox defense = (CheckBox) findViewById(R.id.defenseCheckBox);
    	CheckBox climbAssist = (CheckBox) findViewById(R.id.climbAssistCheckBox);
    	CheckBox humanPlayer = (CheckBox) findViewById(R.id.humanPlayerCheckBox);
    	RadioButton canScore = (RadioButton) findViewById(R.id.canScore);
    	RadioButton canClimb = (RadioButton) findViewById(R.id.canClimb);
    	Spinner colorSpinner = (Spinner) findViewById(R.id.allianceColorSpinner12);
    	CheckBox climb1 = (CheckBox) findViewById(R.id.level1ClimbCheckBox);
    	CheckBox climb2 = (CheckBox) findViewById(R.id.level2ClimbCheckBox);
    	CheckBox climb3 = (CheckBox) findViewById(R.id.level3ClimbCheckBox);
    	CheckBox onePointGoal = (CheckBox) findViewById(R.id.onePointCheckBox);
    	CheckBox twoPointGoal = (CheckBox) findViewById(R.id.twoPointCheckBox);
    	CheckBox threePointGoal = (CheckBox) findViewById(R.id.threePointCheckBox);
    	CheckBox fivePointGoal = (CheckBox) findViewById(R.id.fivePointCheckBox);
    	
    	//Build The Strings for the Alert Box
    	StringBuilder mes = new StringBuilder();
    	mes.append("Match Number: " + matchNumber.getText() + "\n");
    	mes.append("Team Number: " + teamNumber.getText() + "\n");
    	mes.append("Alliance Color: " + colorSpinner.getSelectedItem().toString() + "\n");
    	mes.append("Team Score: " + teamScore.getText() + "\n");
    	mes.append("Alliance Score: " + allianceScore.getText() + "\n");
    	mes.append("Penalties: " + penalites.getText() + "\n");
    	mes.append("\n");
    	if(canScore.isChecked())
    	{
        	mes.append("Scoring: Can Score" + "\n");
        	mes.append("One Point Goal: " + onePointGoal.isChecked() + "\n");
        	mes.append("Two Point Goal: " + twoPointGoal.isChecked() + "\n");
        	mes.append("Three Point Goal: " + threePointGoal.isChecked() + "\n");
        	mes.append("Five Point Goal: " + fivePointGoal.isChecked() + "\n");
    	}
    	else
    	{
    		mes.append("Scoring: Cannot Score" + "\n");
    	}
    	mes.append("\n");
    	if(canClimb.isChecked())
    	{
        	mes.append("Climbing: Can Climb" + "\n");
        	mes.append("Level 1: " + climb1.isChecked() + "\n");
        	mes.append("Level 2: " + climb2.isChecked() + "\n");
        	mes.append("Level 3: " + climb3.isChecked() + "\n");
    	}
    	else
    	{
    		mes.append("Climbing: Cannot Climb" + "\n");
    	}
    	mes.append("\n");
    	mes.append("Special Abilities: " + "\n");
    	mes.append("Defense: " + defense.isChecked() + "\n");
    	mes.append("Climbing Assist: " + climbAssist.isChecked() + "\n");
    	mes.append("Scoring Human Player: " + humanPlayer.isChecked() + "\n");
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
		md.setPenalties(penalites.getText().toString());
		md.setScoringStatus(canScore.isChecked(), onePointGoal.isChecked(), twoPointGoal.isChecked(), threePointGoal.isChecked(), fivePointGoal.isChecked());
		md.setClimbingStatus(canClimb.isChecked(), climb1.isChecked(), climb2.isChecked(), climb3.isChecked());
		md.setSpecialFeatures(defense.isChecked(), climbAssist.isChecked(), humanPlayer.isChecked());
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
