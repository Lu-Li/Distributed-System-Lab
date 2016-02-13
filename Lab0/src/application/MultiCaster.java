package application;

import message.Message;

public class MultiCaster implements DistributedApplication{
	String Message_B_MultiCast = "B_MultiCast";
	String Message_B_Deliver = "B_Deliver";
	//....
	
	void B_MultiCast(){
		
	}
	
	void B_Deliver(){
		
	}
	
	void R_MultiCast(){
		
	}
	
	void R_Deliver(){
		
	}

	void CO_MultiCast(){
		
	}
	
	void CO_Deliver(){
		
	}

	@Override
	public void OnMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAppName() {
		return "MultiCaster";
	}
	
	
}
