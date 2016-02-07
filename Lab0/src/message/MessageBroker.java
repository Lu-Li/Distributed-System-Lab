package message;
/*
 * Singleton pattern
 * TODO: not finish yet
 */
public class Broker {
	private static Broker instance = null;
	
	public static synchronized Broker getInstance() {
      if(instance == null)
           instance = new Broker();
        return instance;
    }
	
	// when receive message, do specific action
	// logger is singleton? or new a logger each time
	public void saveMessage(Message message) {
		
	}
}
