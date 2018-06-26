package reschu.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import reschu.constants.MyGame;
import reschu.game.controller.Reschu;

public class SuggestionSystem {
	private Game _game;
	private static List<Point> _pointlist = new ArrayList<Point>();
	private static List<ArrayList<Point>> _arealist = new ArrayList<ArrayList<Point>>();
	private static int[] suggestedPoint = new int[2];
	
	// the suggestion / decision support system will be enabled
	// only in those scenarios with Guidance
	public SuggestionSystem(Game game) throws FileNotFoundException {
		_game = game;
		LoadHeatMapData();
		SplitHeatMapData();
	}
	
	public class Point {
		// _x, _y are the coordinates, _z is the data amount
		private int _x, _y, _z;
		// _rate is the success rate on a specific point
		private double _rate;
		
		public Point(int X, int Y, int Z, double RATE) {
			_x = X;
			_y = Y;
			_z = Z;
			_rate = RATE;
		}
		public int getX() {return _x;}
		public int getY() {return _y;}
		public int getZ() {return _z;}
		public double getRate() {return _rate;}
		
		public void addZ(int z) {_z += z;}
		public void addRate(int z, double rate) {
			// _z and z are both positive in the data set
			_rate = (double)(_rate*_z+rate*z)/(double)(_z+z);
		}
		
		public double getDistance(int x, int y) {
			return Math.sqrt(Math.pow((_x-x),2)+Math.pow((_y-y),2));
		}
	}
	
	public void LoadHeatMapData() throws FileNotFoundException {
		String line;
		String[] lineParams;
		File heatmap = new File(MyGame.HeatMap);
		BufferedReader br = new BufferedReader(new FileReader(heatmap));
		
		// parse all lines on the heat map data file
		try {
			while((line = br.readLine()) != null) {
				lineParams = line.split(" ");
				_pointlist.add(new Point(Integer.parseInt(lineParams[0]),
						Integer.parseInt(lineParams[1]),
						Integer.parseInt(lineParams[2]),
						Double.parseDouble(lineParams[3])));
			}
		} catch (NumberFormatException e) {
			System.out.println("Illegal non-numeric values in heat map data file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Illegal non-numeric values in heat map data file");
			e.printStackTrace();
		}
	}
	
	public void SplitHeatMapData() {
		// initialize _areaList
		int interval = 49;
		int MapMax = 980;
		for(int i=0; i*interval<MapMax; i++) {
			_arealist.add(new ArrayList<Point>());
			for(int j=0; j*interval<MapMax; j++) {
				_arealist.get(i).add(new Point(i*interval+interval/2, j*interval+interval/2, 0, 0.0));
			}
		}
		// add heat map data into _areaList
		for(int i=0; i<_pointlist.size(); i++) {
			int x_index = _pointlist.get(i).getX()/interval;
			int y_index = _pointlist.get(i).getY()/interval;
			_arealist.get(x_index).get(y_index).addZ(_pointlist.get(i).getZ());
			_arealist.get(x_index).get(y_index).addRate(_pointlist.get(i).getZ(), _pointlist.get(i).getRate());
		}
		// check _arealist initialization
		/*
		for(int i=0; i<_arealist.size(); i++) {
			for(int j=0; j<_arealist.get(i).size(); j++) {
				System.out.println("_arealist["+i+"]["+j+"] "+_arealist.get(i).get(j).getX()+" "+_arealist.get(i).get(j).getY()
						+" "+_arealist.get(i).get(j).getZ()+" "+_arealist.get(i).get(j).getRate());
			}
		}
		*/
	}
	
	public static int[] getWaypointSuggestion(Game game, Vehicle v) {
		double score = -9999.9;
		double temp_score;
		for(int i=0; i<_arealist.size(); i++) {
			for(int j=0; j<_arealist.get(i).size(); j++) {
				// decision support system evaluation equation
				temp_score = -_arealist.get(i).get(j).getDistance(v.getX(), v.getY())
						+ 500*_arealist.get(i).get(j).getRate();
				if(temp_score > score) {
					score = temp_score;
					suggestedPoint[0] = _arealist.get(i).get(j).getX();
					suggestedPoint[1] = _arealist.get(i).get(j).getY();
				}
			}
		}
		return suggestedPoint;
	}
}