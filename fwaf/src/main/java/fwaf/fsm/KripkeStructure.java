package fwaf.fsm;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fwaf.fsm.parser.StripComments;

public class KripkeStructure {

	
	
	// on second thought...this parsing SMART input was a bad idea
	// it won't support the state route attributes that we need
	// and its grammar is pretty complex...better to just make our own format
	// and allow exporting to SMART module checker format
//	/**
//	 * Parses a SMART model checker input program for the kripke structure contents
//	 * @param smartFSM
//	 * @throws IOException  
//	 */
//	public KripkeStructure(File smartFSM) throws IOException {
//		// really need a full fledged lexer to do this...
//		// for now just using a lexer to strip the comments
//		String program = StripComments.stripComments(smartFSM);
//		
//		// replace space following keyword with underscore
//		Set<String> keywords = new HashSet<String>();
//		keywords.add("state");
//		keywords.add("stateset");
//		for(String keyword : keywords){
//			program = program.replace(keyword + " ", keyword + "_");
//		}
//		
//		// strip all whitespace
//		program = program.replaceAll("\\s+","");
//		
//		// put each item following {,},;,keywords on its own line 
//		program = program.replaceAll(";", ";\n");
//		program = program.replaceAll("\\{", "{\n");
//		program = program.replaceAll("\\}", "}\n");
//		
//		// restore keyword spaces
//		for(String keyword : keywords){
//			program = program.replace(keyword + "_", keyword + " ");
//		}
//		
//		// trim whitespace before and after program
//		program = program.trim();
//	}
	
}
