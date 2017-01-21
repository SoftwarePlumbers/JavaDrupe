package org.javadrupe.reflection.test;

import static org.junit.Assert.*;

import org.javadrupe.reflection.Types;

import org.junit.Test;

public class TestTypes {

	@Test
	public void testSchemaTypeFromPrimitive() {
		assertEquals(Types.QN_DOUBLE, Types.getSchemaType(double.class).get());
	}

	@Test
	public void testSchemaTypeFromBoxedType() {
		assertEquals(Types.QN_DOUBLE, Types.getSchemaType(Double.class).get());
	}
}
