package lab0;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Listener implements Runnable {
	private int port;

	public Listener(int port) {
		this.port = port;
	}

	public void run() {
		ServerSocket sersoc;
		try {
			sersoc = new ServerSocket(this.port);
			while (true) {
				System.out.println("listener starts");
				Socket socket = sersoc.accept();
				System.out.println("socket connect");
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				Message firIn = (Message) ois.readObject(); 
				//TODO: change des from message passer
				Message firOut = new Message(firIn.getSrc(), "confirm", "first message");
				oos.writeObject(firOut);
				String name = firIn.getSrc();
				StreamPair pair = new StreamPair(ois, oos);
				SessionMap.addStreamPair(name, pair);
				// TODO: change here,call receive msg function and add this first useful msg to read queue
				new MessagePasser().getMessageFromSocketCallback(firIn);
				
				// Create a new thread to receive message from this new connection.
				Thread thread = new Thread(new Receiver(ois));
                thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
