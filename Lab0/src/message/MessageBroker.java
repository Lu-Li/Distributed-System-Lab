package message;
/*
 * Singleton pattern
 * TODO: not finish yet
 */
public class MessageBroker {
	private static MessageBroker instance = null;
	
	public static synchronized MessageBroker getInstance() {
      if(instance == null)
           instance = new MessageBroker();
        return instance;
    }
	
	// when receive message, do specific action
	// logger is singleton? or new a logger each time
	public void onMessage(Message message) {
		if (message instanceof TimestampedMessage){
			
		} else {
			
		}
	}
}
