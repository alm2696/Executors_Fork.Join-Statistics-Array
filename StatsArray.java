package mod08_OYO_02;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * The StatsArray class handles the creation, population, and computation of statistics for an array of
 * integers. It provides methods for executing these tasks using both Executors and Fork/Join frameworks.
 * 
 * @author angel
 */
public class StatsArray {

	/** Array to store random integer values. */
	private int[] array;

	/** Minimum value in the array. */
	private int min;

	/** Maximum value in the array. */
	private int max;

	/** Total sum of the values in the array. */
	private long total;

	/** Lock object for thread-safe updates to statistics. */
	private final Object lockStats = new Object();

	/**
	 * Initializes the array with the specified size.
	 * 
	 * @param size the size of the array
	 */
	public void create(int size) {
		this.array = new int[size];  // Create an array of the specified size
	}

	/**
	 * Populates the array with random integers using a fixed thread pool with the
	 * specified number of threads. Each thread is responsible for a portion of the array.
	 * 
	 * @param threadCount the number of threads to use
	 */
	public void populateWithExecutors(int threadCount) {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		int range = array.length / threadCount;  // Divide the array into ranges for each thread

		// Assign each thread a range to populate
		for (int i = 0; i < threadCount; i++) {
			int start = i * range;
			int end = (i == threadCount - 1) ? array.length : start + range;
			executor.execute(() -> {
				Random random = new Random();
				for (int j = start; j < end; j++) {
					array[j] = random.nextInt();  // Fill the array with random integers
				}
			});
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	/**
	 * Computes the minimum, maximum, and total sum of the array elements
	 * using a fixed thread pool with the specified number of threads.
	 * 
	 * @param threadCount the number of threads to use
	 */
	public void computeWithExecutors(int threadCount) {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		int range = array.length / threadCount;

		// Initialize min, max, and total
		min = array[0];
		max = array[0];
		total = 0;

		// Assign each thread a range to compute statistics
		for (int i = 0; i < threadCount; i++) {
			int start = i * range;
			int end = (i == threadCount - 1) ? array.length : start + range;
			executor.execute(() -> updateStats(start, end));  // Compute stats for the given range
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

	/**
	 * Updates the minimum, maximum, and total sum for a given range of the array.
	 * This method is synchronized to ensure thread-safe updates to shared variables.
	 * 
	 * @param start the starting index of the range
	 * @param end   the ending index of the range
	 */
	private void updateStats(int start, int end) {
		int localMin = array[start];
		int localMax = array[start];
		long localTotal = 0;

		// Calculate local min, max, and total within the range
		for (int i = start; i < end; i++) {
			if (array[i] < localMin) localMin = array[i];
			if (array[i] > localMax) localMax = array[i];
			localTotal += array[i];
		}

		// Synchronize updates to shared variables
		synchronized (lockStats) {
			if (localMin < min) min = localMin;
			if (localMax > max) max = localMax;
			total += localTotal;
		}
	}

	/**
	 * Populates the array with random integers using the Fork/Join framework.
	 */
	public void populateWithForkJoin() {
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(new PopulateTask(array, 0, array.length));  // Fork and join task for populating
	}

	/**
	 * Computes the min, max, and total of the array elements using the Fork/Join framework.
	 */
	public void computeWithForkJoin() {
		min = array[0];
		max = array[0];
		total = 0;

		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(new ComputeTask(0, array.length));  // Fork and join task for computation
	}

	/**
	 * Recursive task for populating the array using the Fork/Join framework.
	 */
	private class PopulateTask extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private int[] array;
		private int start, end;
		private static final int THRESHOLD = 10000;

		/**
		 * Constructs a new PopulateTask for a given range of the array.
		 * 
		 * @param array the array to populate
		 * @param start the starting index
		 * @param end   the ending index
		 */
		PopulateTask(int[] array, int start, int end) {
			this.array = array;
			this.start = start;
			this.end = end;
		}

		@Override
		protected void compute() {
			if (end - start <= THRESHOLD) {
				Random random = new Random();
				for (int i = start; i < end; i++) {
					array[i] = random.nextInt();  // Populate directly if below the threshold
				}
			} else {
				int mid = (start + end) / 2;
				invokeAll(new PopulateTask(array, start, mid), new PopulateTask(array, mid, end));
			}
		}
	}

	/**
	 * Recursive task for computing statistics using the Fork/Join framework.
	 */
	private class ComputeTask extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private int start, end;
		private static final int THRESHOLD = 10000;

		/**
		 * Constructs a new ComputeTask for a given range of the array.
		 * 
		 * @param start the starting index
		 * @param end   the ending index
		 */
		ComputeTask(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		protected void compute() {
			if (end - start <= THRESHOLD) {
				updateStats(start, end);  // Compute directly if below the threshold
			} else {
				int mid = (start + end) / 2;
				invokeAll(new ComputeTask(start, mid), new ComputeTask(mid, end));
			}
		}
	}

	/**
	 * Returns the minimum value in the array.
	 * 
	 * @return the minimum value
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Returns the maximum value in the array.
	 * 
	 * @return the maximum value
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Returns the average value of the array.
	 * 
	 * @return the mean value
	 */
	public double getMean() {
		return (double) total / array.length;
	}
}
