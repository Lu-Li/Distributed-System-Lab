package lab0;

public class Driver {
	public static void main(String[] args) {
		/*
		// Test
		Configuration config = new Configuration();
		Message message = new Message("bob", "ACK", null);
		message.set_seqNum(0);
		message.set_source("charlie");
		Action action = config.getAction(message, Direction.Receive);
		System.out.println(action);
		*/
		
		int argn = args.length;
		if (argn > 3 || argn == 0){
			System.out.println("Usage: Driver [config file path] name");
			return;
		}
		
		MessagePasser messagePasser;
		if (argn == 2)
			messagePasser = new MessagePasser(null, args[1]);
		else 
			messagePasser = new MessagePasser(args[1], args[2]);
	}

}
