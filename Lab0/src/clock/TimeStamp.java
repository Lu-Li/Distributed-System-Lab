package clock;

import java.io.Serializable;

public abstract class TimeStamp implements Comparable<TimeStamp>,Serializable {
	private static final long serialVersionUID = 1L;
	// identical, not only parallel
	public abstract boolean isIdenticalTo(TimeStamp o);
}
