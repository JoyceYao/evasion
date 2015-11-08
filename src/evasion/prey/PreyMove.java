package evasion.prey;
import org.json.*;

import evasion.CardinalDirections;
import evasion.Move;
public class PreyMove extends Move {

	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("command", "M");
			CardinalDirections d = CardinalDirections.getDirectionFromMove(this);
			if (d == CardinalDirections.NOMOVE) {
			
			} else {
				obj.put("direction", d);
			}
			return obj.toString();
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
