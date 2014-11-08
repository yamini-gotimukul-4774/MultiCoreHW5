package problem3;

// debug:
import java.util.HashSet;
import java.util.Random;

public class HashTableThread extends Thread {
	private volatile MyHashTable<Integer> table;
	private int n_ops;
	protected int n_adds;
	protected int n_contains;
	protected int n_removes;
	protected long add_time;
	protected long contains_time;
	protected long remove_time;
	
	// debug:
//	protected HashSet<Integer> local_adds;
//	protected HashSet<Integer> local_removes;
//	protected int n_successful_removes = 0;
//	protected int n_successful_adds = 0;
	
	public HashTableThread(MyHashTable<Integer> table, int n_ops) {
		this.table = table;
		this.n_ops = n_ops;
		n_adds = n_contains = n_removes = 0;
		add_time = contains_time = remove_time = 0;
		
		// debug:
//		local_adds = new HashSet<Integer>();
//		local_removes = new HashSet<Integer>();
	}
	
	public void run() {
		Random rgen = new Random();
		for (int i = 0; i < n_ops; i++) {
			double p = Math.random();
			Integer val = rgen.nextInt(100001);
			if (p < 0.5) {
				n_contains++;
				long t = System.nanoTime();
				table.contains(val);
				contains_time += System.nanoTime() - t;
			}
			else if (p < 0.9) {
				n_adds++;
				long t = System.nanoTime();
				boolean wasAdded = table.add(val);
				add_time += System.nanoTime() - t;
				// debug:
//				if (wasAdded) {
//					n_successful_adds++;
//					local_adds.add(val);
//				}
				// end debug
			}
			else {
				n_removes++;
				long t = System.nanoTime();
				boolean wasRemoved = table.remove(val);
				remove_time += System.nanoTime() - t;
				// debug:
//				if (wasRemoved) {
//					n_successful_removes++;
//					local_removes.add(val);
//				}
			}
		}
		
	}

}
