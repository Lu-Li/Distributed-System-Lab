package driver;

import java.util.*;
import java.util.zip.Inflater;

import message.Message;
import message.MessagePasser;
import socket.Listener;

public class Driver {

	private static void test() {
		MessagePasser.init(null, "b");
		Message msg = new Message("a", "Ack", "ss");
		MessagePasser.send(msg);
		MessagePasser.send(msg);
		MessagePasser.send(msg);
		
		
		List<String> names = MessagePasser.getAllNames();
		for (String name : names)
			System.out.println(name);

		System.out.println(MessagePasser.getIpStringForName("bob"));
	}

	public static void main(String[] args) throws InterruptedException {
		// Read arguments
		int argn = args.length;
		if (argn > 2 || argn == 0) {
			System.out.println("Usage: Driver [config file path] name");
			return;
		}

		System.out.println("Welcome to team10-lab0!");

		// init MessagePasser, read configuration file and start listening
		if (argn == 1)
			MessagePasser.init(null, args[0]);
		else
			MessagePasser.init(args[0], args[1]);

		// simple UI
		boolean done = false;
		while (!done) {
			Thread.sleep(300);
			System.out.println("=======================");
			System.out.println("1. Send a Message");
			System.out.println("2. Receive a Message");
			System.out.println("other number: exit");
			System.out.println("=======================");
			Scanner scanner = new Scanner(System.in);

			try {
				int selection = scanner.nextInt();
				Message message;
				switch (selection) {
				case 1:
					System.out.print("destination(name):");
					String dest = scanner.next();
					System.out.print("kind:");
					String kind = scanner.next();
					System.out.print("content:");
					String payload = scanner.next();
					message = new Message(dest, kind, payload);
					MessagePasser.send(message);				
					break;
				case 2:
					message = MessagePasser.receive();
					if (message == null){
						System.out.println("No message!");
						break;
					}
					System.out.println("seq:" + message.getSeqNum());
					System.out.println("src:" + message.getSrc());
					System.out.println("kind:" + message.getKind());
					System.out.println("content:" + message.getData().toString());
					break;

				default:
					MessagePasser.terminateAll();
					return;
				}				
			} catch (InputMismatchException e) {
				System.out.println("INPUT MISMATCH!");
			}
		}
	}

}
