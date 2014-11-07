package problem2;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleLockFreeStack implements Stack {

	private AtomicReference<Node> top;


	public SimpleLockFreeStack() {
		Node n = new Node(-99);
		top = new AtomicReference<Node>(n);
	}

	public boolean push(Integer x) {
		while (true) {
			Node new_Node = new Node(x);
			Node current = top.get();
			new_Node.next = current;
			if (top.compareAndSet(current, new_Node)) {
				return true;
			} else {
				continue;
			}
		}

	}

	public Integer pop() {
		while (true) {
			Node current_top = top.get();
			if (current_top == null) {
				continue;
			} else {
				Node new_top = current_top.next;
				if (top.compareAndSet(current_top, new_top)) {
					Node removed_node = current_top;
					if (removed_node != null) {
						return removed_node.value;
					} else {
						continue;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current = top.get();
		while (current.next != null) {
			sb.append(current.value + " | ");
			current = current.next;
		}
		return sb.toString();
	}

	/*
	 * public class LockFreeStack { private AtomicReference top = new
	 * AtomicReference(null); public boolean tryPush(Node node){ Node oldTop =
	 * top.get(); node.next = oldTop; return(top.compareAndSet(oldTop, node)) }
	 * public void push(T value) { Node node = new Node(value); while (true) {
	 * if (tryPush(node)) { return; } else backoff.backoff(); }}
	 */

	public static void main(String[] args) {
/*
		SimpleLockFreeStack s = new SimpleLockFreeStack();
		for (int i = 0; i < 10; i++) {
			s.push(i);
		}

		for (int i = 0; i < 10; i++) {
			s.pop();
		}
*/
		SimpleLockFreeStack s = null;
		long total_time = 0;

		for (int j = 2; j <= 6; j++) {

			s = new SimpleLockFreeStack();
			for (int i = 0; i < 100; i++) {
				s.push(-1);
			}
			StackThread[] t_array = new StackThread[j];
			for (int i = 0; i < j; i++) {
				StackThread t = new StackThread(s, j);
				t_array[i] = t;
				t.start();
			}

			for (int i = 0; i < j; i++) {
				try {
					t_array[i].join();
					total_time = t_array[i].push_time + t_array[i].pop_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// System.out.println(s);
			double throughput = 25000.0 * Math.pow(10, 9) / total_time;
			System.out.println("Throughput for " + j + " threads " + throughput / j);

		}

		// System.out.println(s);
	}

}
