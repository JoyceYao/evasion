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
	
	public int N;//walls cannot be built more frequently than N steps 
	public int M;//max walls
	public int SQ_CAPTURE_DIST = 16;
	
	public Hunter _hunter;
	public Prey _prey;
	public List<Wall> _walls;
	public List<Move> prevHunterLocs;
	static String hunterStr = "HUNTER";
	static String preyStr = "PREY";
	
	//P is initially at point (230, 200) and H at position (0,0).
	public Board() {
		_hunter = new Hunter();
		_prey = new Prey();
		_walls = new ArrayList<Wall>();
		_hunter.hl.xloc = 0; _hunter.hl.yloc = 0;
		_prey.pl.xloc = PREY_INIT_X; _prey.pl.yloc = PREY_INIT_Y;
		prevHunterLocs = new ArrayList<Move>();
	}
	public void addHunterMove(HunterMove hm) {
		System.out.println("addHunterMove[0]");
		HunterMove ehm = getEffectiveHunterMove(hm);
		System.out.println("addHunterMove[1]");
		_hunter.hl.xloc += ehm.deltaX;
		_hunter.hl.yloc += ehm.deltaY;
		System.out.println("addHunterMove[2]");
		_hunter.hunterDirection = CardinalDirections.getDirectionFromMove(hm);
		//build or tear walls down
		System.out.println("addHunterMove[3]");
		prevHunterLocs.add(ehm);
	}
	
	public void addPreyMove(PreyMove pm) {
		PreyMove epm = getEffectivePreyMove(pm);
		_prey.pl.xloc += epm.deltaX;
		_prey.pl.yloc += epm.deltaY;
		
	}
	
	//calculate move after bounces
	public HunterMove getEffectiveHunterMove (HunterMove hm) {
		System.out.println("getEffectiveHunterMove[0]");
		//return (HunterMove) getEffectiveMove(hm);
		HunterMove m = (HunterMove)getEffectiveMove(hm, Board.hunterStr);
		System.out.println("getEffectiveHunterMove[1]");
		return m;
	}
	
	//calculate move after bounces
	public PreyMove getEffectivePreyMove(PreyMove pm) {
		return (PreyMove) getEffectiveMove(pm, Board.preyStr);
	}
	
	public Move getEffectiveMove(Move mv, String s) {
		System.out.println("getEffectiveMove[0]");
		Location target = new Location();
		target.xloc = mv.fromX + mv.deltaX;
		target.yloc = mv.fromY + mv.deltaY;
		
		System.out.println("getEffectiveMove[0-1] target.xloc=" + target.xloc + " target.yloc=" + target.yloc);
		Move effm = null;
		if(s == "HUNTER"){ effm = new HunterMove(); }
		else{ effm = new PreyMove(); }
		effm.fromX = mv.fromX; effm.fromY = mv.fromY;
		effm.deltaX = mv.deltaX; effm.deltaY = mv.deltaY;
		
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
			return effm;
		}
	}
	

	public Wall getWallThatRunsThrough(Location a) {
		for (Wall aw : _walls) {
			if (aw.leftEnd.xloc == a.xloc) {
				return aw;
			}
			if (aw.rightEnd.xloc == a.xloc) {
				return aw;
			}
			if (aw.leftEnd.yloc == a.yloc) {
				return aw;
			}
			if (aw.rightEnd.yloc == a.yloc) {
				return aw;
			}
		}
		return null;
	}
	
	public boolean aWallRunsThrough(Location a) {
		for (Wall aw : _walls) {
			if (aw.leftEnd.xloc == a.xloc) {
				return true;
			}
			if (aw.rightEnd.xloc == a.xloc) {
				return true;
			}
			if (aw.leftEnd.yloc == a.yloc) {
				return true;
			}
			if (aw.rightEnd.yloc == a.yloc) {
				return true;
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
}
