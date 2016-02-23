package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.yaml.snakeyaml.Yaml;

import message.Broker;
import message.Message;
import message.MessagePasser;

public class Locker implements DistributedApplication{
	enum State {
		hold,release,want
	};
	
	static String myGroup; 		//TODO: grace read my group info
	static int myGroupSize = 3; //TODO: grace read my group info
	
	static Queue<String> queue = new LinkedList<>(); 	// queue of src
	static State state = State.release; 				// current state
	static int counter = 0; 		// ack counter
	static boolean voting = false; 	// voting or not
	
	static boolean enabled = false; //used to be compatible with existing code
	
	static MultiCaster multiCaster;
		
	public Locker(String configFilename, Broker broker,MultiCaster mc) {
		this.parseConfigFile(configFilename);
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
	 * parseConfigFile
	 * @param filename
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void parseConfigFile(String filename) {
		InputStream input;
		try {
			input = new FileInputStream(new File(filename));
			Yaml yaml = new Yaml();

			Map config = (Map) yaml.load(input);
			List<Map> network = (List<Map>) config.get("configuration");
			for (Map map : network) {
				String name = (String) map.get("name");
				if (name.equals(MessagePasser.getLocalName())) {
					myGroup = (String) map.get("memberOf");
				}
			}
			System.out.println("myGroup = " + myGroup);
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
		}
	}
	/**
	 * functionalities
	 */
	public static void onEvent(String eventType, String src){
		//if (src.equals(MessagePasser.getLocalName()))
//			return;
		Log.verbose("Locker", "event:"+eventType+" from:"+src);
		if (eventType.equals("request")){			
			if (state == State.hold || voting){
				// cannot vote, wait
				queue.add(src);
				Log.info("Locker", "request received, cannot vote, waiting.. qsize = "+queue.size());	
			} else {
				Log.info("Locker", "request received, vote for "+src);
				
				//vote for that request's src
				Message message = new Message(src, "ack", "ack");
				MessagePasser.send(message);
				voting = true;
			}
		} else if (eventType.equals("release")){
			if (queue.size()!=0){
				String waitingReq = queue.poll();
				Message message = new Message(waitingReq, "ack", "ack");
				
				Log.info("Locker", "release received, send ack to first waiting requester:"+waitingReq);
				MessagePasser.send(message);
				voting = true;				
			} else {
				Log.info("Locker", "release received, no one waiting, voting = false");
				voting = false;
			}
		} else if (eventType.equals("ack")){
			Log.info("Locker", "ack received from "+src+", counter = "+counter);
			
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
		Log.verbose("Locker", "sending request to all in group "+myGroup);

		//multicast to all member in my group, wait for acks
		counter = 0;
		multiCaster.R_MultiCast(myGroup, new Message(null, "request", "request"));
	}
	
	/**
	 * Release the lock
	 */
	public static void releaseLock(){
		if (state != State.hold){
			System.out.println("Not in CS");
			return;
		}		
		Log.verbose("Locker", "sending release to all in group "+myGroup);
		state = State.release;
		
		//multicast to all member in my group
		multiCaster.R_MultiCast(myGroup, new Message(null, "release", "release"));
	}

	/**
	 * distapp
	 */
	@Override
	public void OnMessage(Message msg) {
		if (msg.getKind().equals("ack")){
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
