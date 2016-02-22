package application;

import java.util.LinkedList;
import java.util.Queue;

import message.Broker;
import message.Message;

public class Locker implements DistributedApplication{
	enum State {
		hold,release
	};
	
	static Queue<String> queue = new LinkedList<>(); // queue of src
	static State state = State.release; // current state
	static int counter = 0;
	static boolean enabled = false;
		
	public Locker(Broker broker) {
		broker.register("ack", this);
		enable();
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
	public static void onMessage(String message, String src){
		if (message.equals("request")){
			
		} else if (message.equals("release")){
			
		} else if (message.equals("ack")){
			
		}
	}
	
	public static void requestLock(){
		if (state == State.hold){
			System.out.println("Currently in CS");
			return;
		}
	}
	
	public static void releaseLock(){
		if (state != State.hold){
			System.out.println("Not in CS");
			return;
		}		
	}

	/**
	 * distapp
	 */
	@Override
	public void OnMessage(Message msg) {
		if (msg.equals("ack")){
			onMessage((String)msg.getData(), msg.getSrc());
		}
	}


	@Override
	public String getAppName() {
		return "Locker";
	}
}
