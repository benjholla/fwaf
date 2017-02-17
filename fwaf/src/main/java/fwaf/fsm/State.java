package fwaf.fsm;

public class State {
	private String uri;
	private long timestamp;

	public State(String uri, long timestamp) {
		this.uri = uri;
		this.timestamp = timestamp;
	}

	public String getUri() {
		return uri;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
