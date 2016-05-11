package it4bi.ufrt.xwt.OWLStoPDDL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OWLSParser {
	
	private String filePath;
	
	public OWLSParser(String filePath) {
		this.filePath = filePath;
	}

	private NodeList getNodeList(String expression) {
		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList nodeList = null;
		
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(DOMUtils.getDocument(filePath), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	public String extractService() {
		String expression = "//*[local-name()='Service']";

		String service = null;
		
		NodeList nodeList = getNodeList(expression);

		// is it possible in this case for the list to have more than 1 node?
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				//System.out.println("Service ID : "+ element.getAttribute("rdf:ID"));
				service = element.getAttribute("rdf:ID");
			}
		}
		return service;
	}

	public void extractProfile() {

	}

	public void extractProcess() {

	}

	public String extractProcessInput() {
		String expression = "//*[local-name()='Input']/*[local-name()='parameterType']";

		String input = "";
		
		NodeList nodeList = getNodeList(expression);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				input += element.getTextContent();
				if (i < nodeList.getLength()-1) {
					input += "\n";
				}
			}
		}
		return input;
	}

	public String extractProcessPreConditions() {
		String expression = "//*[local-name()='SWRL-Condition']//*[contains(local-name(), 'argument')]";
		
		String preConditions = "";
		
		NodeList nodeList = getNodeList(expression);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				//System.out.println("resource ID : "+ element.getAttribute("rdf:resource"));
				preConditions += element.getAttribute("rdf:resource");
				if (i < nodeList.getLength()-1) {
					preConditions += "\n";
				}
			}
		}
		preConditions = preConditions.replace("http://127.0.0.1/", "");
		return preConditions;
	}

	public String extractProcessOutput() {
		String expression = "//*[local-name()='Output']/*[local-name()='parameterType']";
		
		String output = "";

		NodeList nodeList = getNodeList(expression);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				//System.out.println("Content : "+ element.getTextContent());
				output += element.getTextContent();
				if (i < nodeList.getLength()-1) {
					output += "\n";
				}
			}
		}
		output = output.replace("http://127.0.0.1/", "");
		return output;
	}

	public String extractProcessResults() {
		String expression = "//*[local-name()='SWRL-Expression']//*[contains(local-name(), 'argument')]";

		String results = "";
		
		NodeList nodeList = getNodeList(expression);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				//System.out.println("resource ID : "+ element.getAttribute("rdf:resource"));
				results += element.getAttribute("rdf:resource");
				if (i < nodeList.getLength()-1) {
					results += "\n";
				}
			}
		}
		results = results.replace("http://127.0.0.1/", "");
		return results;
	}

}
