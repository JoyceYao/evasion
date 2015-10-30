package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerListener {
	ServerSocket sSocket;
	Socket cSocket;
	BufferedReader in;
	PrintWriter out;
	List<GameClientHandler> lgc = new ArrayList();
	
	public void startTCP(int port) throws UnknownHostException, IOException{
	    sSocket = new ServerSocket(port);			
		while (true) {
		    cSocket = sSocket.accept();
		    GameClientHandler gc = new GameClientHandler(cSocket);
			lgc.add(gc);
			new Thread(gc).start();
		}
	}
	
	public void closeTCP() throws IOException{
		sSocket.close();
	}
	
	
//	public void write(String output) throws IOException{
//		//outToServer.writeBytes(output);
//		out.println(output);
//	}
//	
//	public String readBuffered() throws IOException {
//		String content = in.readLine();
//		//System.out.println(content);
//		return content;
//	}

}
