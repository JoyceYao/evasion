package evasion;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.BitSet;

import org.json.JSONObject;

import evasion.hunter.HunterMove;
import evasion.hunter.strategies.AbsHunterStrategy;
import evasion.hunter.strategies.HSRandomV1;
import evasion.prey.PreyMove;
import evasion.prey.strategies.AbsPreyStrategy;

public class Main {

	TCPClient tcpClient;
	String teamName = "mv_cly2";
	PlayerRole role; //Hunter/Prey
	Board board;
	AbsHunterStrategy hstrategy;
	AbsPreyStrategy pstrategy;
	
	final String host = "localhost";
	int port = 1337; //Port
	final static int ARGS_LEN = 6;
	//$java -jar evasion.jar -rH -sR -tmv_cly -p1337 -N3 -M10 
	public static void main(String[] args) {
	
		Main m = new Main();
		m.board = new Board();
		m.tcpClient = new TCPClient();
		
		BitSet bs = new BitSet(ARGS_LEN);
		
		if (args.length != ARGS_LEN) {
			System.out.println("Not all initialization command line arguments were specified. Halting");
			System.exit(-1);
		}
		for (int i = 0; i < args.length; i++) {
			switch(args[i].substring(0, 2)) {
				case "-s": //strategy
					if (m.role == null) {
						System.out.println("Player Role needs to be specified before strategy");
						System.exit(-1);
					}
					if (m.role == PlayerRole.HUNTER) {
						m.hstrategy = AbsHunterStrategy.getStrategy(args[i].substring(2, 3));
					} else {
						m.pstrategy = AbsPreyStrategy.getStrategy(args[i].substring(2, 3));
					}
					bs.set(0);
					break;
				case "-r": //role: Prey or Hunter
					m.role = PlayerRole.getRole(args[i].substring(2, 3));
					bs.set(1);
					break;
				case "-t": //teamname
					m.teamName = args[i].substring(2);
					bs.set(2);
					break;
				case "-p": //port
					m.port = Integer.parseInt(args[i].substring(2));
					bs.set(3);
					break;
				case "-N": //H may create a wall not more frequently than every N time steps
					m.board.N = Integer.parseInt(args[i].substring(2));
					bs.set(4);
					break;
				case "-M": //maximum number of walls at any time
					m.board.M = Integer.parseInt(args[i].substring(2));
					bs.set(5);
					break;
				default:
					break;
			}
		}
		if (bs.cardinality() != ARGS_LEN) {
			System.out.println("Not all initialization parameters supplied. Halting");
			System.exit(-1);			
		}
		
		try {
			m.tcpClient.startTCP(m.host, m.port);
			m.run();
			m.tcpClient.closeTCP();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void run() throws IOException{
		boolean playing = true;
		
		while(playing){
			String input = tcpClient.read();
//			String input = tcpClient.readBuffered();
			System.out.println("Receive Input:" + input);
			String[] line = input.split("\n");
			int lastIdx = line.length-1;
		
			switch(line[lastIdx]){
				case "TEAM": tcpClient.write(getTeamName()); break;
			
				case "MOVE": tcpClient.write(getNextMove(line)); break;
				
				case "END": playing = false; break;
				
			}
		}
	}	
	
	
	private String getTeamName(){
		return teamName;
	}

	private String getNextMove(String[] line){
		JSONObject jsonObj = new JSONObject(line);
		// store other players' move to my board
//		for(int i=0; i<line.length-1; i++){
//			System.out.println("Move i: " + i + "; line[i]: " + line[i] );
//			String[] otherPLayersMove = line[i].split(" ");
//			if(otherPLayersMove.length < 3){ continue; }
//			String playerName = otherPLayersMove[0];
//			int x = Integer.parseInt(otherPLayersMove[1]);
//			int y = Integer.parseInt(otherPLayersMove[2]);
////			board.addPrevMove(Move.createOthersMove(i+1, x, y));
//		}
		
		
		//I am a Hunter, so I get a PREY Move
		if (role == PlayerRole.HUNTER) {
			// {}
			PreyMove pm = (PreyMove) CardinalDirections.getDeltaMoveFromCardinalString(
					jsonObj.getString("direction"));
			board.addPreyMove(pm);
		} else {//I get a Hunter Move
			// {}
			HunterMove hm = (HunterMove) CardinalDirections.getDeltaMoveFromCardinalString(
					jsonObj.getString("direction"));
					//jsonObj.getJSONObject(arg0)
			board.addHunterMove(hm);			
		}
		
		// compute next move and add myMove to my board
		if (role == PlayerRole.HUNTER) {
			HunterMove hMove = hstrategy.makeAMove(board);
			System.out.println("making move=" + hMove.toString());
			board.addHunterMove(hMove);
			return hMove.toString();
		} else {
			PreyMove pMove = pstrategy.makeAMove(board);
			System.out.println("making move=" + pMove.toString());			
			board.addPreyMove(pMove);
			return pMove.toString();
		}
	}
}
	