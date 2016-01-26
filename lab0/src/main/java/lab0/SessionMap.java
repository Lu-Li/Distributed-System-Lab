package lab0;

import java.util.HashMap;

public class SessionMap {
	// <name, stream>
	private static HashMap<String, StreamPair> sessionMap = new HashMap<String, StreamPair>();
	// <name, info>
	private static HashMap<String, ServerInfo> infoMap = new HashMap<String, ServerInfo>();
	
	
	public static HashMap<String, StreamPair> getSessionMap() {
		return sessionMap;
	}

	public static void setSessionMap(HashMap<String, StreamPair> sessionMap) {
		SessionMap.sessionMap = sessionMap;
	}

	public static HashMap<String, ServerInfo> getInfoMap() {
		return infoMap;
	}

	public static void setInfoMap(HashMap<String, ServerInfo> infoMap) {
		SessionMap.infoMap = infoMap;
	}

	// use at beginning to store all info of server
	// TODO: initial this map
	public static void addServerMap(String serverName, ServerInfo info) {
		infoMap.put(serverName, info);
	}
	
	// new socket set up, add streampair
	public static void addStreamPair(String serverName, StreamPair pair) {
		sessionMap.put(serverName, pair);
	}
	
}
