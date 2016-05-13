package it4bi.ufrt.xwt.OWLStoPDDL;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Implementations of DOM API methods in our project context.
 *
 */

public class DOMUtils {

	/**
	 * Returns a new instance of DocumentBuilderFactory
	 * 
	 * @return dBuilder
	 */

	public static DocumentBuilder getDocumentBuilder() {
		DocumentBuilder dBuilder = null;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return dBuilder;
	}

	/**
	 * Get a Document object from the given input file
	 * 
	 * @param filePath
	 * @return document
	 */

	public static Document getDocument(String filePath) {
		File inputFile = new File(filePath);

		Document document = null;

		try {
			document = getDocumentBuilder().parse(inputFile);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		document.getDocumentElement().normalize();

		return document;
	}
} // End of Document