package evasion;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

public class MessagePublisher {
	Socket socket;
	DataOutputStream outToServer;
	InputStream is;
	PrintWriter out;
	
	public void startTCP(String host, int port) throws UnknownHostException, IOException{
		socket = new Socket(host, port);
		outToServer = new DataOutputStream(socket.getOutputStream());
		is = socket.getInputStream();
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void closeTCP() throws IOException{
		socket.close();
	}
	
//	public String read() throws IOException{
//		String s = readAll();
//		return s;
//	}
//	
//	public String readAll() throws IOException{
//		byte[] b = new byte[1024];
//		int msg = is.read(b);
//		return new String(b, 0, msg);
//	}
	
	public void write(String output) throws IOException{
		//outToServer.writeBytes(output);
		out.println(output);
	}
	
	public String readBuffered() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String content = br.readLine();
		//System.out.println(content);
		return content;
	}

	private String sendAndReceive(String s) throws IOException {
		write(s);
		return readBuffered();
	}

	public String getPositions() throws IOException {
		// TODO Auto-generated method stub
		JSONObject j = new JSONObject();
		j.put("command", "P");
		return sendAndReceive(j.toString());
	}
	
	public String getWalls() throws IOException {
		// TODO Auto-generated method stub
		JSONObject j = new JSONObject();
		j.put("command", "W");
		return sendAndReceive(j.toString());
	}
	
}
