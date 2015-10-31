package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONWriter;

public class GameClientHandler implements Runnable {
	Socket cSocket;
	BufferedReader in;
	PrintWriter out;
	boolean isHunter = false;
	HashMap<String, String> gameParams;
	
	GameClientHandler(Socket sc) throws IOException {
		cSocket = sc;
	    out = new PrintWriter(cSocket.getOutputStream(), true);
	    in = new BufferedReader(
	        new InputStreamReader(cSocket.getInputStream()));
	}
	
	void setParams(HashMap<String, String> hm) {
		gameParams = hm;
	}
	
	void updateRole(String str) {
		if (gameParams.get("HUNTER").equalsIgnoreCase(str.trim()) ) {
			isHunter = true;
		}
	}
	
	@Override
	public void run() {
		String line;
		//ask for team name
		//out.println("TEAM");
		while (true) {
			try {
				line = in.readLine();
				switch (line) {
					case "TEAM":
						line = in.readLine();
						updateRole(line);
						break;
					case "MOVE":
						//parse JSON
						//if hunter, make the move in the board
						//send move to prey
						break;
					case "END":
						cSocket.close();
						break;
					default:
						break;
				}
			} catch (IOException e) {
				System.out.println("io exception " + e);
			}
		}
	}

}
