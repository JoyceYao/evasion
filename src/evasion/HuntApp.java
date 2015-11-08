package evasion;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import evasion.connections.GameWithPlayerSocket;
import evasion.connections.GameWithPublisherSocket;
import evasion.connections.PlayerWebSocket;
import evasion.connections.PublisherWebSocket;
import evasion.hunter.HunterMove;
import evasion.hunter.strategies.AbsHunterStrategy;

public class HuntApp implements GameWithPublisherSocket, GameWithPlayerSocket{
    JSONParser parser = new JSONParser();

    WebSocketClient publClient = new WebSocketClient();
    WebSocketClient playerClient = new WebSocketClient();
    PublisherWebSocket publSocket = new PublisherWebSocket(this);
    PlayerWebSocket playerSocket = new PlayerWebSocket(this);
    boolean connectedToPlayer = false; 
    boolean connectedToPublisher = false; 

    static HuntApp Hunter;
    //public HunterEndpoint hep;
    public Board board;
    public AbsHunterStrategy strategy;
	private static CountDownLatch messageLatch;
	private static String SENT_MESSAGE = "";
	
	private static String publisherEndpoint = "ws://localhost:1990",
				   		  hunterEndpoint = "ws://localhost:1991",
				   		  preyEndpoint = "ws://localhost:1992";

	public static void main( String[] args ) {  
		Hunter = new HuntApp();
		
	}
	
	public HuntApp() {
		board = new Board();
		strategy = AbsHunterStrategy.getStrategy("W");
		//hep = 
        setUpConnection(hunterEndpoint, publisherEndpoint);
	}
	

    // Receives JSon object as string
    public void ReceivedMessageFromPlayerSocket(String message) {
        // This will update your game
       // parsePlayerMessage(message);
    } 

    public void ReceivedMessageFromPublisherSocket(String message) {
        // Received publisher message
        // Turn is over, Time to make new turn
        parsePublisherMessage(message);
        // USE this to decide your move
        playerMakeMove();
    }
    
    public void playerMakeMove() {
    	//Hunter moves immediately
        HunterMove hm = MakeDecision();
    	String bw = hm.buildWallToString();
    	if ( (bw != null) && !bw.trim().equals("") ) {
    		sendDecision(bw);
    	}
    	
    	String tw = hm.tearDownWallToString();
    	if ( (tw != null) && !tw.trim().equals("") ) {
    		sendDecision(tw);
    	}
        
    	String mv = hm.moveToString();
    	if ( (mv != null) && !mv.trim().equals("") ) {
    		sendDecision(mv);
    	}
    }

    private HunterMove MakeDecision() {
		// TODO Auto-generated method stub
    	return strategy.makeAMove(board);
	}

    //org.json.simple
	public void sendDecision(JSONObject decision) {
        playerSocket.sendMessage(decision.toJSONString());
    }

	//org.json
	public void sendDecision(org.json.JSONObject decision) {
        playerSocket.sendMessage(decision.toString());
    }

	public void sendDecision(String decision) {
        playerSocket.sendMessage(decision);
    }


    public void ConnectionMadeWithPlayerSocket() {
        this.connectedToPlayer = true;
    }

    public void ConnectionMadeWithPublisherSocket() {
        this.connectedToPublisher = true;
        //wait for the connection to make the first HunterMove
        Hunter.playerMakeMove();
    }


    public void setUpConnection(String playerDest, String publDest) {
        try {
            publClient.start();
            playerClient.start();
            URI publURI = new URI(publDest);
            URI playerURI = new URI(playerDest);
            ClientUpgradeRequest publRequest = new ClientUpgradeRequest();
            ClientUpgradeRequest playerRequest = new ClientUpgradeRequest();
            publClient.connect(publSocket, publURI, publRequest);
            playerClient.connect(playerSocket, playerURI, playerRequest);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

/**
    public synchronized void parsePlayerMessage(String message) {
        System.out.println(message);
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                Object obj = parser.parse(message);
                jsonObject = (JSONObject) obj;
            }

            catch (ParseException pe) {
                System.out.println(pe);
            }

            String commandValue = (String) jsonObject.get("command");

            if (commandValue.equals("W")) {
                JSONArray walls = (JSONArray) jsonObject.get("walls");
                ArrayList<Wall> readInWalls = parseJSONArrayWalls(walls);

                updateWalls(readInWalls);
            }

            else if (commandValue.equals("P")) {
                JSONArray hunterCoordinates = (JSONArray) jsonObject.get("hunter");
                Point hunterPoint = parseJSONArrayCoordinates(hunterCoordinates);

                JSONArray preyCoordinates = (JSONArray) jsonObject.get("prey");
                Point preyPoint = parseJSONArrayCoordinates(preyCoordinates);

                updatePositions(hunterPoint, preyPoint);
            }

            else {
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
**/
    

    public synchronized void parsePublisherMessage(String message) {
        message.replace("false", "\"false\"");
        message.replace("true", "\"true\"");
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                Object obj = parser.parse(message);
                jsonObject = (JSONObject) obj;
            }

            catch (ParseException pe) {
                System.out.println(pe);
            }

            JSONArray hunterCoordinates = (JSONArray) jsonObject.get("hunter");
            //Point hunterPoint = parseJSONArrayCoordinates(hunterCoordinates);
            System.out.println(hunterCoordinates);
            board._hunter.hl.xloc = Integer.parseInt(hunterCoordinates.get(0).toString());
            board._hunter.hl.yloc = Integer.parseInt(hunterCoordinates.get(1).toString());
            System.out.println("hx: " + board._hunter.hl.xloc);

            
            JSONArray preyCoordinates = (JSONArray) jsonObject.get("prey");
            //Point preyPoint = parseJSONArrayCoordinates(preyCoordinates);
            System.out.println(preyCoordinates);
            board._prey.pl.xloc = Integer.parseInt(preyCoordinates.get(0).toString());
            board._prey.pl.yloc = Integer.parseInt(preyCoordinates.get(1).toString());

            System.out.println("px: " + board._prey.pl.xloc);
            
            String huntDirection = (String) jsonObject.get("hunterDir");
            board._hunter.hunterDirection = CardinalDirections.getCardinalFromString(huntDirection);
            
            //Point hunterDir = serverDirectionToPoint(huntDirection);


            long timeL = (Long) jsonObject.get("time");
            board.time = (int) timeL;

            boolean gameOver = (Boolean) jsonObject.get("gameover");
            if (gameOver) {
            	System.out.println("No way, Hannan!!!");
            	return;
            }
            
        }

        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
	public Location jsonArrayToLocation(JSONArray coords) {
    	Location ln = new Location();
    	ln.xloc = Integer.parseInt(coords.get(0).toString());
    	ln.yloc = Integer.parseInt(coords.get(1).toString());
    	return ln;
    }
}
