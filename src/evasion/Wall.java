package evasion;

public class Wall {
	public Location leftEnd;
	public Location rightEnd;
	public WallOperation wop;
	public int wallIndex;
	public Orientation getOrientation() {
		if (leftEnd.xloc == rightEnd.xloc) {
			return Orientation.VERTICAL;
		}
		return Orientation.HORIZONTAL;
	};
	
	public Wall(){};
	
	public Wall(Location leftEnd, Location rightEnd, WallOperation wop, int wallIndex){
		this.leftEnd = leftEnd;
		this.rightEnd = rightEnd;
		this.wop = wop;
		this.wallIndex = wallIndex;
	}
		
	public int getLength() {
		if (getOrientation() == Orientation.HORIZONTAL) {
			return Math.abs(leftEnd.xloc - rightEnd.xloc);
		}  
		return Math.abs(leftEnd.yloc - rightEnd.yloc);
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
}
