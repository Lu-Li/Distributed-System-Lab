package edu.cmu.ds.lab0;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Sender implements Runnable {
	// <name, stream>
	private HashMap<String, StreamPair> map;
	// <name, info>
	private HashMap<String, ServerInfo> info;
	
	private int serverPort;
	
	
	public Sender(int serverPort) {
		this.serverPort = serverPort;
	}



	public void run() {
		while (true) {
			try {
				Message message = new Message("10.0.22.146", "kind", "first out msg");
                String serverName = message.getDest();
                String serverIp = info.get(serverName).getIp();
                int serverPort = info.get(serverName).getPort();
				// TODO: change to find stream from stored map first, if none, open a new socket
				Socket socket = new Socket(serverIp, serverPort);
//				Socket socket = new Socket("10.0.22.146", serverPort);
				System.out.println("sender starts");
				// TODO: use map to get stream if exist, otherwise new one
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(message);
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ois.readObject();
				
				// Create a new thread to receive message from this new connection.
				Thread thread = new Thread(new Receiver(ois));
                thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
	}
	
}
