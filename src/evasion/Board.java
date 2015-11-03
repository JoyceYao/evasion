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
	
	//P is initially at point (230, 200) and H at position (0,0).
	public Board() {
		_hunter = new Hunter();
		_prey = new Prey();
		_walls = new ArrayList<Wall>();
		_hunter.hl.xloc = 0; _hunter.hl.yloc = 0;
		_prey.pl.xloc = PREY_INIT_X; _prey.pl.yloc = PREY_INIT_Y;
	}
	public void addHunterMove(HunterMove hm) {
		HunterMove ehm = getEffectiveHunterMove(hm);
		_hunter.hl.xloc += ehm.deltaX;
		_hunter.hl.yloc += ehm.deltaY;
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
		_walls.add(hm.buildWall);
		for (Wall aw:hm.teardownWalls) {
			removeWall(aw.wallIndex);
		}
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
		return (HunterMove) getEffectiveMove(hm);
	}
	
	//calculate move after bounces
	public PreyMove getEffectivePreyMove(PreyMove pm) {
		return (PreyMove) getEffectiveMove(pm);
	}
	
	public Move getEffectiveMove(Move mv) {
		Location target = new Location();
		target.xloc = mv.fromX + mv.deltaX;
		target.yloc = mv.fromY + mv.deltaY;
		Move effm = new Move();
		effm.fromX = mv.fromX; effm.fromY = mv.fromY;
		effm.deltaX = mv.deltaX; effm.deltaY = mv.deltaY;
		
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
		Wall aw = getWallThatRunsThrough(target);
		if (aw != null) {		
			if (aw.getOrientation() == Orientation.HORIZONTAL) {
				effm.deltaY = 0;
			} else {
				effm.deltaX = 0;
			}
			return getEffectiveMove(effm);
		} else {
			return effm;
		}		
	}
	
	public Wall getWallThatRunsThrough(Location a) {
		for (Wall aw : _walls) {
			if (aw.getOrientation() == Orientation.VERTICAL) {
				if (aw.leftEnd.xloc == a.xloc) {
					if ( (aw.leftEnd.yloc <= a.yloc) && (a.yloc <= aw.rightEnd.yloc) ) {
						return aw;
					}
				}				
			} else {//horizontal wall
				if (aw.leftEnd.yloc == a.yloc) {
					if ( (aw.leftEnd.xloc <= a.xloc) && (a.xloc <= aw.rightEnd.xloc) ) {
						return aw;
					}
				}
			}
		}
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
}
