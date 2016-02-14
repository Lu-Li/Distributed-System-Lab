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
	
	public MultiCastTimestampedMessage(Message message, String dest,String groupName,String kind) {
		super(message);
		if (message instanceof MultiCastTimestampedMessage){			
			this.originSrc = ((MultiCastTimestampedMessage) message).originSrc;
			this.originTimestamp = ((MultiCastTimestampedMessage) message).originTimestamp;
			this.groupName = ((MultiCastTimestampedMessage) message).groupName;		
		} else {			
			this.originSrc = this.src;
			this.originTimestamp = this.timestamp;
		}
		this.dest = dest;
		this.groupName = groupName;
		this.kind = kind;
	}

	
//	public MultiCastTimestampedMessage(String dest, String kind, Object data, TimeStamp timeStamp, String oriSrc) {
//		super(dest, kind, data);
//	}
//
//
//	public MultiCastTimestampedMessage copy(){
//		return new MultiCastTimestampedMessage(dest, kind, data, timestamp, originSrc);
//	}
	
	
	// TODO: equals && hashcode
	@Override
	public boolean equals(Object obj) {		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
