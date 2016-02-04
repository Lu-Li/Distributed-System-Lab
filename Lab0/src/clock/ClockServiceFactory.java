package clock;

public class ClockServiceFactory {
	// TODO: is this function needed? given a initial timestamp value?
	public static ClockService getClockService (String clockType, TimeStamp ts){
	      if(clockType == null) {
	         return null;
	      }		
	      if(clockType.equalsIgnoreCase("Logical")) {
	         return new LogicalClockService(ts);	         
	      } else if(clockType.equalsIgnoreCase("Vector")) {
	         return new VectorClockService(ts);         
	      }	      
	      return null;
	   }
	
	public static ClockService getClockService (String clockType){
	      if(clockType == null) {
	         return null;
	      }		
	      if(clockType.equalsIgnoreCase("Logical")) {
	         return new LogicalClockService();	         
	      } else if(clockType.equalsIgnoreCase("Vector")) {
	         return new VectorClockService();         
	      }	      
	      return null;
	   }
}
