package edu.cmu.ds.lab0;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StreamPair {
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public StreamPair(ObjectInputStream ois, ObjectOutputStream oos) {
		this.ois = ois;
		this.oos = oos;
	}
	
	public ObjectInputStream getOis() {
		return ois;
	}
	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}
	public ObjectOutputStream getOos() {
		return oos;
	}
	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}
		
}
