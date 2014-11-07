package problem2;

import java.util.Random;


public class StackThread  extends Thread{
	Stack s;
	long start = 0;
	long total_time = 0;
	long push_time = 0;
	long pop_time = 0;
	int numb_threads = 0;

	public StackThread(Stack s,int num) {
		this.s = s;
		numb_threads = num;
	}

	public void run() {
		Random randomGenerator = new Random();
		int q = 25000 / numb_threads;
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