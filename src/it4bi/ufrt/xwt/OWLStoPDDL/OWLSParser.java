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
			nodeList = (NodeList) xPath.compile(expression).evaluate(DOMUtils.getDocument(filePath),
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	private NodeList getNodeListFromCurrentNode(String expression, Node inputNode) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;

		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(inputNode, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	private Node getNode(String expression, Node inputNode) {
		XPath xPath = XPathFactory.newInstance().newXPath();

		Node node = null;

		try {
			node = (Node) xPath.compile(expression).evaluate(inputNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return node;
	}

	public String extractService() {
		String expression = "//*[local-name()='Service']";

		String service = null;

		NodeList nodeList = getNodeList(expression);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				service = element.getAttribute("rdf:ID");
			}
		}
		return service;
	}

	public String extractParameters() {
		String expression = "//*[local-name()='AtomicProcess']/*[contains(local-name(), 'has')]";
		String parameters = ":parameters(";

		NodeList nodeList = getNodeList(expression);

		if (nodeList.getLength() != 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) node;
					if (element.getNodeName() == "process:hasPrecondition") {
						String predicate = element.getAttribute("rdf:resource").replace("#", "");
						String argexpression = "//*[local-name()='SWRL-Condition'][@ID='" + predicate
								+ "']//*[contains(local-name(), 'argument')]";
						parameters = extractParamsfromPrecondtionsAndResults(parameters, argexpression);
					}else if (element.getNodeName() == "process:hasResult") {
						String predicate = element.getAttribute("rdf:resource").replace("#", "");
						String argexpression = "//*[local-name()='Result'][@ID='" + predicate
								+ "']//*[contains(local-name(), 'argument')]";
						parameters = extractParamsfromPrecondtionsAndResults(parameters, argexpression);

					} else {
						parameters += "?" + element.getAttribute("rdf:resource").replace("#_", "").toLowerCase();
					}
					if (i < nodeList.getLength() - 1) {
						parameters += " ";
					}
				}
			}
			parameters += ")\n";
		}

		return parameters;
	}

	private String extractParamsfromPrecondtionsAndResults (String parameters, String expression) {
		NodeList argList = getNodeList(expression);
		for (int j = 0; j < argList.getLength(); j++) {
			Node childArg = argList.item(j);
			if (childArg.getNodeType() == Node.ELEMENT_NODE) {
				parameters += "?" + getParamFromText(((Element) childArg).getAttribute("rdf:resource"))
						.toLowerCase();
			}
			if (j < argList.getLength() - 1) {
				parameters += " ";
			}
		}
		return parameters;
	}

	public String extractProcessInput() {
		String expression = "//*[local-name()='Input']/*[local-name()='parameterType']";

		String input = "";
		String input_param = "";
		NodeList nodeList = getNodeList(expression);

		if (nodeList.getLength() != 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					input += "\t(" + element.getTextContent();
					input_param = getParamFromText(input);
					input += " ?" + input_param + ")";
					if (i < nodeList.getLength() - 1) {
						input += "\n";
					}
				}
			}
			input = input.replace("http://127.0.0.1/", "");
			input += "\n";
		}

		return input;
	}

	private String getParamFromText(String textContent) {
		String param;
		param = textContent.substring(textContent.lastIndexOf("#") + 1)
				.replaceAll("[^\\p{L}\\p{Nd}]+", "")
				.toLowerCase();
		return param;
	}

	public String extractProcessPreConditions() {

		String expression = "//*[local-name()='SWRL-Condition']//*[contains(local-name(), 'IndividualPropertyAtom')]";
		String predicateExpression = ".//*[contains(local-name(), 'propertyPredicate')]";
		String argExpression = ".//*[contains(local-name(), 'argument')]";

		NodeList nodeList = getNodeList(expression);
		String preConditions = "";
		String preconditions_param = "";

		if (nodeList.getLength() != 0) {

			for (int i = 0; i < nodeList.getLength(); i++) {
				preConditions += "\t(";
				Node node = nodeList.item(i);

				Node predicateNode = getNode(predicateExpression, node);
				if (predicateNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) predicateNode;
					preConditions += getParamFromText(element.getAttribute("rdf:resource")) + " ";
				}

				NodeList argumentList = getNodeListFromCurrentNode(argExpression, node);
				for (int j = 0; j < argumentList.getLength(); j++) {
					Node argument = argumentList.item(j);
					if (argument.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) argument;
						preConditions += element.getAttribute("rdf:resource");
						preconditions_param = getParamFromText(preConditions);
						preConditions += " ?" + preconditions_param;
						if (j < argumentList.getLength() - 1) {
							preConditions += " ";
						}
					}
				}
				preConditions += ")";
			}
			preConditions = preConditions.replace("http://127.0.0.1/", "");
		}
		return preConditions;
	}

	public String extractProcessOutput() {
		String expression = "//*[local-name()='Output']/*[local-name()='parameterType']";

		String output = "";
		String output_param = "";
		NodeList nodeList = getNodeList(expression);

		if (nodeList.getLength() != 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					output += "\t(" + element.getTextContent();
					output_param = getParamFromText(output);
					output += " ?" + output_param + ")";
					if (i < nodeList.getLength() - 1) {
						output += "\n";
					}
				}
			}
			output = output.replace("http://127.0.0.1/", "");
			output += "";
		}
		return output;
	}

	public String extractProcessResults() {
		String expression = "//*[local-name()='Result']//*[contains(local-name(), 'IndividualPropertyAtom')]";
		String predicateExpression = ".//*[contains(local-name(), 'propertyPredicate')]";
		String argExpression = ".//*[contains(local-name(), 'argument')]";

		String results = "";
		String results_param = "";
		NodeList nodeList = getNodeList(expression);

		if (nodeList.getLength() != 0) {

			for (int i = 0; i < nodeList.getLength(); i++) {
				results += "\t(";
				Node node = nodeList.item(i);

				Node predicateNode = getNode(predicateExpression, node);
				if (predicateNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) predicateNode;
					results += getParamFromText(element.getAttribute("rdf:resource")) + " ";
				}

				NodeList argumentList = getNodeListFromCurrentNode(argExpression, node);
				for (int j = 0; j < argumentList.getLength(); j++) {
					Node argument = argumentList.item(j);
					if (argument.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) argument;
						results += element.getAttribute("rdf:resource");
						results_param = getParamFromText(results);
						results += " ?" + results_param;
						if (j < argumentList.getLength() - 1) {
							results += " ";
						}
					}
				}
				results += ")";
			}

			results = results.replace("http://127.0.0.1/", "");
		}
		return results;
	}
}