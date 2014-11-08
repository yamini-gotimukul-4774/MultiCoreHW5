package problem3;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
	public Integer value;
	public AtomicMarkableReference<Node> next;

	public Node(Integer value) {
		this.value = value;
		next = new AtomicMarkableReference<Node>(null,false);
	}
}
