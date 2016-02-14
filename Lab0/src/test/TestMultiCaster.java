package test;

import java.util.InputMismatchException;
import java.util.Scanner;

import application.LogEntry;
import application.Logger;
import application.MultiCaster;
import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Broker;
import message.Message;
import message.MessagePasser;

public class TestMultiCaster {
	public static void  main(String[] args) throws InterruptedException {
		// Read arguments
		if(args.length != 2) {
			System.out.println("Usage : TestDriver <config file path> <Local Name>");
			System.exit(1);
		}

		// init MessagePasser, read configuration file and start listening
		System.out.println("Welcome to team22-lab1");

		// TODO : clean this mess, all sorts of init just make no sense
		ClockServiceFactory.setClockService("Vector",4);
		MessagePasser.init(args[0], args[1]);
		Broker broker = new Broker();
		Logger logger = new Logger(args[0], args[1]);
		MultiCaster multiCaster = new MultiCaster(args[0], args[1]);
		
		broker.register("Logger", logger);
		
		// simple UI
		boolean done = false;
		while (!done) {
			Thread.sleep(300);
			System.out.println("=======================");
			System.out.println("1. send a log");
			System.out.println("2. dump all logs");
			System.out.println("3. show current time");
			System.out.println("4. send normal message");
			System.out.println("5. issue a timestamp");
			System.out.println("6. multicast a message");
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
					System.out.print("kind:Logger\n");
					String kind = "Logger";//scanner.next();
					System.out.print("logcontent:");
					String payload = scanner.next();
					LogEntry logEntry = new LogEntry(payload);
					message = new Message(dest, kind, logEntry);
					MessagePasser.send(message);
					break;
				case 2:
					System.out.println("Current log file is as follows!");
					logger.dumpLog();

					try {
						while (true){
							System.out.println("Compare?");
			
							int s1 = scanner.nextInt();
							int s2 = scanner.nextInt();
							LogEntry m1 = Logger.msglog.get(s1);
							LogEntry m2 = Logger.msglog.get(s2);
							System.out.println("Result:"+m1.compareTo(m2));
						}
										
					} catch (InputMismatchException e) {
						System.out.println("Done");
					}
					break;
				case 3:
					TimeStamp ts = ClockServiceFactory.getClockService().getTimeStamp();
					System.out.println("Current timeStamp is" + ts);
					break;
				case 4:
					System.out.print("destination(name):");
					dest = scanner.next();
					System.out.print("kind:");
					kind = scanner.next();
					System.out.print("content:");
					payload = scanner.next();
					message = new Message(dest, kind, payload);
					MessagePasser.send(message);				
					break;
				case 5:
					ClockServiceFactory.getClockService().issueTimeStamp();				
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
