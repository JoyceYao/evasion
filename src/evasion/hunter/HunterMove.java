package evasion.hunter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import evasion.Board;
import evasion.CardinalDirections;
import evasion.Location;
import evasion.Move;
import evasion.Wall;

public class HunterMove extends Move {
	public Wall buildWall;
	public List<Wall> teardownWalls;
	
//	public String toString() {
//		JSONObject obj = new JSONObject();
//		obj.put("Command", "M");
//		CardinalDirections d = CardinalDirections.getDirectionFromMove(this);
//		if (d == CardinalDirections.NOMOVE) {
//			
//		} else {
//			obj.put("direction", d);
//		}
//		return obj.toString();
//	}
	
	public String moveToString() {
		
    	ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
		HashMap<String, Object>hm = new HashMap<String, Object>();
		hm.put("command", "M");
		String action = "";
		try {
			action = writer.writeValueAsString(hm);
			System.out.println("action: " + action);
			//action = mapper.writeValueAsString(node1);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
	}

//	public String buildWallToString() {
//		if (buildWall == null) {
//			return "";
//		}
//		JSONObject obj = new JSONObject();
//		obj.put("Command", "B");
//		
//		JSONObject wallObj = new JSONObject();
//		wallObj.put("leftx", buildWall.leftEnd.xloc);
//		wallObj.put("lefty", buildWall.leftEnd.yloc);
//		wallObj.put("rightx", buildWall.rightEnd.xloc);
//		wallObj.put("righty", buildWall.rightEnd.yloc);
//	
//		CardinalDirections d = CardinalDirections.getDirectionOfWall(buildWall, this);
//		if (d == CardinalDirections.NOMOVE) {
//			
//		} else {
//			wallObj.put("direction", d);
//		}
//		obj.put("wall", wallObj);
//		return obj.toString();
//	}
	
	public String tearDownWallToString() {
		if (teardownWalls == null || teardownWalls.isEmpty()) {
			return "";
		}

	   	ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
			HashMap<String, Object>hm = new HashMap<String, Object>();
			hm.put("command", "D");
			List<Integer> wids = new LinkedList<Integer>();
			for (Wall aw : teardownWalls) {
				wids.add(aw.wallIndex);
			}
			hm.put("wallIndex", wids);
			String action = "";
			try {
				action = writer.writeValueAsString(hm);
				System.out.println("action: " + action);
				//action = mapper.writeValueAsString(node1);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return action;
	}
	
	
	public String buildWallToString() {
    	if (buildWall == null || buildWall.leftEnd == null) {
    		return "";
    	}
    	System.out.println("building wall command");
    	ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
		HashMap<String, Object>hm = new HashMap<String, Object>();
    	HashMap<String, String>wall = new HashMap<String, String>();
    			
//		  "wall" : {
//	    "length" : "200",
//	    "direction" : "W"
//	  },
//	  "command" : "B"

		Location here = new Location();
		here.xloc = this.fromX; here.yloc = this.fromY;
		CardinalDirections wdir = buildWall.getWallDirFromLocation( here);
    	String sdir = "";
		switch (wdir) {
			case N: 
		    	if (buildWall.getLength() >= Board.MAX_Y) { 
		    		sdir = "V";
		    	} else {
		    		sdir = "N";
		    		wall.put("length", "" + buildWall.getLength());
		    	}
				break;
			case S: 
		    	if (buildWall.getLength() >= Board.MAX_Y) { 
		    		sdir = "V";
		    	} else {
		    		sdir = "S";
		    		wall.put("length", "" + buildWall.getLength());
		    	}
				break;
			case W: 
		    	if (buildWall.getLength() >= Board.MAX_X) { 
		    		sdir = "H";
		    	} else {
		    		sdir = "W";
		    		wall.put("length", "" + buildWall.getLength());
		    	}
				sdir = "W"; 
				break;
			default: 
		    	if (buildWall.getLength() >= Board.MAX_X) { 
		    		sdir = "H";
		    	} else {
		    		sdir = "E";
		    		wall.put("length", "" + buildWall.getLength());
		    	}
				break;
		}
		
		wall.put("direction", sdir);
		
		hm.put("command", "B");
		hm.put("wall", wall);

		String action = "";
		try {
			action = writer.writeValueAsString(hm);
			System.out.println("action: " + action);
			//action = mapper.writeValueAsString(node1);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
		
	}
	
}
