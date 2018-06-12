package reschu.game.model;

import reschu.constants.MyGame;
import reschu.game.controller.GUI_Listener;
import reschu.game.view.PanelMap;
import reschu.game.view.PanelMsgBoard;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// import reschu.game.controller.Reschu;

public class AttackNotificationEngine {
	public static final String ATTACK_NOTIFICATIONS_FILENAME = MyGame.AttackFile;
	public Map<Integer, Integer> hackData; // Map Vehicles to the notification times
	public Map<String, Integer> timerToVehicle; // Map Timers to Vehicles
	public Map<String, Integer> delayToVehicle; // Map delay time to vehicles
	private GUI_Listener lsnr;
	private boolean hackPaneOpen = false; // track as instance variable so that new pane can close old one
	private JOptionPane hackPane;
	private JDialog optionDialog;
	private int prevIdx = -1;

	public Game game;


	public AttackNotificationEngine(GUI_Listener l, Game g) throws FileNotFoundException {
		lsnr = l;
		game = g;
		String line;
		Timer nextTimer;
		int delay;
		int vIdx;
		String action;
		
		File attackFile = new File(ATTACK_NOTIFICATIONS_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerToVehicle = new HashMap<String, Integer>();
		delayToVehicle = new HashMap<String, Integer>();
		hackData = new HashMap<Integer, Integer>();
		
		class Hack extends TimerTask {	
			String timerName;
			public Hack(String timerName) {
				this.timerName = timerName;
			}

			@Override 
			public void run() {
				int vIdx = timerToVehicle.get(timerName);
				int delay = delayToVehicle.get(timerName);
				try {
					Vehicle v = g.getVehicleList().getVehicle(vIdx);
					Boolean engage = false;
					for(int i=0; i<g.getVehicleList().size(); i++) {
						if(g.getVehicleList().getVehicle(i).getEngageStatus()) engage = true;
					}
					
					if(!v.getLostStatus()) {
						if(!(engage || (v.getPath().size()==0 && v.getTarget()!=null) || v.TargetDistance()<=MyGame.MIN_HACK_DISTANCE)) {
							System.out.println("Launching Hacking Notification for UAV["+(vIdx+1)+"]");
							launchHackWarning(vIdx);
						}
						else {
							Timer nTimer = new Timer(timerName);
							nTimer.schedule(new Hack(timerName), 5000);
							System.out.println("Reschedule Notification for UAV["+(vIdx+1)+"]");
						}
					}
				}
				catch(IllegalArgumentException e) {
					System.out.println("Hack data file has illegal hack coordinates");
				}
			}
		};

		try {
			while ((line = br.readLine()) != null){  
				if (!line.startsWith("//")){
					String[] attackParams = line.split(",");
					action = attackParams[0];
					if (!action.equals("NOTIFY")){
						continue;
					}
					vIdx = Integer.parseInt(attackParams[1]) - 1;
					delay = Integer.parseInt(attackParams[2]);
					hackData.put(vIdx, delay);
					String timerName = "HackTimer" + vIdx; // TEMP : IN FUTURE, SAME VEHICLE CAN APPEAR IN MULTIPLE LINES
					nextTimer = new Timer(timerName);
					nextTimer.schedule(new Hack(timerName), delay);
					timerToVehicle.put(timerName, vIdx);
					delayToVehicle.put(timerName, delay);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		}
	}
	
	public void launchHackWarning(int VehicleID) {
		// Launch warning 
		if (hackPane != null && hackPaneOpen){
			// Add logging event for auto-closing of window
			lsnr.EVT_Hack_Notification_Missed(prevIdx + 1);
			optionDialog.setVisible(false);
		} 
		else {
			//if (!hackPaneOpen) System.out.println("Hack pane is closed");
			//if (hackPane != null) System.out.println("Hack pane is null");
		}
		hackPaneOpen = true;
		prevIdx = VehicleID;
		lsnr.EVT_Hack_Notification_Launch(VehicleID + 1);
		int selected = displayNotification(VehicleID);
		if (selected == 0) {
			lsnr.EVT_Hack_Notification_Investigate(VehicleID + 1);
			lsnr.activateUAVFeed(VehicleID);
			lsnr.Vechicle_Selected_From_Investigate(VehicleID); 
		}
		else {
			lsnr.EVT_Hack_Notification_Ignore(VehicleID + 1);
		}
		hackPaneOpen = false;
	}
	
	public int displayNotification(int VehicleID) {
		int dispayVehicleID = VehicleID + 1;		
		Object[] options = {"Investigate", "Ignore"};
		hackPane = new JOptionPane("Possible cyber attack under UAV " + dispayVehicleID, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[1]); 
		// hackPane.setInitialValue(options[1]);
		hackPane.setVisible(true);
		optionDialog = hackPane.createDialog(hackPane.getParent(), "Attack Notification");
		optionDialog.setVisible(true);
		hackPane.selectInitialValue();
		hackPaneOpen = true;
		Object selectedValue = hackPane.getValue();
		// System.out.println("selected value: " + selectedValue);
		optionDialog.dispose();
		PanelMsgBoard.Msg("Possible cyber attack under UAV ["+dispayVehicleID+"]");

		if(selectedValue == "Investigate") {
			// the method of generating a dialog window with options
			/*
			Object[] ok_msg = {"OK"};
			JOptionPane invest_mode = new JOptionPane("Investigation mode start", JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, ok_msg, ok_msg[0]);
			invest_mode.setVisible(true);
			JDialog invest_dialog = invest_mode.createDialog(invest_mode.getParent(), "Investigation Notification");
			invest_dialog.setVisible(true);
			*/
			PanelMap.investigatedVehicle = VehicleList.getVehicle(VehicleID);
			VehicleList.getVehicle(VehicleID).setInvestigateStatus(true);
			//change param later
			game.map.setSuggestedArea(100, 40);
            game.map.setSuggestedDest(500, 500);
		}
		
		if(selectedValue == null) {
			return JOptionPane.CLOSED_OPTION;
		}
		for(int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if(options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return JOptionPane.CLOSED_OPTION;
	}
}
