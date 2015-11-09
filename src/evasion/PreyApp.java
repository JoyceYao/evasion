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
import evasion.prey.PreyMove;
import evasion.prey.strategies.AbsPreyStrategy;

public class PreyApp implements GameWithPublisherSocket, GameWithPlayerSocket{
    JSONParser parser = new JSONParser();

    WebSocketClient publClient = new WebSocketClient();
    WebSocketClient playerClient = new WebSocketClient();
    PublisherWebSocket publSocket = new PublisherWebSocket(this);
    PlayerWebSocket playerSocket = new PlayerWebSocket(this);
    boolean connectedToPlayer = false; 
    boolean connectedToPublisher = false; 

    static PreyApp Prey;
    public Board board;
    public AbsPreyStrategy strategy;
	
	private static String publisherEndpoint = "ws://localhost:1990",
				   		  hunterEndpoint = "ws://localhost:1991",
				   		  preyEndpoint = "ws://localhost:1992";

	public static void main( String[] args ) {  
		Prey = new PreyApp();
	}
	
	public PreyApp() {
		board = new Board();
		strategy = AbsPreyStrategy.getStrategy("R");
        setUpConnection(preyEndpoint, publisherEndpoint);
	}
	
	public static JSONObject GetPositions(){
		JSONObject obj = new JSONObject();
		obj.put("command", "P");
		return obj;
	}
	
	public static JSONObject GetWalls(){
		JSONObject obj = new JSONObject();
		obj.put("command", "W");
		return obj;
	}

    public void sendPWToPlayerServer() {
        JSONObject positions = GetPositions();
        playerSocket.sendMessage(positions.toJSONString());
        
        JSONObject walls = GetWalls();
        playerSocket.sendMessage(walls.toJSONString());
    }

    // Receives JSon object as string
    public void ReceivedMessageFromPlayerSocket(String message) {
        // This will update your game
       parsePlayerMessage(message);
    }

    public void ReceivedMessageFromPublisherSocket(String message) {
        // Received publisher message
        // Turn is over, Time to make new turn
        parsePublisherMessage(message);
        // USE this to decide your move
        playerMakeMove();
    }
    
    public void playerMakeMove() {
    	if (board.time % 2 == 1) {
            PreyMove pm = MakeDecision();
        	String mv = pm.toString();
        	if ( (mv != null) && !mv.equals("") ) {
        		sendDecision(mv);
        	}
    	}
    }
    public void playerMakeMoveResume() {
        PreyMove pm = MakeDecision();
    	String mv = pm.toString();
    	if ( (mv != null) && !mv.equals("") ) {
    		sendDecision(mv);
    	}
    }

    private PreyMove MakeDecision() {
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
        sendPWToPlayerServer();
    }

    public void ConnectionMadeWithPublisherSocket() {
        this.connectedToPublisher = true;
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
                updateWalls(walls);
            } else if (commandValue.equals("P")) {
                JSONArray hunterCoordinates = (JSONArray) jsonObject.get("hunter");
                board._hunter.hl = jsonArrayToLocation(hunterCoordinates);
            }
            if ( (board._hunter.hl.xloc != 0) && (board._hunter.hl.yloc != 0) ) {
            	System.out.println("received player mesg");
            	playerMakeMoveResume();//resume a waiting game or startoff a game when board.time not known
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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


            
            JSONArray preyCoordinates = (JSONArray) jsonObject.get("prey");
            System.out.println(preyCoordinates);
            if (! board._prey.pl.isEqual(jsonArrayToLocation(preyCoordinates))){
            	System.out.println("What is this Hannan?? You are telling me that I am not where I am, huh?");
            }
            board._prey.pl = jsonArrayToLocation(preyCoordinates);
            //System.out.println("px: " + board._prey.pl.xloc);
            
            JSONArray hunterCoordinates = (JSONArray) jsonObject.get("hunter");
            //System.out.println(hunterCoordinates);
            board._hunter.hl = jsonArrayToLocation(hunterCoordinates);
            //System.out.println("hx: " + board._hunter.hl.xloc);
            
            String huntDirection = (String) jsonObject.get("hunterDir");
            board._hunter.hunterDirection = CardinalDirections.getCardinalFromString(huntDirection);

            long timeL = (Long) jsonObject.get("time");
            board.time = (int) timeL;
            boolean gameOver = (Boolean) jsonObject.get("gameover");
            if (gameOver) {
            	System.out.println("No way, Hannan!!!");
            	return;
            }
            
            updateWalls((JSONArray)jsonObject.get("walls"));

//            clearBoardWalls();
//            JSONArray walls = (JSONArray) jsonObject.get("wall");
//            for (Object wobj : walls) {
//            	JSONObject jobj = (JSONObject) wobj;
//            	int idx = (int)jobj.get("id");
//            	Location stPos = jsonArrayToLocation((JSONArray)jobj.get("position"));
//            	int len = (int) jobj.get("length");
//            	String dir = (String)jobj.get("direction");
//            	addWallToBoard(idx, stPos, len, dir);
//            }
        }

        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    private void updateWalls(JSONArray walls) {
        clearBoardWalls();
        for (Object wobj : walls) {
        	JSONObject jobj = (JSONObject) wobj;
        	long idxL = (Long)jobj.get("id");
        	int idx = (int) idxL;
        	Location stPos = jsonArrayToLocation((JSONArray)jobj.get("position"));
        	long lenL = (Long) jobj.get("length");
        	int len = (int) lenL;
        	String dir = (String)jobj.get("direction");
        	addWallToBoard(idx, stPos, len, dir);
        }
    }
    
    private void clearBoardWalls() {
        board._walls.clear();
        Wall.runningWallCount = 0;
    }
//    
    private void addWallToBoard(int idx, Location stPos, int len, String dir) {
	// TODO Auto-generated method stub
    	Wall aw = new Wall();
    	aw.wallIndex = idx;
    	Location vertex1 = stPos;
    	Location vertex2 = new Location();
    	//aw.leftEnd = stPos;
    	CardinalDirections cd = CardinalDirections.getCardinalFromString(dir);
    	switch (cd) {
    	case N:
    		vertex2.xloc = vertex1.xloc; vertex2.yloc = vertex1.yloc - len;
    		break;
    	case S:
    		vertex2.xloc = vertex1.xloc; vertex2.yloc = vertex1.yloc + len;
    		break;
    	case E:
    		vertex2.xloc = vertex1.xloc+len; vertex2.yloc = vertex1.yloc;
    		break;
    	default: //W
    		vertex2.xloc = vertex1.xloc-len; vertex2.yloc = vertex1.yloc;
    		break;
    	}
		aw.leftEnd = vertex1; aw.rightEnd = vertex2;
		board._walls.add(aw);
    }
//
    
	public Location jsonArrayToLocation(JSONArray coords) {
    	Location ln = new Location();
    	ln.xloc = Integer.parseInt(coords.get(0).toString());
    	ln.yloc = Integer.parseInt(coords.get(1).toString());
    	return ln;
    }
/**    
    public ArrayList<Wall> parseJSONArrayWalls(JSONArray walls) {
        ArrayList<Wall> returnWalls = new ArrayList<Wall>();
        //Update walls count
        if (walls != null) {
            for (int id = 0; id < walls.size(); id++) {
                JSONObject currentWallJsonObj = (JSONObject) walls.get(id);

                long currentWallLengthL = (Long) currentWallJsonObj.get("length");
                int currentWallLength = (int) currentWallLengthL;

                JSONArray currentWallCoordinates = (JSONArray) currentWallJsonObj.get("position");
                Point wallStart = parseJSONArrayCoordinates(currentWallCoordinates);

                String currentWallDirection = (String) currentWallJsonObj.get("direction");
                Point wallDirection = serverDirectionToPoint(currentWallDirection);

                Wall tempWall = new Wall(wallDirection, wallStart, currentWallLength, id);
                returnWalls.add(tempWall);
            }
        }

        return returnWalls;
    }
*/
}