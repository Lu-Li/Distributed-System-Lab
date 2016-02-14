package message;

import java.io.Serializable;

public class Message implements Serializable {
	// serialization version 1. 
	private static final long serialVersionUID = 1L;
	
	protected String src;
	protected String dest;
	protected String kind;
	protected Integer seqNum;
	protected Object data;
	
	/**
	 * Initializers
	 * @param dest : destination of message as a name in the config file 
	 * @param kind : kind of message that would be feed into Broker
	 * @param data : payload, determined by kind
	 */
	public Message(String dest, String kind, Object data){
		this.dest = dest;
		this.kind = kind;
		this.data = data;
	}
	
	public Message(Message message) {
		this.dest = message.dest;
		this.kind = message.kind;
		this.data = message.data;
	}
	
	
	/**
	 * Setters
	 * These settors are used by MessagePasser.send( ), not your app
	 */
	public void set_source(String source){ // name of sending process
		this.src = source;
	}
	
	public void set_seqNum(int sequenceNumber){
		this.seqNum = sequenceNumber;
	}
	
	public String getSrc() {
		return src;
	}
	public String getDest() {
		return dest;
	}
	public String getKind() {
		return kind;
	}
	public Integer getSeqNum() {
		return seqNum;
	}
	public Object getData() {
		return data;
	}
	// other accessors, toString, etc as needed
	public boolean isValid(){
		return (src!=null && seqNum!=null);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("MESSAGE #");
		stringBuilder.append(seqNum);
		stringBuilder.append(" :");
		stringBuilder.append(src);
		stringBuilder.append(" -> ");
		stringBuilder.append(dest);
		stringBuilder.append(", kind = ");
		stringBuilder.append(kind);		
		stringBuilder.append(", data = ");
		stringBuilder.append(data);
		return stringBuilder.toString();
	}
}