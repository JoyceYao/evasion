package evasion.prey;
import org.json.*;

import evasion.CardinalDirections;
import evasion.Move;
public class PreyMove extends Move {

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
}
