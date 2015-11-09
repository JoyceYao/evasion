package evasion.hunter.strategies;

import java.util.*;

import evasion.Board;
import evasion.CardinalDirections;
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
	int lastCreateWallTime = 0;
	int currSteps = 0;
	
	public MinSpaceStrategy(){
		System.out.println("MinSpaceStrategy");
	}

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
			hm = (HunterMove)CardinalDirections.getMoveFromCardinalDirections(b._hunter.hl, b._hunter.hunterDirection, "HUNTER");
			HunterMove prevMove = hunterMoveHist.get(hunterMoveHist.size()-1);
			
			System.out.println("MinSpaceStrategy makeAMove[2] hm.deltaX=" + hm.deltaX + " hm.deltaY=" + hm.deltaY);
			//if (b.wallExistsBetween(b._hunter.hl, new Location(nextX, nextY))){
			Wall newWall = null;
			
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] lastCreateWallTime + b.N=" + (lastCreateWallTime + b.N));
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] currSteps" + currSteps);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] prevMove.fromX" + prevMove.fromX);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] b._prey.pl.xloc" + b._prey.pl.xloc);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] prevMove.fromY" + prevMove.fromY);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] b._prey.pl.yloc" + b._prey.pl.yloc);			
			if(lastCreateWallTime <= 0 || lastCreateWallTime + b.N < currSteps && 
					(Math.abs(prevMove.fromX-b._prey.pl.xloc) == 4 || Math.abs(prevMove.fromX-b._prey.pl.xloc) == 4)){ 
				newWall = getWall(b, prevMove, b._prey); 
			}
			
			int meetTime = Math.min(Math.abs(b._hunter.hl.xloc - b._prey.pl.xloc), Math.abs(b._hunter.hl.yloc - b._prey.pl.yloc));
			if(newWall == null && (lastCreateWallTime <= 0 || lastCreateWallTime + b.N < currSteps) && meetTime == b.N+2){
				Wall tmpWall = getSmallestEnclosingWall(b._walls, b._hunter.hl, hm.deltaX, hm.deltaY, b._prey.pl);
				if(tmpWall.enclosingArea < Integer.MAX_VALUE){
					newWall = tmpWall;
				}
			}
			
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
		System.out.println("MinSpaceStrategy makeAMove[6-2] b._walls.size()=" + b._walls.size());
		System.out.println("MinSpaceStrategy makeAMove[6-2] b.M=" + b.M);
		if(b._walls.size() > 0 && b._walls.size() == b.M){
			if(hm.teardownWalls == null){
				hm.teardownWalls = new ArrayList<Wall>();
			}
			System.out.println("MinSpaceStrategy makeAMove[6-3] b._walls.get(0)="+b._walls.get(0));
			hm.teardownWalls.add(b._walls.get(0));
		}
		
		//System.out.println("MinSpaceStrategy makeAMove[7]");
		//HunterMove m = b.addHunterMove(hm);
		//System.out.println("MinSpaceStrategy makeAMove[8]");
		hunterMoveHist.add(hm);
		//m.buildWall = hm.buildWall;
		//m.teardownWalls = hm.teardownWalls;
		//System.out.println("MinSpaceStrategy makeAMove[9]");
		//System.out.println(m.moveToString());
		//System.out.println("MinSpaceStrategy makeAMove[10]");
		
		//if(hm.deltaX == 0){
		//	prevDeltaX *= -1;
		//}
		//if(hm.deltaY == 0){
		//	prevDeltaY *= -1;
		//}
		
		System.out.println("MinSpaceStrategy makeAMove[11] prevDeltaX=" + prevDeltaX + " prevDeltaY=" + prevDeltaY);

		//if(hm.teardownWalls == null){ hm.teardownWalls = new ArrayList<Wall>(); }
		currSteps++;
		if(hm.buildWall != null){ lastCreateWallTime = currSteps; }
		return hm;
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
		System.out.println("MinSpaceStrategy getWall[1] prevHM.fromX=" + prevHM.fromX);
		System.out.println("MinSpaceStrategy getWall[1] p.pl.xloc=" + p.pl.xloc);
		System.out.println("MinSpaceStrategy getWall[1] prevHM.fromY=" + prevHM.fromY);
		System.out.println("MinSpaceStrategy getWall[1] p.pl.yloc=" + p.pl.yloc);
		if(Math.abs(prevHM.fromX-p.pl.xloc) == 4){
			//get smallest enclosing wall
			Location hloc = new Location(prevHM.fromX, prevHM.fromY);
			Wall thisWall = getSmallestEnclosingWall(b._walls, hloc, prevHM.deltaX, prevHM.deltaY, p.pl);
			int timeInterval = (p.pl.yloc-prevHM.fromY)*2/3;
			int meetY = prevHM.fromY+prevHM.deltaY*timeInterval; //hunter move twice faster than prey
			int meetX = getFutureXLoc(hloc, prevHM.deltaX, b._walls, timeInterval);
			Wall nextWall = getSmallestEnclosingWall(b._walls, new Location(meetX, meetY), prevHM.deltaX, prevHM.deltaY, p.pl);
			
			System.out.println("MinSpaceStrategy getWall[2] thisWall.enclosingArea=" + thisWall.enclosingArea);
			System.out.println("MinSpaceStrategy getWall[2] nextWall.enclosingArea=" + nextWall.enclosingArea);
			 
			if(thisWall.enclosingArea < nextWall.enclosingArea){
				return thisWall;
			}else{
				return nextWall;
			}
			
		} else if (Math.abs(prevHM.fromY-p.pl.yloc) == 4){
			//get smallest enclosing wall
			Location hloc = new Location(prevHM.fromX, prevHM.fromY);
			Wall thisWall = getSmallestEnclosingWall(b._walls, hloc, prevHM.deltaX, prevHM.deltaY, p.pl);
			int timeInterval = (p.pl.xloc-prevHM.fromX)*2/3;
			int meetX = prevHM.fromX+prevHM.deltaX*timeInterval;
			int meetY = getFutureYLoc(new Location(prevHM.fromX, prevHM.fromY), prevHM.deltaY, b._walls, timeInterval);
			Wall nextWall = getSmallestEnclosingWall(b._walls, new Location(meetX, meetY), prevHM.deltaX, prevHM.deltaY, p.pl);
			
			System.out.println("MinSpaceStrategy getWall[3] thisWall.enclosingArea=" + thisWall.enclosingArea);
			System.out.println("MinSpaceStrategy getWall[3] nextWall.enclosingArea=" + nextWall.enclosingArea);
			
			if(thisWall.enclosingArea < nextWall.enclosingArea){
				return thisWall;
			}else{
				return nextWall;
			}
			
		} else {
			System.out.println("MinSpaceStrategy getWall[4]");
			return null;
		}
	}
	
	public Wall getSmallestEnclosingWall(List<Wall> walls, Location hl, int deltaX, int deltaY, Location pl){
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
		//if hunter going south and the prey is in the north, or vice versa , then don't build wall
		if((deltaY > 0 && pl.yloc < hl.yloc) || (deltaY < 0 && pl.yloc > hl.yloc)){
			hWallArea = Integer.MAX_VALUE;
		}else{
			if(hl.yloc > pl.yloc){ //S wall
				hWallArea = (maxX-minX)*(hl.yloc-minY);
			}else{ // N wall
				hWallArea = (maxX-minX)*(maxY-hl.yloc);
			}
		}
		hWall.enclosingArea = hWallArea;
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[3]");
		
		Location vwstart = new Location(hl.xloc, minY);
		Location vwend = new Location(hl.xloc, maxY);
		Wall vWall = new Wall(vwstart, vwend, WallOperation.BUILD, wallIdx);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4]");
		
		int vWallArea = 0;
		//if hunter going east and the prey is in the west, or vice versa , then don't build wall
		if(deltaX > 0 && pl.xloc < hl.xloc || deltaX < 0 && pl.xloc > hl.xloc){
			vWallArea = Integer.MAX_VALUE;
		}else{
			if(hl.xloc < pl.xloc){  // W wall
				vWallArea = (maxX-hl.xloc)*(maxY-minY);
			}else{ // E wall
				vWallArea = (hl.xloc-minX)*(maxY-minY);
			}
		}
		vWall.enclosingArea = vWallArea;
		
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
		System.out.println("getMinMaxXY walls.size= " + walls.size());
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
	
	public int getFutureXLoc(Location h, int deltaX, List<Wall> walls, int timeInterval){
		if(deltaX == 0){ return h.xloc; }
		int eastIdx = getEWallIdx(walls, h.xloc);
		int westIdx = getWWallIdx(walls, h.xloc);
		int eastX = eastIdx == -1? 299 : walls.get(eastIdx).leftEnd.xloc;
		int westX = westIdx == -1? 0 : walls.get(westIdx).leftEnd.xloc;
		System.out.println("getFutureXLoc eastX=" + eastX);
		System.out.println("getFutureXLoc westX=" + westX);
		int newInterval = timeInterval%(eastX-westX);
		return h.xloc+newInterval*deltaX;
	}
	
	public int getFutureYLoc(Location h, int deltaY, List<Wall> walls, int timeInterval){
		if(deltaY == 0){ return h.yloc; }
		int northIdx = getNWallIdx(walls, h.yloc);
		int southIdx = getSWallIdx(walls, h.yloc);
		int northX = northIdx == -1? 0 : walls.get(northIdx).leftEnd.yloc;
		int southX = southIdx == -1? 299 : walls.get(southIdx).leftEnd.yloc;
		System.out.println("getFutureYLoc northX=" + northX);
		System.out.println("getFutureYLoc southX=" + southX);
		int newInterval = timeInterval%(southX-northX);
		return h.yloc+newInterval*deltaY;
	}

}
