package problem3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;
//import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BaseHashTable<T> extends MyHashTable<T> {
	// private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private volatile ReentrantReadWriteLock[] locks;
	private AtomicMarkableReference<Thread> owner;

	public BaseHashTable(int capacity) {
		super(capacity);
		locks = new ReentrantReadWriteLock[capacity];
		for (int i = 0; i < capacity; i++) {
			locks[i] = new ReentrantReadWriteLock();
		}
		owner = new AtomicMarkableReference<Thread>(null, false);
	}

	@Override
	public boolean contains(T x) {
		acquire(x, "read");
		try {
			int bucket = x.hashCode() % table.length;
			return table[bucket].contains(x);
		} finally {
			release(x, "read");
		}
	}

	@Override
	public boolean add(T x) {
		boolean wasAdded = false;
		acquire(x, "write");
		try {
			int bucket = x.hashCode() % table.length;
			if (!table[bucket].contains(x)) {
				table[bucket].add(x);
				wasAdded = true;
				size.getAndIncrement();
			}
		} finally {
			release(x, "write");
		}
		if (policy()) {
			resize();
		}
		return wasAdded;
	}

	@Override
	public boolean remove(T x) {
		boolean wasRemoved = false;
		acquire(x, "write");
		try {
			int bucket = x.hashCode() % table.length;
			if (table[bucket].remove(x)) {
				size.getAndDecrement();
				wasRemoved = true;
			}
			return wasRemoved;
		} finally {
			release(x, "write");
		}
	}

	@Override
	public void resize() {
		int old_capacity = table.length;
//		boolean[] mark = {false};
		int new_capacity = 2*old_capacity;
		Thread me = Thread.currentThread();
		if (owner.compareAndSet(null, me, false, true)) {
			try {
				if (table.length != old_capacity) {
					return;
				}
				quiesce();
				List<T>[] old_table = table;
				table = (List<T>[]) new List[new_capacity];
				for (int i = 0; i < new_capacity; i++) {
					table[i] = new ArrayList<T>();
				}
				locks = new ReentrantReadWriteLock[new_capacity];
				for (int j = 0; j < locks.length; j++) {
					locks[j] = new ReentrantReadWriteLock();
				}
				for (List<T> list : old_table) {
					for (T t : list) {
						table[t.hashCode() % table.length].add(t);
					}
				}
			} finally {
				owner.set(null, false);
			}
		}
	}
	
	protected void quiesce() {
		for (ReentrantReadWriteLock lock : locks) {
			while (lock.isWriteLocked()) { }
		}
	}

	public boolean policy() {
		return size.get() / table.length > (table.length / 2);
	}

	public void acquire(T x, String kind) {
		boolean[] mark = {true};
		Thread me = Thread.currentThread();
		Thread who;
		while (true) {
			do {
				who = owner.get(mark);
			} while (mark[0] && who != me);
			ReentrantReadWriteLock[] oldLocks = locks;
			ReentrantReadWriteLock oldLock = oldLocks[x.hashCode() % oldLocks.length];
			if (kind.equals("read")) {
				oldLock.readLock().lock();
			}
			else {
				oldLock.writeLock().lock();
			}
			who = owner.get(mark);
			if ((!mark[0] || who == me) && locks == oldLocks) {
				return;
			} else {
				if (kind.equals("read")) {
					oldLock.readLock().unlock();
				}
				else {
					oldLock.writeLock().unlock();
				}
			}
		}
	}

	public void release(T x, String kind) {
		if (kind.equals("read")) {
			locks[x.hashCode() % locks.length].readLock().unlock();
		}
		else {
			locks[x.hashCode() % locks.length].writeLock().unlock();
		}
		
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
	
//	public static void main(String[] args) {
//		MyHashTable<Integer> b = new BaseHashTable<>(4);
//		System.out.println(b.contains(4) + " " + b.add(9) + " " + b.add(9) + " " + b.add(0));
//		for(int i=0; i<10;i++){
//			 b.add(i);
//		}		
//		System.out.println(b);
//		for(int i=0; i<5;i++){
//			 b.remove(i*3);
//		}
//		System.out.println(b);
//		System.out.println();
//	}

}
