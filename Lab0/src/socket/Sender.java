package socket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.Message;
import message.MessagePasser;

public class Sender extends Thread {

	private static final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

	// call this function to add message to queue whenever want to send msg
	public static void addMsg(Message msg) {
		try {
			queue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.err.println("[Sender] started");
		while (true) {
			try {
				// block while queue is empty
				Message message = queue.take();
				
				System.err.println("[Sender] about to send message: kind = "+ message.getKind());
				
				String serverName = message.getDest();
				HashMap<String, StreamPair> stream = SessionMap.getSessionMap();

				// first get outputstream from server name
				StreamPair sp = stream.get(serverName);
				ObjectOutputStream oos = sp == null ? null : sp.getOos();
				ObjectInputStream ois = sp == null ? null : sp.getOis();

				// if outputStream not exist, new one socket
				if (oos == null) {
					String serverIp = MessagePasser.getIpStringForName(serverName);
					int serverPort = MessagePasser.getPortNumberForName(serverName);

					Socket socket = new Socket(serverIp, serverPort);
					oos = new ObjectOutputStream(socket.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());

					System.err.println("[Sender] send message using new socket");
					oos.writeObject(message);
					
					// no need to store to receive queue since this first
					// response is only to set up connection
					Message firstRep = (Message) ois.readObject();

					System.err.println("[Sender] first response from new connection : " + firstRep.getData());
					stream.put(serverName, new StreamPair(ois, oos));

					// Create a new thread to receive message from this new
					// connection.
					Thread thread = new Thread(new Receiver(ois));
					thread.start();
				} else {
					System.err.println("[Sender] send message using reused socket");
					oos.writeObject(message);
				}

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
