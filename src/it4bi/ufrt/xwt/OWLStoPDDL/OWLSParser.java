package it4bi.ufrt.xwt.OWLStoPDDL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The transformation logic for OWLS to PDDL implementation The logic is
 * implemented using DOM Parser API and XPath expressions.
 *
 */
public class OWLSParser {

	private String filePath;

	public OWLSParser(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * This method validate the Xpath expression on the DOM document object and
	 * returns the list of Nodes satisfying the given Xpath expression
	 * 
	 * @param expression
	 * @return nodeList
	 */

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

	/**
	 * Instead of validating the Xpath on the entire document, this method helps
	 * to validate the expression on a given node. (Often used to validate the
	 * expression on the context node)
	 * 
	 * @param expression
	 * @param inputNode
	 * @return nodeList
	 */
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

	/**
	 * If the Xpath expression is specific to return a single node, this method
	 * validate the expression on a given node (often used to validate the
	 * expression on the context node)
	 * 
	 * @param expression
	 * @param inputNode
	 * @return node
	 */

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

	/**
	 * This method helps to extract the parameters name for a precondition or
	 * result from the corresponding textContent in OWLS file
	 * 
	 * @param textContent
	 * @return param
	 */

	private String getParamFromText(String textContent) {
		String param;
		param = textContent.substring(textContent.lastIndexOf("#") + 1).replaceAll("[^\\p{L}\\p{Nd}]+", "")
				.toLowerCase();
		return param;
	}

	/**
	 * The ID attribute of the Service element is extracted from the OWLS
	 * file.This will be mapped to the action element of the PDDL output.
	 * 
	 * @return service
	 */

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

	/**
	 * The parameters of the PDDL output is extracted from the AtomicProcess
	 * node in the OWLS input file. If it has a precondition or result nodes,
	 * the corresponding parameters has to be identified from the implementation
	 * nodes (SWRL-Condition or Results) in the OWLS file
	 * 
	 * @return parameters
	 */
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
					} else if (element.getNodeName() == "process:hasResult") {
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

	/**
	 * This method is used inside the extractParameters method, and it helps to
	 * identify the parameters from the 'resource' attribute of a set of nodes
	 * identified by the Xpath expression.
	 * 
	 * @param parameters
	 * @param expression
	 * @return parameters
	 */

	private String extractParamsfromPrecondtionsAndResults(String parameters, String expression) {
		NodeList argList = getNodeList(expression);
		for (int j = 0; j < argList.getLength(); j++) {
			Node childArg = argList.item(j);
			if (childArg.getNodeType() == Node.ELEMENT_NODE) {
				parameters += "?" + getParamFromText(((Element) childArg).getAttribute("rdf:resource")).toLowerCase();
			}
			if (j < argList.getLength() - 1) {
				parameters += " ";
			}
		}
		return parameters;
	}

	/**
	 * First part of the preconditions in the PDDL output are derived from the
	 * 'Input' nodes in OWLS file. There could be multiple Input nodes in the
	 * same OWLS file.
	 * 
	 * @return input
	 */

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

	/**
	 * The second part of the preconditions in PDDL file is extracted from the
	 * 'SWRL-Condition' node. There could be multiple occurrences of such nodes,
	 * and each of them might contain multiple preconditions.
	 * 
	 * @return preConditions
	 */
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

	/**
	 * The first part of the effect element in PDDL output is extracted from the
	 * 'Output' nodes in OWLS file. There could be multiple occurrences of such
	 * nodes.
	 * 
	 * @return output
	 */

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

	/**
	 * The second part of effect element in PDDL file is extracted from the
	 * 'Result' nodes of OWLS file. There could be multiple occurrences of such
	 * nodes, each containing one or more effects.
	 * 
	 * @return results
	 */
	public String extractProcessResults() {
		String expression = "//*[local-name()='Result']//*[contains(local-name(), 'IndividualPropertyAtom')]";
		String predicateExpression = ".//*[contains(local-name(), 'propertyPredicate')]";
		String argExpression = ".//*[contains(local-name(), 'argument')]";

		String results = "";
		String results_param = "";
		NodeList nodeList = getNodeList(expression);

		if (nodeList.getLength() != 0) {

			for (int i = 0; i < nodeList.getLength(); i++) {
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
						//results_param = getParamFromText(results);
						//results += " ?" + results_param;
						if (j < argumentList.getLength() - 1) {
							results += " ";
						}
					}
				}
				//results += ")";
			}

			results = results.replace("http://127.0.0.1/", "");
		}
		return results;
	}
} // End of Document