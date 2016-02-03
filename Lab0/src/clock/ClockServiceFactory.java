package clock;

public class ClockServiceFactory {
	
	public ClockService getClockService (String clockType, TimeStamp ts){
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
}
