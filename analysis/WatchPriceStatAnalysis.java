package analysis;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import newscrawler.Globals;
import dbconnection.MySqlConnection;

public class WatchPriceStatAnalysis {
	private MySqlConnection mysqlConnection = null;

	public WatchPriceStatAnalysis() {
		this.mysqlConnection = new MySqlConnection();
	}

	// Calculate all watch topics statistics
	public void calculateWatchsPriceStat() {
		String[] horologyTopics = Globals.HOROLOGYTOPICS;

		for (int i = 0; i < horologyTopics.length; i++) {
			System.out.println("Topic " + Globals.HOROLOGYTOPICS[i]);
			this.calculateWatchPriceStat(i + 1);
		}
	}

	// Find the mean of an array of integer
	public static float findMeanOfArray(Integer[] arr) {
		float mean = 0;

		for (int i = 0; i < arr.length; i++) {
			mean += arr[i];
		}

		if (arr.length > 0)
			mean = mean / (float) arr.length;

		return mean;
	}

	// Find the median of a sorted array of integer
	public static int findMedianOfSortedArray(Integer[] arr) {
		int median = 0;

		if (arr.length > 0)
			median = arr[arr.length / 2];

		return median;
	}

	// Find the 20 points of 1/20 distribution of a sorted array
	public static void findDistributionOfSortedArray(Integer[] arr,
			int[] values, int[] numbers, float mean, float median, float std) {
		if (arr.length == 0 || values.length < 20 || numbers.length < 20)
			return;
		
		float middlePoint = mean;
		float distanceDown = Math.min(std, middlePoint)/10;
		float distanceUp = std/10;
		
		// If the dataset is too dominated by outliers, attempt to remove them
		if (std > mean * 1.5 && arr.length > 2000) {
			ArrayList<Integer> newArrList = new ArrayList<Integer>();
			
			int lowerBound = (int)Math.round((float)arr.length * 0.00025);
			int upperBound = (int)Math.round((float)arr.length * 0.99975);
			for (int i = lowerBound; i < upperBound; i++) {
				newArrList.add(arr[i]);
			}
			
			Integer[] newArr = new Integer[newArrList.size()];
			for (int i = 0; i < newArrList.size(); i++) {
				newArr[i] = newArrList.get(i);
			}
			
			middlePoint = WatchPriceStatAnalysis.findMeanOfArray(newArr);
			float newStd = WatchPriceStatAnalysis.findStandardDeviationOfArray(newArr);
			distanceDown = Math.min(newStd, middlePoint)/11;
			distanceUp = (newStd+std)/20;
		}
		
		// Values are taken as 20 values in between middlePoint-std and middlePoint+std
		for (int i = 0; i < 10; i++) {
			values[i] = Math.max(Math.round(middlePoint-distanceDown*(10-i)), 0);
		}
		for (int i = 0; i < 9; i++) {
			values[i+10] = Math.round(middlePoint+distanceUp*i);
		}
		
		// Populate how many watches are in each interval
		int index = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] <= values[index] || index == 19) {
				numbers[index]++;
			} else {
				index++;
				i--;
			}
		}
	}

	// Find the standard deviation of an array of integer
	public static float findStandardDeviationOfArray(Integer[] arr) {
		float std = 0;

		if (arr.length > 0) {
			float mean = WatchPriceStatAnalysis.findMeanOfArray(arr);

			for (int i = 0; i < arr.length; i++) {
				std += (arr[i] - mean) * (arr[i] - mean);
			}

			std = std / (float) arr.length;
			std = (float) Math.sqrt(std);
		}

		return std;
	}

	// Calculate statistic for a specified watch topic
	public void calculateWatchPriceStat(int topicId) {
		float mean = 0;
		float median = 0;
		float std = 0;
		int lowestPrice = 0;
		int highestPrice = 0;
		int numWatches = 0;
		int numArticles = this.mysqlConnection
				.getNumArticleWithTopicId(topicId);

		int[] values = new int[20];
		int[] numbers = new int[20];

		ResultSet resultSet = this.mysqlConnection
				.getWatchInfo(topicId, -1, -1);

		if (resultSet != null) {
			ArrayList<Integer> watchPrice = new ArrayList<Integer>();
			try {
				// Iterate through the result set to populate the information
				while (resultSet.next()) {
					Integer price = resultSet.getInt(7);

					if (price != null)
						watchPrice.add(price);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Get prices of all the watches of the specific topic into
			// priceLists array and sort the array
			Integer[] priceLists = new Integer[watchPrice.size()];
			priceLists = (Integer[]) watchPrice.toArray(priceLists);
			Arrays.sort(priceLists);

			// Calculate the mean, median, standard deviation and distribution
			// of the priceLists array
			mean = WatchPriceStatAnalysis.findMeanOfArray(priceLists);
			median = WatchPriceStatAnalysis.findMedianOfSortedArray(priceLists);
			std = WatchPriceStatAnalysis
					.findStandardDeviationOfArray(priceLists);

			if (priceLists.length > 0) {
				lowestPrice = priceLists[0];
				highestPrice = priceLists[priceLists.length - 1];
				numWatches = priceLists.length;
			}

			WatchPriceStatAnalysis.findDistributionOfSortedArray(priceLists,
					values, numbers, mean, median, std);
		}

		// Print out the topic price statistics
		System.out.println("TopicId " + topicId);
		System.out.println("Num Articles " + numArticles);
		System.out.println("Num Watches " + numWatches);
		System.out.println("Lowest Price " + lowestPrice);
		System.out.println("Highest Price " + highestPrice);
		System.out.println("Mean " + mean);
		System.out.println("Median " + median);
		System.out.println("Standard Deviation " + std);
		System.out.println("Values: ");
		for (int i = 0; i < values.length; i++)
			System.out.print(values[i] + " ");
		System.out.println();
		System.out.println("Numbers: ");
		for (int i = 0; i < numbers.length; i++)
			System.out.print(numbers[i] + " ");
		System.out.println();
		System.out.println();

		// Insert the new statistic into the database
		this.mysqlConnection.addWatchPriceStat(topicId, numArticles,
				numWatches, lowestPrice, highestPrice, mean, median, std,
				values, numbers);
	}

	public static void main(String[] args) {
		WatchPriceStatAnalysis analyzer = new WatchPriceStatAnalysis();
		analyzer.calculateWatchsPriceStat();
	}
}
