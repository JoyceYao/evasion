package evasion.prey.strategies;

import java.util.Random;

import evasion.Board;
import evasion.prey.PreyMove;

public class Test extends AbsPreyStrategy{
	Random rand = new Random();
	int range = 30; //[0-9)
	int direction = 0;
	int steps = 0;
	@Override
	public PreyMove makeAMove(Board bd) {
		int randomNum = rand.nextInt(range); //NE, SE, SW, NW 
		PreyMove pm = new PreyMove();
		pm.fromX = bd._prey.pl.xloc; pm.fromY = bd._prey.pl.yloc;
		
		if(steps > 0){
			switch(direction){
				case 0: pm.deltaX = 1; pm.deltaY = 0; break;
				case 1: pm.deltaX = 1; pm.deltaY = 1; break;
				case 2: pm.deltaX = -1; pm.deltaY = 0; break;
				case 3: pm.deltaX = 0; pm.deltaY = -1; break;
				case 4: pm.deltaX = 0; pm.deltaY = 0; break;
				case 5: pm.deltaX = -1; pm.deltaY = 1; break;

			}
			steps--;
		}else{
			steps = randomNum;
			direction++;
			direction %= 6;
		}
		return pm;
	}
	
}
