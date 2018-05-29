package reschu.constants;

public class MySpeed {
    final static public int VELOCITY = 1;
    final static public double VELOCITY64 = 1.0/20.0; // 20.0
	final static public int SPEED_CONTROL = 20; // a coefficient for controlling time step, changed to 20 because SIZE_CELL changed to 1
    final static public int SPEED_TIMER = 500 / SPEED_CONTROL;
    final static public int SPEED_CLOCK = 20000 / SPEED_CONTROL; // one SPEED_CLOCK = 1 second
    final static public int SPEED_CLOCK_DAMAGE_CHECK = SPEED_CLOCK * 3;
    final static public int SPEED_CLOCK_HAZARD_AREA_UPDATE = SPEED_CLOCK * 10;
    final static public int SPEED_CLOCK_HAZARD_AREA_UPDATE_TUTORIAL = SPEED_CLOCK * 80;
    final static public int SPEED_CLOCK_TARGET_AREA_UPDATE = SPEED_CLOCK * 5;
    final static public int SPEED_CLOCK_AUTO_TARGET_ASSIGN_UPDATE = SPEED_CLOCK * 15;
}