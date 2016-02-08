package clock;

import java.util.List;

import application.Log;
import message.Message;
import message.MessagePasser;
import message.TimestampedMessage;

public class VectorClockService extends ClockService {
	private static int size;

	/**
	 * initializer
	 * @param size : count of nodes in the system
	 */
	public VectorClockService(int size) {
		this.timestamp = new VectorTimeStamp(size);
		this.size = size;
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
			TimestampedMessage timestampedMessage = 
					new TimestampedMessage(message,new VectorTimeStamp(vectorTimeStamp));
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
				VectorTimeStamp receivedTimestamp = (VectorTimeStamp)timeStamp;				
				VectorTimeStamp myTimestamp = (VectorTimeStamp)this.timestamp;
				
				VectorTimeStamp resultTimetamp = new VectorTimeStamp(this.size);
				
				for (int index=0; index<size; index++){
					int myTime = myTimestamp.getVectorItem(index);
					int senderTime = receivedTimestamp.getVectorItem(index);
					resultTimetamp.setVectorItem(index, myTime>senderTime?myTime:senderTime);
				}
				int index = names.indexOf(timestampedMessage.getDest());
				resultTimetamp.incrementVectorItem(index);
				
				Log.info("VectorClockService", "ReceivedTimestampedMessage:"+timeStamp+"+"+myTimestamp+"->"+resultTimetamp);
				//set current time for clockservice
				this.timestamp = resultTimetamp;
			} else Log.error("VectorClockService", "timestamp type error");
		}
	}

}
