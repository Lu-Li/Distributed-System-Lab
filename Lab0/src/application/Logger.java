package application;

import java.util.*;

import message.DistributedApplication;
import message.Message;

/*
 * Logger is an additional member of the distributed system 
 * (and thus needs to be initialized using the config file).
 * The log output should clearly show as many messages in the proper order as is possible. 
 * Concurrent messages should be noted, as well as showing the limits of this concurrency
 * the comparisons done by the Logger should be via the comparison methods of the Timestamps
 *  so here, msglog use the comparator of timestamp
 */
public class Logger implements DistributedApplication{
	// TODO: config file design?
	private String clockType;
	// TODO: change type from Message to logEntry
	private static ArrayList<Message> msglog = new ArrayList<>();
	
	public Logger(String configFilename, String localName) {
		this.parseConfigFile(configFilename);
	}
	
	public void parseConfigFile(String filename) {
		
	}
	
	// display message. 
	// TODO: 1,sort before dump. 2, indicate whether two messages are concurrent
	// Question: how to show the limit of this concurrent?
	public void dumpMessage() {
		for (int i = 0; i < msglog.size(); i++) {
			Message m = msglog.get(i);
			System.out.println("seq:" + m.getSeqNum() + "/t" 
					+ "content:" + m.getData().toString() + "/t" + "timestamp need to add");
		}
	}

	@Override
	public void OnMessage(Message msg) {
		msglog.add(msg);
	}
}
