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

	public static CardinalDirections getReverseDir(CardinalDirections dir) {
		switch(dir) {
			case N: return S;
			case S: return N;
			case E: return W;
			case W: return E;
			case NE: return SW;
			case SE: return NW;
			case NW: return SE;
			default: return NE;//SW
		}		
	}

	//As reflected from a horizontal wall
	public static CardinalDirections getHorizontalReflectedDir(CardinalDirections dir) {
		switch(dir) {
			case N: return S;
			case S: return N;
			case E: return E;//no reflection
			case W: return W;//no refl
			case NE: return SE;
			case SE: return NE;
			case NW: return SW;
			default: return NW;//SW
		}
	}

	//As reflected from a vertical wall
	public static CardinalDirections getVerticalReflectedDir(CardinalDirections dir) {
		switch(dir) {
			case N: return N;//no reflection
			case S: return S;//no refl
			case E: return W;
			case W: return E;
			case NE: return NW;
			case SE: return SW;
			case NW: return NE;
			default: return SE;//SW
		}
	}

	//As reflected from a horizontal wall
	public static CardinalDirections getReflectedDirFromOriginalDirAndFinalDelta(CardinalDirections odir, int dx, int dy) {
		switch(odir) {
			case NE: 
				if ( (dx == 0) && (dy == -1) ) {
					return NW;
				}
				if ( (dx == 1) && (dy == 0) ) {
					return SE;
				}
				return SW;
			case SE: 
				if ( (dx == 0) && (dy == 1) ) {
					return SW;
				}
				if ( (dx == 1) && (dy == 0) ) {
					return NE;
				}
				return NW;
			case NW: 
				if ( (dx == 0) && (dy == 1) ) {
					return NE;
				}
				if ( (dx == -1) && (dy == 0) ) {
					return SW;
				}
				return SE;
			default: //SW
				if ( (dx == 0) && (dy == 1) ) {
					return SE;
				}
				if ( (dx == -1) && (dy == 0) ) {
					return NW;
				}
				return NE;
		}
	}

	//going from a to ==> b is NE? NW?...
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
	
	public static Move getMoveFromCardinalDirections(Location loc, CardinalDirections cd){
		if(loc == null){ return null; }
		Move m = new Move();
		m.fromX = loc.xloc;
		m.fromY = loc.yloc;
		switch(cd) {
			case N: m.deltaX=0; m.deltaY=-1; break;
			case S: m.deltaX=0; m.deltaY=1; break;
			case E: m.deltaX=1; m.deltaY=0; break;// [1,0],
			case W: m.deltaX=-1; m.deltaY=0; break;// [-1,0],
			case NE: m.deltaX=1; m.deltaY=-1; break;// [1,-1],
			case NW: m.deltaX=-1; m.deltaY=-1; break;// [-1,-1],
			case SE: m.deltaX=1; m.deltaY=1; break;// [1,1],
			case SW: m.deltaX=-1; m.deltaY=1; break; //: [-1,1]
			case NOMOVE: m.deltaX=0; m.deltaY=0; break; //[0,0]
		}
		return m;
	}
}
		

