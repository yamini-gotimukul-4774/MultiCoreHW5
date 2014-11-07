package problem2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * (2)Implement Lock-based and Lock-Free unbounded queues. For the lock based
 * implementation, use different locks for enq and deq operations. For the
 * variable count use AtomicInteger. For the lock-free implementation, use
 * Michael and Scott’s algorithm as explained in the class. The deq operation
 * should block if the queue is empty.
 * 
 * 
 * (a) For both the data structures use a list based implementation (rather than
 * an array based implementation). Compare the throughput for the program that
 * uses n threads, where n = 1..6. For queues assume that 60% of the operations
 * are enq and 40% operations are deq. For stacks assume that 60% of the
 * operations are push and 40% operations are pop.
 * 
 * @author yaminigotimukul
 * 
 */

public class LockBasedQueue {
	private final ReentrantLock enqLock = new ReentrantLock();
	private final ReentrantLock deqLock = new ReentrantLock();
	public volatile AtomicLong enqueOperationTime = new AtomicLong();
	public volatile AtomicLong dequeOperationTime = new AtomicLong();
	private static int numb_threads;
	private Node tail;
	private Node head;
	double tput;

	public LockBasedQueue(int numb_threads) {
		this.numb_threads = numb_threads;
		tail = new Node(null);
		head = tail;
	}

	public void enq(Integer x) {
		enqLock.lock();
		try {
			Node n = new Node(x);
			tail.next = n;
			tail = n;
		} finally {
			enqLock.unlock();
		}
	}

	public Integer deque() {
		Integer rs;
		deqLock.lock();
		try {
			if (head.next == null) {
				while (head.next == null) {
				}
			}
			rs = head.next.value;
			head = head.next;
		} finally {
			deqLock.unlock();
		}
		return rs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Node current = head;
		while (current.next != null) {
			sb.append(current.next.value + " | ");
			current = current.next;
		}
		return sb.toString();
	}

	public static class QueueThread extends Thread {
		private LockBasedQueue l;
		long start = 0;
		long total_time = 0;
		long enque_time = 0;
		long deque_time = 0;

		public QueueThread(LockBasedQueue l) {
			this.l = l;

		}

		public void run() {
			l.tput = 0.0;
			Random randomGenerator = new Random();
			int q = 24000 / numb_threads;
			for (int i = 0; i < q; i++) {
				double d = randomGenerator.nextDouble();
				Integer x = randomGenerator.nextInt(5000);
				if (d <= 0.4) {
					start = System.nanoTime();
					l.deque();
					deque_time = deque_time + (System.nanoTime() - start);
				} else if (d > 0.4) {
					start = System.nanoTime();
					l.enq(x);
					enque_time = enque_time + (System.nanoTime() - start);
				}
			}

		}
	}

	public static void main(String[] args) {

		/*
		 * l.enq(3); l.enq(5); System.out.println(l); l.deque(); l.deque();
		 * System.out.println(l);
		 */
		long total_time = 0;
		int numb_threads = 5;
		for (int j = 2; j <= 6; j++) {
			LockBasedQueue l = new LockBasedQueue(j);
			for (int i = 0; i < 100; i++) {
				l.enq(-1);
			}
			QueueThread[] t_array = new QueueThread[numb_threads];
			// long start = System.currentTimeMillis();
			for (int i = 0; i < numb_threads; i++) {
				QueueThread t = new QueueThread(l);
				t_array[i] = t;
				t.start();
			}

			for (int i = 0; i < numb_threads; i++) {
				try {
					t_array[i].join();
					total_time =t_array[i].enque_time+ t_array[i].deque_time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//	System.out.println("time " + total_time);
				double throughput = 24000.0 * Math.pow(10, 9)/ total_time;
			//	l.tput = l.tput + throughput;
			System.out.println("Throughput for " + j + " threads " + throughput/ j);

		}
		/*
		 * long total_time = System.currentTimeMillis() - start;
		 * System.out.println("time " + total_time); long throughput = 25000 *
		 * 1000 / total_time; System.out.println("Throughput " + throughput);
		 */
	}
}
