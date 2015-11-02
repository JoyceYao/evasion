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
	
	public List<Wall> verticalWalls = new ArrayList<Wall>();
	public List<Wall> horizontalWalls = new ArrayList<Wall>();
	
	List<HunterMove> hunterMoveHist = new ArrayList<HunterMove>();
	int wallIdx = 0;
	List<Integer> innerWall = new ArrayList<Integer>();

	@Override
	public HunterMove makeAMove(Board b) {
		HunterMove hm = null;
		if(hunterMoveHist.size() == 0){
			hm = getInitialMove();
			hunterMoveHist.add(hm);
			return hm;
		} else {
			hm = new HunterMove();
			HunterMove prevMove = hunterMoveHist.get(hunterMoveHist.size()-1);
			int nextX = b.h.hl.xloc + prevMove.deltaX;
			int nextY = b.h.hl.yloc + prevMove.deltaY;
			if (b.wallExistsBetween(b.h.hl, new Location(nextX, nextY))){
				hm.buildWall = getWall(b, prevMove, b.p);
			}
			
		}
		
		// remove extra walls
		for(int i=0; i<b.walls.size(); i++){
			if(!innerWall.contains(b.walls.get(i).wallIndex)){
				//b.walls.remove(i);
				hm.teardownWall = b.walls.get(i);
			}
		}
		
		return hm;
	}
	
	private HunterMove getInitialMove(){
		HunterMove hm = new HunterMove();
		hm.buildWall = null;
		hm.teardownWall = null;
		hm.deltaX = 1;
		hm.deltaY = 1;
		return hm;
	}
	
	
	private Wall getWall(Board b, HunterMove prevHM,  Prey p){
		if(prevHM.fromX-p.pl.xloc < 2 || prevHM.fromY-p.pl.yloc < 2){
			//get smallest enclosing wall
			return getSmallestEnclosingWall(b, new Location(prevHM.fromX, prevHM.fromY), new Location(prevHM.fromX+prevHM.deltaX, prevHM.fromY+prevHM.deltaY), p.pl);
		} else {
			return null;
		}
	}

	/*
	private List<Location> getIntersects(){
		List<Location> intersects = new ArrayList<Location>();
		for(int i=0; i<verticalWalls.size(); i++){
			for(int j=0; j<horizontalWalls.size(); j++){
				Wall v = verticalWalls.get(i);
				Wall h = horizontalWalls.get(j);
				Location inter = getIntersect(v, h);
				if(inter != null){
					intersects.add(inter);
				}
			}
		}
		
		for(int i=0; i<verticalWalls.size(); i++){
			Wall v = verticalWalls.get(i);
			if(v.leftEnd.yloc == Board.MIN_Y || v.rightEnd.yloc == Board.MIN_Y){
				intersects.add(new Location(v.leftEnd.xloc, Board.MIN_Y));
			}
			if(v.leftEnd.yloc == Board.MAX_Y || v.rightEnd.yloc == Board.MAX_Y){
				intersects.add(new Location(v.leftEnd.xloc, Board.MAX_Y));
			}
		}
		
		for(int j=0; j<horizontalWalls.size(); j++){
			Wall h = horizontalWalls.get(j);
			if(h.leftEnd.xloc == Board.MIN_X || h.rightEnd.xloc == Board.MIN_X){
				intersects.add(new Location(Board.MIN_X, h.leftEnd.yloc));
			}
			if(h.leftEnd.yloc == Board.MAX_X || h.rightEnd.yloc == Board.MAX_X){
				intersects.add(new Location(Board.MAX_X, h.leftEnd.yloc));
			}
		}
		
		intersects.add(new Location(Board.MIN_X, Board.MIN_Y));
		intersects.add(new Location(Board.MAX_X, Board.MAX_Y));
		intersects.add(new Location(Board.MIN_X, Board.MAX_Y));
		intersects.add(new Location(Board.MAX_X, Board.MIN_Y));
		return intersects;
	}
	
	private Location getIntersect(Wall v, Wall h){
		int minX = Math.min(h.leftEnd.xloc, h.rightEnd.xloc);
		int maxX = Math.max(h.leftEnd.xloc, h.rightEnd.xloc);
		
		int minY = Math.min(v.leftEnd.yloc, v.rightEnd.yloc);
		int maxY = Math.max(v.leftEnd.yloc, v.rightEnd.yloc);
		if(v.leftEnd.xloc <= maxX && v.leftEnd.xloc >= minX && h.leftEnd.yloc >= minY && h.leftEnd.yloc <= maxY){
			return new Location(v.leftEnd.xloc, h.leftEnd.yloc);
		}
		return null;
	}*/
	
	
	private Wall getSmallestEnclosingWall(Board b, Location hl, Location hl2, Location pl){
		// get enclosing space before building new wall
		int WWallIdx = getWWallIdx(b, Math.max(hl.xloc, pl.xloc));
		int EWallIdx = getEWallIdx(b, Math.min(hl.xloc, pl.xloc));
		int NWallIdx = getNWallIdx(b, Math.min(hl.yloc, pl.yloc));
		int SWallIdx = getSWallIdx(b, Math.max(hl.yloc, pl.yloc));
		
		int minX = EWallIdx == -1 ? Board.MIN_X : b.walls.get(EWallIdx).leftEnd.xloc;
		int maxX = WWallIdx == -1 ? Board.MAX_X : b.walls.get(WWallIdx).leftEnd.xloc;
		int minY = NWallIdx == -1 ? Board.MIN_Y : b.walls.get(NWallIdx).leftEnd.yloc;
		int maxY = SWallIdx == -1 ? Board.MAX_Y : b.walls.get(SWallIdx).leftEnd.yloc;
		
		int area = (maxX-minX)*(maxY-minY);
		
		Location hwstart = new Location(b.walls.get(EWallIdx).leftEnd.xloc, hl.yloc);
		Location hwend = new Location(b.walls.get(WWallIdx).leftEnd.xloc, hl.yloc);
		Wall hWall = new Wall(hwstart, hwend, WallOperation.BUILD, wallIdx);
		
		int hWallArea = 0;
		if(hl2.yloc < hl.yloc){ //S wall
			hWallArea = (maxX-minX)*(hl.yloc-minY);
		}else{ // N wall
			hWallArea = (maxX-minX)*(maxY-hl.yloc);
		}
		
		Location vwstart = new Location(hl.xloc, b.walls.get(NWallIdx).leftEnd.yloc);
		Location vwend = new Location(hl.xloc, b.walls.get(SWallIdx).leftEnd.yloc);
		Wall vWall = new Wall(vwstart, vwend, WallOperation.BUILD, wallIdx);
		
		int vWallArea = 0;
		if(hl2.xloc < hl.xloc){  // W wall
			vWallArea = (maxX-hl.xloc)*(maxY-minY);
		}else{ // E wall
			vWallArea = (hl.xloc-minX)*(maxY-minY);
		}
		
		wallIdx++;
		
		// set inner walls list
		innerWall.clear();
		innerWall.add(b.walls.get(EWallIdx).wallIndex);
		innerWall.add(b.walls.get(WWallIdx).wallIndex);
		innerWall.add(b.walls.get(NWallIdx).wallIndex);
		innerWall.add(b.walls.get(SWallIdx).wallIndex);
		Wall result = hWallArea < vWallArea ? hWall : vWall;
		b.walls.add(result);
		innerWall.add(result.wallIndex);
		return result;
	}
	
	private int getWWallIdx(Board b, int x){
		int currIdx = -1;
		for(int i=0; i<b.walls.size(); i++){
			Wall w = b.walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isVerticalWall(w) && w.leftEnd.xloc > x 
					&& (currIdx == -1 || b.walls.get(currIdx).leftEnd.xloc > w.leftEnd.xloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}
	
	private int getEWallIdx(Board b, int x){
		int currIdx = -1;
		for(int i=0; i<b.walls.size(); i++){
			Wall w = b.walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isVerticalWall(w) && w.leftEnd.xloc < x 
					&& (currIdx == -1 || b.walls.get(currIdx).leftEnd.xloc < w.leftEnd.xloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}
	
	private int getNWallIdx(Board b, int y){
		int currIdx = -1;
		for(int i=0; i<b.walls.size(); i++){
			Wall w = b.walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isHorizontalWall(w) && w.leftEnd.yloc < y 
					&& (currIdx == -1 || b.walls.get(currIdx).leftEnd.yloc < w.leftEnd.yloc)){
				currIdx = i;
			}
		}
		return currIdx;
	}

	private int getSWallIdx(Board b, int y){
		int currIdx = -1;
		for(int i=0; i<b.walls.size(); i++){
			Wall w = b.walls.get(i);
			// if this a a vertical wall, and on the right of my current position
			// and smaller than current west end
			if(isHorizontalWall(w) && w.leftEnd.yloc > y 
					&& (currIdx == -1 || b.walls.get(currIdx).leftEnd.yloc > w.leftEnd.yloc)){
				currIdx = i;
			}
		}
		return currIdx;
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
