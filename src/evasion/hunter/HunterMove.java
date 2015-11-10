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


	
	public String tearDownWallToString() {
		if (teardownWalls == null || teardownWalls.isEmpty()) {
			return null;
		}

	   	ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
			HashMap<String, Object>hm = new HashMap<String, Object>();
			hm.put("command", "D");
			List<Integer> wids = new LinkedList<Integer>();
			for (Wall aw : teardownWalls) {
				wids.add(aw.wallIndex);
			}
			hm.put("wallIds", wids);
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
    		return null;
    	}
    	//System.out.println("building wall command");
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
			//System.out.println("action: " + action);
			//action = mapper.writeValueAsString(node1);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
		
	}
	
	
	/**
	 * BD command build and delete walls in one command
	 * @return
	 */
	public String BDWallToString() {
    	if (buildWall == null || buildWall.leftEnd == null) {
    		return null;
    	}
		if (teardownWalls == null || teardownWalls.isEmpty()) {
			return null;
		}
    	
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
		
		hm.put("command", "BD");
		hm.put("wall", wall);

		List<Integer> wids = new LinkedList<Integer>();
		for (Wall aw : teardownWalls) {
			wids.add(aw.wallIndex);
		}
		hm.put("wallIds", wids);
		
		String action = "";
		try {
			action = writer.writeValueAsString(hm);
			//System.out.println("action: " + action);
			//action = mapper.writeValueAsString(node1);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
	}
	
}
