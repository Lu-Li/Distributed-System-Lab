package clock;

import driver.Log;
import message.Message;
import message.TimestampedMessage;

public abstract class ClockService {

	protected TimeStamp timestamp;

	public ClockService() {
	};

	public ClockService(TimeStamp ts) {
		timestamp = ts;
	}

	public abstract TimeStamp getTimeStamp();

	public abstract TimeStamp updateTimeStamp(TimestampedMessage message);
}
