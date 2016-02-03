package clock;

import message.Message;

public abstract class ClockService {
	
	private static TimeStamp timestamp;
	
	public ClockService(TimeStamp ts) {
		timestamp = ts;
	}
	public abstract TimeStamp getTimeStamp ();
	
	public abstract TimeStamp updateTimeStamp(Message message);
}
