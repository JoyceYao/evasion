package evasion.prey.strategies;

import java.util.Arrays;
import java.util.List;

import evasion.Board;
import evasion.CardinalDirections;
import evasion.Location;
import evasion.Move;
import evasion.Wall;
import evasion.prey.PreyMove;
import evasion.hunter.strategies.MinSpaceStrategy;;

// Go to the place as far as possible from hunter
public class FellowHunterStrategy extends AbsPreyStrategy {

	MinSpaceStrategy hs = new MinSpaceStrategy();
	
	public FellowHunterStrategy(){
		System.out.println("using FellowHunterStrategy!!!");
	}
	
	@Override
	public PreyMove makeAMove(Board bd) {
		System.out.println("makeAMove[0]");
		PreyMove pm = new PreyMove();
		
		if(bd._hunter.hunterDirection == null){
			System.out.println("makeAMove[1]");
			pm = getInitailPreyMove();
		}else{
			System.out.println("makeAMove[2]");
			pm = getBestDirection(bd, bd._hunter.hl, bd._prey.pl);
		}
		
		pm = bd.addPreyMove(pm);
		return pm;
	}
	
	private PreyMove getInitailPreyMove(){
		PreyMove pm = new PreyMove();
		pm.fromX = Board.PREY_INIT_X;
		pm.fromY = Board.PREY_INIT_Y;
		pm.deltaX = -1;
		pm.deltaY = 0;
		return pm;
	}
	
	private PreyMove getBestDirection(Board b, Location hl, Location pl){
		PreyMove m = new PreyMove();
		
		//Move hm = b.prevHunterMoves.get(b.prevHunterMoves.size()-1);
		Move hm = CardinalDirections.getMoveFromCardinalDirections(b._hunter.hl, b._hunter.hunterDirection, "PREY");
		
		System.out.println("getBestDirection[0] hm=" + hm.toString());
		
		int distBefore = Math.abs(hl.xloc-pl.xloc) + Math.abs(hl.yloc-pl.yloc);
		int distAfter = Math.abs(hl.xloc+hm.deltaX-pl.xloc) + Math.abs(hl.yloc+hm.deltaY-pl.yloc);
		
		System.out.println("getBestDirection[1] distBefore=" + distBefore);
		System.out.println("getBestDirection[2] distAfter=" + distAfter);
		
		if(distAfter <= distBefore){
			// if the hunter is approaching, also approaching the hunter 
			if(!b.wallExistsBetween(hl, pl)){
				m = getCounterDirection(b, hm, hl, pl);
				System.out.println("getBestDirection[3-1]  m="+ m.toString());
			}else{
				// if has wall, go to the position as far as possible from the hunter
				m = getLeavingDirection(b, hm, hl, pl);
				System.out.println("getBestDirection[3-2] m="+ m.toString());
			}
		} else {
			// if hunter is leaving, fellow the hunter
			m = getFellowDirection(b, hm, hl, pl);
			System.out.println("getBestDirection[3-3] m=" + m.toString());
		}
		return m;
	}
	
	/*
	private Location findSafePoint(List<Wall> walls, Location[] locs, Move hm, Location hl, Location pl){
		// = hs.getMinMaxXY(walls, hl, pl);
		int xDiff = Math.abs(hl.xloc-pl.xloc);
		int yDiff = Math.abs(hl.yloc-pl.yloc);
		int time = Math.min(xDiff, yDiff);
		
		boolean[][] zone = new boolean[9][9];
		for(boolean[] row:zone){ Arrays.fill(row, true); }
		int minX = pl.xloc-4;
		int minY = pl.yloc-4;
		
		for(int i=-4; i<=4; i++){
			for(int j=-4; j<=4; j++){
				if(pl.xloc+i < 0 ||pl.xloc+i > Board.MAX_X || pl.yloc+i < 0 || pl.xloc+i > Board.MAX_X){ 
					zone[i+4][j+4] = false; 
				}
			}
		}
		
		for(int i=0; i<8; i++){
			int hunterFutureX = hs.getFutureXLoc(hl, hm.deltaX, walls, i);
			int hunterFutureY = hs.getFutureYLoc(hl, hm.deltaY, walls, i);
			for(int j=-4; j<=4; j++){
				for(int k=-4; k<=4; k++){
					if(Math.abs(j*k) == 16){ continue; }
					int xTmp = hunterFutureX+j;
					int yTmp = hunterFutureY+k;
					zone[j+4][k+4] = false;
				}
			}
		}
		
	}*/
	
	private PreyMove getCounterDirection(Board b, Move hm, Location hl, Location pl){
		PreyMove m = new PreyMove();
		//int caughtTime = 8;
		int xDiff = Math.abs(hl.xloc-pl.xloc);
		int yDiff = Math.abs(hl.yloc-pl.yloc);
		int time = Math.min(xDiff, yDiff);
		
		// if in the hunter's way, get away from it
		int hunterFutureX = hs.getFutureXLoc(hl, hm.deltaX, b._walls, time);
		int hunterFutureY = hs.getFutureYLoc(hl, hm.deltaY, b._walls, time);
		
		int bestDeltaX = 0;
		int bestDeltaY = 0;
		
		System.out.println("getCounterDirection hunterFutureX==" + hunterFutureX);
		System.out.println("getCounterDirection hunterFutureY==" + hunterFutureY);
		
		// In danger
		if(Math.abs(pl.xloc-hunterFutureX) <= 6 && Math.abs(pl.yloc-hunterFutureY) <= 6){
			System.out.println("getCounterDirection[2]");
			if(pl.yloc < hunterFutureY){
				System.out.println("getCounterDirection[2-1]");
				if(hm.deltaY < 0){
					// if the hunter go north(deltaY < 0) and the prey are above the hunter path, go horizontally counter direction
					bestDeltaX = hm.deltaX*-1;
				}else{
					bestDeltaY = hm.deltaY*-1;
				}
			}else{
				System.out.println("getCounterDirection[2-2]");
				// if below the path
				if(hm.deltaY < 0){
					bestDeltaY = hm.deltaY*-1;	
				}else{
					bestDeltaX = hm.deltaX*-1;
				}
			}
		}else{
			// go to the counter direction of the hunter
			bestDeltaX = hm.deltaX*-1;
			bestDeltaY = hm.deltaY*-1;
			System.out.println("getCounterDirection[2-3]");
		}
		
		m.fromX = pl.xloc;
		m.fromY = pl.yloc;
		m.deltaX = bestDeltaX;
		m.deltaY = bestDeltaY;
		
		System.out.println("getCounterDirection[3-1] deltaX="+bestDeltaX);
		System.out.println("getCounterDirection[3-2] deltaY="+bestDeltaY);
		System.out.println("getCounterDirection[3-3] ");
		return m;
	}
	
	
	private PreyMove getLeavingDirection(Board b, Move hm, Location hl, Location pl){
		PreyMove m = new PreyMove();
		int xDiff = Math.abs(hl.xloc-pl.xloc);
		int yDiff = Math.abs(hl.yloc-pl.yloc);
		
		// expect the hunter's location using the shortest in X Y distance
		int time = Math.min(xDiff, yDiff);
		int hunterFutureX = hs.getFutureXLoc(hl, hm.deltaX, b._walls, time);
		int hunterFutureY = hs.getFutureYLoc(hl, hm.deltaY, b._walls, time);
		
		System.out.println("getLeavingDirection hunterFutureX==" + hunterFutureX);
		System.out.println("getLeavingDirection hunterFutureY==" + hunterFutureY);
		
		int bestDeltaX = 0;
		int bestDeltaY = 0;
		
		if(pl.yloc < hunterFutureY){
			if(hm.deltaY < 0){
				// if the hunter go north(deltaY < 0) and the prey are above the hunter path, go horizontally counter direction
				bestDeltaX = hm.deltaX*-1;
			}else{
				bestDeltaY = hm.deltaY*-1;
			}
			System.out.println("getLeavingDirection [2]");
		}else{
			// if below the path
			if(hm.deltaY < 0){
				bestDeltaY = hm.deltaY*-1;	
			}else{
				bestDeltaX = hm.deltaX*-1;
			}
			System.out.println("getLeavingDirection [3]");
		}
		
		m.fromX = pl.xloc;
		m.fromY = pl.yloc;
		m.deltaX = bestDeltaX;
		m.deltaY = bestDeltaY;
		
		System.out.println("getLeavingDirection [3-1] deltaX=" + bestDeltaX);
		System.out.println("getLeavingDirection [3-2] deltaY=" + bestDeltaY);
		System.out.println("getLeavingDirection [3]");
		return m;
		
	}
	
	private PreyMove getFellowDirection(Board b, Move hm, Location hl, Location pl){
		PreyMove m = new PreyMove();
		
		// check if the prey will have danger to fellow the hunter
		int hunterSteps = 8;
		int hunterFutureX = hs.getFutureXLoc(hl, hm.deltaX, b._walls, hunterSteps);
		int hunterFutureY = hs.getFutureYLoc(hl, hm.deltaY, b._walls, hunterSteps);
		
		int preySteps = 4;
		int preyFutureX = hs.getFutureXLoc(pl, hm.deltaX, b._walls, preySteps);
		int preyFutureY = hs.getFutureYLoc(pl, hm.deltaY, b._walls, preySteps);
		
		if(Math.abs(preyFutureX-hunterFutureX) <= 4 && Math.abs(preyFutureY-hunterFutureY) <= 4){
			return getCounterDirection(b, hm, hl, pl);
		}else{
			m.fromX = pl.xloc;
			m.fromY = pl.yloc;
			m.deltaX = hm.deltaX;
			m.deltaY = hm.deltaY;
			return m;
		}
	}

}
