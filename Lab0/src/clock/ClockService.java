package clock;

import driver.Log;
import message.Message;

public abstract class ClockService {

	protected TimeStamp timestamp;

	public ClockService() {
	};

	public ClockService(TimeStamp ts) {
		timestamp = ts;
	}

	public abstract TimeStamp getTimeStamp();

	public abstract TimeStamp updateTimeStamp(Message message);
}
