package com.frc2410.scoutingapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import android.os.Environment;

public class MatchScoutData 
{
	private String dateString;
	private int matchNumber;
	private int teamNumber;
	private String allianceColor;
	private int pointsScored;
	private int allianceScore;
	private int penalties;
	private String additionalComments;
	private String movementDescription;
	
	private boolean scoring;
	private boolean onePointGoal;
	private boolean twoPointGoal;
	private boolean threePointGoal;
	private boolean fivePointGoal;
	
	private boolean climbing;
	private boolean levelOne;
	private boolean levelTwo;
	private boolean levelThree;
	
	private boolean defense;
	private boolean climbAssist;
	private boolean scoringHumanPlayer;
	
	public MatchScoutData()
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
			 
			 //Read Initial Text Data
			 matchNumber = Integer.parseInt(splitData[0]);
			 teamNumber = Integer.parseInt(splitData[1]);
			 allianceColor = splitData[2];
			 pointsScored = Integer.parseInt(splitData[3]);
			 allianceScore = Integer.parseInt(splitData[4]);
			 penalties = Integer.parseInt(splitData[5]);
			 
			 //Read Scoring Data
			 scoring = stringToBooleanDecode(splitData[6]);
			 onePointGoal = stringToBooleanDecode(splitData[7]);
			 twoPointGoal = stringToBooleanDecode(splitData[8]);
			 threePointGoal = stringToBooleanDecode(splitData[9]);
			 fivePointGoal = stringToBooleanDecode(splitData[10]);
			 
			 //Read Climbing Data
			 climbing = stringToBooleanDecode(splitData[11]);
			 levelOne = stringToBooleanDecode(splitData[12]);
			 levelTwo = stringToBooleanDecode(splitData[13]);
			 levelThree = stringToBooleanDecode(splitData[14]);
			 
			 //Read Special Features Data
			 defense = stringToBooleanDecode(splitData[15]);
			 climbAssist = stringToBooleanDecode(splitData[16]);
			 scoringHumanPlayer = stringToBooleanDecode(splitData[17]);
			 
			 //Read Final Text Data
			 movementDescription = splitData[18];
			 additionalComments = splitData[19];
			 inStream.close();
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
		boolean returnBoolean;
		
		if(readLine.equals("Yes"))
		{
			returnBoolean = true;
		}
		else if(readLine.equals("No"))
		{
			returnBoolean = false;
		}
		else
		{
			returnBoolean = false;
		}
		return returnBoolean;
	}

	public String getDate()
	{
		return dateString;
	}
	
	public void setMatchNumber(String mn)
	{
		matchNumber = Integer.parseInt(mn);
	}
	
	public void setTeamNumber(String tn)
	{
		teamNumber = Integer.parseInt(tn);
	}
	
	public void setAllianceColor(String ac)
	{
		allianceColor = ac;
	}
	
	public void setPointsScored(String ps)
	{
		pointsScored = Integer.parseInt(ps);
	}
	
	public void setAllianceScore(String as)
	{
		allianceScore = Integer.parseInt(as);
	}
	
	public void setPenalties(String p)
	{
		penalties = Integer.parseInt(p);
	}
	
	public void setMovementDescription(String md)
	{
		movementDescription = md;
	}
	
	public void setComments(String c)
	{
		additionalComments =c;
	}
	
	public void setScoringStatus(boolean canScore, boolean onePoint, boolean twoPoint, boolean threePoint, boolean fivePoint)
	{
		scoring = canScore;
		onePointGoal = onePoint;
		twoPointGoal = twoPoint;
		threePointGoal = threePoint;
		fivePointGoal = fivePoint;
	}
	
	public void setClimbingStatus(boolean canClimb, boolean l1, boolean l2, boolean l3)
	{
		climbing = canClimb;
		levelOne = l1;
		levelTwo = l2;
		levelThree = l3;
	}
	
	public void setSpecialFeatures(boolean df, boolean ca, boolean hp)
	{
		defense = df;
		climbAssist = ca;
		scoringHumanPlayer = hp;
	}
	
	public int getMatchNumber()
	{
		return matchNumber;
	}
	
	public int getTeamNumber()
	{
		return teamNumber;
	}
	
	public String getAllianceColor()
	{
		return allianceColor;
	}
	
	public int getPointsScored()
	{
		return pointsScored;
	}
	
	public int getAllianceScore()
	{
		return allianceScore;
	}
	
	public int getPenalties()
	{
		return penalties;
	}
	
	public String getMovementDescription()
	{
		return movementDescription;
	}
	
	public String getComments()
	{
		return additionalComments;
	}
	
	public boolean[] getScoringStatus()
	{
		boolean[] scoringData = new boolean[5];
		scoringData[0] = scoring;
		scoringData[1] = onePointGoal;
		scoringData[2] = twoPointGoal;
		scoringData[3] = threePointGoal;
		scoringData[4] = fivePointGoal;
		return scoringData;
	}
	
	public boolean[] getClimbingStatus()
	{
		boolean[] climbingData = new boolean[4];
		climbingData[0] = climbing;
		climbingData[1] = levelOne;
		climbingData[2] = levelTwo;
		climbingData[3] = levelThree;
		return climbingData;
	}
	
	public boolean[] getSpecialFeatures()
	{
		boolean[] specialFeatures = new boolean[3];
		specialFeatures[0] = defense;
		specialFeatures[1] = climbAssist;
		specialFeatures[2] = scoringHumanPlayer;
		return specialFeatures;
	}
}
