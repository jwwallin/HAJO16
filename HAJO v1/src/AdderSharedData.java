
/**
 * @author Jussi Wallin
 *
 */
public class AdderSharedData {
	int adderDataSum[];
	int adderNumberCount[];
	int totalSum;
	
	/**
	 * synchronized getter for the sum of a specific adder
	 */
	public synchronized int getAdderDataSum(int i) {
		return adderDataSum[i];
	}

	/**
	 * synchronized method for operating on the data
	 */
	public synchronized void increaseAdderDataSum(int index, int num) {
		adderDataSum[index] += num;
		adderNumberCount[index]++;
		totalSum += num;
	}

	/**
	 * synchronized getter for the summed integer count
	 */
	public synchronized int getAdderNumberCount(int i) {
		return adderNumberCount[i];
	}

	/**
	 * synchronized getter for the current total sum
	 */
	public synchronized int getTotalSum() {
		return totalSum;
	}

	/**
	 * constructor
	 */
	public AdderSharedData(int i) {
		adderDataSum = new int[i];
		adderNumberCount = new int[i];
	}
}
