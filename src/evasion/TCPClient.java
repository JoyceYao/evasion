package evasion;

import java.io.*;
import java.net.*;

public class TCPClient {
	
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
	
	public String read() throws IOException{
		String s = readAll();
		return s;
	}
	
	public String readAll() throws IOException{
		byte[] b = new byte[1024];
		int msg = is.read(b);
		return new String(b, 0, msg);
	}
	
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
}
