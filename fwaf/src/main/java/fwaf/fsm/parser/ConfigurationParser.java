package fwaf.fsm.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fwaf.fsm.KripkeStructure;
import fwaf.fsm.State;

public class ConfigurationParser {

	public static KripkeStructure parse(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		Document xml = db.parse(xmlFile);
		
		ArrayList<State> states = new ArrayList<State>();
		Map<State,Set<State>> transitions = new HashMap<State,Set<State>>();
		
		NodeList stateElements = xml.getElementsByTagName("state");
		for (int i = 0; i < stateElements.getLength(); i++) {
			Node node = stateElements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element stateElement = (Element) node;
				State currentState = new State(stateElement.getAttribute("uri"));
				states.add(currentState);
				for (int j = 0; j < stateElement.getChildNodes().getLength(); j++) {
					Node child = stateElement.getChildNodes().item(j);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element) child;
						if(childElement.getTagName().equals("transition")){
							Element transition = childElement;
							State nextState = new State(transition.getAttribute("uri"));
							if(transitions.containsKey(currentState)){
								transitions.get(currentState).add(nextState);
							} else {
								Set<State> transitionStates = new HashSet<State>();
								transitionStates.add(nextState);
								transitions.put(currentState, transitionStates);
							}
						}
					}
				}
			}
		}
		
		// sanity check - all transition states are known states
		for(Set<State> transitionStates : transitions.values()){
			for(State transitionState : transitionStates){
				if(!transitions.containsKey(transitionState)){
					throw new IllegalArgumentException("Malformed input: transition state " + transitionState.getUri() + " does not exist.");
				}
			}
		}
		
		// sanity check - all states have a future transition
		for(State state : transitions.keySet()){
			if(transitions.get(state).isEmpty()){
				throw new IllegalArgumentException("Malformed input: state " + state.getUri() + " does not have a transition state.");
			}
		}
		
		State[] stateArray = new State[states.size()];
		states.toArray(stateArray);
		return new KripkeStructure(stateArray, transitions);
	}

}
