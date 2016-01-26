package driver;

import java.util.*;
import message.MessagePasser;
import socket.Listener;

public class Driver {

	private static void test(){
		List<String> names = MessagePasser.getAllNames();
		for (String name : names)
			System.out.println(name);
		
		System.out.println(MessagePasser.getIpStringForName("bob"));	
	}
	
	public static void main(String[] args) throws InterruptedException {		
		// Read arguments
		int argn = args.length;
		if (argn > 2 || argn == 0){
			System.out.println("Usage: Driver [config file path] name");
			return;
		}

		System.out.println("Welcome to team10-lab0!");
		
		// init MessagePasser, read configuration file and start listening 
		if (argn == 1)
			MessagePasser.init(null, args[0]);
		else 
			MessagePasser.init(args[0], args[1]);

		//
		System.out.println("1. Send a Message");
		System.out.println("2. Receive a Message");
		Scanner scanner = new Scanner(System.in);
		
		int selection = scanner.nextInt();		
	}

}
