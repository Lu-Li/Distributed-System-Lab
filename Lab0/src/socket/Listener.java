package socket;

import java.io.*;
import java.net.*;

import message.Message;
import message.MessagePasser;

public class Listener extends Thread{
	private int port;
	private static boolean flag = true;

	public Listener(int port) {
		this.port = port;
	}

	public static void setFlagFalse() {
		flag = false;
	}

	public void run() {
		ServerSocket sersoc;
		try {
			sersoc = new ServerSocket(this.port);
			while (flag) {
				System.err.println("[Listener] starts");
				Socket socket = sersoc.accept();
								
				System.err.println("[Listener] socket connection accepted");
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				Message firIn = (Message) ois.readObject(); 
				
				System.err.println("[Listener] first message: kind = "+firIn.getKind());
				Message firOut = new Message(firIn.getSrc(), "confirm", "first message");
				oos.writeObject(firOut);
				String name = firIn.getSrc();
				StreamPair pair = new StreamPair(ois, oos);
				SessionMap.addStreamPair(name, pair);
							
				MessagePasser.getMessageFromSocketCallback(firIn);
				
				// Create a new thread to receive message from this new connection.
				new Receiver(ois, name).start();
			}
			sersoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}

}
