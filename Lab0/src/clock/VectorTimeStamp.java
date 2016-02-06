package clock;

import java.util.ArrayList;
import java.util.List;

import driver.Log;

public class VectorTimeStamp extends TimeStamp {

	private List<Integer> vector; 
	
	public VectorTimeStamp() {
		Log.error("TimeStamp", "default initializer not implemented");
	}
	
	
	public VectorTimeStamp(int size) {
		vector = new ArrayList<>(size);
	}
	
	
	public void increment(int position) {
		vector.set(position, vector.get(position)+1);
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof VectorTimeStamp){
			//get vector of the other timestamp
			VectorTimeStamp vectorTimeStamp = (VectorTimeStamp)o;			
			List<Integer> otherVector = vectorTimeStamp.vector;
			
			//exceptions			
			if (vector.size()!=otherVector.size()){
				Log.error("VectorTimeStamp", "length mismatch!");
				throw new ClassCastException();
			}
			
			//element-wise comparation
			boolean smaller=false, larger=false;
			for (int i = 0; i<vector.size(); i++){
				if (vector.get(i).intValue() < otherVector.get(i).intValue())
					smaller = true;
				if (vector.get(i).intValue() < otherVector.get(i).intValue())
					larger = true;
			}
			if (smaller && !larger)
				return -1;
			if (!smaller && larger)
				return 1;
			return 0;
		} else throw new ClassCastException();
	}

}
