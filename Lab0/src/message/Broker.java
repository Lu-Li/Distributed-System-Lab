package message;

import java.util.HashMap;

import application.DistributedApplication;
import application.Log;

/*
 * Singleton pattern
 * TODO: not finish yet
 */
public class Broker{
	private static Broker instance = null;
	private HashMap<String, DistributedApplication> appMap;
	private Thread readMessageThread;
	
	public Broker() {
		appMap = new HashMap<String, DistributedApplication>();
		readMessageThread = new Thread(){
			@Override
			public void run() {
				while (true) {
					Message m = null;
					if ((m = MessagePasser.receive()) != null) 
						handleMessage(m);
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		readMessageThread.start();
	};
	
	public static synchronized Broker getInstance() {
		if(instance == null)
           instance = new Broker();
        return instance;
    }
	
	// when receive message, do specific action
	// logger is singleton? or new a logger each time
	public void handleMessage(Message message) {
		String kind = message.getKind();
		if (appMap.containsKey(kind)) {
			DistributedApplication disapp = appMap.get(kind);
			Log.info("Broker", kind + " -> " + disapp.getAppName());
			disapp.OnMessage(message);
		} else {
			Log.error("Broker", "no kind in message");
		}
	}

	public void register (String type, DistributedApplication application) {
		if (!appMap.containsKey(type)) {
			appMap.put(type, application);
		} else {
			Log.error("Broker", "application already exists error");
		}
	}
	public void deregister (String type, DistributedApplication application) {
		if (appMap.containsKey(type)) {
			appMap.remove(type);
		} else {
			Log.error("Broker", "Application not registered before.");
		}
	}

}
