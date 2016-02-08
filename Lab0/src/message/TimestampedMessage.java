package message;

import application.Log;
import clock.TimeStamp;

public class TimestampedMessage extends Message{
	private static final long serialVersionUID = 1L;

	protected TimeStamp timestamp;
	
	/**
	 * Initializers
	 * @param dest : destination of message as a name in the config file 
	 * @param kind : kind of message that would be feed into Broker
	 * @param data : payload, determined by kind
	 */
	public TimestampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}
	
	/**
	 * Initializers
	 * @param dest : destination of message as a name in the config file 
	 * @param kind : kind of message that would be feed into Broker
	 * @param data : payload, determined by kind
	 * @param timeStamp : timeStamp generated by ClockService
	 */

	public TimestampedMessage(Message message, TimeStamp timeStamp) {
		super(message.getDest(), message.getKind(), message.getData());
		set_seqNum(message.getSeqNum());
		set_source(message.getSrc());
		this.timestamp = timeStamp;
	}
	
	public TimeStamp getTimeStamp() {
		if (this.timestamp==null)
			Log.error("TimestampedMessage", "No timestamp");
		return this.timestamp;
	}
	
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timestamp = timeStamp;
	}

	@Override
	public String toString() {
		return super.toString() + ", timestamp = "+ timestamp;
	}
	
}