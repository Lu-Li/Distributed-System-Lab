package application;

import java.util.*;

import javax.rmi.CORBA.Tie;

import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Message;
import message.MessagePasser;
import message.TimestampedMessage;

/*
 * Logger is an additional member of the distributed system 
 * (and thus needs to be initialized using the config file).
 * The log output should clearly show as many messages in the proper order as is possible. 
 * Concurrent messages should be noted, as well as showing the limits of this concurrency
 * the comparisons done by the Logger should be via the comparison methods of the Timestamps
 *  so here, msglog use the comparator of timestamp
 */
public class Logger implements DistributedApplication{
	public static ArrayList<LogEntry> msglog = new ArrayList<>();
	
	public Logger(String configFilename, String localName) {
		this.parseConfigFile(configFilename);
	}
	
	public void parseConfigFile(String filename) {
		
	}
	
	// display message. 
	public void dumpLog() {
		if (ClockServiceFactory.getClockService().equals("Logical")){
			Collections.sort(msglog);
			for (int i = 0; i < msglog.size(); i++) {
				LogEntry m = msglog.get(i);
				System.out.println("content:" + m.getMessage() + "\ttimestamp:" + m.getTimestamp());
				if (i<msglog.size()-1 && msglog.get(i).compareTo(msglog.get(i+1))!=0)
					System.out.println("------------------");
			}
		} else {
			Collections.sort(msglog);
			for (int i = 0; i < msglog.size(); i++) {
				LogEntry m = msglog.get(i);
				System.out.print("#"+i+" :" + m.getMessage() + "\tts:" + m.getTimestamp());
				for (int j = 0; j < msglog.size(); j++) {
					if (j==i)
						continue;
					LogEntry m2 = msglog.get(j);
					if (m.getTimestamp().compareTo(m2.getTimestamp())==0)
						System.out.print(" = " + m2.getTimestamp());
				}
				System.out.println();
			}
		}
	}

	// parse payload
	@Override
	public void OnMessage(Message msg) {
		if (msg.getData() instanceof LogEntry)
			msglog.add((LogEntry)msg.getData());
		else {
			Log.error("Logger", "data type mismatch");
		}
	}

	@Override
	public String getAppName() {		
		return "LoggerApp";
	}
}
