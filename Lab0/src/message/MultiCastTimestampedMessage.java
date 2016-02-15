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
	

	// TODO: equals && hashcode
	@Override
	public boolean equals(Object obj) {		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString() + ", group = "+ groupName + ", sender = "+originSrc;
	}

	public TimeStamp getOriginTimestamp() {
		return originTimestamp;
	}


	public String getOriginSrc() {
		return originSrc;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}


