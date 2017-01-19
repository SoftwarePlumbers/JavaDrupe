package org.javadrupe.collections;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Simple Name/Value pair.
 * 
 * Often used in place of a Map.Entry.
 * 
 * @author Jonathan Essex
 *
 * @param <T> Value type.
 */
public class NameValuePair<T>  {
	
	/** Name associated with value */
	public final String name;
	/** The value */
	public final T value;
	
	/** Construct a new name/value pair.
	 * 
	 * @param name
	 * @param value
	 */
	public NameValuePair(String name, T value) {
		this.name = name;
		this.value = value;
	}
	
	/** Construct a new name/value pair.
	 * 
	 * Convenience function using type inference, making code a little less verbose.
	 * 
	 * @param name
	 * @param value
	 * @return a Name value pair.
	 */
	public static <T> NameValuePair<T> of(String name, T value) {
		return new NameValuePair<T>(name, value);
	}
	
	/** String converter.
	 * 
	 * @return String in format: { "name" : value } 
	 * 
	 */
	public String toString() {
		return "{\""+name+ "\": " + value + "}";
	}
	
	/** Convenience function for collecting Name/Value pairs into a map.
	 * 
	 * @return A collector outputting a map from name to value for a stream of name/value pairs.
	 */
	public static <T> Collector<NameValuePair<T>, ?, Map<String,T>> toMap() {
		return Collectors.toMap(pair -> pair.name, pair -> pair.value );
	}
	
	/** Equality operator
	 * 
	 * Names must be non-null. Values may be null.
	 * 
	 * @param other
	 * @return true if name and value are both equal, or if names are equal and values are both null.
	 */
	boolean equals(NameValuePair<T> other) {
		if (this.name.equals(other.name)) {
			if (this.value == null && other.value == null) return true;
			if (this.value != null && other.value != null) return this.value.equals(other.value);
		}
		return false;
	}
	
	/** Equality operator
	 * 
	 * Names must be non-null. Values may be null.
	 * 
	 * @param other
	 * @return true if name and value are both equal, or if names are equal and values are both null.
	 */
	public boolean equals(Object other) {
		return other instanceof NameValuePair ? equals((NameValuePair<?>)other) : false;
	}
	
	/** Hash code 
	 * 
	 * @return has code based on hashes of both name and value.
	 */
	public int hashCode() {
		return name.hashCode() * 13 ^ value.hashCode();
	}
}