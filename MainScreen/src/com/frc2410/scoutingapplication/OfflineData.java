package com.frc2410.scoutingapplication;

import java.io.File;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OfflineData extends Activity
{
	//Variable to Store a List of Files
	File[] filesToRead;
	//Extra For File Name
	public final static String FILE_NAME = "com.frc2410.scoutingapplication.FILE_NAME";
	
    //Arrays for the List Views
    private ArrayAdapter<String> scoutedMatches;
    private ArrayAdapter<String> scoutedPits;
    
    //Helper Object for File Access
    FileStorageHelper fileAccess;
    
    //Variable to Prevent multiple loads
    private boolean alreadyLoaded = false;
    
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
        //Set View to the File Picker Screen
        setContentView(R.layout.activity_offlinedata);
        
        //Initialize Back Button
        Button goBackButton = (Button) findViewById(R.id.offlineGoBackButton);
        goBackButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                finish();
            }
        });
        
        //Initialize Refresh Button
        Button refreshButton = (Button) findViewById(R.id.offlineDataRefreshButton);
        refreshButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	//Reload Saved Scouts
            	loadPreviousScouts();
            }
        });
        
        // Initialize array adapters
        scoutedMatches = new ArrayAdapter<String>(this, R.layout.previous_scout);
        scoutedPits = new ArrayAdapter<String>(this, R.layout.previous_scout);
        
        // Find and set up the ListView for Previously Scouted Matches
        ListView matchesListView = (ListView) findViewById(R.id.scoutedMatches);
        matchesListView.setAdapter(scoutedMatches);
        matchesListView.setOnItemClickListener(scoutedMatchesClickListener);

        // Find and set up the ListView for Previously Scouted Pits
        ListView pitsListView = (ListView) findViewById(R.id.scoutedPits);
        pitsListView.setAdapter(scoutedPits);
        pitsListView.setOnItemClickListener(scoutedPitsClickListener);
	}
	
	protected void onStop()
	{
		super.onStop();
		
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStop(this);
	}
	
	protected void onStart()
	{
    	super.onStart();
    	
    	//Setup Google Analytics
    	EasyTracker.getInstance().setContext(getApplicationContext());
    	EasyTracker.getInstance().activityStart(this);
    	
    	//Load Scouts
    	loadPreviousScouts();
	}

	private void loadPreviousScouts() 
	{
		//Create File Helper Object
		fileAccess = new FileStorageHelper();
		
		//Clear List Views to Ensure no Double Population
		scoutedMatches.clear();
		scoutedPits.clear();
		
		//Make Sure we can read from External Storage
		if(fileAccess.canWeRead())
		{
			filesToRead = fileAccess.getFileList();
			if(filesToRead == null)
			{
				scoutedMatches.add("No Scouted Matches!");
				scoutedPits.add("No Scouted Pits!");
			}
			else
			{
				for(File temp : filesToRead)
				{
					String fileName = temp.getName();
					String typeQualifier = fileName.substring(0, 2);
					if(typeQualifier.equals("MS"))
					{
						//Match Scout File
						scoutedMatches.add(decodeFileNameMatches(fileName) + "\n" + fileName);
					}
					else if(typeQualifier.equals("PS"))
					{
						//Pit Scout File
						scoutedPits.add(decodeFileNamePits(fileName) + "\n" + fileName);
					}
					else
					{
						//Unknown File
						//Do Nothing
					}
				}
			}
			if(scoutedMatches.isEmpty())
			{
				scoutedMatches.add("No Scouted Matches!");
			}
			if(scoutedPits.isEmpty())
			{
				scoutedPits.add("No Scouted Pits!");
			}
		}
		else
		{
			//Display Error Toast
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to Read Files, Make Sure your External Storage is Mounted!", Toast.LENGTH_LONG);
			toast.show();
			//Finish Activity
			finish();
		}
	}
	
    //Click Listener For Scouted Matches
    private OnItemClickListener scoutedMatchesClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
        {
        	//Get the Name of the Selected File
        	String selectedData = ((TextView) v).getText().toString();
        	if(!selectedData.equals("No Scouted Matches!"));
        	{
        		//Get Filename from Selected Data
        		int indexOfM = selectedData.lastIndexOf("M");
        	
        		//Name of File
        		String fileName = selectedData.substring(indexOfM, selectedData.length());
        	
        		//Create New Intent to view the Selected Data
        		Intent intent = new Intent(OfflineData.this, ViewSavedMatch.class);
        		intent.putExtra(FILE_NAME, fileName);
        		startActivity(intent);
        	}
        }
    };
    
    //Click Listener For Scouted Pits
    private OnItemClickListener scoutedPitsClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
        {
        	//Get the Name of the Selected File
        	String selectedData = ((TextView) v).getText().toString();
        	if(!selectedData.equals("No Scouted Pits!"));
        	{
        		//Get Filename from Selected Data
        		int indexOfP = selectedData.lastIndexOf("P");
        	
        		//Name of File
        		String fileName = selectedData.substring(indexOfP, selectedData.length());

        		//Create New Intent to view the Selected Data
        		Intent intent = new Intent(OfflineData.this, ViewSavedPit.class);
        		intent.putExtra(FILE_NAME, fileName);
        		startActivity(intent);
        	}
        }
    };
    
    private String decodeFileNameMatches(String fileName)
    {
    	StringBuilder sb = new StringBuilder();
    	
		//Create String without Qualifier
		String newName = fileName.substring(3,fileName.length());
		
		//Create String without Extension
		String[] splitData = newName.split("\\.");
		String stringToParse = splitData[0];
		
		//Split String for Fields
		String[] parseSplit = stringToParse.split("_");
    	int partNum = 1;
    	for(String splitPart : parseSplit)
    	{
    		if(partNum == 1)
    		{
    			sb.append("Match Number: " + splitPart);
    			sb.append("   ");
    			partNum++;
    		}
    		else if(partNum == 2)
    		{
    			sb.append("Team Number: " + splitPart);
    			sb.append("   ");
    			partNum++;
    		}
    		else if(partNum == 3)
    		{
    			sb.append("Date: " + splitPart);
    			sb.append("   ");
    			partNum++;
    		}
    		else
    		{
    			
    		}
    	}
    	return sb.toString();
    }
    
    private String decodeFileNamePits(String fileName)
    {
    	StringBuilder sb = new StringBuilder();
    	
		//Create String without Qualifier
		String newName = fileName.substring(3,fileName.length());
		
		//Create String without Extension
		String[] splitData = newName.split("\\.");
		String stringToParse = splitData[0];
		
		//Split String for Fields
		String[] parseSplit = stringToParse.split("_");
    	int partNum = 1;
    	
    	for(String splitPart : parseSplit)
    	{
    		if(partNum == 1)
    		{
    			sb.append("Team Number: " + splitPart);
    			sb.append("   ");
    			partNum++;
    		}
    		else if(partNum == 2)
    		{
    			sb.append("Date: " + splitPart);
    			sb.append("   ");
    			partNum++;
    		}
    		else
    		{
    			
    		}
    	}
    	return sb.toString();
    }
}
