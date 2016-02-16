package message;

import java.util.Objects;

import clock.TimeStamp;

public class MultiCastTimestampedMessage extends TimestampedMessage {
	protected TimeStamp originTimestamp;
	protected String originSrc;
	protected String groupName;

	/**
	 * Initializers
	 * 
	 * @param dest
	 *            : destination of message as a name in the config file
	 * @param kind
	 *            : kind of message that would be feed into Broker
	 * @param data
	 *            : payload, determined by kind
	 */

	public MultiCastTimestampedMessage(Message message, String dest, String groupName, String kind) {
		super(message);
		if (message instanceof MultiCastTimestampedMessage) {
			this.originSrc = ((MultiCastTimestampedMessage) message).originSrc;
			this.originTimestamp = ((MultiCastTimestampedMessage) message).originTimestamp;
			this.groupName = ((MultiCastTimestampedMessage) message).groupName;
		} else {
			this.originSrc = MessagePasser.getLocalName();
			this.originTimestamp = this.timestamp;
		}
		this.dest = dest;
		this.groupName = groupName;
		this.kind = kind;
	}

	@Override
	public String toString() {
		return super.toString() + ", group = " + groupName + ", sender = " + originSrc + ", origTS:" + originTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + ((originSrc == null) ? 0 : originSrc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiCastTimestampedMessage other = (MultiCastTimestampedMessage) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (originSrc == null) {
			if (other.originSrc != null)
				return false;
		} else if (!originSrc.equals(other.originSrc))
			return false;
		if (originTimestamp == null) {
			if (other.originTimestamp != null)
				return false;
		} else if (!originTimestamp.isIdenticalTo(other.originTimestamp))
			return false;
		return true;
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
