package clock;

import application.Log;
import message.Message;
import message.TimestampedMessage;

public abstract class ClockService {

	protected TimeStamp timestamp;

	public ClockService() {
	};

	public ClockService(TimeStamp ts) {
		timestamp = ts;
	}

	/**
	 * get current time
	 * @return a TimeStamp instance representing current time
	 */
	public abstract TimeStamp getTimeStamp();

	/**
	 * update current time
	 * @param message a message that is just received/sent
	 * @return a TimeStamp instance representing current time after the update
	 */
	public abstract TimeStamp updateTimeStamp(TimestampedMessage message);
	
	public abstract TimestampedMessage addTimeStampToMessage(Message message);
	
	public abstract void ReceivedTimestampedMessage(TimestampedMessage timestampedMessage);
}
