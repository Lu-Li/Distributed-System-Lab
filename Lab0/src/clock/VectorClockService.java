package clock;

import message.Message;

public class VectorClockService extends ClockService{

	public VectorClockService() {};
	public VectorClockService(TimeStamp ts) {
		super(ts);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TimeStamp getTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeStamp updateTimeStamp(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

}
