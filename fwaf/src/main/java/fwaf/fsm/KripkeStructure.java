package fwaf.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KripkeStructure {

	private final State[] states;
	private final Map<State,Set<State>> transitions;
	
	public KripkeStructure(State[] states, Map<State,Set<State>> transitions){
		this.states = states;
		this.transitions = transitions;
	}
	
	public Set<State> getPreImage(State state){
		throw new RuntimeException("not implemented");
	}
	
	public Set<State> getPostImage(State state){
		return transitions.get(state);
	}
	
}
