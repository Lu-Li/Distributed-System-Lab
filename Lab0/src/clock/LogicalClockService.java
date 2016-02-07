package clock;

import message.Message;
import message.TimestampedMessage;

public class LogicalClockService extends ClockService{

	public LogicalClockService() {};
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

}
