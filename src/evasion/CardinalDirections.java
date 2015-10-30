package evasion;

import evasion.hunter.HunterMove;

public enum CardinalDirections {
	N, //: [0,-1],
	S, //: [0,1],
	E, // [1,0],
	W, // [-1,0],
	NE, // [1,-1],
	NW, // [-1,-1],
	SE, // [1,1],
	SW,  //: [-1,1]
	NOMOVE; //[0,0]
	
	public static CardinalDirections getDirectionFromMove(Move m) {
		switch(m.deltaX) {
			case -1:
				if (m.deltaY == -1) {
					return NW;
				}
				if (m.deltaY == 0) {
					return W;
				}
				if (m.deltaY == 1) {
					return SW;
				}
				break;
			case 0:
				if (m.deltaY == -1) {
					return N;
				}
//				if (m.deltaY == 0) {
//					return W;
//				}
				if (m.deltaY == 1) {
					return S;
				}
				break;
			case 1:
				if (m.deltaY == -1) {
					return NE;
				}
				if (m.deltaY == 0) {
					return E;
				}
				if (m.deltaY == 1) {
					return SE;
				}
				break;
			default:
				break;
		}
		return NOMOVE;
	}
	
	public static CardinalDirections getDirectionOfWall(Wall w, HunterMove hm) {
		Location ln = new Location();
		ln.xloc = hm.fromX; ln.yloc = hm.fromY;
		if ( ln.isEqual(w.leftEnd) ) {
			return getCardinalDirsFromLocns(ln, w.rightEnd);
		}
		
		return getCardinalDirsFromLocns(ln, w.leftEnd);
	}

	//from a to b is NE? NW?...
	public static CardinalDirections getCardinalDirsFromLocns(Location a, Location b) {
		int deltaX = b.xloc - a.xloc;
		int deltaY = b.yloc - a.yloc;
		switch(deltaX) {
			case -1:
				if (deltaY == -1) {
					return NW;
				}
				if (deltaY == 0) {
					return W;
				}
				if (deltaY == 1) {
					return SW;
				}
				break;
			case 0:
				if (deltaY == -1) {
					return N;
				}
	//			if (m.deltaY == 0) {
	//				return W;
	//			}
				if (deltaY == 1) {
					return S;
				}
				break;
			case 1:
				if (deltaY == -1) {
					return NE;
				}
				if (deltaY == 0) {
					return E;
				}
				if (deltaY == 1) {
					return SE;
				}
				break;
			default:
				break;
		}
		return NOMOVE;
	}
	
	public static Move getDeltaMoveFromCardinalString(String str) {
		Move m = new Move();
		switch(getCardinalFromString(str)) {
			case N: m.deltaX = 0; m.deltaY = -1; return m;
			case S: m.deltaX = 0; m.deltaY = 1; return m;
			case E: m.deltaX = 1; m.deltaY = 0; return m;
			case W: m.deltaX = -1; m.deltaY = 0; return m;
			case NE: m.deltaX = 1; m.deltaY = -1; return m;
			case SE: m.deltaX = 1; m.deltaY = 1; return m;
			case NW: m.deltaX = -1; m.deltaY = -1; return m;
			case SW: m.deltaX = -1; m.deltaY = 1; return m;
			default: m.deltaX = 0; m.deltaY = 0; return m;
		}
	}
	public static CardinalDirections getCardinalFromString(String str) {
		switch(str) {
			case "N": return CardinalDirections.N;
			case "S": return CardinalDirections.S;
			case "E": return CardinalDirections.E;
			case "W": return CardinalDirections.W;
			case "NE": return CardinalDirections.NE;
			case "SE": return CardinalDirections.SE;
			case "NW": return CardinalDirections.NW;
			case "SW": return CardinalDirections.SW;
			default: return CardinalDirections.NOMOVE;
		}
	}
}
		

