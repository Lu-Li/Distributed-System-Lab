package clock;

import java.util.ArrayList;
import java.util.List;

import driver.Log;

public class VectorTimeStamp extends TimeStamp {
	// the vector, a list of integer with the size equal to number of nodes
	private List<Integer> vector; 
	
	public VectorTimeStamp() {
		Log.error("TimeStamp", "default initializer not implemented");
	}
	
	/**
	 * initializer
	 * @param size number of nodes in config file
	 */
	public VectorTimeStamp(int size) {
		vector = new ArrayList<>(size);
	}
	
	
	/**
	 * increment the logical time for a vector index
	 * @param position : index of the logical time
	 */
	public void incrementVectorItem(int position) {
		vector.set(position, vector.get(position)+1);
	}
	
	/**
	 * get the logical time for a vector index
	 * @param position : index of the logical time
	 */
	public int getVectorItem(int index) {
		return vector.get(index).intValue();
	}
	
	/**
	 * set the logical time for a vector index
	 * @param position : index of the logical time
	 */
	public void setVectorItem(int position, int time) {
		vector.set(position, time);
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
