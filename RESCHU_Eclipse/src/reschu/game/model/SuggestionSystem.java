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
	private static Map _map;
	private static VehicleList _vlist;
	private static List<Point> _pointlist = new ArrayList<Point>();
	private static List<ArrayList<Point>> _tilelist = new ArrayList<ArrayList<Point>>();
	private static int[] suggestedPoint = new int[2];
	private static Target suggestedTarget = new Target();
	private static int MapInterval = 49;
	private static int MapMax = 980;
	private static int MapIndexMax = MapMax/MapInterval;
	private static int MapIndexMin = 0;
	
	// the suggestion / decision support system will be enabled
	// only in those scenarios with Guidance
	public SuggestionSystem(Game game) throws FileNotFoundException {
		_game = game;
		_map = game.getMap();
		_vlist = _game.getVehicleList();
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
		public int[] getPos() {
			int[] pos = new int[2];
			pos[0] = _x;
			pos[1] = _y;
			return pos;
		}
		
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
		// initialize _tilelist
		for(int i=0; i<MapIndexMax; i++) {
			_tilelist.add(new ArrayList<Point>());
			for(int j=0; j<MapIndexMax; j++) {
				_tilelist.get(i).add(new Point(i*MapInterval+MapInterval/2, j*MapInterval+MapInterval/2, 0, 0.0));
			}
		}
		// add heat map data into _tilelist
		for(int i=0; i<_pointlist.size(); i++) {
			int[] pos = getTileIndex(_pointlist.get(i).getPos());
			_tilelist.get(pos[0]).get(pos[1]).addZ(_pointlist.get(i).getZ());
			_tilelist.get(pos[0]).get(pos[1]).addRate(_pointlist.get(i).getZ(), _pointlist.get(i).getRate());
		}
		// check _tilelist initialization
		/*
		for(int i=0; i<_tilelist.size(); i++) {
			for(int j=0; j<_tilelist.get(i).size(); j++) {
				System.out.println("_tilelist["+i+"]["+j+"] "+_tilelist.get(i).get(j).getX()+" "+_tilelist.get(i).get(j).getY()
						+" "+_tilelist.get(i).get(j).getZ()+" "+_tilelist.get(i).get(j).getRate());
			}
		}
		*/
	}
	
	public static double getPointEvaluation(Point point, Vehicle v) {
		// decision support system evaluation criterion for suggested point
		int notiUAV = 0;
		for(int i=0; i<_vlist.size(); i++) {
			if(_vlist.getVehicle(i).getNotifiedStatus()) notiUAV ++;
		}
		return -(1+notiUAV)*point.getDistance(v.getX(), v.getY()) + 400*point.getRate() + 10*point.getZ();
	}
	
	public static int[] getTileIndex(int[] pos) {
		int[] tile = new int[2];
		tile[0] = pos[0]/MapInterval;
		tile[1] = pos[1]/MapInterval;
		return tile;
	}

	public static List<Point> getEightTiles(int[] index) {
		List<Point> list = new ArrayList<Point>();
		if(index[0] > MapIndexMin) {
			list.add(_tilelist.get(index[0]-1).get(index[1]));
			if(index[1] > MapIndexMin) list.add(_tilelist.get(index[0]-1).get(index[1]-1));
			if(index[1] < MapIndexMax) list.add(_tilelist.get(index[0]-1).get(index[1]+1));
		}
		if(index[0] < MapIndexMax) {
			list.add(_tilelist.get(index[0]+1).get(index[1]));
			if(index[1] > MapIndexMin) list.add(_tilelist.get(index[0]+1).get(index[1]-1));
			if(index[1] < MapIndexMax) list.add(_tilelist.get(index[0]+1).get(index[1]+1));
		}
		if(index[1] > MapIndexMin) list.add(_tilelist.get(index[0]).get(index[1]-1));
		if(index[1] < MapIndexMax) list.add(_tilelist.get(index[0]).get(index[1]+1));
		return list;
	}
	
	public static double getAreaEvaluation(Target target, Vehicle v) {
		double score = 0.0;
		int[] tPos = target.getPos();
		int[] vPos = v.getPosInt();
		List<Point> tilelist_v = getEightTiles(getTileIndex(v.getPosInt()));
		List<Point> tilelist_t = null;
		double distance = 9999.9;
		double temp_dis;
		Point point = null;

		// check the direction of UAV movement
		for(int i=0; i<tilelist_v.size(); i++) {
			temp_dis = tilelist_v.get(i).getDistance(tPos[0], tPos[1])+tilelist_v.get(i).getDistance(vPos[0], vPos[1]);
			if(temp_dis < distance) {
				distance = temp_dis;
				point = tilelist_v.get(i);
			}
		}
		
		// calculate the evaluation score for a specific direction
		if(point != null) {
			tilelist_t = getEightTiles(getTileIndex(point.getPos()));
			score += getPointEvaluation(point, v);
		}
		if(tilelist_t != null) {
			for(int i=0; i<tilelist_t.size(); i++) {
				score += getPointEvaluation(tilelist_t.get(i), v);
			}
		}
		return score;
	}
	
	public static boolean comparePrePoint(int[] pre, int[] pos) {
		// check whether pre and pos are initialized
		if((pre==null) || (pos==null)) {
			return false;
		}
		else {
			if(((pre[0]==0)&&(pre[1]==0)) || ((pos[0]==0)&&(pos[1]==0))) {
				return false;
			}
			else {
				if((pre[0]==pos[0]) && (pre[1]==pos[1])) {
					return true;
				}
				else return false;
			}
		}
	}
	
	public static int[] getWaypointSuggestion(Game game, Vehicle v) {
		double score = -9999.9;
		double temp_score;
		for(int i=0; i<_tilelist.size(); i++) {
			for(int j=0; j<_tilelist.get(i).size(); j++) {
				if(comparePrePoint(_tilelist.get(i).get(j).getPos(), _map.getPreSuggestedPoint())) continue;
				temp_score = getPointEvaluation(_tilelist.get(i).get(j), v);
				if(temp_score > score) {
					score = temp_score;
					suggestedPoint[0] = _tilelist.get(i).get(j).getX();
					suggestedPoint[1] = _tilelist.get(i).get(j).getY();
				}
			}
		}
		return suggestedPoint;
	}
	
	public static Target getTargetSuggestion(Game game, Vehicle v) {
		double score = -9999.9;
		double temp_score;
		for(int i=0; i<game.getMap().getListUnassignedTarget().size(); i++) {
			if(comparePrePoint(game.getMap().getListUnassignedTarget().get(i).getPos(),
					_map.getPreSuggestedTarget().getPos())) continue;
			temp_score = getAreaEvaluation(game.getMap().getListUnassignedTarget().get(i), v);
			if(temp_score > score) {
				score = temp_score;
				suggestedTarget = game.getMap().getListUnassignedTarget().get(i);
			}
		}
		return suggestedTarget;
	}
}