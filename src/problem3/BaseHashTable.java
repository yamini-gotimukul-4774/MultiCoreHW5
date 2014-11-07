package problem3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BaseHashTable<T> extends MyHashTable<T> {
	// private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantLock lock = new ReentrantLock();

	public BaseHashTable(int capacity) {
		super(capacity);
	}

	@Override
	public boolean contains(T x) {
		acquire(x);
		try {
			int bucket = x.hashCode() % table.length;
			return table[bucket].contains(x);
		} finally {
			release(x);
		}
	}

	@Override
	public boolean add(T x) {
		boolean wasAdded = false;
		acquire(x);
		try {
			int bucket = x.hashCode() % table.length;
			if (!table[bucket].contains(x)) {
				table[bucket].add(x);
				wasAdded = true;
				size++;
				if (policy()) {
					resize();
				}
			}
		} finally {
			release(x);
		}
		return wasAdded;
	}

	@Override
	public boolean remove(T x) {
		boolean wasRemoved = false;
		acquire(x);
		try {
			int bucket = x.hashCode() % table.length;
			if (table[bucket].remove(x)) {
				size--;
				wasRemoved = true;
			}
			return wasRemoved;
		} finally {
			release(x);
		}
	}

	@Override
	public void resize() {
		int old_cap = table.length;
		lock.lock();
		try {
			if (old_cap != table.length) {
				return;
			}
			int new_cap = old_cap * 2;
			List<T>[] old_table = table;
			table = (List<T>[]) new List[new_cap];
			for (int i = 0; i < new_cap; i++) {
				table[i] = new ArrayList<T>();
			}
			for (List<T> list : old_table) {
				for (T t : list) {
					table[t.hashCode() % table.length].add(t);
				}
			}
		} finally {
			lock.unlock();
		}

	}

	public boolean policy() {
		return size / table.length > (table.length / 2);
	}

	public void acquire(T x) {
		lock.lock();
	}

	public void release(T x) {
		lock.unlock();
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		int length = table.length;
		for (int i = 0; i < length; i++) {
			s.append(table[i] + "\n");
		}
		return s.toString();
	}
	
	public List<T> toList(){
		 List<T> table_as_list = new ArrayList<>();
		 int s = table.length;
		 for(int i=0;i<s;i++){
			 table_as_list.addAll(table[i]);
		 }		 
		 return table_as_list;	
	}
	
	public static void main(String[] args) {
		MyHashTable<Integer> b = new BaseHashTable<>(4);
		System.out.println(b.contains(4) + " " + b.add(9) + " " + b.add(9) + " " + b.add(0));
		for(int i=0; i<10;i++){
			 b.add(i);
		}		
		System.out.println(b);
		for(int i=0; i<5;i++){
			 b.remove(i*3);
		}
		System.out.println(b);
		System.out.println();
	}

}
