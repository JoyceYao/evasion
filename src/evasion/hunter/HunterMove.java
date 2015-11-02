package evasion.hunter;

import org.json.JSONException;
import org.json.JSONObject;

import evasion.CardinalDirections;
import evasion.Move;
import evasion.Wall;

public class HunterMove extends Move {
	public Wall buildWall;
	public Wall teardownWall;
	
	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("Command", "M");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CardinalDirections d = CardinalDirections.getDirectionFromMove(this);
		if (d == CardinalDirections.NOMOVE) {
			
		} else {
			try {
				obj.put("direction", d);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return obj.toString();
	}
	
	public String moveToString() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("Command", "M");
		CardinalDirections d = CardinalDirections.getDirectionFromMove(this);
		if (d == CardinalDirections.NOMOVE) {
			
		} else {
			obj.put("direction", d);
		}
		return obj.toString();
	}

	public String buildWallToString() throws JSONException {
		if (buildWall == null) {
			return "";
		}
		JSONObject obj = new JSONObject();
		obj.put("Command", "B");
		
		JSONObject wallObj = new JSONObject();
		wallObj.put("leftx", buildWall.leftEnd.xloc);
		wallObj.put("lefty", buildWall.leftEnd.yloc);
		wallObj.put("rightx", buildWall.rightEnd.xloc);
		wallObj.put("righty", buildWall.rightEnd.yloc);
	
		CardinalDirections d = CardinalDirections.getDirectionOfWall(buildWall, this);
		if (d == CardinalDirections.NOMOVE) {
			
		} else {
			wallObj.put("direction", d);
		}
		obj.put("wall", wallObj);
		return obj.toString();
	}
	
	public String tearDownWallToString() throws JSONException {
		if (teardownWall == null) {
			return "";
		}
		JSONObject obj = new JSONObject();
		obj.put("Command", "D");
		obj.put("wallIndex", teardownWall.wallIndex);
		return obj.toString();
	}
	
}
