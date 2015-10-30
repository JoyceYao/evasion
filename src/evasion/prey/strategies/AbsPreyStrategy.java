package evasion.prey.strategies;
import evasion.Board;
import evasion.prey.PreyMove;

public abstract class AbsPreyStrategy {

	Board b;
	
	public abstract PreyMove makeAMove(Board bd);
	
	public boolean isAValidMove(PreyMove hm) {
		return false;
	}
	
	public PreyMove bounceAdjustMove(PreyMove hm) {
		return null;
	}
	public static AbsPreyStrategy getStrategy(String str) {
		switch (str) {
			case "R":
				return new PRRandomV1();
			default:
				return new PRRandomV1();
		}
	}
}
