package evasion.prey.strategies;

import java.util.Random;

import evasion.Board;
import evasion.prey.PreyMove;

public class PRRandomV1 extends AbsPreyStrategy{
	Random rand = new Random();
	int range = 8; //[0-9)
	@Override
	public PreyMove makeAMove(Board bd) {
		int randomNum = rand.nextInt(range); //NE, SE, SW, NW 
		PreyMove pm = new PreyMove();
		pm.fromX = b.p.pl.xloc; pm.fromY = b.p.pl.yloc;
		switch (randomNum) {
			case 0: pm.deltaX = 1; pm.deltaY = -1; return pm; //NE
			case 1: pm.deltaX = 1; pm.deltaY = 0; return pm; //E
			case 2: pm.deltaX = 1; pm.deltaY = 1; return pm; //SE
			case 3: pm.deltaX = 0; pm.deltaY = 1; return pm; //S
			case 4: pm.deltaX = -1; pm.deltaY = 1; return pm; //SW
			case 5: pm.deltaX = -1; pm.deltaY = 0; return pm; //W
			case 6: pm.deltaX = -1; pm.deltaY = -1; return pm; //NW
			case 7: pm.deltaX = 0; pm.deltaY = -1; return pm; //N
			case 8: pm.deltaX = 0; pm.deltaY = 0; return pm; //no move
			default: pm.deltaX = 1; pm.deltaY = -1; return pm; //NE
		}
	}
	
}
