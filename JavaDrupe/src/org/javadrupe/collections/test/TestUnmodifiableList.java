package org.javadrupe.collections.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javadrupe.collections.UnmodifiableList;
import org.junit.Test;

public class TestUnmodifiableList {
	
	List<Integer> numbers = Arrays.asList(1,2,7,55,33,21,9,22,68);
	List<Integer> numbers2 = Arrays.asList(11,12,13);


	@Test
	public void testRoundTrip() {
		UnmodifiableList<Integer> ul1 = UnmodifiableList.of((Integer[])numbers.toArray());
		assertEquals(numbers, ul1.stream().collect(Collectors.toList()));
	}
	
	@Test
	public void testChainedAdd() {
		UnmodifiableList<Integer> ul1 = UnmodifiableList.EMPTY.add(13).add(12).add(11);
		assertEquals(numbers2, ul1.stream().collect(Collectors.toList()));
	}

	@Test
	public void testJoin() {
		UnmodifiableList<Integer> ul1 = UnmodifiableList.of((Integer[])numbers.toArray());
		UnmodifiableList<Integer> ul2 = UnmodifiableList.of((Integer[])numbers2.toArray());
		UnmodifiableList<Integer> ul3 = ul1.join(ul2);
		List<Integer> l2 = new ArrayList<Integer>();
		l2.addAll(numbers);
		l2.addAll(numbers2);
		assertEquals(l2, ul3.stream().collect(Collectors.toList()));
	}

	@Test
	public void testView() {
		UnmodifiableList<Integer> ul1 = UnmodifiableList.view(numbers);
		assertEquals(numbers, ul1.stream().collect(Collectors.toList()));
	}
}
