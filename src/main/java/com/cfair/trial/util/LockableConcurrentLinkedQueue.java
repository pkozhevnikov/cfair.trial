package com.cfair.trial.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class LockableConcurrentLinkedQueue<E> {
	
	private Queue<E> queue = new ConcurrentLinkedQueue<>();
	
	private ReentrantLock lock = new ReentrantLock();

	public boolean offer(E e) {
		lock.lock();
		try {
			return queue.offer(e);
		} finally {
			lock.unlock();
		}
	}

	public E poll() {
		return queue.poll();
	}

	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}

}
