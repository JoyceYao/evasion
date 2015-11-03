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

	@Override
	public HunterMove makeAMove(Board b) {
		System.out.println("MinSpaceStrategy makeAMove");
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
		
		System.out.println(hm.moveToString());
		return hm;
	}
	
	private HunterMove getInitialMove(){
		System.out.println("MinSpaceStrategy getInitialMove");
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
			return getSmallestEnclosingWall(b.walls, new Location(prevHM.fromX, prevHM.fromY), new Location(prevHM.fromX+prevHM.deltaX, prevHM.fromY+prevHM.deltaY), p.pl);
		} else {
			return null;
		}
	}
	
	private Wall getSmallestEnclosingWall(List<Wall> walls, Location hl, Location hl2, Location pl){
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
		
		Location hwstart = new Location(walls.get(EWallIdx).leftEnd.xloc, hl.yloc);
		Location hwend = new Location(walls.get(WWallIdx).leftEnd.xloc, hl.yloc);
		Wall hWall = new Wall(hwstart, hwend, WallOperation.BUILD, wallIdx);
		
		int hWallArea = 0;
		if(hl2.yloc < hl.yloc){ //S wall
			hWallArea = (maxX-minX)*(hl.yloc-minY);
		}else{ // N wall
			hWallArea = (maxX-minX)*(maxY-hl.yloc);
		}
		
		Location vwstart = new Location(hl.xloc, walls.get(NWallIdx).leftEnd.yloc);
		Location vwend = new Location(hl.xloc, walls.get(SWallIdx).leftEnd.yloc);
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
		innerWall.add(walls.get(EWallIdx).wallIndex);
		innerWall.add(walls.get(WWallIdx).wallIndex);
		innerWall.add(walls.get(NWallIdx).wallIndex);
		innerWall.add(walls.get(SWallIdx).wallIndex);
		Wall result = hWallArea < vWallArea ? hWall : vWall;
		walls.add(result);
		innerWall.add(result.wallIndex);
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
