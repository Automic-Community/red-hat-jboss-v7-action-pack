package com.uc4.ara.feature.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.uc4.ara.common.unicode.UnicodeOutputStreamWriter;
import com.uc4.importexportservice.Result;

public class XmlUtil {
	public static Document createDocumentFromFile(File xmlFile)
			throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new FileReader(
				xmlFile)));
		return document;
	}

	public static Document createDocumentFromString(String xmlContent)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicID, String systemID) {

				return new InputSource(new StringReader(""));
			}
		});

		Document document = builder.parse(new InputSource(new StringReader(
				xmlContent)));

		return document;
	}

	public static Document createNewDocument() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = builder.newDocument();

		return document;
	}

	public static void updateNameNodeValue(String nodeName, String nodeValue,
			Document activity) throws Exception {
		Node plannedFromNode = XmlUtil.createNode(activity, nodeName,
				nodeValue.toString());
		Node oldPlannedFromNode = XmlUtil.findNodeByAttribute(activity,
				"Property", "name", nodeName);
		Node parentNode = oldPlannedFromNode.getParentNode();
		parentNode.replaceChild(plannedFromNode, oldPlannedFromNode);
	}

	public static void setIdentityAttribute(Document document,
			String attributeValue) {
		boolean isFound = false;
		NodeList propertyList = document.getElementsByTagName("Property");
		for (int i = 0; i < propertyList.getLength(); i++) {
			Node node = propertyList.item(i);
			for (int j = 0; j < node.getAttributes().getLength(); j++) {
				Node attribute = node.getAttributes().item(j);
				if (attribute.getNodeName().equals("name")
						&& attribute.getNodeValue().equals(attributeValue)) {
					Element element = (Element) node;
					element.setAttribute("isIdentity", "true");
					isFound = true;
					break;
				}
			}
			if (isFound)
				break;
		}
	}

	public static void setAllIdentityAttribute(Document document,
			String attributeValue) {
		NodeList propertyList = document.getElementsByTagName("Property");
		for (int i = 0; i < propertyList.getLength(); i++) {
			Node node = propertyList.item(i);
			for (int j = 0; j < node.getAttributes().getLength(); j++) {
				Node attribute = node.getAttributes().item(j);
				if (attribute.getNodeName().equals("name")
						&& attribute.getNodeValue().equals(attributeValue)) {
					Element element = (Element) node;
					element.setAttribute("isIdentity", "true");
				}
			}
		}
	}

	public static String transformXmlToString(Document importPackageDocument)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		StringWriter writer = new StringWriter();
		javax.xml.transform.Result result = new StreamResult(writer);
		Source source = new DOMSource(importPackageDocument);
		transformer.transform(source, result);
		writer.close();
		String xml = writer.toString();
		return xml;
	}

	public static String transformXmlToString(Document doc, String encoding)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Document document = builder.newDocument();

		Element e = doc.getDocumentElement();
		Node n = document.importNode(e, true);
		document.appendChild(n);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(document);

		StringWriter sw = new StringWriter();

		StreamResult result = new StreamResult(sw);

		transformer.transform(source, result);

		return sw.toString();
	}

	public static void transformXmlToFile(Document doc,
			String encoding, boolean bom, RandomAccessFile raf)
			throws TransformerException, ParserConfigurationException,
			IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Document document = builder.newDocument();

		Element e = doc.getDocumentElement();
		Node n = document.importNode(e, true);
		document.appendChild(n);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(document);
		
		raf.setLength(0L);
		Writer writer = new UnicodeOutputStreamWriter(raf.getFD(), encoding, bom);
		StreamResult result = new StreamResult(writer);

		transformer.transform(source, result);

	}

	public static void transformXmlToFile(Document doc, File f)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(f);
		transformer.transform(source, result);

	}

	public static Node createNode(Document document, String nodeName,
			String nodeValue) throws Exception {
		Element nodeElement = document.createElement("Property");
		nodeElement.setAttribute("name", nodeName);
		Element nodeValueElement = document.createElement("Value");
		nodeValueElement.setTextContent(nodeValue);
		nodeElement.appendChild(nodeValueElement);
		return nodeElement;
	}

	public static Document getDocumentFromResult(Result result)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		String fileName = System.getProperty("user.dir") + File.separator
				+ "test_import.xml";
		File newFile = new File(fileName);
		FileWriter writer = new FileWriter(newFile);
		writer.write(result.getData());
		writer.close();
		Document document = builder.parse(new InputSource(new FileReader(
				fileName)));
		newFile.delete();
		return document;
	}

	public static Document createDocument(String mainType, String customType)
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		document.setXmlStandalone(false);
		Element sync = document.createElement("Sync");
		document.appendChild(sync);
		Element entity = document.createElement("Entity");
		entity.setAttribute("mainType", mainType);
		if (customType.length() > 0)
			entity.setAttribute("customType", customType);
		document.getElementsByTagName("Sync").item(0).appendChild(entity);
		return document;
	}

	public static Node findNodeByAttribute(Document document, String tagName,
			String attributeName, String attributeValue) {
		Node foundNode = null;
		NodeList nodes = document.getElementsByTagName(tagName);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			for (int j = 0; j < node.getAttributes().getLength(); j++) {
				Node attribute = node.getAttributes().item(j);
				if (attribute.getNodeName().equals(attributeName)
						&& attribute.getNodeValue().equals(attributeValue)) {
					foundNode = node;
					break;
				}
			}
			if (foundNode != null)
				break;
		}
		return foundNode;
	}

	public static Node findNodeByTagName(Document document, String tagName) {
		Node foundNode = null;
		NodeList nodes = document.getElementsByTagName(tagName);
		if (nodes.getLength() > 0)
			foundNode = nodes.item(0);
		return foundNode;
	}

	public static Document transformStringToXML(String xmlString)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(xmlString));
		Document document = builder.parse(source);

		return document;
	}

	public static Node[] findNodesByTagName(Document document, String tagName) {
		ArrayList<Node> nodes = new ArrayList<Node>();

		NodeList foundNodes = document.getElementsByTagName(tagName);
		for (int i = 0; i < foundNodes.getLength(); i++)
			nodes.add(foundNodes.item(i));

		Node[] returnNodes = new Node[nodes.size()];
		return nodes.toArray(returnNodes);
	}

	public static Node[] findNodesByTagName(Node parentNode, String tagName) {
		ArrayList<Node> nodes = new ArrayList<Node>();

		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node node = parentNode.getChildNodes().item(i);

			if (node.getNodeName().equals(tagName))
				nodes.add(node);

			if (node.hasChildNodes()) {
				Node[] foundChildNodes = findNodesByTagName(node, tagName);
				for (int j = 0; j < foundChildNodes.length; j++)
					nodes.add(foundChildNodes[j]);

			}
		}

		Node[] returnNodes = new Node[nodes.size()];
		return nodes.toArray(returnNodes);
	}

	public static List<String> getValuesFromDocumentByTagAndAttribute(
			Node parentNode, String tagName, String attributeName,
			String attributeValue) {
		ArrayList<String> values = new ArrayList<String>();

		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			Node childNode = parentNode.getChildNodes().item(i);

			if (childNode != null) {
				if (childNode.hasAttributes()) {
					for (int j = 0; j < childNode.getAttributes().getLength(); j++) {
						Node attribute = childNode.getAttributes().item(j);
						if (attribute.getNodeName().equals(attributeName)
								&& attribute.getNodeValue().equals(
										attributeValue)) {
							values.add(childNode.getTextContent().trim());
						}
					}
				}

				if (childNode.hasChildNodes()) {
					values.addAll(getValuesFromDocumentByTagAndAttribute(
							childNode, tagName, attributeName, attributeValue));
				}
			}
		}

		return values;
	}

	public static String getNodeAttribute(Node node, String name) {

		if (node.hasAttributes()) {

			NamedNodeMap attrs = node.getAttributes();

			for (int i = 0; i < attrs.getLength(); i++) {
				Attr attribute = (Attr) attrs.item(i);

				if (attribute.getName().equals(name)) {
					return attribute.getValue();
				}

			}
		}

		return null;

	}

	public static void removeNodeAttribute(Node node, String name) {

		if (node.hasAttributes()) {

			NamedNodeMap attrs = node.getAttributes();
			if (attrs.getNamedItem(name) != null) {
				attrs.removeNamedItem(name);
			}
		}

	}

	public static void removeNodeAttributes(Node node) {

		NamedNodeMap attrs = node.getAttributes();

		if ((attrs != null) && (attrs.getLength() > 0)) {
			String[] names = new String[attrs.getLength()];
			for (int i = 0; i < names.length; i++) {
				names[i] = attrs.item(i).getNodeName();
			}
			for (int i = 0; i < names.length; i++) {
				attrs.removeNamedItem(names[i]);
			}
		}

	}

	public static String getNodeHierarchy(Node node) {

		StringBuffer sb = new StringBuffer();
		if (node != null) {
			Stack<Node> st = new Stack<Node>();
			st.push(node);

			Node parent = node.getParentNode();

			while ((parent != null)
					&& (parent.getNodeType() != Node.DOCUMENT_NODE)) {

				st.push(parent);

				parent = parent.getParentNode();
			}

			// constructs node hierarchy
			Node n = null;
			while (!st.isEmpty() && null != (n = st.pop())) {

				if (n instanceof Element) {
					Element e = (Element) n;

					if (sb.length() == 0) {
						sb.append(e.getTagName());
					} else {
						sb.append("/" + e.getTagName());
					}
				}

			}
		}
		return sb.toString();

	}

	public static void removeElementXML(Node node, short nodeType, String name) {
		if (node.getNodeType() == nodeType
				&& (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		} else {
			// Visit the children
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeElementXML(list.item(i), nodeType, name);
			}
		}
	}

	public static String getFirstLevelTextContent(Node node) {
		NodeList list = node.getChildNodes();
		StringBuilder textContent = new StringBuilder();
		for (int i = 0; i < list.getLength(); ++i) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				textContent.append(child.getTextContent());
		}
		return textContent.toString().trim();
	}
}
