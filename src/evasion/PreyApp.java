package evasion;
import java.io.IOException;
import java.net.URI;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

import evasion.prey.PreyMove;

public class PreyApp {
	private static CountDownLatch messageLatch;
	
	private static String SENT_MESSAGE = "";
	
	private static String publisherEndpoint = "ws://localhost:1990",
				   		  hunterEndpoint = "ws://localhost:1991",
				   		  preyEndpoint = "ws://localhost:1992";
	    
	//static Session session;
    public static void main( String[] args )
    {        
        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            
            Endpoint prey = new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig arg1) {
					session.addMessageHandler(new MessageHandler.Whole<String>() {
					    public void onMessage(String message) {
					        System.out.println("Received message: "+message);
					        messageLatch.countDown();
					        
					        try {
					            SENT_MESSAGE = getWallsCommand();
								session.getBasicRemote().sendText(SENT_MESSAGE);
						        SENT_MESSAGE = getPositionsCommand();
					            session.getBasicRemote().sendText(SENT_MESSAGE);                        	
					            session.getBasicRemote().sendText(makeARandomMove());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}					    	
					    }
//			            SENT_MESSAGE = getWallsCommand();
//						session.getBasicRemote().sendText(SENT_MESSAGE);
//				        SENT_MESSAGE = getPositionsCommand();
//			            session.getBasicRemote().sendText(SENT_MESSAGE);                        	
//					    
					});
					
				}
            };
            client.connectToServer(prey, cec, new URI(preyEndpoint));
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
    
    public static String makeARandomMove() {
    	String action = "";
    	ObjectMapper mapper = new ObjectMapper();
    	ObjectNode node = mapper.createObjectNode();
        node.put("command", "M"); 
        String dir = new String();
        Random rand = new Random();
		int randomNum = rand.nextInt(9); //
		switch (randomNum) {
			case 0: dir = "NE"; break;//NE
			case 1: dir = "E"; break;//E
			case 2: dir = "SE"; break;//SE
			case 3: dir = "S"; break;//S
			case 4: dir = "SW"; break;//SW
			case 5: dir = "W"; break;//W
			case 6: dir = "NW"; break;//NW
			case 7: dir = "N"; break;//N
			case 8: dir = "NE"; break;//no move
			default: dir = "NE"; break;//NE
		}
		
		node.put("direction", dir);
		//System.out.println( "M," + dir);
		
        try {
			action = mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action;
    }
    

}
