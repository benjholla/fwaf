package fwaf;

import fwaf.fsm.State;

public class SessionState extends State {

	private long timestamp;

	public SessionState(String uri, long timestamp) {
		super(uri);
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
