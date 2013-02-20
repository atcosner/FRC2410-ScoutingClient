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
	private int teamPenalties;
	private int alliancePenalties;
	private String additionalComments;
	private String movementDescription;
	
	private boolean shooting;
	private boolean onePointGoal;
	private boolean twoPointGoal;
	private boolean threePointGoal;
	private boolean fivePointGoal;
	private int teamShooterPenalties;
	private int allianceShooterPenalties;
	private int onePointGoalShotsMade;
	private int twoPointGoalShotsMade;
	private int threePointGoalShotsMade;
	private int fivePointGoalShotsMade;
	private int onePointGoalShotsTaken;
	private int twoPointGoalShotsTaken;
	private int threePointGoalShotsTaken;
	private int fivePointGoalShotsTaken;
	
	private boolean climbing;
	private boolean levelOne;
	private boolean levelTwo;
	private boolean levelThree;
	private int sucessfulClimbs;
	private int totalClimbAttempts;
	private int climbingTeamPenalties;
	private int climbingAlliancePenalties;
	
	private boolean autonomous;
	private int autonomousTeamPenalties;
	private int autonomousAlliancePenalties;
	private boolean onePointGoalAutonomous;
	private boolean twoPointGoalAutonomous;
	private boolean threePointGoalAutonomous;
	private boolean fivePointGoalAutonomous;
	private int onePointGoalShotsMadeAutonomous;
	private int twoPointGoalShotsMadeAutonomous;
	private int threePointGoalShotsMadeAutonomous;
	private int fivePointGoalShotsMadeAutonomous;
	private int onePointGoalShotsTakenAutonomous;
	private int twoPointGoalShotsTakenAutonomous;
	private int threePointGoalShotsTakenAutonomous;
	private int fivePointGoalShotsTakenAutonomous;
	
	private boolean defense;
	private int defenseRank;
	private boolean climbAssist;
	private boolean humanPlayer;
	private int humanPlayerPenalties;
	private int humanPlayerShotsMade;
	private int humanPlayerShotsTaken;
	
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
			 teamPenalties = Integer.parseInt(splitData[5]);
			 alliancePenalties = Integer.parseInt(splitData[6]);
			 
			 //Read Shooting Data
			 shooting = stringToBooleanDecode(splitData[7]);
			 onePointGoal = stringToBooleanDecode(splitData[8]);
			 twoPointGoal = stringToBooleanDecode(splitData[9]);
			 threePointGoal = stringToBooleanDecode(splitData[10]);
			 fivePointGoal = stringToBooleanDecode(splitData[11]);
			 teamShooterPenalties = Integer.parseInt(splitData[12]);
			 allianceShooterPenalties = Integer.parseInt(splitData[13]);
			 onePointGoalShotsMade = Integer.parseInt(splitData[14]);
			 twoPointGoalShotsMade = Integer.parseInt(splitData[15]);
			 threePointGoalShotsMade = Integer.parseInt(splitData[16]);
			 fivePointGoalShotsMade = Integer.parseInt(splitData[17]);
			 onePointGoalShotsTaken = Integer.parseInt(splitData[18]);
			 twoPointGoalShotsTaken = Integer.parseInt(splitData[19]);
			 threePointGoalShotsTaken = Integer.parseInt(splitData[20]);
			 fivePointGoalShotsTaken = Integer.parseInt(splitData[21]);
			 
			 //Read Climbing Data
			 climbing = stringToBooleanDecode(splitData[22]);
			 levelOne = stringToBooleanDecode(splitData[23]);
			 levelTwo = stringToBooleanDecode(splitData[24]);
			 levelThree = stringToBooleanDecode(splitData[25]);
			 sucessfulClimbs = Integer.parseInt(splitData[26]);
			 totalClimbAttempts = Integer.parseInt(splitData[27]);
			 climbingTeamPenalties = Integer.parseInt(splitData[28]);
			 climbingAlliancePenalties = Integer.parseInt(splitData[29]);
				
			 //Read Autonomous Data
			 autonomous = stringToBooleanDecode(splitData[30]);
			 onePointGoalAutonomous = stringToBooleanDecode(splitData[31]);
			 twoPointGoalAutonomous = stringToBooleanDecode(splitData[32]);
			 threePointGoalAutonomous = stringToBooleanDecode(splitData[33]);
			 fivePointGoalAutonomous = stringToBooleanDecode(splitData[34]);
			 autonomousTeamPenalties = Integer.parseInt(splitData[35]);
			 autonomousAlliancePenalties = Integer.parseInt(splitData[36]);
			 onePointGoalShotsMadeAutonomous = Integer.parseInt(splitData[37]);
			 twoPointGoalShotsMadeAutonomous = Integer.parseInt(splitData[38]);
			 threePointGoalShotsMadeAutonomous = Integer.parseInt(splitData[39]);
			 fivePointGoalShotsMadeAutonomous = Integer.parseInt(splitData[40]);
			 onePointGoalShotsTakenAutonomous = Integer.parseInt(splitData[41]);
			 twoPointGoalShotsTakenAutonomous = Integer.parseInt(splitData[42]);
			 threePointGoalShotsTakenAutonomous = Integer.parseInt(splitData[43]);
			 fivePointGoalShotsTakenAutonomous = Integer.parseInt(splitData[44]);
				
			 //Read Special Features Data
			 defense = stringToBooleanDecode(splitData[45]);
			 climbAssist = stringToBooleanDecode(splitData[46]);
			 humanPlayer = stringToBooleanDecode(splitData[47]);
			 defenseRank = Integer.parseInt(splitData[48]);
			 humanPlayerPenalties = Integer.parseInt(splitData[49]);
			 humanPlayerShotsMade = Integer.parseInt(splitData[50]);
			 humanPlayerShotsTaken = Integer.parseInt(splitData[51]);
			 
			 //Read Final Text Data
			 movementDescription = splitData[52];
			 additionalComments = splitData[53];
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
		
		if(readLine.equals("true"))
		{
			returnBoolean = true;
		}
		else if(readLine.equals("false"))
		{
			returnBoolean = false;
		}
		else
		{
			returnBoolean = false;
		}
		return returnBoolean;
	}

	public String createStringToSend()
	{
		StringBuilder sb = new StringBuilder();
		
		 //Write Initial Text Data
		 sb.append(matchNumber);
		 sb.append("-");
		 sb.append(teamNumber);
		 sb.append("-");
		 sb.append(allianceColor);
		 sb.append("-");
		 sb.append(pointsScored);
		 sb.append("-");
		 sb.append(allianceScore);
		 sb.append("-");
		 sb.append(teamPenalties);
		 sb.append("-");
		 sb.append(alliancePenalties);
		 sb.append("-");
		 //Write Shooting Data
		 sb.append(shooting);
		 sb.append("-");
		 sb.append(onePointGoal);
		 sb.append("-");
		 sb.append(twoPointGoal);
		 sb.append("-");
		 sb.append(threePointGoal);
		 sb.append("-");
		 sb.append(fivePointGoal);
		 sb.append("-");
		 sb.append(teamShooterPenalties);
		 sb.append("-");
		 sb.append(allianceShooterPenalties);
		 sb.append("-");
		 sb.append(onePointGoalShotsMade);
		 sb.append("-");
		 sb.append(twoPointGoalShotsMade);
		 sb.append("-");
		 sb.append(threePointGoalShotsMade);
		 sb.append("-");
		 sb.append(fivePointGoalShotsMade);
		 sb.append("-");
		 sb.append(onePointGoalShotsTaken);
		 sb.append("-");
		 sb.append(twoPointGoalShotsTaken);
		 sb.append("-");
		 sb.append(threePointGoalShotsTaken);
		 sb.append("-");
		 sb.append(fivePointGoalShotsTaken);
		 sb.append("-");
		 
		 //Append Climbing Data
		 sb.append(climbing);
		 sb.append("-");
		 sb.append(levelOne);
		 sb.append("-");
		 sb.append(levelTwo);
		 sb.append("-");
		 sb.append(levelThree);
		 sb.append("-");
		 sb.append(sucessfulClimbs);
		 sb.append("-");
		 sb.append(totalClimbAttempts);
		 sb.append("-");
		 sb.append(climbingTeamPenalties);
		 sb.append("-");
		 sb.append(climbingAlliancePenalties);
		 sb.append("-");
		 
		 //Append Autonomous Data
		 sb.append(autonomous);
		 sb.append("-");
		 sb.append(autonomousTeamPenalties);
		 sb.append("-");
		 sb.append(autonomousAlliancePenalties);
		 sb.append("-");
		 sb.append(onePointGoalAutonomous);
		 sb.append("-");
		 sb.append(twoPointGoalAutonomous);
		 sb.append("-");
		 sb.append(threePointGoalAutonomous);
		 sb.append("-");
		 sb.append(fivePointGoalAutonomous);
		 sb.append("-");
		 sb.append(onePointGoalShotsMadeAutonomous);
		 sb.append("-");
		 sb.append(twoPointGoalShotsMadeAutonomous);
		 sb.append("-");
		 sb.append(threePointGoalShotsMadeAutonomous);
		 sb.append("-");
		 sb.append(fivePointGoalShotsMadeAutonomous);
		 sb.append("-");
		 sb.append(onePointGoalShotsTakenAutonomous);
		 sb.append("-");
		 sb.append(twoPointGoalShotsTakenAutonomous);
		 sb.append("-");
		 sb.append(threePointGoalShotsTakenAutonomous);
		 sb.append("-");
		 sb.append(fivePointGoalShotsTakenAutonomous);
		 sb.append("-");
		 //Append Special Features Data
		 sb.append(defense);
		 sb.append("-");
		 sb.append(defenseRank);
		 sb.append("-");
		 sb.append(climbAssist);
		 sb.append("-");
		 sb.append(humanPlayer);
		 sb.append("-");
		 sb.append(humanPlayerPenalties);
		 sb.append("-");
		 sb.append(humanPlayerShotsMade);
		 sb.append("-");
		 sb.append(humanPlayerShotsTaken);
		 sb.append("-");
		 
		 //Append End Text Data
		 String goodMovementData = movementDescription.replace("\n", " ");
		 sb.append(goodMovementData);
		 sb.append("-");
		 String goodAdditionalComments = additionalComments.replace("\n", " ");
		 sb.append(goodAdditionalComments);
		 sb.append("\n");
		 
		 return sb.toString();
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
	
	public void setPenalties(String tP, String aP)
	{
		teamPenalties = Integer.parseInt(tP);
		alliancePenalties = Integer.parseInt(aP);
	}
	
	public void setMovementDescription(String md)
	{
		movementDescription = md;
	}
	
	public void setComments(String c)
	{
		additionalComments =c;
	}
	
	public void setShootingData(boolean canShoot, boolean onePoint, boolean twoPoint, boolean threePoint, boolean fivePoint,int tSP,int aSP,int onePSM,int twoPSM,int threePSM,int fivePSM,
			int onePST,int twoPST,int threePST,int fivePST)
	{
		shooting = canShoot;
		onePointGoal = onePoint;
		twoPointGoal = twoPoint;
		threePointGoal = threePoint;
		fivePointGoal = fivePoint;
		teamShooterPenalties = tSP;
		allianceShooterPenalties = aSP;
		onePointGoalShotsMade = onePSM;
		twoPointGoalShotsMade = twoPSM;
		threePointGoalShotsMade = threePSM;
		fivePointGoalShotsMade = fivePSM;
		onePointGoalShotsTaken = onePST;
		twoPointGoalShotsTaken = twoPST;
		threePointGoalShotsTaken = threePST;
		fivePointGoalShotsTaken = fivePST;
	}
	
	public void setClimbingData(boolean canClimb, boolean l1, boolean l2, boolean l3, int sC, int tCA, int cTP, int cAP)
	{
		climbing = canClimb;
		levelOne = l1;
		levelTwo = l2;
		levelThree = l3;
		sucessfulClimbs = sC;
		totalClimbAttempts = tCA;
		climbingTeamPenalties = cTP;
		climbingAlliancePenalties = cAP;
	}
	
	public void setAutonomousData(boolean hasAutonomous, int aTP, int aAP, boolean onePGA, boolean twoPGA, boolean threePGA, boolean fivePGA, int onePGSMA, int twoPGSMA, int threePGSMA, int fivePGSMA,
			int onePGSTA, int twoPGSTA, int threePGSTA, int fivePGSTA)
	{
		autonomous = hasAutonomous;
		autonomousTeamPenalties = aTP;
		autonomousAlliancePenalties = aAP;
		onePointGoalAutonomous = onePGA;
		twoPointGoalAutonomous = twoPGA;
		threePointGoalAutonomous = threePGA;
		fivePointGoalAutonomous = fivePGA;
		onePointGoalShotsMadeAutonomous = onePGSMA;
		twoPointGoalShotsMadeAutonomous = twoPGSMA;
		threePointGoalShotsMadeAutonomous = threePGSMA;
		fivePointGoalShotsMadeAutonomous = fivePGSMA;
		onePointGoalShotsTakenAutonomous = onePGSTA;
		twoPointGoalShotsTakenAutonomous = twoPGSTA;
		threePointGoalShotsTakenAutonomous = threePGSTA;;
		fivePointGoalShotsTakenAutonomous = fivePGSTA;;
	}
	
	public void setSpecialFeaturesData(boolean df,int dR, boolean ca, boolean hp, int hPP, int hSM, int hST)
	{
		defense = df;
		defenseRank = dR;
		climbAssist = ca;
		humanPlayer = hp;
		humanPlayerPenalties = hPP;
		humanPlayerShotsMade = hSM;
		humanPlayerShotsTaken = hST;
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
	
	public int getTeamPenalties()
	{
		return teamPenalties;
	}
	
	public int getAlliancePenalties()
	{
		return alliancePenalties;
	}
	
	public String getMovementDescription()
	{
		return movementDescription;
	}
	
	public String getComments()
	{
		return additionalComments;
	}
	
	public boolean[] getShootingBooleans()
	{
		boolean[] scoringData = new boolean[5];
		scoringData[0] = shooting;
		scoringData[1] = onePointGoal;
		scoringData[2] = twoPointGoal;
		scoringData[3] = threePointGoal;
		scoringData[4] = fivePointGoal;
		return scoringData;
	}
	
	public int[] getShootingInts()
	{
		int[] scoringData = new int[10];
		scoringData[0] = teamShooterPenalties;
		scoringData[1] = allianceShooterPenalties;
		scoringData[2] = onePointGoalShotsMade;
		scoringData[3] = twoPointGoalShotsMade;
		scoringData[4] = threePointGoalShotsMade;
		scoringData[5] = fivePointGoalShotsMade;
		scoringData[6] = onePointGoalShotsTaken;
		scoringData[7] = twoPointGoalShotsTaken;
		scoringData[8] = threePointGoalShotsTaken;
		scoringData[9] = fivePointGoalShotsTaken;
		return scoringData;
	}
	
	public boolean[] getClimbingBooleans()
	{
		boolean[] climbingData = new boolean[4];
		climbingData[0] = climbing;
		climbingData[1] = levelOne;
		climbingData[2] = levelTwo;
		climbingData[3] = levelThree;
		return climbingData;
	}
	
	public int[] getClimbingInts()
	{
		int[] climbingData = new int[4];
		climbingData[0] = sucessfulClimbs;
		climbingData[1] = totalClimbAttempts;
		climbingData[2] = climbingTeamPenalties;
		climbingData[3] = climbingAlliancePenalties;
		return climbingData;
	}
	
	public boolean[] getAutonomousBooleans()
	{
		boolean[] autonomousData = new boolean[5];
		autonomousData[0] = autonomous;
		autonomousData[1] = onePointGoalAutonomous;
		autonomousData[2] = twoPointGoalAutonomous;
		autonomousData[3] = threePointGoalAutonomous;
		autonomousData[4] = fivePointGoalAutonomous;
		return autonomousData;
	}
	
	public int[] getAutonomousInts()
	{
		int[] autonomousData = new int[10];
		autonomousData[0] = autonomousTeamPenalties;
		autonomousData[1] = autonomousAlliancePenalties;
		autonomousData[2] = onePointGoalShotsMadeAutonomous;
		autonomousData[3] = twoPointGoalShotsMadeAutonomous;
		autonomousData[4] = threePointGoalShotsMadeAutonomous;
		autonomousData[5] = fivePointGoalShotsMadeAutonomous;
		autonomousData[6] = onePointGoalShotsTakenAutonomous;
		autonomousData[7] = twoPointGoalShotsTakenAutonomous;
		autonomousData[8] = threePointGoalShotsTakenAutonomous;
		autonomousData[9] = fivePointGoalShotsTakenAutonomous;
		return autonomousData;
	}
	
	public boolean[] getSpecialFeaturesBooleans()
	{
		boolean[] specialFeatures = new boolean[3];
		specialFeatures[0] = defense;
		specialFeatures[1] = climbAssist;
		specialFeatures[2] = humanPlayer;
		return specialFeatures;
	}
	
	public int[] getSpecialFeaturesInts()
	{
		int[] specialFeatures = new int[4];
		specialFeatures[0] = defenseRank;
		specialFeatures[1] = humanPlayerPenalties;
		specialFeatures[2] = humanPlayerShotsMade;
		specialFeatures[3] = humanPlayerShotsTaken;
		return specialFeatures;
	}
}
