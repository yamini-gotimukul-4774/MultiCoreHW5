package problem2;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedStack {
	ReentrantLock push_lock = new ReentrantLock();
	ReentrantLock pop_lock = new ReentrantLock();
	private Node top;
	static int numb_threads;

	LockBasedStack(int num) {
		numb_threads = num;
		top = new Node(-99);

	}

	public void push(Integer x) {
		push_lock.lock();
		try {
			Node n = new Node(x);
			n.next = top;
			top = n;
		} finally {
			push_lock.unlock();
		}
	}

	public Integer pop() {
		Integer rs;
		pop_lock.lock();
		try {
			Node current = top;
			if (current.next == null) {
				while (current.next == null) {

				}
			} else {
				rs = current.value;
				top = current.next;
				return rs;
			}
		} finally {
			pop_lock.unlock();
		}
		return -121;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current = top;
		while (current.next != null) {
			sb.append(current.value + " | ");
			current = current.next;
		}
		return sb.toString();
	}

	public static class StackThread extends Thread {
		LockBasedStack s;
		long start = 0;
		long total_time = 0;
		long push_time = 0;
		long pop_time = 0;

		public StackThread(LockBasedStack s) {
			this.s = s;
		}

		public void run() {
			Random randomGenerator = new Random();
			int q = 24000 / numb_threads;
			for (int i = 0; i < q; i++) {
				double d = randomGenerator.nextDouble();
				Integer x = randomGenerator.nextInt(5000);
				if (d <= 0.4) {
					start = System.nanoTime();
					s.pop();
					pop_time = pop_time + (System.nanoTime() - start);
				} else if (d > 0.4) {
					start = System.nanoTime();
					s.push(x);
					push_time = push_time + (System.nanoTime() - start);
				}
			}

		}
	}

	public static void main(String[] args) {
		long total_time = 0;
		
		for (int j = 2; j <= 6; j++) {
			LockBasedStack s = new LockBasedStack(j);
			for (int i = 0; i < 100; i++) {
				s.push(-1);
			}
			StackThread[] t_array = new StackThread[numb_threads];
			for (int i = 0; i < numb_threads; i++) {
				StackThread t = new StackThread(s);
				t_array[i] = t;
				t.start();
			}

			for (int i = 0; i < numb_threads; i++) {
				try {
					t_array[i].join();
					total_time =t_array[i].push_time+ t_array[i].pop_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//	System.out.println("time " + total_time);
				double throughput = 25000.0 * Math.pow(10, 9)/ total_time;
			//	l.tput = l.tput + throughput;
			System.out.println("Throughput for " + j + " threads " + throughput/ j);
			
		}
	}
}
