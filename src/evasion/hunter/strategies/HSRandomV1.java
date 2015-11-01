package evasion.hunter.strategies;

import evasion.Board;
import evasion.Wall;
import evasion.hunter.HunterMove;
import java.util.Random;

public class HSRandomV1 extends AbsHunterStrategy{
	Random rand = new Random();
	static int rangeDir = 4; //[0 - 4)
	static int wallChoice = 6; //[0-6); 0 is build no wall
	@Override
	public HunterMove makeAMove(Board b) {
		HunterMove hm = new HunterMove();
		hm.fromX = b.h.hl.xloc; hm.fromY = b.h.hl.yloc;
		int randomNum = rand.nextInt(wallChoice);
		switch (randomNum) {
			case 0: hm.buildWall = null; //No wall
			case 1: hm.buildWall = new Wall(); //horizontal wall to E
					hm.buildWall.leftEnd.xloc = hm.fromX; hm.buildWall.leftEnd.yloc = hm.fromY; 
					hm.buildWall.rightEnd.yloc = hm.buildWall.leftEnd.xloc;
					hm.buildWall.rightEnd.xloc = Board.MAX_X;
					break;
			case 2: hm.buildWall = new Wall(); //vertical wall to S
				hm.buildWall.leftEnd.xloc = hm.fromX; hm.buildWall.leftEnd.yloc = hm.fromY; 
				hm.buildWall.rightEnd.yloc = Board.MAX_Y;
				hm.buildWall.rightEnd.xloc = hm.buildWall.leftEnd.xloc;
				break;
			case 3: hm.buildWall = new Wall(); //horizontal wall to W
				hm.buildWall.leftEnd.xloc = Board.MIN_X; hm.buildWall.leftEnd.yloc = hm.fromY; 
				hm.buildWall.rightEnd.yloc = hm.fromY;
				hm.buildWall.rightEnd.xloc = hm.fromX;
				break;
			case 4: hm.buildWall = new Wall(); //vertical wall to N
				hm.buildWall.leftEnd.xloc = hm.fromX; hm.buildWall.leftEnd.yloc = hm.fromY; 
				hm.buildWall.rightEnd.yloc = Board.MIN_Y;
				hm.buildWall.rightEnd.xloc = hm.fromX;
				break;
			default: //No wall
		}
		randomNum = rand.nextInt(rangeDir); //NE, SE, SW, NW 
		switch (randomNum) {
			case 0: hm.deltaX = 1; hm.deltaY = -1; return hm; //NE
			case 1: hm.deltaX = 1; hm.deltaY = 1; return hm; //SE
			case 2: hm.deltaX = -1; hm.deltaY = -1; return hm; //NW
			case 3: hm.deltaX = -1; hm.deltaY = 1; return hm; //SW
			default: hm.deltaX = 1; hm.deltaY = -1; return hm; //NE
		}
	}
}
