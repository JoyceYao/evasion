package evasion;

public class Location {
	public int xloc;
	public int yloc;
	
	public boolean isEqual(Location b) {
		if ( (xloc == b.xloc) && (yloc == b.yloc) ) {
			return true;
		}
		return false;
	}
	
	public Location(){}
	
	public Location(int xloc, int yloc){
		this.xloc = xloc;
		this.yloc = yloc;
	}
}
