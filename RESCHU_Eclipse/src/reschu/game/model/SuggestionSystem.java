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
	private Reschu _reschu;
	private Game _game;
	private List<Point> _pointlist = new ArrayList<Point>();
	
	// the suggestion / decision support system will be enabled
	// only in those scenarios with Guidance
	public SuggestionSystem(Reschu reschu, Game game) throws FileNotFoundException {
		_reschu = reschu;
		_game = game;
		LoadHeatMapData();
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
}