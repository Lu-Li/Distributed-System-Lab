package lab0;

import java.util.List;
import java.util.Scanner;

public class Driver {
	public static void main(String[] args) throws InterruptedException {
		/*
		// Test
		Configuration config = new Configuration();
		Message message = new Message("bob", "ACK", null);
		message.set_seqNum(0);
		message.set_source("charlie");
		Action action = config.getAction(message, Direction.Receive);
		System.out.println(action);
		
		List<String> names = messagePasser.getAllNames();
		for (String name : names)
			System.out.println(name);
		
		System.out.println(messagePasser.getIpStringForName("bob"));
		*/
		
		// Read arguments
		int argn = args.length;
		if (argn > 3 || argn == 0){
			System.out.println("Usage: Driver [config file path] name");
			return;
		}
		
		// init MessagePasser and read configuration file 
		MessagePasser messagePasser;
		if (argn == 2)
			messagePasser = new MessagePasser(null, args[1]);
		else 
			messagePasser = new MessagePasser(args[1], args[2]);
		
		// TODO(Lu): init socket	
		System.out.println("Welcome to team10-lab0!");
		
		// TODO(Lu): send a message when connection established
		Thread.sleep(1000);

		// TODO(Baiqi): read stdin and run command
		System.out.println("1. Send a Message");
		System.out.println("2. Receive a Message");
		Scanner scanner = new Scanner(System.in);
		
		int selection = scanner.nextInt();		
	}

}
