package evasion.hunter.strategies;
import evasion.Board;
import evasion.hunter.HunterMove;

public abstract class AbsHunterStrategy {
	public abstract HunterMove makeAMove(Board b);
	
	public boolean isAValidMove(HunterMove hm) {
		return false;
	}
	
	public HunterMove bounceAdjustMove(HunterMove hm) {
		return null;
	}
	
	public static AbsHunterStrategy getStrategy(String str) {
		//switch (str) {
		//	case "R":
		//		return new HSRandomV1();
		//	case "M":
				return new MinSpaceStrategy();
		//	default:
		//		return new HSRandomV1();
		//}
	}
}
