package clock;

import java.util.List;

import application.Log;
import message.Message;
import message.MessagePasser;
import message.TimestampedMessage;

public class VectorClockService extends ClockService {

	/**
	 * initializer
	 * @param size : count of nodes in the system
	 */
	public VectorClockService(int size) {
		this.timestamp = new VectorTimeStamp(size);
	};

	public VectorClockService(TimeStamp ts) {
		super(ts);
	}
		
	/**
	 * current timestamp getter
	 */
	@Override
	public TimeStamp getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * update current time
	 * @param message a message that is just received/sent
	 * @return current time after the update
	 */
	@Override
	public TimeStamp updateTimeStamp(TimestampedMessage message) {
		//set timestamp for message, not sure if is required
		message.setTimeStamp(this.timestamp);
		return this.timestamp;
	}

	@Override
	public TimestampedMessage addTimeStampToMessage(Message message) {
		List<String> names = MessagePasser.getAllNames();
		TimeStamp timeStamp = this.timestamp;
		if (timeStamp instanceof VectorTimeStamp){
			VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)timeStamp;
			int index = names.indexOf(message.getSrc());
			vectorTimeStamp.incrementVectorItem(index);

			//set current time for clockservice
			this.timestamp = vectorTimeStamp;
			TimestampedMessage timestampedMessage = new TimestampedMessage(message,this.timestamp);
			return timestampedMessage;
		} else Log.error("VectorClockService", "timestamp type error");
		return null;
	}

	@Override
	public void ReceivedTimestampedMessage(TimestampedMessage timestampedMessage) {
		List<String> names = MessagePasser.getAllNames();
		// message received
		if (timestampedMessage.getDest().equals(MessagePasser.getLocalName())){
			TimeStamp timeStamp = timestampedMessage.getTimeStamp();
			if (timeStamp instanceof VectorTimeStamp){
				VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)timeStamp;
				int myIndex = names.indexOf(timestampedMessage.getDest());
				int senderIndex = names.indexOf(timestampedMessage.getSrc());
				
				int myTime = vectorTimeStamp.getVectorItem(myIndex) + 1;
				int senderTime = vectorTimeStamp.getVectorItem(senderIndex) + 1;
				vectorTimeStamp.setVectorItem(myIndex, myTime>senderTime?myTime:senderTime);
				
				//set current time for clockservice
				this.timestamp = vectorTimeStamp;
			} else Log.error("VectorClockService", "timestamp type error");
		}
	}

}
