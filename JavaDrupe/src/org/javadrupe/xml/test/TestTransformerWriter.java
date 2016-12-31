package org.javadrupe.xml.test;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.javadrupe.xml.TransformerWriter;

import org.junit.Test;

public class TestTransformerWriter {

	private static SAXTransformerFactory TRANSFORMER_FACTORY = (SAXTransformerFactory)TransformerFactory.newInstance();
	private static String XML_PREAMBLE="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static String TEST_NAMESPACE="urn:org.javadrupe.xml.test";

	@Test
	public void testStartEndDocument() throws TransformerConfigurationException, XMLStreamException {
		TransformerHandler handler = TRANSFORMER_FACTORY.newTransformerHandler();
		StringWriter buffer = new StringWriter();
		StreamResult result = new StreamResult(buffer);
		handler.setResult(result);
		XMLStreamWriter writer = new TransformerWriter(handler);
		writer.writeStartDocument();
		writer.writeEndDocument();
		writer.close();		
		assertEquals(XML_PREAMBLE, buffer.toString().trim());
	}

	@Test
	public void testSingleElementDocument() throws TransformerConfigurationException, XMLStreamException {
		TransformerHandler handler = TRANSFORMER_FACTORY.newTransformerHandler();
		StringWriter buffer = new StringWriter();
		StreamResult result = new StreamResult(buffer);
		handler.setResult(result);
		XMLStreamWriter writer = new TransformerWriter(handler);
		writer.writeStartDocument();
		writer.writeStartElement("document");
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();	
		String document = buffer.toString();
		assertTrue(document.indexOf(XML_PREAMBLE) >= 0);
		assertTrue(document.indexOf("<document/>") >= 0);
	}

	@Test
	public void testSingleElementDocumentWithNamespace() throws TransformerConfigurationException, XMLStreamException {
		TransformerHandler handler = TRANSFORMER_FACTORY.newTransformerHandler();
		StringWriter buffer = new StringWriter();
		StreamResult result = new StreamResult(buffer);
		handler.setResult(result);
		XMLStreamWriter writer = new TransformerWriter(handler);
		writer.writeStartDocument();
		writer.writeStartElement(TEST_NAMESPACE,"document");
		writer.writeNamespace("test", TEST_NAMESPACE);
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();	
		String document = buffer.toString();
		assertTrue(document.indexOf(XML_PREAMBLE) >= 0);
		assertTrue(document.indexOf("test:document") >= 0);
		assertTrue(document.indexOf("xmlns:test=\"urn:org.javadrupe.xml.test\"") >= 0);
	}
	
	@Test
	public void testAttributesInElement() throws TransformerConfigurationException, XMLStreamException {
		TransformerHandler handler = TRANSFORMER_FACTORY.newTransformerHandler();
		StringWriter buffer = new StringWriter();
		StreamResult result = new StreamResult(buffer);
		handler.setResult(result);
		XMLStreamWriter writer = new TransformerWriter(handler);
		writer.writeStartDocument();
		writer.writeStartElement("document");
		writer.writeAttribute("attr1","value");
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();	
		String document = buffer.toString();
		assertTrue(document.indexOf(XML_PREAMBLE) >= 0);
		assertTrue(document.indexOf("<document attr1=\"value\"/>") >= 0);
	}
	
	@Test
	public void testAttributesAndElementsInElement() throws TransformerConfigurationException, XMLStreamException {
		TransformerHandler handler = TRANSFORMER_FACTORY.newTransformerHandler();
		StringWriter buffer = new StringWriter();
		StreamResult result = new StreamResult(buffer);
		handler.setResult(result);
		XMLStreamWriter writer = new TransformerWriter(handler);
		writer.writeStartDocument();
		writer.writeStartElement("document");
		writer.writeAttribute("attr1","value");
		writer.writeStartElement("para");
		writer.writeAttribute("attr3","value");
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();	
		String document = buffer.toString();
		System.out.println(document);
		assertTrue(document.indexOf(XML_PREAMBLE) >= 0);
		assertTrue(document.indexOf("<document") >= 0);
		assertTrue(document.indexOf("<document attr1=\"value\">") >= 0);
		assertTrue(document.indexOf("<para attr3=\"value\"/>") >= 0);
	}

}
