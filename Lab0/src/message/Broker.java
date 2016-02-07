package message;

import java.util.HashMap;

import application.Log;

/*
 * Singleton pattern
 * TODO: not finish yet
 */
public class Broker implements Runnable{
	private static Broker instance = null;
	private HashMap<String, DistributedApplication> app;
	
	public Broker() {};
	public static synchronized Broker getInstance() {
		if(instance == null)
           instance = new Broker();
        return instance;
    }
	
	// when receive message, do specific action
	// logger is singleton? or new a logger each time
	public void handleMessage(Message message) {
		String kind = message.getKind();
		if (app.containsKey(kind)) {
			DistributedApplication disapp = app.get(kind);
			disapp.OnMessage(message);
		} else {
			Log.error("distributed application", "");
		}
	}

	public void register (String type, DistributedApplication application) {
		if (!app.containsKey(type)) {
			app.put(type, application);
		} else {
			Log.error("register", "application already exists error");
		}
	}
	public void deregister (String type, DistributedApplication application) {
		if (app.containsKey(type)) {
			app.remove(type);
		} else {
			Log.error("deregister", "Application not registered before.");
		}
	}

	@Override
	public void run() {
		while (true) {
			Message m = null;
			if ((m = MessagePasser.receive()) != null) {
				String kind = m.getKind();
				app.get(kind).OnMessage(m);
			}
		}	
	}
	
}
