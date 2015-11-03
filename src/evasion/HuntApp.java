package evasion;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import evasion.hunter.HunterMove;
import evasion.hunter.strategies.AbsHunterStrategy;

public class HuntApp {
	private static CountDownLatch messageLatch;
	
	private static String SENT_MESSAGE = "";
	
	private static String publisherEndpoint = "ws://localhost:1990",
				   		  hunterEndpoint = "ws://localhost:1991",
				   		  preyEndpoint = "ws://localhost:1992";
	    
    public static void main( String[] args )
    {        
        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            Board board = new Board();
            AbsHunterStrategy hstrategy = AbsHunterStrategy.getStrategy("W");
            Endpoint hunter = new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig arg1) {
					try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            public void onMessage(String message) {
                                System.out.println("Received message: "+message);
                                messageLatch.countDown();
                            }
                        });
                        for(;;) {
                        	//session.getBasicRemote().sendText(makeAMove(board, hstrategy));
                        	session.getBasicRemote().sendText(makeARandomMove());
                            SENT_MESSAGE = getPositionsCommand();
                            session.getBasicRemote().sendText(SENT_MESSAGE);
                            SENT_MESSAGE = getWallsCommand();
                            session.getBasicRemote().sendText(SENT_MESSAGE);
                        	
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
					
				}
            };
            client.connectToServer(hunter, cec, new URI(hunterEndpoint));
            messageLatch.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        
    public static String getPositionsCommand() {
    	return runCommand("P");
    }
    
    public static String getWallsCommand() {
    	return runCommand("W");
    }
    
    public static String runCommand(String command) {
    	String action = "";
    	ObjectMapper mapper = new ObjectMapper();
    	ObjectNode node = mapper.createObjectNode();
        node.put("command", command); 
        try {
			action = mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
    }

    public static String makeAMove(Board b, AbsHunterStrategy s) {
    	System.out.println("makeAMove[0]");
    	HunterMove hm = s.makeAMove(b);
    	ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
		String action = "";
		try {
			action = writer.writeValueAsString(hm);
			System.out.println("action: " + action);
			//action = mapper.writeValueAsString(node1);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("makeAMove[1] action=" + action);
        return action;
    	
    }
    
    public static String makeARandomMove() {
    	System.out.println("makeARandomMove[0]");
    	String action = "";
    	ObjectMapper mapper = new ObjectMapper();
        Random rand = new Random();
		int randomNum = rand.nextInt(3); //0--B, 1--M, 2--D

		//B {
//		  "wall" : {
//		    "length" : "200",
//		    "direction" : "W"
//		  },
//		  "command" : "B"
//		}
		if (randomNum == 0) {
			ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
			
			HashMap<String, Object> hm = new HashMap<String, Object>();
	    	HashMap<String, String>wall = new HashMap<String, String>();
	    	int wLen = rand.nextInt(300) + 1;
			randomNum = rand.nextInt(4);
			String dir = new String();//
			switch (randomNum) {
				case 0: dir = "N"; break;//NE
				case 1: dir = "E"; break;//SE
				case 2: dir = "S"; break;//SW
				case 3: dir = "W"; break;//NW
				default: dir = "E"; break;//SE
			}
			wall.put("length", "" + wLen);
			wall.put("direction", dir);
			hm.put("command", "B");
			hm.put("wall", wall);
			try {
				action = writer.writeValueAsString(hm);
				System.out.println("action: " + action);
				//action = mapper.writeValueAsString(node1);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("makeARandomMove[1] action=" +action);
	        return action;
		}

    	//M {command: "M", direction: "SE"}
//		if (randomNum == 1) {
			ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
   	    	//ObjectNode node2 = mapper.createObjectNode();
			HashMap<String, String> hm = new HashMap();
	        hm.put("command", "M"); 
//	        String dir = new String();
//			randomNum = rand.nextInt(9); //
//			
//			switch (randomNum) {
//				case 0: dir = "NE"; break;//NE
//				case 1: dir = "SE"; break;//SE
//				case 2: dir = "SW"; break;//SW
//				case 3: dir = "NW"; break;//NW
//				default: dir = "SE"; break;//SE
//			}
//			hm.put("direction", dir);
			try {
				action = writer.writeValueAsString(hm);
				System.out.println("action: " + action);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return action;
//		}  
		/**
		else {//D {command: "D", index: 2} // delete wall at index 2
			ObjectWriter writer=mapper.writerWithDefaultPrettyPrinter();
   	    	//ObjectNode node2 = mapper.createObjectNode();
			HashMap<String, String> hm = new HashMap();
	        hm.put("command", "D"); 
	        randomNum = rand.nextInt(5); // delete a random wall
	        hm.put("index", ""+randomNum);
			try {
				action = writer.writeValueAsString(hm);
				System.out.println("action: " + action);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return action;	        
		}
		*/
		
    }

}
