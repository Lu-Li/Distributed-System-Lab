package application;

import java.io.Serializable;

import clock.ClockServiceFactory;
import clock.TimeStamp;
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
		this.timestamp = ClockServiceFactory.getClockService().getTimeStamp();
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
}
