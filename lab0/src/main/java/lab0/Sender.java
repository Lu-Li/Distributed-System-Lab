package edu.cmu.ds.lab0;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class Sender implements Runnable {
	// <name, stream>
	private HashMap<String, StreamPair> stream;
	// <name, info>
	private HashMap<String, ServerInfo> info;
	
//	private int serverPort;
//	
//	
//	public Sender(int serverPort) {
//		this.serverPort = serverPort;
//	}



	public void run() {
		while (true) {
			try {
				// TODO: change to get this message from queue
				Message message = new Message("10.0.22.146", "kind", "first out msg");
                String serverName = message.getDest();
                // first get outputstream from server name
                StreamPair sp = stream.get(serverName);
                ObjectOutputStream oos = sp.getOos();
                ObjectInputStream ois = sp.getOis();
                // if outputStream not exist, new one socket
                if (oos == null) {
                	String serverIp = info.get(serverName).getIp();
                    int serverPort = info.get(serverName).getPort();
    				Socket socket = new Socket(serverIp, serverPort);
    				oos = new ObjectOutputStream(socket.getOutputStream());
    				ois = new ObjectInputStream(socket.getInputStream());
    				Message firstRep = (Message)ois.readObject();
    				System.out.println("first response from new connection : " + firstRep.getData());
    				StreamPair newpair = new StreamPair(ois, oos);
    				stream.put(serverName, newpair);
    				
    				// Create a new thread to receive message from this new connection.
    				Thread thread = new Thread(new Receiver(ois));
                    thread.start();
                }
				System.out.println("sender starts");
				oos.writeObject(message);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
	}
	
}
