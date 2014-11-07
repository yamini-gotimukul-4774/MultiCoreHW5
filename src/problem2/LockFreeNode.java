package problem2;

import java.util.concurrent.atomic.AtomicStampedReference;


public class LockFreeNode {
	Integer value;
	AtomicStampedReference<LockFreeNode> next;
	
	public LockFreeNode(Integer value){
		this.value = value;
		next =new AtomicStampedReference<LockFreeNode>(null, 0);
	}

	@Override
	public String toString() {
		return "|  |";
	}
	
	
}
