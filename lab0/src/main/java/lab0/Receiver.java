package edu.cmu.ds.lab0;

import java.io.IOException;
import java.io.ObjectInputStream;

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
					System.out.println("message: " + message.getData() + " from " + message.getSrc());
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
