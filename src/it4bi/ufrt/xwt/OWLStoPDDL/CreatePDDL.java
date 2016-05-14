package it4bi.ufrt.xwt.OWLStoPDDL;

public class CreatePDDL {

	/**
	 * This class implements the high level logic to create the PDDL file from
	 * any OWLS file.
	 */

	private OWLSParser parser;
	private String PDDLContent;

	/**
	 * The various sections of the PDDL output file is defined in this method.
	 * 
	 * @param filePath
	 * @param fileName
	 */
	public CreatePDDL(String filePath, String fileName) {
		this.parser = new OWLSParser(filePath);

		this.defineAction();
		this.defineParameters();
		this.definePreconditions();
		this.defineEffects();

		this.identText();

		this.writePDDLFile(filePath, fileName);
	}

	private void defineAction() {
		String service = parser.extractService();
		String action = "(action " + service;

		PDDLContent = action + "\n";
	}

	private void defineParameters() {
		String parameters = parser.extractParameters();
		PDDLContent += parameters;
	}

	private void definePreconditions() {
		String preConditions = ":precondition (and" + "\n";

		String inputs = parser.extractProcessInput();
		preConditions += inputs;

		String processPreConditions = parser.extractProcessPreConditions();
		if (processPreConditions.equals("")) {
			System.out.println("vazio");
		}
		preConditions += processPreConditions;

		if (checkAnd(preConditions)) {
			preConditions = preConditions.replace(":precondition (and\n\t", ":precondition ");
		} else {
			preConditions = preConditions.substring(0, preConditions.lastIndexOf('\n'));
			preConditions += ")";
		}

		PDDLContent += preConditions + "\n";
	}

	private void defineEffects() {
		String effect = ":effect (and" + "\n";

		String outputs = parser.extractProcessOutput();
		effect += outputs;

		effect += "\n";

		String results = parser.extractProcessResults();
		effect += results;

		if (checkAnd(effect)) {
			effect = effect.replace(":effect (and\n\t", ":effect ");
		} else {
			effect = effect.substring(0,  effect.lastIndexOf("\n"));
			effect += ")"; // closing effects
		}

		effect += "\n)"; // closing service

		PDDLContent += effect;
	}

	/**
	 * Checks if the output contains an unnecessary "and"
	 * 
	 * @param str
	 * @return
	 */
	private boolean checkAnd(String str) {
		int count = str.length() - str.replace(")", "").length();
		if (count == 1) {
			return true;
		}
		return false;
	}

	/**
	 * Adds the appropriate spaces to the output content
	 */
	private void identText() {
		String[] linesArray = PDDLContent.split("\n");

		linesArray[0] += "\n";

		for (int i = 1; i < linesArray.length - 1; i++) {
			linesArray[i] = "\t" + linesArray[i] + "\n";
		}

		PDDLContent = "";

		for (int i = 0; i < linesArray.length; i++) {
			if (!linesArray[i].equals("\n") && !linesArray[i].equals("\t") && !linesArray[i].equals("\t\n"))
				PDDLContent += linesArray[i];
		}
	}

	/**
	 * Writes the PDDL file to disk with the generated PDDL content
	 * 
	 * @param filePath
	 * @param fileName
	 */
	private void writePDDLFile(String filePath, String fileName) {
		PDDLFileWriter fileWriter = new PDDLFileWriter();
		filePath = filePath.substring(0, filePath.length() - (fileName.length()));
		fileName = fileName.replace(".owls", ".pddl");
		String path = filePath + fileName;

		fileWriter.writePDDLFile(path, PDDLContent);

		System.out.println(PDDLContent);
		System.out.println();
	}
} // End of Document