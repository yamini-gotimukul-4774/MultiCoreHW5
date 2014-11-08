package problem3;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeHashTable<T> extends MyHashTable<T> {

	protected Bucket<T>[] bucket;
	protected AtomicInteger bucket_size;
	private final int THRESHOLD = 5;

	public LockFreeHashTable(int capacity) {
		super(capacity);
		bucket = (Bucket<T>[]) new Bucket[capacity];
		bucket[0] = new Bucket<T>();
		bucket_size = new AtomicInteger(2);

	}

	private Bucket<T> getBucketList(int myBucket) {
		if (bucket[myBucket] == null) {
			initializeBucket(myBucket);
		}
		return bucket[myBucket];
	}

	private void initializeBucket(int myBucket) {
		int parent = getParent(myBucket);
		if (bucket[parent] == null) {
			initializeBucket(parent);
		}
		Bucket<T> b = bucket[parent].getSentinel(myBucket);
		if (b != null) {
			bucket[myBucket] = b;
		}
	}

	private int getParent(int myBucket) {
		int parent = bucket_size.get();
		do {
			parent = parent >> 1;
		} while (parent > myBucket);
		parent = myBucket - parent;
		return parent;
	}

	@Override
	public boolean contains(T x) {
		int myBucket = x.hashCode() % bucket_size.get();
		Bucket<T> b = getBucketList(myBucket);
		return b.contains(x);
	}

	@Override
	public boolean add(T x) {
		int myBucket = x.hashCode() % bucket_size.get();
		Bucket<T> b = getBucketList(myBucket);
		if (!b.add(x)) {
			return false;
		}
		int setSizeNow = size.getAndIncrement();
		int bucketSizeNow = bucket_size.get();
		if (setSizeNow / bucketSizeNow > THRESHOLD) {
			bucket_size.compareAndSet(bucketSizeNow, 2 * bucketSizeNow);
		}
		return true;
	}

	@Override
	public boolean remove(T x) {
		int myBucket = x.hashCode() % bucket_size.get();
		Bucket<T> b = getBucketList(myBucket);
		if(!b.remove(x)){
			return false;
		}
		size.getAndDecrement();
		return true;
	}

	@Override
	public List<T> toList() {
		// TODO Auto-generated method stub
		return null;
	}

}
