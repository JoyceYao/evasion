package evasion.prey.strategies;

import java.util.List;

import evasion.Board;
import evasion.Location;
import evasion.Move;
import evasion.Wall;
import evasion.prey.PreyMove;
import evasion.hunter.strategies.MinSpaceStrategy;;

// Go to the place as far as possible from hunter
public class MaxSpaceStrategy extends AbsPreyStrategy {

	MinSpaceStrategy hs = new MinSpaceStrategy();
	
	@Override
	public PreyMove makeAMove(Board bd) {
		
		if(b.prevHunterLocs.size() == 0){
			return getInitailPreyMove();
		}else{
			return getBestDirection(bd, b._hunter.hl, b._prey.pl);
		}
	
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
		
		int thisFromX = pl.xloc;
		int thisFromY = pl.yloc;

		Move hm = b.prevHunterLocs.get(b.prevHunterLocs.size()-1);
		
		int bestDeltaX = 0;
		int bestDeltaY = 0;
		int maxWallSpace = 0;
		
		// if the hunter going towards to the prey
		int distBefore = Math.abs(hl.xloc-pl.xloc) + Math.abs(hl.yloc-pl.yloc);
		int distAfter = Math.abs(hl.xloc+hm.deltaX-pl.xloc) + Math.abs(hl.yloc+hm.deltaY-pl.yloc);
		if(distAfter < distBefore){
			// find a direction to make largest space
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(b.testWillBeCaught(hl, pl)){ continue; }
					int thisSpace = getWallSpace(b._walls, hl, pl, hm, i, j);
					if(maxWallSpace < thisSpace){
						bestDeltaX = i;
						bestDeltaY = j;
						maxWallSpace = thisSpace;
					}
				}
			}

			m.deltaX = bestDeltaX;
			m.deltaY = bestDeltaY;
			m.fromX = thisFromX;
			m.fromY = thisFromY;
			
		} else {
			m.fromX = thisFromX;
			m.fromY = thisFromY;
		}
		
		return m;
	}


	private int getWallSpace(List<Wall> walls, Location hl, Location pl, Move hm, int deltaX, int deltaY){
		// x is approaching
		int xMeetTime = 0;
		int meetX = -1;
		int diffX = Math.abs(hl.xloc-pl.xloc);
		if(Math.abs(hl.xloc+hm.deltaX-pl.xloc-deltaX) < Math.abs(hl.xloc-pl.xloc)){
			if(hm.deltaX*deltaX < 0){
				xMeetTime = diffX/3;
				meetX = pl.xloc + deltaX*xMeetTime;
			}else if(hm.deltaX != 0){
				xMeetTime = diffX/2;
				meetX = pl.xloc;				
			}else{
				xMeetTime = diffX;
				meetX = hl.xloc;
			}
		}else{
			xMeetTime = Integer.MAX_VALUE;
		}
		
		int yMeetTime = 0;
		int meetY = -1;
		int diffY = Math.abs(hl.yloc-pl.yloc);
		if(Math.abs(hl.yloc+hm.deltaY-pl.yloc-deltaY) < Math.abs(hl.yloc-pl.yloc)){
			if(hm.deltaY*deltaY < 0){
				yMeetTime = diffY/3;
				meetY = pl.yloc + deltaY*yMeetTime;
			}else if(hm.deltaY != 0){
				yMeetTime = diffY/2;
				meetY = pl.yloc;				
			}else{
				yMeetTime = diffY;
				meetY = hl.yloc;		
			}
		}else{
			yMeetTime = Integer.MAX_VALUE;
		}
		
		if(xMeetTime == Integer.MAX_VALUE && yMeetTime == Integer.MAX_VALUE){
			return -1;
		}
		
		if(xMeetTime < yMeetTime){
			pl.xloc = pl.xloc+xMeetTime*deltaX;
			pl.yloc = pl.yloc+xMeetTime*deltaY;
			hl.xloc = hl.xloc+xMeetTime*hm.deltaX;
			hl.yloc = hl.yloc+xMeetTime*hm.deltaY;
			
			Location[] locs = hs.getMinMaxXY(walls, hl, pl);
			if(pl.xloc < locs[0].xloc){ pl.xloc = locs[0].xloc; }
			if(pl.yloc < locs[0].yloc){ pl.yloc = locs[0].yloc; }
			if(pl.xloc > locs[1].xloc){ pl.xloc = locs[1].xloc; }
			if(pl.yloc > locs[1].yloc){ pl.yloc = locs[1].yloc; }			
			return hs.getInnerWallArea(walls, hl, pl);
		}else{
			pl.xloc = pl.xloc+yMeetTime*deltaX;
			pl.yloc = pl.yloc+yMeetTime*deltaY;
			hl.xloc = hl.xloc+yMeetTime*hm.deltaX;
			hl.yloc = hl.yloc+yMeetTime*hm.deltaY;
			
			Location[] locs = hs.getMinMaxXY(walls, hl, pl);
			if(pl.xloc < locs[0].xloc){ pl.xloc = locs[0].xloc; }
			if(pl.yloc < locs[0].yloc){ pl.yloc = locs[0].yloc; }
			if(pl.xloc > locs[1].xloc){ pl.xloc = locs[1].xloc; }
			if(pl.yloc > locs[1].yloc){ pl.yloc = locs[1].yloc; }			
			return hs.getInnerWallArea(walls, hl, pl);			
		}
	}
}
