package test;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

import application.Locker;
import application.LogEntry;
import application.Logger;
import application.MultiCastGroup;
import application.MultiCaster;
import clock.ClockServiceFactory;
import clock.TimeStamp;
import message.Broker;
import message.Message;
import message.MessagePasser;
import message.TimestampedMessage;
import util.FileIO;

public class TestLocker {
	public static void  main(String[] args) throws InterruptedException {
		// Read arguments
		if(args.length != 2) {
			System.out.println("Usage : TestDriver <config file path> <Local Name>");
			System.exit(1);
		}

		// init MessagePasser, read configuration file and start listening
		System.out.println("Welcome to team22-lab1");

		//infrastructure
		ClockServiceFactory.setClockService("Vector",7);
		MessagePasser.init(args[0], args[1]);
		Broker broker = new Broker();
		
		//applications
		Logger logger = new Logger(args[0], args[1],broker);
		MultiCaster multiCaster = new MultiCaster(args[0], args[1],broker);
		Locker locker = new Locker(args[0], broker, multiCaster);
		
		// simple UI
		boolean done = false;
		while (!done) {
			Thread.sleep(300);
			System.out.println("=======================");
			System.out.println("1. enter CS");
			System.out.println("2. exit CS");
			System.out.println("3. check CS");
			System.out.println("other number: exit");
			System.out.println("=======================");
			Scanner scanner = new Scanner(System.in);

			try {
				int selection = scanner.nextInt();
				
				switch (selection) {
				case 1:
					Locker.requestLock();
					break;
				case 2:
					Locker.releaseLock();
					break;
				case 3:
					Locker.reportStatus();
					break;
				case 4:
					FileIO.readFromURL("https://s3.amazonaws.com/luli/ds/config.yaml", "./resources/config.yaml");
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
