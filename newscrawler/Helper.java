package newscrawler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class Helper {
	// Given a string and list of delimiters. Split the string into a set of
	// words with delimiter contained in the list
	public static Set<String> splitString(String string, String[] delimiters) {
		if (string == null || delimiters == null)
			return null;

		Set<String> wordsSet = new HashSet<String>();
		wordsSet.add(string.toLowerCase());
		for (String delimiter : delimiters) {
			Set<String> tempSet = new HashSet<String>();

			for (String word : wordsSet) {
				String[] newWords = word.split(delimiter);
				for (String newWord : newWords)
					tempSet.add(newWord);
			}

			wordsSet = tempSet;
		}

		return wordsSet;
	}

	// Function to count the number of occurence of term in string body
	public static int numOccurance(String body, String term) {
		if (body == null || term == null || body.length() < 1
				|| term.length() < 1)
			return -1;

		int numOccurance = 0;
		String copyBody = body.toLowerCase();
		String copyTerm = term.toLowerCase();

		while (true) {
			int index = copyBody.indexOf(copyTerm);

			if (index < 0) {
				break;
			}

			numOccurance++;
			copyBody = copyBody.substring(index + copyTerm.length());
		}

		return numOccurance;
	}

	// Return the current date, e.g: 2014-05-23
	@SuppressWarnings("deprecation")
	public static String getCurrentDate() {
		Date currentDate = new Date();
		String year = new String("" + (1900 + currentDate.getYear()));
		String month = new String("" + currentDate.getMonth());
		String date = new String("" + currentDate.getDate());

		String dateCrawled = year + "-" + month + "-" + date;

		return dateCrawled;
	}

	// Return the current time 22:11:30
	@SuppressWarnings("deprecation")
	public static String getCurrentTime() {
		Date currentDate = new Date();
		String hour = new String("" + currentDate.getHours());
		String minute = new String("" + currentDate.getMinutes());
		String second = new String("" + currentDate.getSeconds());

		String timeCrawled = hour + ":" + minute + ":" + second;

		return timeCrawled;
	}

	// Hash a plain string text
	public static String hash(String plainText) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		return (new HexBinaryAdapter()).marshal(messageDigest.digest(plainText
				.getBytes()));
	}

	// Check whether a link is a file or not
	public static boolean linkIsFile(String url) {
		if (url == null)
			return false;

		for (String exts : Globals.fileExtenstions) {
			if (url.indexOf(exts) == (url.length() - exts.length()))
				return true;
		}

		return false;
	}

	// Convert month string to int. e.g: November->"11"
	public static String convertMonthStringToIntString(String monthString) {
		monthString = monthString.toLowerCase();
		switch (monthString) {
		case "january":
			return "01";
		case "february":
			return "02";
		case "march":
			return "03";
		case "april":
			return "04";
		case "may":
			return "05";
		case "june":
			return "06";
		case "july":
			return "07";
		case "august":
			return "08";
		case "september":
			return "09";
		case "october":
			return "10";
		case "november":
			return "11";
		case "december":
			return "12";
		default:
			return null;
		}
	}

	// Format date from November 7, 1991 to 1991-11-07
	public static String formatDate(String watchReportDate) {
		if (watchReportDate == null)
			return null;

		String formattedDate = "";

		String[] dateSubstrs = watchReportDate.split(" ");

		// If the string is not in the corrected form, return null
		if (dateSubstrs.length != 3)
			return null;

		String month = Helper.convertMonthStringToIntString(dateSubstrs[0]);
		if (month == null)
			return null;

		int year = -1;
		int day = -1;

		if (dateSubstrs[1].length() < 1)
			return null;

		try {
			year = Integer.parseInt(dateSubstrs[2]);
			day = Integer.parseInt(dateSubstrs[1].substring(0,
					dateSubstrs[1].length() - 1));
		} catch (Exception e) {
			return null;
		}

		// Day and year out of range
		if (day < 1 || day > 31 || year < 1)
			return null;

		String dayString = "" + day;
		if (dayString.length() < 2)
			dayString = "0" + dayString;

		formattedDate = "" + year + "-" + month + "-" + dayString;
		return formattedDate;
	}

	// Given a name like an article name or an entry name, etc... and a set of
	// topic words. Try to identify the topic of the name
	public static Set<String> identifyTopicOfName(String name, String[] topics) {
		if (name == null || topics == null)
			return null;

		// Try to identify topic with exact match
		Set<String> topicsOfName = new HashSet<String>();
		for (String topic : topics) {
			if (name.indexOf(topic) != -1) {
				topicsOfName.add(topic);
				name = name.replace(topic, "");
			}
		}

		// Split the name into a list of words
		String[] delimiters = { " ", "-" };
		Set<String> articleNameWordsSet = Helper.splitString(name, delimiters);

		// TODO make generic
		// Break topic into words and try to find those word in the name
		for (String topic : topics) {
			if (!topicsOfName.contains(topic)) {
				String[] topicWords = topic.split(" ");
				for (String topicWord : topicWords) {
					topicWord = topicWord.trim().toLowerCase();
					if (topicWord.length() > 3
							&& articleNameWordsSet.contains(topicWord)) {
						topicsOfName.add(topic);
						break;
					}
				}
			}
		}

		// Combine a topic'words into 1 word and try to find that word in the
		// name
		for (String topic : topics) {
			if (!topicsOfName.contains(topic)) {
				String topicWord = topic.replace(" ", "");
				topicWord = topicWord.trim().toLowerCase();
				if (topicWord.length() > 3
						&& articleNameWordsSet.contains(topicWord)) {
					topicsOfName.add(topic);
				}
			}
		}

		if (Globals.DEBUG)
			System.out.println("Topics = " + topicsOfName);

		return topicsOfName;
	}

	// Make the current thread wait for a random amount of time between
	// lowerBound and upperBound number of seconds
	public static void waitSec(int lowerBound, int upperBound) {
		try {
			int waitTime = lowerBound
					* 1000
					+ (int) (Math.random() * ((upperBound * 1000 - lowerBound * 1000) + 1));
			System.out.println("Wait for " + waitTime);
			Thread.currentThread();
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Comparator class to sort TreeMap
	protected class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;

		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return 1;
			} else {
				return -1;
			} // returning 0 would merge keys
		}
	}
}
