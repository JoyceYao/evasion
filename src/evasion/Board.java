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
	
	public Hunter h;
	public Prey p;
	public List<Wall> walls;
	public List<Move> prevHunterLocs;
	
	//P is initially at point (230, 200) and H at position (0,0).
	public Board() {
		h = new Hunter();
		p = new Prey();
		walls = new ArrayList<Wall>();
		h.hl.xloc = 0; h.hl.yloc = 0;
		p.pl.xloc = PREY_INIT_X; p.pl.yloc = PREY_INIT_Y;
		prevHunterLocs = new ArrayList<Move>();
	}
	public void addHunterMove(HunterMove hm) {
		HunterMove ehm = getEffectiveHunterMove(hm);
		h.hl.xloc += ehm.deltaX;
		h.hl.yloc += ehm.deltaY;
		h.hunterDirection = CardinalDirections.getDirectionFromMove(hm);
		//build or tear walls down
		prevHunterLocs.add(ehm);
	}
	
	public void addPreyMove(PreyMove pm) {
		PreyMove epm = getEffectivePreyMove(pm);
		p.pl.xloc += epm.deltaX;
		p.pl.yloc += epm.deltaY;
		
	}
	
	//calculate move after bounces
	public HunterMove getEffectiveHunterMove (HunterMove hm) {
//		if (!isValidHunterMove(hm)) {
//			System.out.println("Invalid Hunter Move");
//			return null;
//		}
		return hm;
	}
	
	//calculate move after bounces
	public PreyMove getEffectivePreyMove(PreyMove pm) {
		Location target = new Location();
		target.xloc = pm.fromX + pm.deltaX;
		target.yloc = pm.fromY + pm.deltaY;
		PreyMove effm = new PreyMove();
		effm.fromX = pm.fromX; effm.fromY = pm.fromY;
		effm.deltaX = pm.deltaX; effm.deltaY = pm.deltaY;
		
		if (target.yloc > MAX_Y) {
			effm.deltaY = 0;
		}
		
		if (target.xloc > MAX_X) {
			effm.deltaX = 0;
		}
		Wall aw = getWallThatRunsThrough(target);
		if (aw != null) {		
			if (aw.getOrientation() == Orientation.HORIZONTAL) {
				effm.deltaY = 0;
			} else {
				effm.deltaX = 0;
			}
			return effm;
		} else {
			return effm;
		}		
	}
	
	public Wall getWallThatRunsThrough(Location a) {
		for (Wall aw : walls) {
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
		for (Wall aw : walls) {
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
	
//	public boolean isValidHunterMove(HunterMove hm) {
//		if (hm.fromX + hm.deltaX > MAX_X) {
//			
//		}
//		return false;
//	}
	
//	public boolean isValidPreyMove(PreyMove hm) {
//		return false;
//	}
	
	//Wall blocking straight line path
	public Wall findWallBetween(Hunter h, Prey p) {
		
		return null;
	}
	
	//@TODO
	public boolean wallExistsBetween(Location a, Location b) {
		return false;
	}
	
	public boolean hunterCaughtPrey() {
		double distsq = getDistanceSq(h.hl, p.pl);
		if ( (distsq < SQ_CAPTURE_DIST) && !wallExistsBetween(h.hl, p.pl) ) {
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
