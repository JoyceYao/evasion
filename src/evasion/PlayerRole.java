package evasion;

public enum PlayerRole {
	PREY,
	HUNTER;
	public static PlayerRole getRole(String str) {
		if (str.equalsIgnoreCase("H")) {
			return HUNTER;
		} else {
			return PREY;
		}
	}
}

