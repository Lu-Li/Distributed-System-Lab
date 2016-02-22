package application;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import message.Broker;
import message.Message;
import message.MessagePasser;

public class Locker implements DistributedApplication{
	enum State {
		hold,release,want
	};
	
	static String myGroup; //TODO: grace read my group info
	static int myGroupSize = 3; //TODO: grace read my group info
	
	static Queue<String> queue = new LinkedList<>(); // queue of src
	static State state = State.release; // current state
	static int counter = 0; // ack counter
	static boolean voting = false; // voting or not
	
	static boolean enabled = false; //used to be compatible with existing code
	
	static MultiCaster multiCaster;
		
	public Locker(Broker broker,MultiCaster mc) {
		broker.register("ack", this);
		enable();
		multiCaster = mc;
	}
	
	
	/**
	 * enable/disable
	 */
	public static void enable(){
		enabled = true;
	}
	
	public static void disable(){
		enabled = false;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * functionalities
	 */
	public static void onEvent(String eventType, String src){
		if (eventType.equals("request")){
			Log.info("Locker", "request received");
			
			if (state == State.hold || voting){
				// cannot vote, wait
				queue.add(src);
			} else {
				//vote for that request's src
				Message message = new Message(src, "ack", "no payload");
				MessagePasser.send(message);
				voting = true;
			}
		} else if (eventType.equals("release")){
			Log.info("Locker", "release received");
			
			if (queue.size()!=0){
				String waitingReq = queue.poll();
				Message message = new Message(waitingReq, "ack", "no payload");
				MessagePasser.send(message);
				voting = true;				
			} else {
				voting = false;
			}
		} else if (eventType.equals("ack")){
			Log.info("Locker", "ack received, now = "+counter);
			
			counter++;
			if (counter == myGroupSize){
				state = State.hold;
				System.out.println("Holding lock!");
			}
		}
	}
	
	/**
	 * Request for lock
	 */
	public static void requestLock(){
		if (state == State.hold){
			System.out.println("Currently in CS");
			return;
		}
		state = State.want;		
		//multicast to all member in my group, wait for acks
		counter = 0;
		multiCaster.R_MultiCast(myGroup, new Message(null, "request", "no payload"));
	}
	
	/**
	 * Release the lock
	 */
	public static void releaseLock(){
		if (state != State.hold){
			System.out.println("Not in CS");
			return;
		}		
		state = State.release;
		//multicast to all member in my group
		multiCaster.R_MultiCast(myGroup, new Message(null, "release", "no payload"));
	}

	/**
	 * distapp
	 */
	@Override
	public void OnMessage(Message msg) {
		if (msg.equals("ack")){
			onEvent((String)msg.getData(), msg.getSrc());
		}
	}


	@Override
	public String getAppName() {
		return "Locker";
	}


	public static void reportStatus() {
		switch (state) {
		case release:
			System.out.println("Lock released, Voting = "+voting);
			break;
		case hold:
			System.out.println("Holding lock, Voting = "+voting);
			break;		
		default:
			System.out.println("Want lock, Voting = "+voting);
			break;
		}
	}
}
