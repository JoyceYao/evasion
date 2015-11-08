package evasion.prey.strategies;

import java.util.List;

import evasion.Board;
import evasion.CardinalDirections;
import evasion.Location;
import evasion.Move;
import evasion.Wall;
import evasion.prey.PreyMove;
import evasion.hunter.strategies.MinSpaceStrategy;;

// Go to the place as far as possible from hunter
public class MaxSpaceStrategy extends AbsPreyStrategy {

	MinSpaceStrategy hs = new MinSpaceStrategy();
	
	public MaxSpaceStrategy(){
		System.out.println("using MaxSpaceStrategy!!!");
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
		
		int thisFromX = pl.xloc;
		int thisFromY = pl.yloc;

		//Move hm = b.prevHunterMoves.get(b.prevHunterMoves.size()-1);
		Move hm = CardinalDirections.getMoveFromCardinalDirections(b._hunter.hl, b._hunter.hunterDirection);
		
		System.out.println("getBestDirection[0] hm=" + hm.toString());
		
		int bestDeltaX = 0;
		int bestDeltaY = 0;
		int maxWallSpace = 0;
		
		// if the hunter going towards to the prey
		int distBefore = Math.abs(hl.xloc-pl.xloc) + Math.abs(hl.yloc-pl.yloc);
		int distAfter = Math.abs(hl.xloc+hm.deltaX-pl.xloc) + Math.abs(hl.yloc+hm.deltaY-pl.yloc);
		
		System.out.println("getBestDirection[1] distBefore=" + distBefore);
		System.out.println("getBestDirection[2] distAfter=" + distAfter);
		
		if(distAfter < distBefore){
			// find a direction to make largest space
			for(int i=-1; i<=1; i++){
				for(int j=-1; j<=1; j++){
					if(b.testWillBeCaught(hl, pl)){ continue; }
					int thisSpace = getWallSpace(b._walls, hl, pl, hm, i, j);
					System.out.println("getBestDirection[2-1] maxWallSpace=" + maxWallSpace);
					System.out.println("getBestDirection[2-2] thisSpace=" + thisSpace);
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
			
			System.out.println("getBestDirection[3] bestDeltaX=" + bestDeltaX);
			System.out.println("getBestDirection[4] bestDeltaY=" + bestDeltaY);

			
		} else {
			m.fromX = thisFromX;
			m.fromY = thisFromY;
		}
		
		return m;
	}


	private int getWallSpace(List<Wall> walls, Location hl, Location pl, Move hm, int deltaX, int deltaY){
		// x is approaching
		
		System.out.println("getWallSpace[0]");
		
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
		
		System.out.println("getWallSpace[1] xMeetTime=" + xMeetTime);
		System.out.println("getWallSpace[2] yMeetTime=" + yMeetTime);
		
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
