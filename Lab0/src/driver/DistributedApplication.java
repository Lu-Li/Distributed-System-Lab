package driver;

import message.Message;

public interface DistributedApplication {
	public void OnMessage (Message msg);
}
