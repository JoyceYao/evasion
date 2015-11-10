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
	int timeSinceLastBuild = -100;
	//int COOL_DOWN_TIME = 100;
	//int currSteps = 0;
	int bufferDist = 4;
	
	public MinSpaceStrategy(){
		System.out.println("MinSpaceStrategy");
	}

	@Override
	public HunterMove makeAMove(Board b) {
		System.out.println("MinSpaceStrategy makeAMove[0]");
		HunterMove hm = null;
		//if(hm.fromX == 0 && hm.fromY == 0){
		if(hunterMoveHist.size() == 0){
			hm = getInitialMove();
			hunterMoveHist.add(hm);
			return hm;
		} else {
			System.out.println("MinSpaceStrategy makeAMove[1]");
			hm = (HunterMove)CardinalDirections.getMoveFromCardinalDirections(b._hunter.hl, b._hunter.hunterDirection, "HUNTER");
			//HunterMove prevMove = hunterMoveHist.get(hunterMoveHist.size()-1);
			
			System.out.println("MinSpaceStrategy makeAMove[2] hm.deltaX=" + hm.deltaX + " hm.deltaY=" + hm.deltaY);
			//if (b.wallExistsBetween(b._hunter.hl, new Location(nextX, nextY))){
			Wall newWall = null;
			
			//System.out.println("MinSpaceStrategy makeAMove[2-0-1] timeSinceLastBuild + b.N=" + (timeSinceLastBuild + b.N));
			//System.out.println("MinSpaceStrategy makeAMove[2-0-1] currSteps=" + currSteps);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] hm.fromX=" + hm.fromX);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] hm.fromY=" + hm.fromY);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] b._prey.pl.xloc=" + b._prey.pl.xloc);
			System.out.println("MinSpaceStrategy makeAMove[2-0-1] b._prey.pl.yloc=" + b._prey.pl.yloc);			
			System.out.println("MinSpaceStrategy makeAMove[2-0-3] b._hunter.hl x=" + b._hunter.hl.xloc);
			System.out.println("MinSpaceStrategy makeAMove[2-0-3] b._hunter.hl y=" + b._hunter.hl.yloc);
			
			//System.out.println("MinSpaceStrategy makeAMove[2-0-3] lastCreateWallTime + b.N < currSteps=" + (lastCreateWallTime + b.N < currSteps));
			System.out.println("MinSpaceStrategy makeAMove[2-0-3] (Math.abs(hm.fromX-b._prey.pl.xloc)==4 =" + (Math.abs(hm.fromX-b._prey.pl.xloc)==4));
			System.out.println("MinSpaceStrategy makeAMove[2-0-3] (Math.abs(hm.fromY-b._prey.pl.yloc)==4=" + (Math.abs(hm.fromY-b._prey.pl.yloc)==4));
			
			//if((lastCreateWallTime <= 0 || lastCreateWallTime + b.N <= currSteps) && 
			if(canBuildWall(b) && (Math.abs(hm.fromX-b._prey.pl.xloc) <= bufferDist || Math.abs(hm.fromY-b._prey.pl.yloc) <= bufferDist)){ 
				System.out.println("MinSpaceStrategy makeAMove[2-0-3-1]");
				newWall = getWall(b, hm, b._prey); 
			}
			
			int meetTime = Math.min(Math.abs(b._hunter.hl.xloc - b._prey.pl.xloc), Math.abs(b._hunter.hl.yloc - b._prey.pl.yloc))/3*2;
			// if has chance to build a wall before meet the prey
			System.out.println("MinSpaceStrategy makeAMove[2-0-4] meetTime=" + meetTime);
			if(newWall == null && canBuildWall(b) && !isLeavingPrey(hm, b._prey.pl) && (meetTime == b.N + bufferDist || meetTime == b.N + bufferDist-1)){
				
				System.out.println("MinSpaceStrategy makeAMove[2-0-4] b._hunter.hl x=" + b._hunter.hl.xloc);
				System.out.println("MinSpaceStrategy makeAMove[2-0-4] b._hunter.hl y=" + b._hunter.hl.yloc);
				Wall tmpWall = getSmallestEnclosingWall(b._walls, b._hunter.hl, hm.deltaX, hm.deltaY, b._prey.pl);
				if(tmpWall != null && tmpWall.enclosingArea < Integer.MAX_VALUE){
					newWall = tmpWall;
				}
			}
			
			// don't build new walls when there are walls separate hunter and prey 
			if(b.findWallBetween(b._hunter, b._prey) != null){
				newWall = null;
			}
			
			System.out.println("MinSpaceStrategy makeAMove[2-0-5] newWall=" + newWall);
			int minMeetTime = getMinMeetTime(b._walls, hm, b._hunter.hl, b._prey.pl);  // minMeetTime > b.N &&
			// if leaving the prey, build a wall to close it
			if(newWall == null && canBuildWall(b) && isLeavingPrey(hm, b._prey.pl) && minMeetTime > b.N && b.findWallBetween(b._hunter, b._prey) == null){
				System.out.println("MinSpaceStrategy makeAMove[2-0-6] newWall=" + newWall);
				newWall = getSmallestEnclosingWall(b._walls, b._hunter.hl, hm.deltaX, hm.deltaY, b._prey.pl);
				System.out.println("MinSpaceStrategy makeAMove[2-0-7] newWall=" + newWall);
			}
			
			System.out.println("MinSpaceStrategy makeAMove[2-0] newWall=" + newWall);
			
			if(newWall != null){
				
				int overlapWallIdx = b.hasOverlapWall(newWall);
				Wall w = b.findWallBetween(b._hunter, b._prey);
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
			System.out.println("MinSpaceStrategy makeAMove[6-4] hm.teardownWalls="+hm.teardownWalls);
			//hm.teardownWalls.add(b._walls.get(0));
			hm.teardownWalls.addAll(getRemoveWallList(b._walls, b._hunter.hl, b._prey.pl, b));
			System.out.println("MinSpaceStrategy makeAMove[6-5] hm.teardownWalls="+hm.teardownWalls);
		}
		
		System.out.println("MinSpaceStrategy makeAMove[7] hm.teardownWalls=" + hm.teardownWalls);
		
		Wall wBetweenHAndP = tearDownSeperateWall(b, hm);
		System.out.println("MinSpaceStrategy makeAMove[8] hm.teardownWalls=" + hm.teardownWalls);
		if(wBetweenHAndP != null){
			System.out.println("MinSpaceStrategy makeAMove[9]");
			if(hm.teardownWalls == null){
				hm.teardownWalls = new ArrayList<Wall>();
			}
			System.out.println("MinSpaceStrategy makeAMove[10]");
			hm.teardownWalls.add(wBetweenHAndP);
		}
		
		//hunterMoveHist.add(hm);
		
		System.out.println("MinSpaceStrategy makeAMove[11] prevDeltaX=" + prevDeltaX + " prevDeltaY=" + prevDeltaY);

		//currSteps++;
		if(hm.buildWall != null){ timeSinceLastBuild = b.time; }
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
	
	
	private Wall getWall(Board b, HunterMove hm,  Prey p){
		System.out.println("MinSpaceStrategy getWall[0]");
		System.out.println("MinSpaceStrategy getWall[1] hm.fromX=" + hm.fromX);
		System.out.println("MinSpaceStrategy getWall[1] p.pl.xloc=" + p.pl.xloc);
		System.out.println("MinSpaceStrategy getWall[1] hm.fromY=" + hm.fromY);
		System.out.println("MinSpaceStrategy getWall[1] p.pl.yloc=" + p.pl.yloc);
		if(Math.abs(hm.fromX-p.pl.xloc) <= bufferDist){
			//get smallest enclosing wall
			Location hloc = new Location(hm.fromX, hm.fromY);
			Wall thisWall = getSmallestEnclosingWall(b._walls, hloc, hm.deltaX, hm.deltaY, p.pl);
			if(thisWall != null){System.out.println("MinSpaceStrategy getWall[2] thisWall.enclosingArea=" + thisWall.enclosingArea); }
			
			// if the hunter is leaving prey, don't consider nextWall
			Wall nextWall = null;
			if(!isLeavingPrey(hm, p.pl)){
				int timeInterval = (Math.abs(p.pl.yloc-hm.fromY))*2/3;
				int meetY = hm.fromY+hm.deltaY*timeInterval; //hunter move twice faster than prey
				int meetX = getFutureXLoc(hloc, hm.deltaX, b._walls, timeInterval);
				nextWall = getSmallestEnclosingWall(b._walls, new Location(meetX, meetY), hm.deltaX, hm.deltaY, p.pl);
				if(nextWall != null){ System.out.println("MinSpaceStrategy getWall[2] nextWall.enclosingArea=" + nextWall.enclosingArea);}
			}
			 
			if(nextWall == null || thisWall.enclosingArea < nextWall.enclosingArea){
				return thisWall;
			}else{
				return nextWall;
			}
			
		} else if (Math.abs(hm.fromY-p.pl.yloc) <= bufferDist){
			//get smallest enclosing wall
			Location hloc = new Location(hm.fromX, hm.fromY);
			Wall thisWall = getSmallestEnclosingWall(b._walls, hloc, hm.deltaX, hm.deltaY, p.pl);
			if(thisWall != null){ System.out.println("MinSpaceStrategy getWall[3] thisWall.enclosingArea=" + thisWall.enclosingArea); }
			
			Wall nextWall = null;
			if(!isLeavingPrey(hm, p.pl)){
				int timeInterval = (p.pl.xloc-hm.fromX)*2/3;
				int meetX = hm.fromX+hm.deltaX*timeInterval;
				int meetY = getFutureYLoc(new Location(hm.fromX, hm.fromY), hm.deltaY, b._walls, timeInterval);
				nextWall = getSmallestEnclosingWall(b._walls, new Location(meetX, meetY), hm.deltaX, hm.deltaY, p.pl);
				if(nextWall != null){ System.out.println("MinSpaceStrategy getWall[3] nextWall.enclosingArea=" + nextWall.enclosingArea); }
			}
					
			if(nextWall == null || thisWall.enclosingArea < nextWall.enclosingArea){
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
		
		int WWallIdx = getWWallIdx(walls, Math.min(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(walls, Math.max(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(walls, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(walls, Math.max(hl.yloc, pl.yloc));
		
		int maxX = EWallIdx == -1 ? Board.MAX_X : walls.get(EWallIdx).leftEnd.xloc;
		int minX = WWallIdx == -1 ? Board.MIN_X : walls.get(WWallIdx).leftEnd.xloc;
		int minY = NWallIdx == -1 ? Board.MIN_Y : walls.get(NWallIdx).leftEnd.yloc;
		int maxY = SWallIdx == -1 ? Board.MAX_Y : walls.get(SWallIdx).leftEnd.yloc;
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] minX=" + minX);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] maxX=" + maxX);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] minY=" + minY);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] maxY=" + maxY);
		
		if(maxX - minX <= 2 || maxY- minY <= 2){ return null; }
		
		int area = (maxX-minX)*(maxY-minY);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[0-1] area=" + area);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2] hl.yloc=" + hl.yloc);
		
		Location hwstart = new Location(minX, hl.yloc);
		Location hwend = new Location(maxX, hl.yloc);
		Wall hWall = new Wall(hwstart, hwend, WallOperation.BUILD, wallIdx);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-1] hwstart x=" + hwstart.xloc + " y=" + hwstart.yloc);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-2] hwend x=" + hwend.xloc + " y=" + hwend.yloc);
		
		int hWallArea = 0;
		//if hunter going south and the prey is in the north, or vice versa , then don't build wall
		//if((deltaY > 0 && pl.yloc < hl.yloc) || (deltaY < 0 && pl.yloc > hl.yloc)){
		//	System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-3-1]");
		//	hWallArea = Integer.MAX_VALUE;
		//}else{
			if(hl.yloc > pl.yloc){ //S wall
				hWallArea = (maxX-minX)*(hl.yloc-minY);
				System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-3-2] hWallArea=" + hWallArea);
			}else{ // N wall
				hWallArea = (maxX-minX)*(maxY-hl.yloc);
				System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-3-3] hWallArea=" + hWallArea);
			}
		//}
		hWall.enclosingArea = hWallArea;
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[2-3] hWallArea=" + hWallArea);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[3] hl.xloc=" + hl.xloc);
		
		Location vwstart = new Location(hl.xloc, minY);
		Location vwend = new Location(hl.xloc, maxY);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[3] vwstart x=" + vwstart.xloc + " y=" + vwstart.yloc);
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[3] vwend x=" + vwend.xloc + " y=" + vwend.yloc);
		Wall vWall = new Wall(vwstart, vwend, WallOperation.BUILD, wallIdx);
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4]");
		
		int vWallArea = 0;
		//if hunter going east and the prey is in the west, or vice versa , then don't build wall
		//if(deltaX > 0 && pl.xloc < hl.xloc || deltaX < 0 && pl.xloc > hl.xloc){
		//	vWallArea = Integer.MAX_VALUE;
		//	System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4-1]");
		//}else{
			if(hl.xloc < pl.xloc){  // W wall
				vWallArea = (maxX-hl.xloc)*(maxY-minY);
				System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4-2] vWallArea=" + vWallArea);
			}else{ // E wall
				vWallArea = (hl.xloc-minX)*(maxY-minY);
				System.out.println("MinSpaceStrategy getSmallestEnclosingWall[4-3] vWallArea=" + vWallArea);
			}
		//}
		vWall.enclosingArea = vWallArea;
		
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[5]");
		
		wallIdx++;
		
		if(vWallArea == Integer.MAX_VALUE && hWallArea == Integer.MAX_VALUE){
			return null;
		}

		Wall result = hWallArea < vWallArea ? hWall : vWall;
		System.out.println("MinSpaceStrategy getSmallestEnclosingWall[6]");
		return result;
	}
	
	public int getInnerWallArea(List<Wall> walls, Location hl, Location pl){
		// get enclosing space before building new wall
		int WWallIdx = getWWallIdx(walls, Math.min(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(walls, Math.max(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(walls, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(walls, Math.max(hl.yloc, pl.yloc));
		
		int maxX = EWallIdx == -1 ? Board.MAX_X : walls.get(EWallIdx).leftEnd.xloc;
		int minX = WWallIdx == -1 ? Board.MIN_X : walls.get(WWallIdx).leftEnd.xloc;
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
			if(isVerticalWall(w) && w.leftEnd.xloc < x 
					&& (currIdx == -1 || w.leftEnd.xloc > walls.get(currIdx).leftEnd.xloc)){
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
			if(isVerticalWall(w) && w.leftEnd.xloc > x 
					&& (currIdx == -1 || w.leftEnd.xloc < walls.get(currIdx).leftEnd.xloc)){
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
					&& (currIdx == -1 || w.leftEnd.yloc > walls.get(currIdx).leftEnd.yloc)){
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
					&& (currIdx == -1 || w.leftEnd.yloc < walls.get(currIdx).leftEnd.yloc)){
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
	
	public boolean isLeavingPrey(Move hm, Location pl){
		// moving south and prey is in my south
		if(hm.deltaY > 0 && pl.yloc > hm.fromY){
			return false;
		}
		// moving north and prey is in my north
		if(hm.deltaY < 0 && pl.yloc < hm.fromY){
			return false;
		}
		// moving east and prey is in my east
		if(hm.deltaX > 0 && pl.xloc > hm.fromX){
			return false;
		}
		// moving west and prey is in my west
		if(hm.deltaX < 0 && pl.xloc < hm.fromX){
			return false;
		}
		return true;
	}
	
	private List<Wall> getRemoveWallList(List<Wall> walls, Location hl, Location pl, Board b){
		List<Wall> result = new ArrayList<Wall>();
		int currArea = getInnerWallArea(walls, hl, pl);
		Wall w = b.findWallBetween(b._hunter, b._prey);
		
		for(int i=0; i<walls.size(); i++){
			List<Wall> tmpWalls = new ArrayList<Wall>(walls);
			tmpWalls.remove(i);
			if(getInnerWallArea(tmpWalls, hl, pl) == currArea){
				if(w!= null && w.wallIndex == i){ continue; }
				result.add(walls.get(i));
				break;
			}
		}
		return result;
	}
	
	private boolean canBuildWall(Board b){
		System.out.println("canBuildWall!! b.time = " + b.time);
		System.out.println("canBuildWall!! timeSinceLastBuild = " + timeSinceLastBuild);
		System.out.println("canBuildWall!! b.N = " + b.N);
		return b.time - timeSinceLastBuild > b.N;
	}
	
	private Wall tearDownSeperateWall(Board b, HunterMove hm){
		//int nextX = getFutureXLoc(b._hunter.hl, hm.deltaX, b._walls, 6);
		//int nextY = getFutureYLoc(b._hunter.hl, hm.deltaY, b._walls, 6);
		Wall w1 = null;
		for(int i=1; i<=8; i++){
			int nextX = hm.fromX + hm.deltaX*i;
			nextX = nextX > Board.MAX_X ? nextX = Board.MAX_X : nextX < Board.MIN_X ? Board.MIN_X : nextX;
			int nextY = hm.fromY + hm.deltaY*i;
			nextY = nextY > Board.MAX_Y ? nextY = Board.MAX_Y : nextY < Board.MIN_Y ? Board.MIN_Y : nextY;
			w1 = b.wallBetween(b._hunter.hl, new Location(nextX, nextY));
			if(w1 != null){ break; }
		}
		 
		Wall w2 = b.findWallBetween(b._hunter, b._prey);
		
		System.out.println("tearDownSeperateWall [0] w1==" + w1);
		System.out.println("tearDownSeperateWall [1] w2==" + w2);
		if(w1 != null){ System.out.println("tearDownSeperateWall [0] w1.id==" + w1.wallIndex); }
		if(w2 != null){ System.out.println("tearDownSeperateWall [0] w2.id==" + w2.wallIndex); }
		
		if(w1 != null && w2 != null && w1.wallIndex == w2.wallIndex){
			System.out.println("tearDownSeperateWall [2]");
			return w1;
		}
		System.out.println("tearDownSeperateWall [3]");
		return null;
	}
	
	private int getMinMeetTime(List<Wall> walls, HunterMove hm, Location hl, Location pl){
		Location[] locs = getMinMaxXY(walls, hl, pl);
		int XWall = 0;
		int YWall = 0;
		if(hm.deltaX > 0){
			XWall = locs[1].xloc;
		}else{
			XWall = locs[0].xloc;
		}
		
		if(hm.deltaY > 0){
			YWall = locs[1].yloc;
		}else{
			YWall = locs[0].yloc;
		}
		
		int xMeetTime = (Math.abs(hl.xloc-pl.xloc)+Math.abs(hl.xloc-XWall)/2)*2/3;
		int yMeetTime = (Math.abs(hl.yloc-pl.yloc)+Math.abs(hl.yloc-YWall)/2)*2/3;
		return Math.min(xMeetTime, yMeetTime);
	}

}
