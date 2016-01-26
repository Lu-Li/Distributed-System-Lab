package socket;

import java.io.IOException;
import java.io.ObjectInputStream;

import message.Message;
import message.MessagePasser;

public class Receiver extends Thread{

	private ObjectInputStream ois;
	
	public Receiver(ObjectInputStream ois) {
		this.ois = ois;
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
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
