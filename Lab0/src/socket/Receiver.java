package socket;

import java.io.IOException;
import java.io.ObjectInputStream;

import message.Message;
import message.MessagePasser;

public class Receiver implements Runnable {

	private ObjectInputStream ois;
	
	public Receiver(ObjectInputStream ois) {
		this.ois = ois;
	}

	public void run() {
		Message message = null;
		try {
			while (true) {
				if ((message = (Message) (ois.readObject())) != null) {
					System.err.println("[INFO] message: " + message.getData() + " from " + message.getSrc());
					MessagePasser.getMessageFromSocketCallback(message);
				}
				Thread.sleep(1);
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
