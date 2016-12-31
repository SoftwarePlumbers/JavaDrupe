package org.javadrupe.reflection;

import java.util.HashMap;

public class Boxing {
	
	  private static final HashMap<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>,Class<?>>();
	  
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
	  };
	  
	  public static Class<?> getWrapperType(Class<?> clazz) {
		  return PRIMITIVES_TO_WRAPPERS.get(clazz);
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
		  return to.isAssignableFrom(from) 
				  || from.isPrimitive() && to.isAssignableFrom(getWrapperType(from)) 
				  || to.isPrimitive() && getWrapperType(to).isAssignableFrom(from); 
	  }
}
