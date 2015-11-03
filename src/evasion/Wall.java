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
}
