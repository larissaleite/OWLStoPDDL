package it4bi.ufrt.xwt.OWLStoPDDL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConceptsExtractor {
	
	private HashMap<String, List<String>> conceptsFile1 = new HashMap<String, List<String>>();
	private HashMap<String, List<String>> conceptsFile2 = new HashMap<String, List<String>>();
	
	private OWLSParser parserFile1;
	private OWLSParser parserFile2;

	public ConceptsExtractor(String file1, String file2) {
		parserFile1 = new OWLSParser(file1);
		parserFile2 = new OWLSParser(file2);
		
		extractInputs(parserFile1, conceptsFile1);
		System.out.println();
		extractOutputs(parserFile1, conceptsFile1);
		
		System.out.println();
		
		extractInputs(parserFile2, conceptsFile2);
		System.out.println();
		extractOutputs(parserFile2, conceptsFile2);
	}
	
	private void extractInputs(OWLSParser parser, HashMap<String, List<String>> concepts) {
		String inputs = parserFile1.extractProcessInput();
		System.out.println("input(s)");
		List<String> extractedInputs = extractNames(inputs);
		concepts.put("inputs", extractedInputs);
	}

	private void extractOutputs(OWLSParser parser, HashMap<String, List<String>> concepts) {
		String outputs = parserFile1.extractProcessOutput();
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
