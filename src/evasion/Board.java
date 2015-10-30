package evasion;
import java.util.ArrayList;
import java.util.List;

import evasion.hunter.Hunter;
import evasion.hunter.HunterMove;
import evasion.prey.Prey;
import evasion.prey.PreyMove;

public class Board {
	public int MAX_X = 300;//0..299
	public int MAX_Y = 300;//0..299
	
	public int N;//walls cannot be built more frequently than N steps 
	public int M;//max walls
	public int SQ_CAPTURE_DIST = 16;
	
	public Hunter h;
	public Prey p;
	public List<Wall> walls;
	
	//P is initially at point (230, 200) and H at position (0,0).
	public Board() {
		h = new Hunter();
		p = new Prey();
		walls = new ArrayList<Wall>();
		h.hl.xloc = 0; h.hl.yloc = 0;
		p.pl.xloc = 230; p.pl.yloc = 200;
	}
	public void addHunterMove(HunterMove hm) {
		
	}
	
	public void addPreyMove(PreyMove pm) {
		
	}
	
	public boolean isValidHunterMove(HunterMove hm) {
		return false;
	}
	
	public boolean isValidPreyMove(PreyMove hm) {
		return false;
	}
	
	//Wall blocking straight line path
	public Wall findWallBetween(Hunter h, Prey p) {
		
		return null;
	}
	
	//@TODO
	public boolean wallExistsBetween(Location a, Location b) {
		return false;
	}
	
	public boolean didHunterCatchPrey() {
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
}
