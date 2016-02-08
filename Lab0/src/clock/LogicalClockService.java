package clock;

import java.sql.Time;

import application.Log;
import message.Message;
import message.TimestampedMessage;

public class LogicalClockService extends ClockService{

	public LogicalClockService(){
		this.timestamp = new LogicalTimeStamp();
	}
	
	public LogicalClockService(TimeStamp ts) {
		super(ts);
	}

	/**
	 * current timestamp getter
	 */
	@Override
	public TimeStamp getTimeStamp() {
		return this.timestamp;
	}

	@Override
	public TimeStamp updateTimeStamp(TimestampedMessage message) {
		TimeStamp timeStamp = message.getTimeStamp();
		if (timeStamp instanceof LogicalTimeStamp && this.timestamp instanceof LogicalTimeStamp) {
			LogicalTimeStamp lts = (LogicalTimeStamp) timeStamp;
			LogicalTimeStamp cur = (LogicalTimeStamp) this.timestamp;
			int msgt = lts.getLogicalTime();
			int max = Math.max(msgt, cur.getLogicalTime()) + 1;
			lts.setLogicalTime(max);
			this.timestamp = lts;
			message.setTimeStamp(this.timestamp);
		}
		return this.timestamp;
	}

	@Override
	public TimestampedMessage addTimeStampToMessage(Message message) {
		if (this.timestamp instanceof LogicalTimeStamp) {
			LogicalTimeStamp cur = (LogicalTimeStamp) this.timestamp;
			cur.setLogicalTime(cur.getLogicalTime()+1);
			this.timestamp = cur;
			TimestampedMessage timestampedMessage = new TimestampedMessage(message, this.timestamp);
			Log.info("LogicalClockService", "added timestamp to message:"+timestampedMessage.toString());
			return timestampedMessage;
		} else Log.error("LogicalClockService", "timestamp type error"); 
			
		return null;
	}

	@Override
	public void ReceivedTimestampedMessage(TimestampedMessage timestampedMessage) {
		TimeStamp timeStamp = timestampedMessage.getTimeStamp();
		if (timeStamp instanceof LogicalTimeStamp && this.timestamp instanceof LogicalTimeStamp) {
			LogicalTimeStamp lts = (LogicalTimeStamp) timeStamp;
			LogicalTimeStamp cur = (LogicalTimeStamp) this.timestamp;
			int msgt = lts.getLogicalTime();
			int max = Math.max(msgt, cur.getLogicalTime()) + 1;
			lts.setLogicalTime(max);
			this.timestamp = lts;
			timestampedMessage.setTimeStamp(this.timestamp);
		} else Log.error("LogicalClockService", "timestamp type error");
	}

}
