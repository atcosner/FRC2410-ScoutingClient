package com.frc2410.scoutingapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import android.os.Environment;

public class PitScoutData 
{
	private String dateString;
	private int teamNumber;
	private String teamName;
	private String strategy;
	
	private boolean shooter;
	private String shooterDescription;
	private int finalSpeed;
	private int shooterAngle;
	private boolean adjustableShooter;
	private boolean floorPickup;
	private int systemSpeed;
	
	private String drivetrainDescription;
	private String drivetrainWheels;
	private int drivetrainSpeed;
	private String drivetrainSpecial;
	
	private boolean climbing;
	private int climbingSpeed;
	private int climbingLevel;
	private boolean climbingSide;
	private boolean climbingCorner;
	
	private boolean autonomous;
	private int autonomousPlacement;
	private int autonomousNumPreloaded;
	private int autonomousLevelAimed;
	private boolean autonomousFloorPickup;
	
	private int maintTime;
	private int tripTime;
	private boolean ableToDefend;
	private boolean coloredDiscs;
	private boolean visionTargeting;
	private int estimatedScore;
	
	public PitScoutData()
	{
		Calendar c = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(c.get(Calendar.MONTH) + 1);
		sb.append("-");
		sb.append(c.get(Calendar.DATE));
		sb.append("-");
		sb.append(c.get(Calendar.YEAR));
		sb.append("-");
		sb.append(c.get(Calendar.HOUR));
		sb.append("-");
		sb.append(c.get(Calendar.MINUTE));
		dateString = sb.toString();	
	}
	
	public void populateFromFile(String fileName)
	{
		 //Get Folder Path To Files
		 File rootPath = new File(Environment.getExternalStorageDirectory(),"scoutingData");
		 //Get File with name Parameter
		 File matchFileToLoad = new File(rootPath,fileName);
		 try
		 {
			 //Open Reading Streams
			 FileReader fr = new FileReader(matchFileToLoad);
			 BufferedReader inStream = new BufferedReader(fr);
			 StringBuilder sb = new StringBuilder();
			 String bigText = "";
			 
			 do
			 {
				 bigText = inStream.readLine();
				 sb.append(bigText);
			 }
			 while(!bigText.substring(bigText.length()-1).equals("@"));
			 
			 String goodData = sb.substring(0,sb.length()-1);
			 
			 String[] splitData = goodData.split("-");
			 
			 //Read Header Data
			 teamNumber = Integer.parseInt(splitData[0]);
			 teamName = splitData[1];
			 strategy = splitData[2];
			 
			 //Read Shooter Data
			 shooter = stringToBooleanDecode(splitData[3]);
			 shooterDescription = splitData[4];
			 finalSpeed = Integer.parseInt(splitData[5]);
			 shooterAngle = Integer.parseInt(splitData[6]);
			 adjustableShooter = stringToBooleanDecode(splitData[7]);
			 floorPickup = stringToBooleanDecode(splitData[8]);
			 systemSpeed = Integer.parseInt(splitData[9]);
			 
			 //Read Drivetrain Data
			 drivetrainDescription = splitData[10];
			 drivetrainWheels = splitData[11];
			 drivetrainSpeed = Integer.parseInt(splitData[12]);
			 drivetrainSpecial = splitData[13];
			 
			 //Read Climbing Data
			 climbing = stringToBooleanDecode(splitData[14]);
			 climbingSpeed = Integer.parseInt(splitData[15]);
			 climbingLevel = Integer.parseInt(splitData[16]);
			 climbingSide = stringToBooleanDecode(splitData[17]);
			 climbingCorner = stringToBooleanDecode(splitData[18]);
			 
			 //Read Misc Data
			 maintTime = Integer.parseInt(splitData[19]);
			 ableToDefend = stringToBooleanDecode(splitData[20]);
			 coloredDiscs = stringToBooleanDecode(splitData[21]);
			 visionTargeting = stringToBooleanDecode(splitData[22]);
			 estimatedScore = Integer.parseInt(splitData[23]);
			 tripTime = Integer.parseInt(splitData[24]);
			 
			 //Read Autonomous Data
			 autonomous = stringToBooleanDecode(splitData[25]);
			 autonomousPlacement = Integer.parseInt(splitData[26]);
			 autonomousNumPreloaded = Integer.parseInt(splitData[27]);
			 autonomousLevelAimed = Integer.parseInt(splitData[28]);
			 autonomousFloorPickup = stringToBooleanDecode(splitData[29]);
		 }
		 catch (FileNotFoundException e)
		 {
			 e.printStackTrace();
		 }
		 catch (IOException e)
		 {
			 e.printStackTrace();
		 }
	}
	
	private boolean stringToBooleanDecode(String readLine) 
	{
		
		if(readLine.equals("true"))
		{
			return true;
		}
		else if(readLine.equals("false"))
		{
			return false;
		}
		else
		{
			return false;
		}
	}
	
	public String getDate()
	{
		return dateString;
	}
	
	public int getTripTime()
	{
		return tripTime;
	}
	
	public int getTeamNumber()
	{
		return teamNumber;
	}
	
	public String getTeamName()
	{
		return teamName;
	}
	
	public String getStrategy()
	{
		return strategy;
	}
	
	public boolean getShooterStatus()
	{
		return shooter;
	}
	
	public String getShooterDescription()
	{
		return shooterDescription;
	}
	
	public int getShooterFinalSpeed()
	{
		return finalSpeed;
	}
	
	public int getShooterAngle()
	{
		return shooterAngle;
	}
	
	public boolean getShooterAdjustable()
	{
		return adjustableShooter;
	}
	
	public boolean getShooterPickUp()
	{
		return floorPickup;
	}
	
	public int getShooterSystemSpeed()
	{
		return systemSpeed;
	}
	
	public boolean getClimbingStatus()
	{
		return climbing;
	}
	
	public int getClimbingSpeed()
	{
		return climbingSpeed;
	}
	
	public int getClimbingLevel()
	{
		return climbingLevel;
	}
	
	public boolean getSideClimb()
	{
		return climbingSide;
	}
	
	public boolean getCornerClimb()
	{
		return climbingCorner;
	}
	
	public boolean getAutonomousStatus()
	{
		return autonomous;
	}
	
	public int getAutonomousPlacement()
	{
		return autonomousPlacement;
	}
	
	public int getAutonomousNumPreloaded()
	{
		return autonomousNumPreloaded;
	}
	
	public int getAutonomousLevelAimed()
	{
		return autonomousLevelAimed;
	}
	
	public boolean getAutonomousFloorPickup()
	{
		return autonomousFloorPickup;
	}
	
	public String getDrivetrainDescription()
	{
		return drivetrainDescription;
	}
	
	public String getDrivetrainWheels()
	{
		return drivetrainWheels;
	}
	
	public int getDrivetrainSpeed()
	{
		return drivetrainSpeed;
	}
	
	public String getDrivetrainSpecial()
	{
		return drivetrainSpecial;
	}
	
	public int getMaintenanceTime()
	{
		return maintTime;
	}
	
	public boolean getAbleToDefend()
	{
		return ableToDefend;
	}
	
	public boolean getColoredDiscs()
	{
		return coloredDiscs;
	}
	
	public boolean getVisionTargeting()
	{
		return visionTargeting;
	}
	
	public int getEstimatedScore()
	{
		return estimatedScore;
	}
	
	public void setHeaderData(int tN, String tNM, String s)
	{
		teamNumber = tN;
		teamName = tNM;
		strategy = s;
	}
	
	public void setShooterData(boolean sh, String sD, int fS, int sA, boolean adj, boolean fP, int sS)
	{
		shooter = sh;
		shooterDescription = sD;
		finalSpeed = fS;
		shooterAngle = sA;
		adjustableShooter = adj;
		floorPickup = fP;
		systemSpeed = sS;
	}
	
	public void setDrivetrainData(String dD,String dW, int dS, String dSP)
	{
		drivetrainDescription = dD;
		drivetrainWheels = dW;
		drivetrainSpeed = dS;
		drivetrainSpecial = dSP;
	}
	
	public void setClimbingData(boolean c, int cS, int cL, boolean cSide, boolean cCorner)
	{		
		climbing = c;
		climbingSpeed = cS;
		climbingLevel = cL;
		climbingSide = cSide;
		climbingCorner = cCorner;
	}
	
	public void setMiscData(int mT, boolean aD, boolean cD, boolean vT, int eS,int tT)
	{		
		maintTime = mT;
		ableToDefend = aD;
		coloredDiscs = cD;
		visionTargeting = vT;
		estimatedScore = eS;
		tripTime = tT;
	}
	
	public void setAutonomousData(boolean aT, int p, int nP, int lA, boolean fP)
	{		
		autonomous = aT;
		autonomousPlacement = p;
		autonomousNumPreloaded = nP;
		autonomousLevelAimed = lA;
		autonomousFloorPickup = fP;
	}
}
