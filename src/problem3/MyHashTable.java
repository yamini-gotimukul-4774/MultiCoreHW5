package problem3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MyHashTable<T> {
	protected List<T>[] table;
	protected AtomicInteger size;

	public MyHashTable(int capacity) {
		size = new AtomicInteger(0);
		table = (List<T>[]) new List[capacity];
		for (int i = 0; i < capacity; i++) {
			table[i] = new ArrayList<T>();
		}
	}

	public abstract boolean contains(T x);

	public abstract boolean add(T x);

	public abstract boolean remove(T x);

	public abstract void resize();
	
	public abstract List<T> toList();

}
