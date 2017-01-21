package org.javadrupe.reflection;

import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

public class Types {
	
	  public static final String SCHEMA_PREFIX = "xs";
	  public static final String SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
	  
	  public static QName toQName(String type_name) {
		  return new QName(SCHEMA_URI, type_name, SCHEMA_PREFIX);
	  }
	  
	  private static final HashMap<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>,Class<?>>();
	  private static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVES;
	  private static final HashMap<Class<?>, QName> JAVA_PRIMITIVE_TO_XML = new HashMap<Class<?>,QName>();
	  private static final Map<QName, Class<?>> XML_TO_JAVA_PRIMITIVE;
	  private static final HashMap<Class<?>, QName> JAVA_TO_XML = new HashMap<Class<?>,QName>();
	  private static final HashMap<QName, Class<?>> XML_TO_JAVA = new HashMap<QName, Class<?>>();

	  public static final QName QN_BOOLEAN = toQName("boolean");
	  public static final QName QN_BYTE = toQName("byte") ;
	  public static final QName QN_STRING = toQName("string");
	  public static final QName QN_DOUBLE = toQName("double");
	  public static final QName QN_FLOAT = toQName("float");
	  public static final QName QN_INT = toQName("int");
	  public static final QName QN_LONG = toQName("long");
	  public static final QName QN_SHORT = toQName("short");
	  
	  public static final QName QN_INTEGER = toQName("integer");
	  public static final QName QN_DECIMAL = toQName("decimal"); 
	  public static final QName QN_QNAME = toQName("QName");  
	  public static final QName QN_DATETIME = toQName("dateTime"); 
	  public static final QName QN_BASE64BIN = toQName("base64Binary"); 
	  public static final QName QN_HEXBIN = toQName("hexBinary"); 
	  public static final QName QN_UINT = toQName("unsignedInt"); 
	  public static final QName QN_USHORT = toQName("unsignedShort"); 
	  public static final QName QN_UBYTE = toQName("unsignedByte"); 
	  public static final QName QN_TIME = toQName("time"); 
	  public static final QName QN_DATE = toQName("date"); 
	  public static final QName QN_G = toQName( "g"); 
	  public static final QName QN_ANYSIMPLETYPE = toQName("anySimpleType"); 
	  public static final QName QN_DURATION = toQName("duration"); 
	  public static final QName QN_NOTATION = toQName("NOTATION"); 
	  
	  static {
		  PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
		  PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
		  PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
		  PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
		  PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
		  PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
		  PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
		  PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
		  PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
		  
		  WRAPPERS_TO_PRIMITIVES = PRIMITIVES_TO_WRAPPERS.entrySet().stream().collect(Collectors.toMap(e->e.getValue(), e->e.getKey()));
		  
		  JAVA_PRIMITIVE_TO_XML.put(boolean.class, QN_BOOLEAN);
		  JAVA_PRIMITIVE_TO_XML.put(byte.class, QN_BYTE);
		  JAVA_PRIMITIVE_TO_XML.put(char.class, QN_STRING);
		  JAVA_PRIMITIVE_TO_XML.put(double.class, QN_DOUBLE) ;
		  JAVA_PRIMITIVE_TO_XML.put(float.class,  QN_FLOAT);
		  JAVA_PRIMITIVE_TO_XML.put(int.class, QN_INT);
		  JAVA_PRIMITIVE_TO_XML.put(long.class, QN_LONG);
		  JAVA_PRIMITIVE_TO_XML.put(short.class, QN_SHORT);
		  
		  XML_TO_JAVA_PRIMITIVE = JAVA_PRIMITIVE_TO_XML.entrySet().stream().collect(Collectors.toMap(e->e.getValue(), e->e.getKey()));
		  
		  XML_TO_JAVA.put(QN_STRING,String.class);
		  XML_TO_JAVA.put(QN_INTEGER, BigInteger.class);
		  XML_TO_JAVA.put(QN_DECIMAL, BigDecimal.class); 
		  XML_TO_JAVA.put(QN_QNAME, QName.class);
		  XML_TO_JAVA.put(QN_DATETIME, XMLGregorianCalendar.class); 
		  XML_TO_JAVA.put(QN_BASE64BIN, byte[].class); 
		  XML_TO_JAVA.put(QN_HEXBIN, byte[].class); 
		  XML_TO_JAVA.put(QN_UINT, long.class);
		  XML_TO_JAVA.put(QN_USHORT, int.class); 
		  XML_TO_JAVA.put(QN_UBYTE, short.class);
		  XML_TO_JAVA.put(QN_TIME, XMLGregorianCalendar.class); 
		  XML_TO_JAVA.put(QN_DATE, XMLGregorianCalendar.class);
		  XML_TO_JAVA.put(QN_G, XMLGregorianCalendar.class); 
		  XML_TO_JAVA.put(QN_ANYSIMPLETYPE, String.class); 
		  XML_TO_JAVA.put(QN_DURATION, Duration.class); 
		  XML_TO_JAVA.put(QN_NOTATION, QName.class); 
		  
		  JAVA_TO_XML.put(String.class, QN_STRING);
		  JAVA_TO_XML.put(BigInteger.class, QN_INTEGER);
		  JAVA_TO_XML.put(BigDecimal.class, QN_DECIMAL);
		  JAVA_TO_XML.put(Calendar.class, QN_DATETIME);
		  JAVA_TO_XML.put(Date.class, QN_DATETIME);
		  JAVA_TO_XML.put(QName.class, QN_QNAME);
		  JAVA_TO_XML.put(URI.class, QN_STRING);
		  JAVA_TO_XML.put(XMLGregorianCalendar.class, QN_ANYSIMPLETYPE);
		  JAVA_TO_XML.put(Duration.class, QN_DURATION);
		  JAVA_TO_XML.put(Image.class, QN_BASE64BIN);
		  JAVA_TO_XML.put(DataHandler.class, QN_BASE64BIN);
		  JAVA_TO_XML.put(Source.class, QN_BASE64BIN);
		  JAVA_TO_XML.put(UUID.class, QN_STRING);
		  
	  };
	  
	  /** Get Wrapper type for a given primitive type
	   * 
	   * @param clazz Primitive type
	   * @return Wrapper type
	   */
	  public static Optional<Class<?>> getWrapperType(Class<?> clazz) {
		  return Optional.ofNullable(PRIMITIVES_TO_WRAPPERS.get(clazz));
	  }
	  
	  /** Get primitive type for a given wrapper type
	   * 
	   * @param clazz wrapper type
	   * @return Wrapper type
	   */
	  public static Optional<Class<?>> getTypeFromWrapper(Class<?> clazz) {
		  return Optional.ofNullable(WRAPPERS_TO_PRIMITIVES.get(clazz));
	  }
	  
	  /** Find XML schema type for a given primitive class.
	   * 
	   * @param clazz
	   * @return QName of schema type.
	   */
	  public static Optional<QName> getSchemaTypeFromPrimitive(Class<?> clazz) {
		  Class<?> primitive = getTypeFromWrapper(clazz).orElse(clazz);
		  return Optional.ofNullable(JAVA_PRIMITIVE_TO_XML.get(primitive));
	  }
	  
	  /** Find XML schema type for a given class.
	   * 
	   * @param clazz
	   * @return QName of schema type.
	   */
	  public static Optional<QName> getSchemaType(Class<?> clazz) {
		  Optional<QName> name = Optional.ofNullable(JAVA_TO_XML.get(clazz));
		  return name.isPresent() ? name : getSchemaTypeFromPrimitive(clazz);
	  }
	  
	  
	  /** Find XML schema type for a given primitive class.
	   * 
	   * @param clazz
	   * @return QName of schema type.
	   */
	  public static Optional<Class<?>> getPrimitiveTypeFromSchema(QName qname) {
		  return Optional.ofNullable(XML_TO_JAVA_PRIMITIVE.get(qname));
	  }

	  /** Find XML schema type for a given class.
	   * 
	   * @param clazz
	   * @return QName of schema type.
	   */
	  public static Optional<Class<?>> getTypeFromSchema(QName qname) {
		  Optional<Class<?>> result = Optional.of(XML_TO_JAVA.get(qname));
		  // Don't use Optional.orElse for efficiency.
		  return result.isPresent() ? result : getPrimitiveTypeFromSchema(qname);
	  }
	  
	  /** Replacement for Class.isAssignableFrom.
	   * 
	   * Class.isAssignableFrom does not take into account boxing operations. Thus even though we can assign
	   * a double to a Double, Double.class.isAssignableFrom(Double.TYPE) returns false.
	   * 
	   * @param to
	   * @param from
	   * @return True if object for type from can be assigned to type to.
	   */
	  public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
		  if (to.isAssignableFrom(from)) return true;
		  Optional<Class<?>> from_wrapper = getWrapperType(from);
		  Optional<Class<?>> to_wrapper = getWrapperType(to);
		  if (from_wrapper.isPresent() && !to_wrapper.isPresent() && to.isAssignableFrom(from_wrapper.get())) return true;
		  if (!from_wrapper.isPresent() && to_wrapper.isPresent() && to_wrapper.get().isAssignableFrom(from)) return true;
		  return false;
	  }
}
