package socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import message.Message;
import message.MessagePasser;

public class Receiver extends Thread{

	private ObjectInputStream ois;
	private String name;
	
	public Receiver(ObjectInputStream ois, String name) {
		this.ois = ois;
		this.name = name;
	}

	public void run() {
		Message message = null;
		System.err.println("[Receiver] started");
		try {
			while (true) {
				if ((message = (Message) (ois.readObject())) != null) {
					System.err.println("[Receiver] message: " + message.getData() + " from " + message.getSrc());
					MessagePasser.getMessageFromSocketCallback(message);
				}
				Thread.sleep(1);
			}
		} catch (EOFException e) {
			try {
				HashMap<String, StreamPair> stream = SessionMap.getSessionMap();
				StreamPair sp = stream.get(name);
				sp.getOos().close();
				ois.close();
				SessionMap.removeStreamPair(name);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
