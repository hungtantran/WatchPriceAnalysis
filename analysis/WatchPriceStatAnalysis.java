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
			this.calculateWatchPriceStat(i+1);
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
			int[] values, int[] numbers) {
		if (arr.length == 0 || values.length < 20 || numbers.length < 20)
			return;

		int distance = Math.round((float) arr.length / 20);

		int prevIndex = -1;
		for (int i = 0; i < 19; i++) {
			int curIndex = Math.min(Math.round(distance * (i + 1)),
					arr.length - 1);
			values[i] = arr[curIndex];
			numbers[i] = curIndex - prevIndex;
			prevIndex = curIndex;
		}
		numbers[19] = arr.length - (prevIndex + 1);
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

			WatchPriceStatAnalysis.findDistributionOfSortedArray(priceLists,
					values, numbers);
		}

		// Print out the topic price statistics
		System.out.println("TopicId " + topicId + " = " + mean + " " + median
				+ " " + std);
		System.out.println("Values: ");
		for (int i = 0; i < values.length; i++)
			System.out.print(values[i] + " ");
		System.out.println();
		System.out.println("Numbers: ");
		for (int i = 0; i < numbers.length; i++)
			System.out.print(numbers[i] + " ");
		System.out.println();
		System.out.println();
	}

	public static void main(String[] args) {
		WatchPriceStatAnalysis analyzer = new WatchPriceStatAnalysis();
		analyzer.calculateWatchsPriceStat();
	}
}
