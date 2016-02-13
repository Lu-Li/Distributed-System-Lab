package application;	

import java.io.Serializable;

import clock.ClockServiceFactory;
import clock.LogicalTimeStamp;
import clock.TimeStamp;
import clock.VectorTimeStamp;
import message.Message;
import message.TimestampedMessage;
/*
 * TODO: fix out a reasonable and beautiful content to log
 */
public class LogEntry implements Comparable<LogEntry>, Serializable{
	private String message;
	private TimeStamp timestamp;
	
	public LogEntry(String message) {
		this.message = message;		
		TimeStamp timestamp = ClockServiceFactory.getClockService().getTimeStamp();
		if (timestamp instanceof LogicalTimeStamp)
			this.timestamp = new LogicalTimeStamp((LogicalTimeStamp)timestamp);
		if (timestamp instanceof VectorTimeStamp)
			this.timestamp = new VectorTimeStamp((VectorTimeStamp)timestamp);
		Log.error("LogEntry", "No clock type");
	}
	

	public String getMessage() {
		return message;
	}


	public TimeStamp getTimestamp() {
		return timestamp;
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(LogEntry o) {		
		return this.timestamp.compareTo(o.timestamp);
	}	
	
	@Override
	public String toString() {
		return "Log["+message+","+timestamp+"]";
	}
}
