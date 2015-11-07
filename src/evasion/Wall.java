package evasion;

public class Wall {
	public static int runningWallCount = 0;
	public Location leftEnd = new Location();
	public Location rightEnd = new Location();
//	public int wallLength = 0;
	
	public WallOperation wop;
	public int wallIndex;
	
	public Orientation getOrientation() {
		if (leftEnd.xloc == rightEnd.xloc) {
			return Orientation.VERTICAL;
		}
		return Orientation.HORIZONTAL;
	};
	
	public int getLength() {
		if (getOrientation() == Orientation.HORIZONTAL) {
			return Math.abs(leftEnd.xloc - rightEnd.xloc);
		}  
		return Math.abs(leftEnd.yloc - rightEnd.yloc);
	}

	public Wall() {
		wallIndex = runningWallCount;
		runningWallCount++;
	}
	
	public CardinalDirections getWallDirFromLocation(Location a) {
		Orientation on = getOrientation();
		switch (on) {
		case HORIZONTAL://a is in the neighborhood of leftEnd then E
			if ( (Math.abs(a.xloc - leftEnd.xloc) <= 1) && (Math.abs(a.yloc - leftEnd.yloc) <=1)) {
				return CardinalDirections.E;
			}
			return CardinalDirections.W;
		default:
			if ( (Math.abs(a.xloc - leftEnd.xloc) <= 1) && (Math.abs(a.yloc - leftEnd.yloc) <=1)) {
				if (leftEnd.yloc < rightEnd.yloc) {
					return CardinalDirections.S;					
				}
				return CardinalDirections.N;
			}
			return CardinalDirections.N;
		}
	}
	
	public boolean isSame(Wall w) {
		if (this.leftEnd.isEqual(w.leftEnd)) {
			if (this.rightEnd.isEqual(w.rightEnd)) {
				if (this.wallIndex == w.wallIndex) {
					return true;
				}
			}
		}
		return false;
	}
}
