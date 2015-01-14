package newscrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
	private BlockingQueue<Runnable> taskQueue = null;
	private List<PoolThread> threads = new ArrayList<PoolThread>();
	private boolean isStopped = false;

	public ThreadPool(int noOfThreads, int maxNoOfTasks) {
		taskQueue = new LinkedBlockingQueue<Runnable>();

		for (int i = 0; i < noOfThreads; i++) {
			threads.add(new PoolThread(taskQueue));
		}
		for (PoolThread thread : threads) {
			thread.start();
		}
	}

	public synchronized void execute(Runnable task) {
		if (this.isStopped)
			throw new IllegalStateException("ThreadPool is stopped");

		this.taskQueue.add(task);
	}

	public synchronized void stop() {
		this.isStopped = true;
		for (PoolThread thread : threads) {
			thread.stopThread();
		}
	}

	private class PoolThread extends Thread {
		private BlockingQueue<Runnable> taskQueue = null;
		private boolean isStopped = false;

		public PoolThread(BlockingQueue<Runnable> queue) {
			taskQueue = queue;
		}

		public void run() {
			while (!isStopped()) {
				try {
					Runnable runnable = (Runnable) taskQueue.remove();
					runnable.run();
				} catch (Exception e) {
					// log or otherwise report exception,
					// but keep pool thread alive.
				}
			}
		}

		public synchronized void stopThread() {
			isStopped = true;
			this.interrupt(); // break pool thread out of dequeue() call.
		}

		public synchronized boolean isStopped() {
			return isStopped;
		}
	}
}
