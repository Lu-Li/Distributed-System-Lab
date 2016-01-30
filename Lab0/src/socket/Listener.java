package socket;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

import message.Message;
import message.MessagePasser;

public class Listener extends Thread {
	private int port;
	private static boolean flag = true;
	private static Socket socket;
	private static ServerSocket sersoc;
	
	private static LinkedList<ObjectInputStream> receivers = new LinkedList<>();
	
	public Listener(int port) {
		this.port = port;
	}

	public static void setFlagFalse() {
		//Close Sockets
		try {
			if (sersoc != null)
				sersoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Close streams for generated Receivers
		for (ObjectInputStream ois : receivers)
			try {
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		flag = false;
	}

	public void run() {		
		try {
			sersoc = new ServerSocket(this.port);
			flag = true;
			while (flag) {
				System.err.println("[Listener] starts");
				socket = sersoc.accept();

				System.err.println("[Listener] socket connection accepted");
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				Message firIn = (Message) ois.readObject();

				System.err.println("[Listener] get First message: kind = " + firIn.getKind());
				Message firOut = new Message(firIn.getSrc(), "confirm", "first message");
				oos.writeObject(firOut);
				System.err.println("[Listener] reply First message");
				String name = firIn.getSrc();
				StreamPair pair = new StreamPair(ois, oos);
				SessionMap.addStreamPair(name, pair);
				
				MessagePasser.getMessageFromSocketCallback(firIn);

				// Create a new thread to receive message from this new
				// connection.
				
				Receiver receiver = new Receiver(ois, name);
				receivers.add(ois);
				receiver.start();
			}
			sersoc.close();
		} catch (SocketException e) {
			//ignore socket closed exception
		}catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
