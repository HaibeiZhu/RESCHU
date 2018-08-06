package reschu.constants;

import reschu.game.controller.Reschu;

public class MyGame {
	final static public String VERSION_INFO = "RESCHU VER 3.0.0";	
    
    final static public int LAND = 0;
    final static public int SEASHORE = 1;
    final static public int SEA = 2;
	
	final static public int STATUS_VEHICLE_STASIS   = 0;
	final static public int STATUS_VEHICLE_MOVING   = 1;
	final static public int STATUS_VEHICLE_PENDING  = 2;
	final static public int STATUS_VEHICLE_PAYLOAD  = 3;
	final static public int STATUS_VEHICLE_ATTACKED = 4;
	final static public int STATUS_VEHICLE_LOST 	= 5;
 
    final static public int nHAZARD_AREA = 21; // original 21
    final static public int nHAZARD_AREA_TUTORIAL = 3;
    
    final static public int nTARGET_AREA_LAND = 2; // original 8
    final static public int nTARGET_AREA_LAND_TUTORIAL = 7;
    final static public int nTARGET_AREA_SHORE = 4;
    final static public int nTARGET_AREA_COMM = 2;
    final static public int nTARGET_AREA_MORE = 3;
    final static public int nTARGET_AREA_TOTAL = nTARGET_AREA_LAND + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    final static public int nTARGET_AREA_TOTAL_TUTORIAL = nTARGET_AREA_LAND_TUTORIAL + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    final static public int nTARGET_AREA_TOTAL_HIGH = nTARGET_AREA_LAND + nTARGET_AREA_SHORE + nTARGET_AREA_MORE;
    
    final static public double MIN_HACK_DISTANCE = 30.0;
    final static public String AttackFile = (Reschu.experiment_mode())? ((Reschu.if_section_1())? "AttackFile_Test_1.txt" : "AttackFile_Test_2.txt") :
    																	((Reschu.practice_general_mode())? "AttackFile_Practice_General.txt" :
    																									   (Reschu.practice_strategy_mode())? "AttackFile_Practice_Strategy.txt" :
    																										   								  "AttackFile_Practice_Suggestion.txt");
    
    final static public String HeatMap = "HeatMapData.txt";
    
    final static public boolean TargetDataBase = true;
    final static public int TOTAL_SECOND = (Reschu.practice_extra_mode())? 180 : 900; // total time (in second) for one experiment
}