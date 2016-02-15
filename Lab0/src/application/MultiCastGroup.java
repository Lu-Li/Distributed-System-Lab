package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clock.VectorTimeStamp;

public class MultiCastGroup {
	private String name;
	private List<String> members;
	private List<VectorTimeStamp> membersTimestamp;

	public MultiCastGroup() {
		Log.error("MultiCastGroup", "default initializer not implemented");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MultiCastGroup(Map config) {
		name = (String)config.get("name");
		members = (List<String>)config.get("members");
		
		//init V_i for each group g
		int size = members.size();
		membersTimestamp = new ArrayList<VectorTimeStamp>();
		for (int i=0;i<size;i++){			
			membersTimestamp.add(new VectorTimeStamp(size));
		}

	}
	
	public String getName() {
		return name;
	}
		
	public List<String> getAllMembers() {
		return members;
	}
	
	public int getIndexByName(String name){
		for (int i=0;i<members.size();i++)
			if (members.get(i).equals(name))
				return i;
		return -1;
	}
	
	public VectorTimeStamp getTimestampByName(String name) {
		for (int i=0;i<members.size();i++)
			if (members.get(i).equals(name))
				return membersTimestamp.get(i);
		return null;
	}
	
	public int getGroupSize() {
		return members.size();
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		stringBuilder.append(" = { ");
		for (int i=0;i<members.size();i++){
			stringBuilder.append(members.get(i));
			stringBuilder.append(":");
			stringBuilder.append(membersTimestamp.get(i));
			stringBuilder.append(" ");
		}
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}
}
