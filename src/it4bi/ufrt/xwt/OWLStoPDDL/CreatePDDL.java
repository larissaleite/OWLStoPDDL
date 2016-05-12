package it4bi.ufrt.xwt.OWLStoPDDL;

public class CreatePDDL {
	
	private OWLSParser parser;
	private String PDDLContent;
	
	public CreatePDDL(String filePath, String fileName) {
		this.parser = new OWLSParser(filePath);
		
		this.defineAction();
		this.defineParameters();
		this.definePreconditions();
		this.defineEffects();
		
		this.writeFile(filePath, fileName);
	}
	
	private void defineAction() {
		String service = parser.extractService();
		String action = "(action "+service;
		
		PDDLContent = action + "\n";

		System.out.println(action);
	}
	
	private void defineParameters() {
		//don't know how?
		String parameters = ":parameters(?b)";
		PDDLContent += parameters + "\n";
		
		System.out.println(parameters);
	}
	
	private void definePreconditions() {
		String preConditions = ":precondition(";
		
		preConditions += parser.extractProcessPreConditions() + ")";
		PDDLContent += preConditions;

		System.out.println(preConditions);
	}
	
	private void defineEffects() {
		String effect = ":effect(";
		
		String outputs = parser.extractProcessOutput();
		effect += outputs + "\n";
		
		String results = parser.extractProcessResults();
		effect += results + "\n";
		
		effect += "))" + "\n"; //closing effects and service
		
		PDDLContent += effect;
		
		System.out.println(effect);
	}
	
	private void writeFile(String filePath, String fileName) {
		PDDLFileWriter fileWriter = new PDDLFileWriter();
		String p= filePath.substring(0, filePath.length()-(fileName.length()));
		fileName = fileName.replace(".owls", ".pddl");
		String path = p+fileName;
		
		fileWriter.writePDDLFile(path, PDDLContent);
	}

}
