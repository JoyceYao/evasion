package evasion;
import java.util.ArrayList;
import java.util.List;

import evasion.hunter.Hunter;
import evasion.hunter.HunterMove;
import evasion.prey.Prey;
import evasion.prey.PreyMove;

public class Board {
	public static int MAX_X = 299;//0..299
	public static int MAX_Y = 299;//0..299
	
	public static int MIN_X = 0;//0
	public static int MIN_Y = 0;//0
	
	public static int PREY_INIT_X = 230;
	public static int PREY_INIT_Y = 200;
	
	public int N = 100;//walls cannot be built more frequently than N steps 
	public int M = 5;//max walls
	public int SQ_CAPTURE_DIST = 16;
	
	public Hunter _hunter;
	public Prey _prey;
	public List<Wall> _walls;
	public List<Move> prevHunterMoves;
	static String hunterStr = "HUNTER";
	static String preyStr = "PREY";
	
	
	public int[][] wallNo = new int[300][300];
	
	public int time = 0;
	
	//P is initially at point (230, 200) and H at position (0,0).
	public Board() {
		_hunter = new Hunter();
		_prey = new Prey();
		_walls = new ArrayList<Wall>();
		_hunter.hl.xloc = 0; _hunter.hl.yloc = 0;
		_prey.pl.xloc = PREY_INIT_X; _prey.pl.yloc = PREY_INIT_Y;
		prevHunterMoves = new ArrayList<Move>();
	}
	public HunterMove addHunterMove(HunterMove hm) {
		System.out.println("addHunterMove[0]");
		HunterMove ehm = getEffectiveHunterMove(hm);
		System.out.println("addHunterMove[1] ehm.deltaX="+ehm.deltaX + " ehm.deltaY=" + ehm.deltaY);
		_hunter.hl.xloc += ehm.deltaX;
		_hunter.hl.yloc += ehm.deltaY;
		System.out.println("addHunterMove[2]");
		CardinalDirections origDir = CardinalDirections.getDirectionFromMove(hm);
		CardinalDirections newDir = CardinalDirections.getDirectionFromMove(ehm);
		if (origDir == newDir) {
			_hunter.hunterDirection = origDir;
		} else {
			if ( (ehm.deltaX == 0) && (ehm.deltaY == 0) ) {//reflected straight back from a corner or vertical/horizontal wall
				_hunter.hunterDirection = CardinalDirections.getReverseDir(origDir);
			} else {
				_hunter.hunterDirection = CardinalDirections.getReflectedDirFromOriginalDirAndFinalDelta(origDir, ehm.deltaX, ehm.deltaY);
			}
		}
		
		//build or tear walls down
		System.out.println("addHunterMove[3]");
		if(hm.buildWall != null){ _walls.add(hm.buildWall); }
		System.out.println("addHunterMove[3-1]");
		System.out.println("addHunterMove[3-2] hm.teardownWalls=" + hm.teardownWalls);
		if(hm.teardownWalls != null){
			for (Wall aw:hm.teardownWalls) {
				System.out.println("addHunterMove[4]");
				removeWall(aw.wallIndex);
			}
		}
	
		System.out.println("addHunterMove[5] ehm.deltaX=" + ehm.deltaX + " ehm.deltaY=" + ehm.deltaY);
		prevHunterMoves.add(ehm);
		System.out.println("addHunterMove[6]");
		return ehm;
	}
	
	public void removeWall(int idx) {
		int i = 0;
		for(Wall w:_walls) {
			if (w.wallIndex == idx) {
				_walls.remove(i);
				return;
			}
			i++;
		}
	}
	
	public void addPreyMove(PreyMove pm) {
		PreyMove epm = getEffectivePreyMove(pm);
		_prey.pl.xloc += epm.deltaX;
		_prey.pl.yloc += epm.deltaY;
		CardinalDirections origDir = CardinalDirections.getDirectionFromMove(pm);
		CardinalDirections newDir = CardinalDirections.getDirectionFromMove(epm);
		if (origDir == newDir) {
			_prey.preyDirection = origDir;
		} else {
			if ( (epm.deltaX == 0) && (epm.deltaY == 0) ) {//reflected straight back from a corner or vertical/horizontal wall
				_prey.preyDirection = CardinalDirections.getReverseDir(origDir);
			} else {
				_prey.preyDirection = CardinalDirections.getReflectedDirFromOriginalDirAndFinalDelta(origDir, epm.deltaX, epm.deltaY);
			}
		}
		
	}
	
	//calculate move after bounces
	public HunterMove getEffectiveHunterMove (HunterMove hm) {
		System.out.println("getEffectiveHunterMove[0]");
		//return (HunterMove) getEffectiveMove(hm);
		HunterMove m = (HunterMove)getEffectiveMove(hm, Board.hunterStr);
		System.out.println("getEffectiveHunterMove[1] m.deltaX=" + m.deltaX + " m.deltaY=" + m.deltaY);
		return m;
	}
	
	//calculate move after bounces
	public PreyMove getEffectivePreyMove(PreyMove pm) {
		return (PreyMove) getEffectiveMove(pm, Board.preyStr);
	}
	
	public Move getEffectiveMove(Move mv, String s) {
		System.out.println("getEffectiveMove[0] mv.deltaX=" + mv.deltaX + " mv.deltaY=" + mv.deltaY);
		
		Location target = new Location();
		System.out.println("getEffectiveMove[0-1-1]");
		
		System.out.println("getEffectiveMove[0-1] mv.fromX=" + mv.fromX + " mv.fromY=" + mv.fromY);
		
		target.xloc = mv.fromX + mv.deltaX;
		target.yloc = mv.fromY + mv.deltaY;
		
		System.out.println("getEffectiveMove[0-1] target.xloc=" + target.xloc + " target.yloc=" + target.yloc);
		Move effm = null;
		if(s == "HUNTER"){ effm = new HunterMove(); }
		else{ effm = new PreyMove(); }
		effm.fromX = mv.fromX; effm.fromY = mv.fromY;
		effm.deltaX = mv.deltaX; effm.deltaY = mv.deltaY;
		
		System.out.println("getEffectiveMove[0-1] effm.deltaX=" + effm.deltaX + " effm.deltaY=" + effm.deltaY);
		
		System.out.println("getEffectiveMove[1]");
		if (target.yloc > MAX_Y) {
			effm.deltaY = 0;
		}
		if (target.yloc < MIN_Y) {
			effm.deltaY = 0;
		}
		
		if (target.xloc > MAX_X) {
			effm.deltaX = 0;
		}
		if (target.xloc < MIN_X) {
			effm.deltaX = 0;
		}
		
		System.out.println("getEffectiveMove[2] target=" + target.xloc + " " + target.yloc);
		System.out.println("getEffectiveMove[2-2] effm=" + effm.deltaX + " " + effm.deltaY);
		Wall aw = getWallThatRunsThrough(target);
		if (aw != null) {		
			if (aw.getOrientation() == Orientation.HORIZONTAL) {
				effm.deltaY = 0;
			} else {
				effm.deltaX = 0;
			}
			
			System.out.println("getEffectiveMove[3] effm.deltaY=" + effm.deltaY + " effm.deltaX=" + effm.deltaX);
			return getEffectiveMove(effm, s);
		} else {
			System.out.println("getEffectiveMove[4]");
			System.out.println("getEffectiveMove[4-1] effm=" + effm.deltaX + " " + effm.deltaY);
			return effm;
		}
	}
	

	public Wall getWallThatRunsThrough(Location a) {
		System.out.println("getWallThatRunsThrough[0] _walls.size()==" + _walls.size());
		for(int i=0; i<_walls.size(); i++){
			System.out.println("getWallThatRunsThrough[0-1] aw==" + _walls.get(i));
		}
		
		
		for (Wall aw : _walls) {
			System.out.println("getWallThatRunsThrough[0] aw==" + aw);
			System.out.println("getWallThatRunsThrough[1-1] aw start xloc=" + aw.leftEnd.xloc + " yloc=" + aw.leftEnd.yloc);
			System.out.println("getWallThatRunsThrough[1-2] aw end xloc=" + aw.rightEnd.xloc + " yloc=" + aw.rightEnd.yloc);			
			System.out.println("getWallThatRunsThrough[1-3] wallIdx=" + aw.wallIndex);
			if (aw.getOrientation() == Orientation.VERTICAL) {
				if (aw.leftEnd.xloc == a.xloc) {
					if ( (aw.leftEnd.yloc <= a.yloc) && (a.yloc <= aw.rightEnd.yloc) ) {
						return aw;
					}
				}				
			} else {//horizontal wall
				System.out.println("getWallThatRunsThrough[2]");
				if (aw.leftEnd.yloc == a.yloc) {
					if ( (aw.leftEnd.xloc <= a.xloc) && (a.xloc <= aw.rightEnd.xloc) ) {
						return aw;
					}
				}
			}
		}
		System.out.println("getWallThatRunsThrough[3]");
		return null;
	}
	
	public boolean aWallRunsThrough(Location a) {
		for (Wall aw : _walls) {
			if (aw.getOrientation() == Orientation.VERTICAL) {
				if (aw.leftEnd.xloc == a.xloc) {
					if ( (aw.leftEnd.yloc <= a.yloc) && (a.yloc <= aw.rightEnd.yloc) ) {
						return true;
					}
				}				
			} else {//horizontal wall
				if (aw.leftEnd.yloc == a.yloc) {
					if ( (aw.leftEnd.xloc <= a.xloc) && (a.xloc <= aw.rightEnd.xloc) ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	//Wall blocking straight line path from hunter to prey - a coarse estimate does not evaluate slanted lines connecting
	//Hunter and Prey
	public Wall findWallBetween(Hunter h, Prey p) {
		for (Wall aw : _walls) {
			if (aw.getOrientation() == Orientation.HORIZONTAL) {
				if ( (h.hl.yloc < aw.leftEnd.yloc) && (aw.leftEnd.yloc < p.pl.yloc) ) {
					if ( (aw.rightEnd.xloc >= p.pl.xloc) && (aw.rightEnd.xloc >= h.hl.xloc) 
						&& (aw.leftEnd.xloc <= p.pl.xloc) && (aw.leftEnd.xloc <= h.hl.xloc) ) {
						return aw;
					}
				}
				if ( (p.pl.yloc < aw.leftEnd.yloc) && (aw.leftEnd.yloc < h.hl.yloc) ) {
					if ( (aw.rightEnd.xloc >= p.pl.xloc) && (aw.rightEnd.xloc >= h.hl.xloc) 
						&& (aw.leftEnd.xloc <= p.pl.xloc) && (aw.leftEnd.xloc <= h.hl.xloc) ) {
						return aw;
					}
				}				
			}
			if (aw.getOrientation() == Orientation.VERTICAL) {
				if ( (h.hl.xloc < aw.leftEnd.xloc) && (aw.leftEnd.xloc < p.pl.xloc) ) {
					if ( (aw.rightEnd.yloc >= p.pl.yloc) && (aw.rightEnd.yloc >= h.hl.yloc) 
						&& (aw.leftEnd.yloc <= p.pl.yloc) && (aw.leftEnd.yloc <= h.hl.yloc) ) {
						return aw;
					}
				}
				if ( (p.pl.xloc < aw.leftEnd.xloc) && (aw.leftEnd.xloc < h.hl.xloc) ) {
					if ( (aw.rightEnd.yloc >= p.pl.yloc) && (aw.rightEnd.yloc >= h.hl.yloc) 
						&& (aw.leftEnd.yloc <= p.pl.yloc) && (aw.leftEnd.yloc <= h.hl.yloc) ) {
						return aw;
					}
				}
			}
		}
		return null;
	}
	
	//@TODO
	public boolean wallExistsBetween(Location a, Location b) {
		for (Wall aw : _walls) {
			if (aw.getOrientation() == Orientation.HORIZONTAL) {
				if ( (a.yloc < aw.leftEnd.yloc) && (aw.leftEnd.yloc < b.yloc) ) {
					if ( (aw.rightEnd.xloc >= a.xloc) && (aw.rightEnd.xloc >= b.xloc) 
						&& (aw.leftEnd.xloc <= a.xloc) && (aw.leftEnd.xloc <= b.xloc) ) {
						return true;
					}
				}
				if ( (b.yloc < aw.leftEnd.yloc) && (aw.leftEnd.yloc < a.yloc) ) {
					if ( (aw.rightEnd.xloc >= a.xloc) && (aw.rightEnd.xloc >= b.xloc) 
						&& (aw.leftEnd.xloc <= a.xloc) && (aw.leftEnd.xloc <= b.xloc) ) {
						return true;
					}
				}				
			}
			if (aw.getOrientation() == Orientation.VERTICAL) {
				if ( (a.xloc < aw.leftEnd.xloc) && (aw.leftEnd.xloc < b.xloc) ) {
					if ( (aw.rightEnd.yloc >= a.yloc) && (aw.rightEnd.yloc >= b.yloc) 
						&& (aw.leftEnd.yloc <= a.yloc) && (aw.leftEnd.yloc <= b.yloc) ) {
						return true;
					}
				}
				if ( (b.xloc < aw.leftEnd.xloc) && (aw.leftEnd.xloc < a.xloc) ) {
					if ( (aw.rightEnd.yloc >= a.yloc) && (aw.rightEnd.yloc >= b.yloc) 
						&& (aw.leftEnd.yloc <= a.yloc) && (aw.leftEnd.yloc <= b.yloc) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	{
		"hunter":[10,10],
		"hunterDir":"SE",
		"prey":[231,196],
		"walls":[{"position":[1,0],"length":300,"direction":"S","id":0}],
		"time":10,
		"gameover":false,
		"errors":[]
		}
	*/
/**
 * {
"hunter":[7,7],
"hunterDir":"SE",
"prey":[231,197],
"walls":[{"position":[1,0],"length":300,"direction":"S","id":0}],
"time":7,
"gameover":false,
"errors":[{"message":"Walls  do not exist","code":6,"reason":"These wall ids do not exist.","data":{"command":"D","wallIds":[1]}}]
}
 * @param message
 */
	public void parseMessageAndUpdate(String message) {
		System.out.println("Parsing message" + message);
		
	}
	
	public boolean hunterCaughtPrey() {
		double distsq = getDistanceSq(_hunter.hl, _prey.pl);
		if ( (distsq < SQ_CAPTURE_DIST) && !wallExistsBetween(_hunter.hl, _prey.pl) ) {
			return true;
		}
		return false;
	}
	
	public static double getDistanceSq(Location a, Location b) {
		return ((a.xloc - b.xloc)*(a.xloc - b.xloc) + 
				(a.yloc - b.yloc)*(a.yloc - b.yloc));
	}
	
	public boolean testWillBeCaught(Location a, Location b) {
		double distsq = getDistanceSq(a, b);
		if ( (distsq < SQ_CAPTURE_DIST) && !wallExistsBetween(a, b) ) {
			return true;
		}
		return false;
	}
	
	public void printWalls(){
		for(int i=0; i<_walls.size(); i++){
			System.out.println(_walls.get(i).wallIndex);
		}
	}
	
	
	public int hasOverlapWall(Wall wall){
		System.out.println("Board hasOverlapWall[0] _walls.size()=" + _walls.size());
		for(int i=0; i<_walls.size(); i++){
			System.out.println("Board hasOverlapWall[1]");
			if(wall.leftEnd.xloc == _walls.get(i).leftEnd.xloc &&
					wall.leftEnd.xloc == wall.rightEnd.xloc &&
					_walls.get(i).leftEnd.xloc == _walls.get(i).rightEnd.xloc &&
					(wall.leftEnd.yloc >= _walls.get(i).leftEnd.yloc ||
					wall.rightEnd.yloc <= _walls.get(i).rightEnd.yloc)){
				System.out.println("Board hasOverlapWall[2-1-1] " + _walls.get(i).wallIndex + " startX=" + _walls.get(i).leftEnd.xloc + " startY=" + _walls.get(i).leftEnd.yloc);
				System.out.println("Board hasOverlapWall[2-1-2] " + _walls.get(i).wallIndex + " endX=" + _walls.get(i).rightEnd.xloc + " endY=" + _walls.get(i).rightEnd.yloc);
				System.out.println("Board hasOverlapWall[2-1]");
				return i;
			}
			
			if(wall.leftEnd.yloc == _walls.get(i).leftEnd.yloc &&
					wall.leftEnd.yloc == wall.rightEnd.yloc &&
					_walls.get(i).leftEnd.yloc == _walls.get(i).rightEnd.yloc &&
					(wall.leftEnd.xloc >= _walls.get(i).leftEnd.xloc ||
					wall.rightEnd.xloc <= _walls.get(i).rightEnd.xloc)){
				System.out.println("Board hasOverlapWall[2-2-1] " + _walls.get(i).wallIndex + " startX=" + _walls.get(i).leftEnd.xloc + " startY=" + _walls.get(i).leftEnd.yloc);
				System.out.println("Board hasOverlapWall[2-2-2] " + _walls.get(i).wallIndex + " endX=" + _walls.get(i).rightEnd.xloc + " endY=" + _walls.get(i).rightEnd.yloc);
				System.out.println("Board hasOverlapWall[2-2]");
				return i;
			}
		}
		System.out.println("Board hasOverlapWall[4]");
		return -1;
	}
}
