package clock;

import message.Message;
import message.TimestampedMessage;

public class LogicalClockService extends ClockService{

	public LogicalClockService() {};
	public LogicalClockService(TimeStamp ts) {
		super(ts);
	}

	@Override
	public TimeStamp getTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeStamp updateTimeStamp(TimestampedMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

}
