package server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.BitSet;

import evasion.Board;


public class ServerMain {
	static final int ARGS_LEN = 4;
	public Board board;
	public ServerListener sl = new ServerListener();
	public static void main(String[] args) {
		ServerMain s = new ServerMain();
		s.board = new Board();
		s.sl = new ServerListener();
		int port = 1337;
		
		BitSet bs = new BitSet(ARGS_LEN);
		
		System.out.println("bs.cardinality()=" + bs.cardinality());
		
		if (args.length < ARGS_LEN) {
			System.out.println("Not all initialization command line arguments were specified. Halting");
			System.exit(-1);
		}
		for (int i = 0; i < args.length; i++) {
			switch(args[i].substring(0, 2)) {
//				case "-s": //strategy
//					if (m.role == null) {
//						System.out.println("Player Role needs to be specified before strategy");
//						System.exit(-1);
//					}
//					if (m.role == PlayerRole.HUNTER) {
//						m.hstrategy = AbsHunterStrategy.getStrategy(args[i].substring(2, 3));
//					} else {
//						m.pstrategy = AbsPreyStrategy.getStrategy(args[i].substring(2, 3));
//					}
//					bs.set(0);
//					break;
//				case "-r": //role: Prey or Hunter
//					m.role = PlayerRole.getRole(args[i].substring(2, 3));
//					bs.set(1);
//					break;
				case "-p": //port
					port = Integer.parseInt(args[i].substring(2));
					bs.set(0);
					break;
				case "-N": //H may create a wall not more frequently than every N time steps
					s.board.N = Integer.parseInt(args[i].substring(2));
					bs.set(1);
					break;
				case "-M": //maximum number of walls at any time
					s.board.M = Integer.parseInt(args[i].substring(2));
					bs.set(2);
					break;
				case "-H": //hunter teamname
					s.board.h.name = args[i].substring(2);
					bs.set(3);
					break;
				default:
					break;
			}
		}
		if (bs.cardinality() != ARGS_LEN) {
			System.out.println("Not all initialization parameters supplied. Halting");
			System.exit(-1);			
		}
		
		System.out.println("Starting server");
		try {
			s.sl.startTCP(port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
