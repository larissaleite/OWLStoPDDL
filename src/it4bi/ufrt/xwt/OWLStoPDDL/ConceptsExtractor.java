package it4bi.ufrt.xwt.OWLStoPDDL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConceptsExtractor {
	
	private HashMap<String, List<String>> concepts = new HashMap<String, List<String>>();
	
	private OWLSParser parser;

	public ConceptsExtractor(String filePath) {
		parser = new OWLSParser(filePath);
	
		extractInputs();
		System.out.println();
		extractOutputs();
	}
	
	private void extractInputs() {
		String inputs = parser.extractProcessInput();
		System.out.println("input(s)");
		List<String> extractedInputs = extractNames(inputs);
		concepts.put("inputs", extractedInputs);
	}

	private void extractOutputs() {
		String outputs = parser.extractProcessOutput();
		System.out.println("outputs");
		List<String> extractedOutputs = extractNames(outputs);
		concepts.put("output", extractedOutputs);
	}
	
	private List<String> extractNames(String concept) {
		List<String> extracts = new ArrayList<String>();
		
		String[] splitConcept = concept.split("\n");
		
		for (String value : splitConcept) {
			int idx = value.indexOf("#");
			String strAfterHash = value.substring(idx+1);
			int spaceIdx = strAfterHash.indexOf(" ");
			String name = strAfterHash.substring(0, spaceIdx);
			extracts.add(name);
			
			System.out.println(name);
		}
		
		return extracts;
	}

}
