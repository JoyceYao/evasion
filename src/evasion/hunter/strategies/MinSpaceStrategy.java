package evasion.hunter.strategies;

import java.util.*;

import evasion.Board;
import evasion.Move;
import evasion.Location;
import evasion.Wall;
import evasion.WallOperation;
import evasion.hunter.HunterMove;
import evasion.prey.Prey;

public class MinSpaceStrategy extends AbsHunterStrategy {

	List<HunterMove> hunterMoveHist = new ArrayList<HunterMove>();
	int wallIdx = 0;
	List<Integer> innerWall = new ArrayList<Integer>();
	int prevDeltaX = 1;
	int prevDeltaY = 1;

	@Override
	public HunterMove makeAMove(Board b) {
		System.out.println("MinSpaceStrategy makeAMove[0]");
		HunterMove hm = null;
		if(hunterMoveHist.size() == 0){
			hm = getInitialMove();
			hunterMoveHist.add(hm);
			return hm;
		} else {
			System.out.println("MinSpaceStrategy makeAMove[1]");
			hm = new HunterMove();
			HunterMove prevMove = hunterMoveHist.get(hunterMoveHist.size()-1);

			//int nextX = b._hunter.hl.xloc + prevMove.deltaX;
			//int nextY = b._hunter.hl.yloc + prevMove.deltaY;
			hm.fromX = b._hunter.hl.xloc;
			hm.fromY = b._hunter.hl.yloc;
			hm.deltaX = prevDeltaX;
			hm.deltaY = prevDeltaY;
			
			//if(hunterMoveHist.size() > 2){
				//HunterMove prevpreMove = hunterMoveHist.get(hunterMoveHist.size()-2);
				//if(prevpreMove.deltaX == 0 || prevpreMove.deltaY == 0){
				//	prevpreMove = hunterMoveHist.get(hunterMoveHist.size()-3);
				//}
			
			//}
			System.out.println("MinSpaceStrategy makeAMove[2] hm.deltaX=" + hm.deltaX + " hm.deltaY=" + hm.deltaY);
			//if (b.wallExistsBetween(b._hunter.hl, new Location(nextX, nextY))){
			Wall newWall = getWall(b, prevMove, b._prey);
			
			System.out.println("MinSpaceStrategy makeAMove[2-0] newWall=" + newWall);
			
			if(newWall != null){
				
				int overlapWallIdx = b.hasOverlapWall(newWall);
				System.out.println("MinSpaceStrategy makeAMove[2-1] newWall=" + newWall.wallIndex + " startX=" + newWall.leftEnd.xloc + " startY=" + newWall.leftEnd.yloc);
				System.out.println("MinSpaceStrategy makeAMove[2-2] newWall=" + newWall.wallIndex + " endX=" + newWall.rightEnd.xloc + " endY=" + newWall.rightEnd.yloc);
				System.out.println("MinSpaceStrategy makeAMove[2-3] overlapWallIdx=" + overlapWallIdx);
				
				if(overlapWallIdx == -1){
					System.out.println("MinSpaceStrategy makeAMove[3]");
					hm.buildWall = newWall;
				}else{
					System.out.println("MinSpaceStrategy makeAMove[4]");
					Wall otherWall = b._walls.get(overlapWallIdx);
					System.out.println("MinSpaceStrategy makeAMove[5] otherWall=" + otherWall);
					if(newWall.leftEnd.xloc <= otherWall.leftEnd.xloc && newWall.rightEnd.xloc >= otherWall.rightEnd.xloc
							&& newWall.leftEnd.yloc <= otherWall.leftEnd.yloc && newWall.rightEnd.yloc >= otherWall.rightEnd.yloc){
						System.out.println("MinSpaceStrategy makeAMove[5-1] otherWall=" + otherWall);
						hm.buildWall = newWall;
						System.out.println("MinSpaceStrategy makeAMove[5-2] otherWall=" + otherWall);
						if(hm.teardownWalls == null){
							hm.teardownWalls = new ArrayList<Wall>();
							hm.teardownWalls.add(otherWall);
						}
					}
				}
				
			}	
		
			System.out.println("MinSpaceStrategy makeAMove[6-0]");
			
		}
		
		System.out.println("MinSpaceStrategy makeAMove[6-1]");
		// remove extra walls
		
		
		//System.out.println("MinSpaceStrategy makeAMove[3-2-1] b._walls.get(0)="+b._walls.get(0));
		if(b._walls.size() > 0 && b._walls.size() == b.M){
			if(hm.teardownWalls == null){
				hm.teardownWalls = new ArrayList<Wall>();
			}
			hm.teardownWalls.add(b._walls.get(0));
		}
		
		System.out.println("MinSpaceStrategy makeAMove[7]");
		HunterMove m = b.addHunterMove(hm);
		System.out.println("MinSpaceStrategy makeAMove[8]");
		hunterMoveHist.add(m);
		System.out.println("MinSpaceStrategy makeAMove[9]");
		System.out.println(m.moveToString());
		System.out.println("MinSpaceStrategy makeAMove[10]");
		
		if(m.deltaX == 0){
			prevDeltaX *= -1;
		}
		if(m.deltaY == 0){
			prevDeltaY *= -1;
		}
		
		System.out.println("MinSpaceStrategy makeAMove[11] prevDeltaX=" + prevDeltaX + " prevDeltaY=" + prevDeltaY);

		//if(hm.teardownWalls == null){ hm.teardownWalls = new ArrayList<Wall>(); }
		return m;
	}
	
	private HunterMove getInitialMove(){
		System.out.println("MinSpaceStrategy getInitialMove");
		HunterMove hm = new HunterMove();
		hm.buildWall = null;
		hm.teardownWalls = null;
		hm.deltaX = 1;
		hm.deltaY = 1;
		return hm;
	}
	
	
	private Wall getWall(Board b, HunterMove prevHM,  Prey p){
		System.out.println("MinSpaceStrategy getWall[0]");
		if(Math.abs(prevHM.fromX-p.pl.xloc) < 2 || Math.abs(prevHM.fromY-p.pl.yloc) < 2){
			//get smallest enclosing wall
			
			System.out.println("MinSpaceStrategy getWall[1] prevHM.fromX=" + prevHM.fromX);
			System.out.println("MinSpaceStrategy getWall[1] p.pl.xloc=" + p.pl.xloc);
			System.out.println("MinSpaceStrategy getWall[1] prevHM.fromY=" + prevHM.fromY);
			System.out.println("MinSpaceStrategy getWall[1] p.pl.yloc=" + p.pl.yloc);
			return getSmallestEnclosingWall(b._walls, new Location(prevHM.fromX, prevHM.fromY), new Location(prevHM.fromX+prevHM.deltaX, prevHM.fromY+prevHM.deltaY), p.pl);
		} else {
			System.out.println("MinSpaceStrategy getWall[2]");
			return null;
		}
	}
	
	private Wall getSmallestEnclosingWall(List<Wall> walls, Location hl, Location hl2, Location pl){
		// get enclosing space before building new wall
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0]");
		
		int WWallIdx = getWWallIdx(walls, Math.max(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(walls, Math.min(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(walls, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(walls, Math.max(hl.yloc, pl.yloc));
		
		int minX = EWallIdx == -1 ? Board.MIN_X : walls.get(EWallIdx).leftEnd.xloc;
		int maxX = WWallIdx == -1 ? Board.MAX_X : walls.get(WWallIdx).leftEnd.xloc;
		int minY = NWallIdx == -1 ? Board.MIN_Y : walls.get(NWallIdx).leftEnd.yloc;
		int maxY = SWallIdx == -1 ? Board.MAX_Y : walls.get(SWallIdx).leftEnd.yloc;
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] minX=" + minX);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] maxX=" + maxX);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] minY=" + minY);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] maxY=" + maxY);
		
		if(maxX - minX <= 2 || maxY- minY <= 2){ return null; }
		
		int area = (maxX-minX)*(maxY-minY);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] area=" + area);
		
		Location hwstart = new Location(minX, hl.yloc);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[1-1]");
		Location hwend = new Location(maxX, hl.yloc);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[1-2]");
		Wall hWall = new Wall(hwstart, hwend, WallOperation.BUILD, wallIdx);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2]");
		
		int hWallArea = 0;
		if(hl2.yloc < hl.yloc){ //S wall
			hWallArea = (maxX-minX)*(hl.yloc-minY);
		}else{ // N wall
			hWallArea = (maxX-minX)*(maxY-hl.yloc);
		}
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[3]");
		
		Location vwstart = new Location(hl.xloc, minY);
		Location vwend = new Location(hl.xloc, maxY);
		Wall vWall = new Wall(vwstart, vwend, WallOperation.BUILD, wallIdx);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4]");
		
		int vWallArea = 0;
		if(hl2.xloc < hl.xloc){  // W wall
			vWallArea = (maxX-hl.xloc)*(maxY-minY);
		}else{ // E wall
			vWallArea = (hl.xloc-minX)*(maxY-minY);
		}
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[5]");
		
		wallIdx++;
		
		// set inner walls list
		innerWall.clear();
		if(EWallIdx != -1){ innerWall.add(walls.get(EWallIdx).wallIndex); }
		if(WWallIdx != -1){ innerWall.add(walls.get(WWallIdx).wallIndex); }
		if(NWallIdx != -1){ innerWall.add(walls.get(NWallIdx).wallIndex); }
		if(SWallIdx != -1){ innerWall.add(walls.get(SWallIdx).wallIndex); }
		Wall result = hWallArea < vWallArea ? hWall : vWall;
		//walls.add(result);
		innerWall.add(result.wallIndex);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[6]");
		return result;
	}
	
	public int getInnerWallArea(List<Wall> walls, Location hl, Location pl){
		// get enclosing space before building new wall
		int WWallIdx = getWWallIdx(walls, Math.max(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(walls, Math.min(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(walls, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(walls, Math.max(hl.yloc, pl.yloc));
		
		int minX = EWallIdx == -1 ? Board.MIN_X : walls.get(EWallIdx).leftEnd.xloc;
		int maxX = WWallIdx == -1 ? Board.MAX_X : walls.get(WWallIdx).leftEnd.xloc;
		int minY = NWallIdx == -1 ? Board.MIN_Y : walls.get(NWallIdx).leftEnd.yloc;
		int maxY = SWallIdx == -1 ? Board.MAX_Y : walls.get(SWallIdx).leftEnd.yloc;
		
		int area = (maxX-minX)*(maxY-minY);
		return area;
	}
	
	private int getWWallIdx(List<Wall> walls, int x){
		int currIdx = -1;
		for(int i=0; i<walls.size(); i++){
			Wall w = walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isVerticalWall(w) && w.leftEnd.xloc > x 
					&& (currIdx == -1 || walls.get(currIdx).leftEnd.xloc > w.leftEnd.xloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}
	
	private int getEWallIdx(List<Wall> walls, int x){
		int currIdx = -1;
		for(int i=0; i<walls.size(); i++){
			Wall w = walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isVerticalWall(w) && w.leftEnd.xloc < x 
					&& (currIdx == -1 || walls.get(currIdx).leftEnd.xloc < w.leftEnd.xloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}
	
	private int getNWallIdx(List<Wall> walls, int y){
		int currIdx = -1;
		for(int i=0; i<walls.size(); i++){
			Wall w = walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isHorizontalWall(w) && w.leftEnd.yloc < y 
					&& (currIdx == -1 || walls.get(currIdx).leftEnd.yloc < w.leftEnd.yloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}

	private int getSWallIdx(List<Wall> walls, int y){
		int currIdx = -1;
		for(int i=0; i<walls.size(); i++){
			Wall w = walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isHorizontalWall(w) && w.leftEnd.yloc > y 
					&& (currIdx == -1 || walls.get(currIdx).leftEnd.yloc > w.leftEnd.yloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}
	
	public Location[] getMinMaxXY(List<Wall> walls, Location hl, Location pl){
		int WWallIdx = getWWallIdx(walls, Math.max(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(walls, Math.min(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(walls, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(walls, Math.max(hl.yloc, pl.yloc));
		
		int minX = EWallIdx == -1 ? Board.MIN_X : walls.get(EWallIdx).leftEnd.xloc;
		int maxX = WWallIdx == -1 ? Board.MAX_X : walls.get(WWallIdx).leftEnd.xloc;
		int minY = NWallIdx == -1 ? Board.MIN_Y : walls.get(NWallIdx).leftEnd.yloc;
		int maxY = SWallIdx == -1 ? Board.MAX_Y : walls.get(SWallIdx).leftEnd.yloc;
		
		Location[] result = new Location[2];
		result[0] = new Location(minX, minY);
		result[1] = new Location(maxX, maxY);
		return result;
	}
	
	private boolean isVerticalWall(Wall w){
		if(w.leftEnd.xloc == w.rightEnd.xloc){
			return true;
		}
		return false;
	}
	
	private boolean isHorizontalWall(Wall w){
		if(w.leftEnd.yloc == w.rightEnd.yloc){
			return true;
		}
		return false;
	}
}
