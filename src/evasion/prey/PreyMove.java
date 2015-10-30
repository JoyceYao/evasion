package evasion.prey;
import org.json.*;

import evasion.CardinalDirections;
import evasion.Move;
public class PreyMove extends Move {

	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("Command", "M");
		CardinalDirections d = CardinalDirections.getDirectionFromMove(this);
		if (d == CardinalDirections.NOMOVE) {
			
		} else {
			obj.put("direction", d);
		}
		return obj.toString();
	}
}
