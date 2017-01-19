package org.javadrupe.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** This is the ubiquitous unmodifiable list class.
 * 
 * A not-bad implementation of the ubiquitous unmodifiable list class. Can be used in
 * places where Stream.Builder doesn't fit the bill; the join() method allows two partially
 * built lists to be joined together.
 * 
 * TODO: maybe provide better spliterator implementation
 * 
 * @author Jonathan Essex.
 *
 * @param <E> Element type.
 */
public interface UnmodifiableList<E> extends Iterable<E> {
	
	@SuppressWarnings("rawtypes")
	public static final UnmodifiableList EMPTY = new Empty();

	/** Get first element in list. */
	public E head();
	/** Get sublist containing all elements in list after first. */
	public UnmodifiableList<E> tail();
	/** Create a new list, containing all elements in this list plus elem. */
	public UnmodifiableList<E> add(E elem);
	/** Create a new list, containing all elements in this list plus <code>list</code> */
	public UnmodifiableList<E> join(UnmodifiableList<E> list);
	/** is this list empty? */
	public boolean isEmpty();
	/** get stream */
	public default Stream<E> stream() { return StreamSupport.stream(spliterator(), false); }

	
	/** Create an unmodifiable view of an existing list.
	 * 
	 * This allows us, for example, to create a single unmodifiable list containing
	 * several source lists without copying the underlying data in the source lists.
	 * However, it should be noted that unless the 'viewed' list provides an efficient
	 * sublist method, some operations on the resulting unmodifiable list may be very
	 * inefficient.
	 * 
	 */
	public static <E> UnmodifiableList<E> view(List<E> to_view) {
		return new Proxy<E>(to_view);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> UnmodifiableList<E> empty() {
		return (UnmodifiableList<E>)EMPTY;
	}
	
	/** Convenient way to create umodifiable list with type inference.
	 * 
	 *  @param es Array of elmenets to create an unmodifiable list from.
	 *  @return an unmodifiable list containing the specified elements.
	 */
	@SafeVarargs
	public static <E> UnmodifiableList<E> of(E... es) {
		return new Proxy<E>(Arrays.asList(es));
	}

	/** Empty Iterator.
	 * 
	 * Come java 9, hopefully this will be private. Do not use directly.
	 *  
	 */
	public static class EmptyIterator<E> implements Iterator<E> {
		public boolean hasNext() { return false; }
		public E next() { return null; }
	}

	/** Empty Unmodifiable List.
	 * 
	 * Come java 9, hopefully this will be private. Do not use directly.
	 *  
	 */
	public static class Empty<E> implements UnmodifiableList<E> {
		@Override
		public E head() { return null; }
		@Override
		public UnmodifiableList<E> tail() { return null; }
		@Override
		public UnmodifiableList<E> add(E elem) { return new Impl<E>(elem, this); }
		@Override
		public boolean isEmpty() { return true; }
		@Override
		public UnmodifiableList<E> join(UnmodifiableList<E> list) { return list; }
		@Override		
		public Iterator<E> iterator() { return new EmptyIterator<E>(); }
	}
	
	/** Iterator over an unmodifiable list.
	 * 
	 * Come java 9, hopefully this will be private. Do not use directly.
	 *  
	 */
	public static class IteratorImpl<E> implements Iterator<E> {
		private UnmodifiableList<E> next;
		@Override
		public boolean hasNext() { return !next.isEmpty(); }
		@Override
		public E next() { E rv = next.head(); next = next.tail(); return rv; }
		public IteratorImpl(UnmodifiableList<E> next) { this.next = next; }
	}
	
	/** Main implementation class.
	 * 
	 * Come java 9, hopefully this will be private. Do not use directly.
	 *  
	 */
	public static class Impl<E> implements UnmodifiableList<E> {
		private final E head;
		private final UnmodifiableList<E> tail;
		public Impl(E head, UnmodifiableList<E> tail) { this.head = head; this.tail = tail; }
		@Override
		public boolean isEmpty() { return false; }
		@Override
		public E head() { return head; }
		@Override
		public UnmodifiableList<E> tail() { return tail; }
		@Override
		public UnmodifiableList<E> add(E elem) { return new Impl<E>(elem, this); }
		@Override
		public Iterator<E> iterator() { return new IteratorImpl<E>(this); }
		@Override
		public UnmodifiableList<E> join(UnmodifiableList<E> list) { return list.isEmpty() ? this : new Merged<E>(this, list); }
	}
	
	/** Main implementation class.
	 * 
	 * Come java 9, hopefully this will be private. Do not use directly.
	 *  
	 */
	public static class Merged<E> implements UnmodifiableList<E> {
		UnmodifiableList<E> a;
		UnmodifiableList<E> b;	
		public Merged(UnmodifiableList<E> a, UnmodifiableList<E> b) { this.a = a; this.b = b; }
		@Override
		public E head() { return a.head(); }
		@Override
		public UnmodifiableList<E> tail() { return (a.tail().isEmpty()) ? b : new Merged<E>(a.tail(), b); }
		@Override
		public UnmodifiableList<E> add(E elem) { return new Impl<E>(elem, this); }
		@Override
		public Iterator<E> iterator() { return new IteratorImpl<E>(this); }
		@Override
		public UnmodifiableList<E> join(UnmodifiableList<E> list) { return list.isEmpty() ? this : new Merged<E>(this, list); }
		@Override
		public boolean isEmpty() { return false; }		
	}	

	/** Why can't I make this fucking private, java?? */
	public static class Proxy<E> implements UnmodifiableList<E> {
		
		private List<E> proxied;
		
		public Proxy(List<E> proxied) { this.proxied = proxied; }
		@Override
		public boolean isEmpty() { return proxied.isEmpty(); }
		@Override
		public Iterator<E> iterator() { return proxied.iterator(); }
		@Override
		public E head() { return proxied.get(0); }
		@Override
		public UnmodifiableList<E> tail() { return new Proxy<E>(proxied.subList(1, proxied.size())); }
		@Override
		public UnmodifiableList<E> add(E elem) { return new Impl<E>(elem, this); }
		@Override
		public UnmodifiableList<E> join(UnmodifiableList<E> list) { return list.isEmpty() ? this : new Merged<E>(this, list);	}
	}

}
