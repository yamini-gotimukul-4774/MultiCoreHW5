package problem3;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class Bucket<T> implements Set<T> {

	static final int HI_MASK = 0x80000000;
	static final int MASK = 0x00FFFFFF;
	Node head;

	public Bucket() {
		head = new Node(0);
		head.next = new AtomicMarkableReference<Node>(new Node(Integer.MAX_VALUE), false);
	}

	public Bucket(Node n) {
		head = n;
		// head.next = new AtomicMarkableReference<Node>(new
		// Node(Integer.MAX_VALUE), false);
	}

	public int makeOrdinaryKey(T x) {
		int code = x.hashCode() & MASK;
		return Integer.reverse(code | HI_MASK);
	}

	public static int makeSentinel(int key) {
		return Integer.reverse(key & MASK);
	}

	@Override
	public boolean contains(Object x) {
		int key = makeOrdinaryKey((T) x);
		Window window = find(head, key);
		Node curr = window.current;
		return (curr.value == key);
	}

	public Bucket<T> getSentinel(int index) {
		int key = makeSentinel(index);
		boolean splice = false;
		while (true) {
			Window window = find(head, key);
			Node pred = window.previous;
			Node curr = window.current;
			if (curr.value == key) {
				return new Bucket<T>(curr);
			} else {
				Node n = new Node(key);
				n.next.set(pred.next.getReference(), false);
				splice = pred.next.compareAndSet(curr, n, false, false);
				if (splice) {
					return new Bucket<T>(n);
				}
			}
		}

	}

	@Override
	public boolean add(T x) {

		int key = makeOrdinaryKey(x);
		while (true) {
			Window window = find(head, key);
			if (window.current.value == key) {
				return false;
			} else {
				Node new_node = new Node(key);
				new_node.next = new AtomicMarkableReference<Node>(window.current, false);
				if (window.previous.next.compareAndSet(window.current, new_node, false, false)) {
					return true;
				}
			}
		}
	}

	@Override
	public boolean remove(Object o) {
		int key = makeOrdinaryKey((T) o);
		boolean markedAsDeleted = false;
		while (true) {
			Window window = find(head, key);
			if (window.current.value != key) {
				return false;
			} else {
				Node next = window.current.next.getReference();
				markedAsDeleted = window.current.next.attemptMark(next, true);
				if (!markedAsDeleted) {
					continue;
				}
				window.previous.next.compareAndSet(window.current, next, false, false);
				return true;
			}
		}
	}

	public Window find(Node head, int key) {
		Node previous = null, current = null, success = null;
		boolean[] marked = { false };
		boolean sn;
		r1: while (true) {
			previous = head;
			current = previous.next.getReference();
			while (true) {
				success = current.next.get(marked);
				while (marked[0]) {
					sn = previous.next.compareAndSet(current, success, false, false);
					if (!sn)
						continue r1;
					current = success;
					success = current.next.get(marked);
				}
				if (current.value >= key) {
					return new Window(previous, current);
				}
				previous = current;
				current = success;
			}
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
