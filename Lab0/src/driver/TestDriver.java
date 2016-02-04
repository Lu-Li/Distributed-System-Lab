package driver;

import java.util.*;
import java.util.zip.Inflater;

import clock.ClockService;
import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Message;
import message.MessagePasser;
import socket.Listener;

public class TestDriver {

	public static void main(String[] args) throws InterruptedException {
		// Read arguments
		if(args.length != 3) {
			System.out.println("Usage : TestDriver <config file path> <Local Name> <Clock Service Type>");
			System.exit(1);
		}
		// init MessagePasser, read configuration file and start listening
		System.out.println("Welcome to team22-lab1");
		ClockService cs = ClockServiceFactory.getClockService(args[2]);
		MessagePasser.init(args[0], args[1]);
		Logger logger = new Logger(args[0], args[1]);

		// simple UI
		boolean done = false;
		while (!done) {
			Thread.sleep(300);
			System.out.println("=======================");
			System.out.println("1. send a Message to a logger");
			System.out.println("2. Save a Message to logger");
			System.out.println("3. dump all messages");
			System.out.println("4. show timestamp");
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
				case 2:
					// TODO: can driver choose save which message to which logger or just the one init by this driver?
					// QUESTION: logger is init in this testDriver?
					// QUESTION: is the last message receive from MessagePasser?
					message = MessagePasser.receive();
					if (message == null){
						System.out.println("No message to save!");
						break;
					}
					logger.saveMsg(message);
					System.out.print("save message from " + message.getSrc() + "successfully.");			
					break;
				case 3:
					System.out.println("Current log file is as follows!");
					logger.dumpMessage();
					break;
				case 4:
					TimeStamp ts = cs.getTimeStamp();
					System.out.println("Current timeStamp is" + ts);
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
