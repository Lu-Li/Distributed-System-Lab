package socket;

import java.util.HashMap;

public class SessionMap {
	// <name, stream>
	private static HashMap<String, StreamPair> sessionMap = new HashMap<String, StreamPair>();	
	
	public static HashMap<String, StreamPair> getSessionMap() {
		return sessionMap;
	}

	public static void setSessionMap(HashMap<String, StreamPair> sessionMap) {
		SessionMap.sessionMap = sessionMap;
	}
	
	// new socket set up, add streampair
	public static void addStreamPair(String serverName, StreamPair pair) {
		sessionMap.put(serverName, pair);
	}
	
}
