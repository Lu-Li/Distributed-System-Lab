package clock;

import message.Message;
import message.MessagePasser;

public class VectorClockService extends ClockService {

	public VectorClockService(int size) {
		this.timestamp = new VectorTimeStamp(size);
	};

	public VectorClockService(TimeStamp ts) {
		super(ts);
	}
	
	@Override
	public TimeStamp getTimeStamp() {
		return this.timestamp;
	}

	@Override
	public TimeStamp updateTimeStamp(Message message) {
		if (message.getSrc().equals(MessagePasser.getLocalName())){
			// TODO : increment corresponding clock item
		}
		return null;
	}

}
