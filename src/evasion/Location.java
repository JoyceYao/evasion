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
}
