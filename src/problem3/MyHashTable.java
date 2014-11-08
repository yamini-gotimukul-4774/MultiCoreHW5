package problem3;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MyHashTable<T> {
	protected AtomicInteger size;

	public MyHashTable(int capacity) {
		size = new AtomicInteger(0);
	
	}

	public abstract boolean contains(T x);

	public abstract boolean add(T x);

	public abstract boolean remove(T x);
	
	// debug
	public abstract List<T> toList();

}
