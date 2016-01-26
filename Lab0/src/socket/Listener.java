package socket;

import java.io.*;
import java.net.*;

import message.Message;
import message.MessagePasser;

public class Listener extends Thread{
	private int port;

	public Listener(int port) {
		this.port = port;
	}

	public void run() {
		ServerSocket sersoc;
		try {
			sersoc = new ServerSocket(this.port);
			int count = 0;
			while (count < 3) {
				System.err.println("[INFO] listener starts");
				Socket socket = sersoc.accept();
								
				System.err.println("[INFO] socket connected");
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				Message firIn = (Message) ois.readObject(); 
				
				//TODO: change des from message passer
				Message firOut = new Message(firIn.getSrc(), "confirm", "first message");
				oos.writeObject(firOut);
				String name = firIn.getSrc();
				StreamPair pair = new StreamPair(ois, oos);
				SessionMap.addStreamPair(name, pair);
							
				MessagePasser.getMessageFromSocketCallback(firIn);
				
				// Create a new thread to receive message from this new connection.
				Thread thread = new Thread(new Receiver(ois));
                thread.start();
                
                // listen again until connected to all processes
                count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
