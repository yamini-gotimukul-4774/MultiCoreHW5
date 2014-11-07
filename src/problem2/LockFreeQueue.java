package problem2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeQueue {
	AtomicStampedReference<LockFreeNode> head;
	AtomicStampedReference<LockFreeNode> tail;
	static int numb_threads;
	double tput;

	public LockFreeQueue(int numb_threads) {
		this.numb_threads = numb_threads;
		LockFreeNode n = new LockFreeNode(-99);
		head = new AtomicStampedReference<LockFreeNode>(n, 0);
		tail = new AtomicStampedReference<LockFreeNode>(n, 0);
	}

	public boolean enque(Integer x) {
		LockFreeNode newNode = new LockFreeNode(x);

		while (true) {
			int[] tail_Stamp = new int[1];
			LockFreeNode last = tail.get(tail_Stamp);
			int[] last_Stamp = new int[1];
			LockFreeNode next = tail.getReference().next.get(last_Stamp);
			// AtomicInteger stamp_last = new
			// AtomicInteger(last.next.getStamp());
			if (last == tail.getReference()) { // checking if the reference is
												// still current
				if (next == null) { // checking if the next == null, that is we
									// are at the last node
					if (last.next.compareAndSet(next, newNode, last_Stamp[0], last_Stamp[0] + 1)) {
						tail.compareAndSet(last, newNode, tail_Stamp[0], tail_Stamp[0] + 1);
						return true;
					}
				} else {
					tail.compareAndSet(last, next, tail_Stamp[0], tail_Stamp[0] + 1);
				}
			}
		}
	}

	public Integer dequeu() {
		while (true) {
			int[] tail_Stamp = new int[1];
			int[] head_Stamp = new int[1];
			int[] next_stamp = new int[1];
			LockFreeNode last = tail.get(tail_Stamp);
			LockFreeNode first = head.get(head_Stamp);
			LockFreeNode next = first.next.get(next_stamp);
			if (first == head.getReference()) {
				if (first == last) {
					if (next == null) {
						continue;
					}
					tail.compareAndSet(last, next, tail_Stamp[0], tail_Stamp[0] + 1);
				} else {
					int rs = next.value;
					if (head.compareAndSet(first, next, head_Stamp[0], head_Stamp[0] + 1)) {
						return rs;
					}
				}
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		AtomicStampedReference<LockFreeNode> current = head;
		AtomicStampedReference<LockFreeNode> last = tail.getReference().next;
		while (current.getReference() != last.getReference()) {
			sb.append(current.getReference().value + " | ");
			current = current.getReference().next;
		}
		return sb.toString();
	}

	public static class QueueThread extends Thread {
		Integer x;
		LockFreeQueue l;
		long start = 0;
		
		long enque_time = 0;
		long deque_time = 0;

		public QueueThread(LockFreeQueue l) {
			this.l = l;
		}

		public void run() {
			l.tput = 0.0;
			// System.out.println(Thread.currentThread().getName());

			Random randomGenerator = new Random();
			int q = 24000 / numb_threads;
			for (int i = 0; i < q; i++) {
				double d = randomGenerator.nextDouble();
				Integer x = randomGenerator.nextInt(5000);
				if (d <= 0.4) {
					start = System.nanoTime();
					l.dequeu();
					deque_time = deque_time + (System.nanoTime() - start);
				} else if (d > 0.4) {
					start = System.nanoTime();
					l.enque(x);
					enque_time = enque_time + (System.nanoTime() - start);
				}

			}
			/*total_time = enque_time + deque_time;
			System.out.println("time " + total_time);
			double throughput = 24000.0 * Math.pow(10, 9) / total_time;
			l.tput = l.tput + throughput;*/
		}
	}

	public static void main(String[] args) {
		/*
		 * int numb_threads =5;
		 * 
		 * l.enque(3); l.enque(5);
		 */
		long total_time = 0;
		for (int j = 2; j <= 6; j++) {
			LockFreeQueue l = new LockFreeQueue(j);
			for (int i = 0; i < 100; i++) {
				l.enque(-1);
			}
			QueueThread[] t_array = new QueueThread[numb_threads];

			for (int i = 0; i < numb_threads; i++) {
				QueueThread t = new QueueThread(l);
				t_array[i] = t;
				t.start();
			}

			for (int i = 0; i < numb_threads; i++) {
				try {
					t_array[i].join();
					total_time += t_array[i].enque_time + t_array[i].deque_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
			
		//	System.out.println("time " + total_time);
			double throughput = 24000.0 * Math.pow(10, 9) / total_time;
			System.out.println("Throughput for " + j + " threads " + throughput / j);
		}

		/*
		 * QueueThread q1 = new QueueThread(0, l); QueueThread q2 = new
		 * QueueThread(1, l); QueueThread q3 = new QueueThread(-2, l);
		 * q1.start(); q2.start(); q3.start(); try { q1.join(); q2.join();
		 * q3.join(); } catch (InterruptedException e) { e.printStackTrace(); }
		 * l.dequeu(); l.dequeu(); l.dequeu(); l.dequeu(); l.dequeu();
		 * l.dequeu(); //System.out.println(l);
		 */}
}
