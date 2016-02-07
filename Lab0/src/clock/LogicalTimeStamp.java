package clock;

import java.util.ArrayList;
import java.util.List;

import application.Log;

public class LogicalTimeStamp extends TimeStamp{

	private Integer logicalTime = 0;
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof LogicalTimeStamp){
			//get vector of the other timestamp
			LogicalTimeStamp logicalTimeStamp = (LogicalTimeStamp)o;			
			Integer otherTime = logicalTimeStamp.logicalTime;
			
			return logicalTime.compareTo(otherTime);
		} else throw new ClassCastException();
	}

}
