package socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	
	public static void removeStreamPair(String serverName) {
		sessionMap.remove(serverName);
	}
	
	public static void closeAll() {
		for (Map.Entry<String, StreamPair> entry : sessionMap.entrySet()) {
			StreamPair sp = entry.getValue();
			try {
				sp.getOos().close();
				sp.getOis().close();
				sp.setOis(null);
				sp.setOos(null);
			} catch (IOException e) {
				System.err.println("close stream error");
				e.printStackTrace();
			}
		}
		sessionMap.clear();
	}
}
