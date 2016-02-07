package application;

import message.Message;

public interface DistributedApplication {
	public void OnMessage (Message msg);
	public String getAppName();
}
