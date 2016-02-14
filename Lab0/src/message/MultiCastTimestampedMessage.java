package message;

import clock.TimeStamp;

public class MultiCastTimestampedMessage extends TimestampedMessage{
	protected TimeStamp originTimestamp;
	protected String originSrc;
	protected String groupName;
	/**
	 * Initializers
	 * @param dest : destination of message as a name in the config file 
	 * @param kind : kind of message that would be feed into Broker
	 * @param data : payload, determined by kind
	 */
	public MultiCastTimestampedMessage(String dest, String kind, Object data) {
		super(dest, kind, data);
	}

	public MultiCastTimestampedMessage(Message message, TimeStamp timeStamp) {
		super(message, timeStamp);
	}
	
	public MultiCastTimestampedMessage(Message message, TimeStamp timeStamp, TimeStamp ,String oriSrc) {
		super(message, timeStamp);
		this.originSrc = oriSrc;
	}
}
