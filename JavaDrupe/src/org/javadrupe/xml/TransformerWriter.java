package org.javadrupe.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TransformerWriter implements XMLStreamWriter {
	
	private static class Context {
		public final QName name;
		public final TransformerWriter.NamespaceContextImpl namespace;
		public Context(QName name, TransformerWriter.NamespaceContextImpl namespace) { this.name = name; this.namespace = namespace; }
	}
	
	private TransformerHandler handler;
	private Stack<TransformerWriter.Context> context = new Stack<TransformerWriter.Context>();
	private AttributesImpl attributes = new AttributesImpl();
	private boolean isFinished = true;
	
	private static class NamespaceContextImpl implements NamespaceContext {
		
		public final TransformerWriter.NamespaceContextImpl parent_context;
		// map from URI to prefix
		public final TreeMap<String,String> mapped_prefixes = new TreeMap<String,String>();

		@Override
		public String getNamespaceURI(String prefix) {
			Optional<Map.Entry<String, String>> entry = mapped_prefixes.entrySet().stream().filter(v -> v.getValue().equals(prefix)).findFirst();
			if (entry.isPresent()) return entry.get().getKey();
			if (parent_context==null) return null;
			return parent_context.getPrefix(prefix);
		}

		@Override
		public String getPrefix(String namespaceURI) {
			String uri = mapped_prefixes.get(namespaceURI);
			if (uri != null) return uri;
			if (parent_context == null) return null;
			return parent_context.getPrefix(namespaceURI);
		}
		
		public Stream<String> getPrefixesStream(String namespaceURI) {
			String prefix = mapped_prefixes.get(namespaceURI);
			if (prefix == null && parent_context == null) return Stream.empty();
			if (prefix == null) return parent_context.getPrefixesStream(namespaceURI);
			if (parent_context == null) return Stream.of(prefix);
			return Stream.concat(Stream.of(prefix), parent_context.getPrefixesStream(namespaceURI));				
		}
		
		@Override
		public Iterator<String> getPrefixes(String namespaceURI) {
			return getPrefixesStream(namespaceURI).iterator();
		}
		
		public NamespaceContextImpl(TransformerWriter.NamespaceContextImpl parent) {
			this.parent_context = parent;
		}
		
		public Collection<String> getLocalURIs() {
			return mapped_prefixes.keySet();
		}

		public Collection<String> getLocalPrefixes() {
			return mapped_prefixes.values();
		}
		
		public void addLocalBinding(String prefix, String URI) {
			mapped_prefixes.put(URI, prefix);
		}
		
		public String toQualifiedName(QName name) throws XMLStreamException {
			String namespace_uri = name.getNamespaceURI();
			String local_name = name.getLocalPart();
			
			if (XMLConstants.NULL_NS_URI.equals(namespace_uri)) {
				return local_name;
			} else {
				String prefix = getPrefix(namespace_uri);
				if (prefix == null) throw new XMLStreamException("Unknown URI for " + prefix);
				if (prefix == XMLConstants.DEFAULT_NS_PREFIX) return local_name;
				return prefix + ":" + local_name;
			}
		}
	}
	

	
	private void finishStartingElement() throws XMLStreamException {
		if (!isFinished) {
			TransformerWriter.Context current = context.peek();
			try {
				for (String uri : current.namespace.getLocalURIs())
					handler.startPrefixMapping(current.namespace.getPrefix(uri), uri);
				handler.startElement(current.name.getNamespaceURI(), current.name.getLocalPart(), current.namespace.toQualifiedName(current.name), attributes);
			} catch (SAXException e) {
				throw new XMLStreamException(e);
			}
			attributes.clear();
			isFinished = true;
		}			
	}
	
	public void writeStartElement(QName name) throws XMLStreamException {
		finishStartingElement();
		NamespaceContextImpl current_namespace = context.isEmpty() ? null : context.peek().namespace;
		context.push(new Context(name, new NamespaceContextImpl(current_namespace)));
		isFinished = false;	
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		writeStartElement(new QName(localName));	
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		writeStartElement(new QName(namespaceURI, localName));	
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		writeStartElement(new QName(namespaceURI, localName, prefix));
	}

	public void writeEmptyElement(QName name) throws XMLStreamException {
		finishStartingElement();
		try {
			String qname = name.getPrefix() + ":" + name.getLocalPart();
			handler.startElement(name.getNamespaceURI(), name.getLocalPart(), qname, attributes);
			handler.endElement(name.getNamespaceURI(), name.getLocalPart(), qname);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
		attributes.clear();
	}
	
	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		writeEmptyElement(new QName(namespaceURI, localName));
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		writeEmptyElement(new QName(namespaceURI, localName, prefix));
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		writeEmptyElement(new QName(localName));
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		finishStartingElement();
		try {
			TransformerWriter.Context current = context.pop();
			handler.endElement(current.name.getNamespaceURI(), current.name.getLocalPart(), current.namespace.toQualifiedName(current.name));
			for (String prefix : current.namespace.getLocalPrefixes())
				handler.endPrefixMapping(prefix);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
		attributes.clear();		
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		try {
			handler.endDocument();
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public void close() throws XMLStreamException {
	}

	@Override
	public void flush() throws XMLStreamException {
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
		writeAttribute(new QName(localName), value);
	}
	
	public void writeAttribute(QName name, String value)
			throws XMLStreamException {
		String qname = context.peek().namespace.toQualifiedName(name);
		attributes.addAttribute(name.getNamespaceURI(), name.getLocalPart(), qname, "", value );
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		writeAttribute(new QName(namespaceURI, localName, prefix), value);
		
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
		writeAttribute(new QName(namespaceURI, localName),value);			
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
		try {
			context.peek().namespace.addLocalBinding(prefix, namespaceURI);;
			handler.startPrefixMapping(prefix, namespaceURI);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
		
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
		writeNamespace("", namespaceURI);	
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		finishStartingElement();	
		try {
			handler.comment(data.toCharArray(), 0, data.length());
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
		writeProcessingInstruction(target,"");
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
		finishStartingElement();
		try {
			handler.processingInstruction(target, data);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}			
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		finishStartingElement();
		try {
			handler.startCDATA();
			handler.characters(data.toCharArray(), 0, data.length());
			handler.endCDATA();
			
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}			
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		finishStartingElement();
		String[] elements = dtd.split("\\s+");
		String name = elements.length > 0 ? elements[0] : "";
		String publicId = elements.length > 1 ? elements[1] : "";
		String systemId = elements.length > 2 ? elements[2] : "";
		
		try {
			handler.startDTD(name, publicId, systemId);
			handler.endDTD();
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}		
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		finishStartingElement();
		try {
			handler.startEntity(name);
			handler.endEntity(name);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}		
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		try {
			handler.startDocument();
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}		
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		writeStartDocument();
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		writeStartDocument();			
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		writeCharacters(text.toCharArray(), 0, text.length());
		
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		finishStartingElement();
		try {
			handler.characters(text, start, len);
		} catch (SAXException e) {
			throw new XMLStreamException(e);
		}
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return context.peek().namespace.getPrefix(uri);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		context.peek().namespace.addLocalBinding(prefix, uri);			
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		context.peek().namespace.addLocalBinding("", uri);			
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return context.peek().namespace;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return handler.getTransformer().getOutputProperty(name);
	}

	public TransformerWriter(TransformerHandler handler) {
		this.handler = handler;
	}
	
}