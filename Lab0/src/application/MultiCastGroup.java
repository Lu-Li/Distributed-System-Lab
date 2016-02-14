package application;

import java.util.List;
import java.util.Map;

public class MultiCastGroup {
	private String name;
	private List<String> members;
	
	public MultiCastGroup() {
		Log.error("MultiCastGroup", "default initializer not implemented");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MultiCastGroup(Map config) {
		name = (String)config.get("name");
		members = (List<String>)config.get("members");
	}
	
	public String getName() {
		return name;
	}
		
	public List<String> getAllMembers() {
		return members;
	}
	
	public String getMembersByIndex(int index) {
		return members.get(index);
	}
	
	public int getGroupSize() {
		return members.size();
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		stringBuilder.append(" = [");
		for (String s : members)
			stringBuilder.append(s+" ");
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
