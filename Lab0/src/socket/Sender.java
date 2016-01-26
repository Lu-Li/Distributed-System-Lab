package socket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.Message;
import message.MessagePasser;

public class Sender extends Thread{
	
	private static final BlockingQueue<Message> queue =
            new LinkedBlockingQueue<> ();

	// call this function to add message to queue whenever want to send msg
	public static void addMsg(Message msg) {
		try {
			queue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				Message message = queue.take();
                String serverName = message.getDest();
                HashMap<String, StreamPair> stream = SessionMap.getSessionMap();
                
                // first get outputstream from server name
                StreamPair sp = stream.get(serverName);
                ObjectOutputStream oos = sp.getOos();
                ObjectInputStream ois = sp.getOis();

                // if outputStream not exist, new one socket
                if (oos == null) {
                	String serverIp = MessagePasser.getIpStringForName(serverName);
                	String portString = MessagePasser.getIpStringForName(serverName);
                    int serverPort = Integer.parseInt(portString);
                    
    				Socket socket = new Socket(serverIp, serverPort);
    				oos = new ObjectOutputStream(socket.getOutputStream());
    				ois = new ObjectInputStream(socket.getInputStream());
    				
    				// no need to store to receive queue since this first response is only to set up connection
    				Message firstRep = (Message)ois.readObject();
    				
    				System.err.println("[INFO] first response from new connection : " + firstRep.getData());
    				stream.put(serverName, new StreamPair(ois, oos));
    				
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
	}
	
}
