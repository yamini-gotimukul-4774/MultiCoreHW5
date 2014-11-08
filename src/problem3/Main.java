package problem3;

import java.util.HashSet;

public class Main {

	public static void main(String[] args) {
		evaluate_hash_table("lock-free");
	}

	public static void evaluate_hash_table(String type) {
		System.out.println("Evaluating " + type + " hash table ++++++++++++++++++");
		int total_ops = 250;
		int[] n_threads_arr = new int[]{1, 2, 3, 4, 5, 6};
		for (int t = 0; t < n_threads_arr.length; t++) {
			int n_threads = n_threads_arr[t];
			System.out.println("For " + n_threads + " threads: ---------");
			MyHashTable<Integer> table = null;
			// debug:
			HashSet<Integer> union_table = new HashSet<Integer>();
			HashSet<Integer> removes_table = new HashSet<Integer>();

			if (type.equals("lock-based")) {
				table = new BaseHashTable<Integer>(2);
			}else if (type.equals("lock-free")) {
				table = new LockFreeHashTable<Integer>(6000);
			}
			else {
				System.err.println("ERROR: no such hash table implementation");
				System.exit(-1);
			}
			
			int n_adds = 0;
			int n_contains = 0;
			int n_removes = 0;
			long add_time = 0;
			long contains_time = 0;
			long remove_time = 0;
			
			int r = total_ops % n_threads;
			HashTableThread[] threads = new HashTableThread[n_threads];
			for (int i = 0; i < threads.length; i++) {
				int n_ops_per_thread = total_ops/n_threads;
				if (i < r) {
					n_ops_per_thread++;
				}
				threads[i] = new HashTableThread(table, n_ops_per_thread);
			}
			
			for (int i = 0; i < threads.length; i++) {
				threads[i].start();
			}
			
			// debug:
			int n_successful_adds = 0;
			int n_successful_removes = 0;
			
			for (int i = 0; i < threads.length; i++) {
				try {
					threads[i].join();
					n_adds += threads[i].n_adds;
					n_contains += threads[i].n_contains;
					n_removes += threads[i].n_removes;
					add_time += threads[i].add_time;
					contains_time += threads[i].contains_time;
					remove_time += threads[i].remove_time;
					// debug:
					union_table.addAll(threads[i].local_adds);
					removes_table.addAll(threads[i].local_removes);
					n_successful_adds += threads[i].n_successful_adds;
					n_successful_removes += threads[i].n_successful_removes;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long table_time = add_time + contains_time + remove_time;
			
			double throughput = (Math.pow(10, 9))*(n_adds+n_contains+n_removes)/(table_time + 0.0);
			System.out.println("Overall time taken = " + table_time + " ns");
			System.out.println("Overall throughput: " + throughput + " ops/s");
			
			// debug:
			union_table.removeAll(removes_table);
//			if (union_table.containsAll(table.toList()) && (table.toList().containsAll(union_table))) {
//				System.out.println("table matches union table");
//			}
			if (table.size.get() == (n_successful_adds-n_successful_removes)) {
				System.out.println("table matches union table");
			}
			else {
				System.out.println("bug!!! ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
//			System.out.println("shared table: " + table);
//			System.out.println("union table: " + union_table);
			System.out.println("size of shared table = " + table.size.get());
//			System.out.println("size of union table = " + union_table.size());
			System.out.println("N successful adds = " + n_successful_adds + "; N successful removes = " + n_successful_removes);
//			System.out.println("N adds = " + n_adds + "; N removes = " + n_removes + "; N contains = " + n_contains);
		}
	}
}

