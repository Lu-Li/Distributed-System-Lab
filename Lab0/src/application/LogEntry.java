package application;

import clock.TimeStamp;
import message.Message;
import message.TimestampedMessage;
/*
 * TODO: fix out a reasonable and beautiful content to log
 */
public class LogEntry implements Comparable<LogEntry>{
	private String src;
	private String dest;
	private String kind;
	private String message;
	private TimeStamp timestamp;
	
	
	public String getSrc() {
		return src;
	}


	public String getDest() {
		return dest;
	}


	public String getKind() {
		return kind;
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
