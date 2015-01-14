package commonlib;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class Helper {
	public static final char[] invalidFilenameChar = { '<', '>', ':', '"', '/',
		'\\', '|', '?', '*', '\1', '\2', '\3', '\4', '\5', '\6', '\7',
		'\t', '\10', '\11', '\12', '\13', '\14', '\15', '\16', '\17',
		'\20', '\21', '\22', '\23', '\24', '\25', '\26', '\27',
		'\30', '\31' };

	public static String[] splitString(String string, String delimiter) {
		if (string == null || delimiter == null)
			return null;

		ArrayList<String> splitString = new ArrayList<String>();
		int prevPos = 0;
		for (int i = 0; i < string.length() - delimiter.length() + 1; i++) {
			String subStringAtCurPos = string.substring(i,
					i + delimiter.length());
			if (subStringAtCurPos.equals(delimiter)) {
				String token = string.substring(prevPos, i);
				if (token.length() > 0)
					splitString.add(token);
				i += delimiter.length();
				prevPos = i;
			}
		}

		String token = string.substring(prevPos, string.length());
		if (token.length() > 0)
			splitString.add(token);

		String[] wordsArray = new String[splitString.size()];
		for (int i = 0; i < splitString.size(); i++)
			wordsArray[i] = splitString.get(i);

		return wordsArray;
	}

	// Given a string and list of delimiters. Split the string into a set of
	// words with delimiter contained in the list
	public static Set<String> splitString(String string, String[] delimiters) {
		if (string == null || delimiters == null)
			return null;

		Set<String> wordsSet = new HashSet<String>();
		wordsSet.add(string);
		for (String delimiter : delimiters) {
			Set<String> tempSet = new HashSet<String>();

			for (String word : wordsSet) {
				String[] newWords = Helper.splitString(word, delimiter);
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
		
		StringBuilder dateString = new StringBuilder(); 
		dateString.append(1900 + currentDate.getYear());
		dateString.append("-");
		dateString.append(currentDate.getMonth());
		dateString.append("-");
		dateString.append(currentDate.getDate());

		return dateString.toString();
	}

	// Return the current time 22:11:30
	@SuppressWarnings("deprecation")
	public static String getCurrentTime() {
		Date currentDate = new Date();
		
		StringBuilder timeString = new StringBuilder();
		timeString.append(currentDate.getHours());
		timeString.append(":");
		timeString.append(currentDate.getMinutes());
		timeString.append(":");
		timeString.append(currentDate.getSeconds());

		return timeString.toString();
	}
	
	// Return the current time 22:11:30:79
	@SuppressWarnings("deprecation")
	public static String getCurrentTimeWithMilisec() {
		Date currentDate = new Date();
		
		StringBuilder timeString = new StringBuilder();
		timeString.append(currentDate.getHours());
		timeString.append(":");
		timeString.append(currentDate.getMinutes());
		timeString.append(":");
		timeString.append(currentDate.getSeconds());
		timeString.append(":");
		timeString.append(currentDate.getTime() % 100);

		return timeString.toString();
	}

	// Hash a plain string text
	public static String hash(String plainText) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
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

		String formattedDate = "" + year + "-" + month + "-" + dayString;
		return formattedDate;
	}

	// Remove all the accent from a string
	public static String removeAccents(String text) {
		return text == null ? null : Normalizer.normalize(text, Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	// Given a name like an article name or an entry name, etc... and a set of
	// topic words. Try to identify the topic of the name
	public static Set<String> identifyTopicOfName(String watchName,
			String[] topics) {
		if (watchName == null || topics == null)
			return null;
		
		Set<String> topicsOfName = new HashSet<String>();
		String name = new String(Helper.removeAccents(watchName));
		name = name.toLowerCase();

		// Split the name into a list of words
		String[] delimiters = { " ", "-", ",", ";", "/", ".", "\"", "(", ")" };
		Set<String> articleNameWordsSet = Helper.splitString(name, delimiters);
		
		// Break topic into words and try to find
		for (String topic : topics) {
			String lowerCaseTopic = topic.toLowerCase();
			Set<String> topicWords = Helper.splitString(lowerCaseTopic, delimiters);

			// If the name has all the words contained in the topic, the topic
			// is true
			boolean topicIsTrue = true;
			int maxPosition = 0;
			int minPosition = name.length();
			for (String topicWord : topicWords) {
				if (!articleNameWordsSet.contains(topicWord)) {	
					topicIsTrue = false;
					break;
				} else {
					// Word of the same topic can't be too far from each other
					int position = name.indexOf(topicWord);
					maxPosition = Math.max(maxPosition, position);
					minPosition = Math.min(minPosition, position);
					if (maxPosition - minPosition > lowerCaseTopic.length()) {
						topicIsTrue = false;
						break;
					}
				}
			}

			// If the topic is true, remove the topic from the name and add it
			// to the list of possible topic
			if (topicIsTrue) {
				for (String topicWord : topicWords)
					name = name.replace(topicWord, "");

				topicsOfName.add(topic);
			}
		}

		// The new words set has less words than the original one since some
		// potential topic words have been removed
		articleNameWordsSet = Helper.splitString(name, delimiters);
		
		// TODO make generic
		// Break topic into words and try to find those word in the name
		for (String topic : topics) {
			if (!topicsOfName.contains(topic)) {
				String lowerCaseTopic = topic.toLowerCase();
				Set<String> topicWords = Helper.splitString(lowerCaseTopic, delimiters);
				for (String topicWord : topicWords) {
					if (topicWord.length() > 3
							&& articleNameWordsSet.contains(topicWord)
							&& !Globals.HOROLOGYTOPICSSTOPWORDS
									.contains(topicWord)) {
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
			Globals.crawlerLogManager.writeLog("Topics = " + topicsOfName);

		return topicsOfName;
	}

	// Make the current thread wait for a random amount of time between
	// lowerBound and upperBound number of seconds
	public static void waitSec(int lowerBound, int upperBound) {
		try {
			int waitTime = lowerBound
					* 1000
					+ (int) (Math.random() * ((upperBound * 1000 - lowerBound * 1000) + 1));
			Globals.crawlerLogManager.writeLog("Wait for " + waitTime);
			Thread.currentThread();
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}
	}
	
	// Convert topic to topic id
	public static Integer[] convertTopicToTopicId(String[] topics) {
		if (Globals.idTopicMap == null)
			return null;
		
		Integer[] topicsId = new Integer[topics.length];

		// Get the id of topics
		for (int i = 0; i < topics.length; i++) {
			topicsId[i] = Globals.idTopicMap.get(topics[i]);
		}

		return topicsId;
	}
	
	// Convert domain to domain id
	public static Integer[] convertDomainToDomainId(Globals.Domain[] domains) {
		if (Globals.idDomainMap == null)
			return null;
		
		Integer[] domainsId = new Integer[3];

		// Get the id of domains
		for (int i = 0; i < domains.length; i++)
			domainsId[i] = domains[i].value;

		for (int i = domains.length; i < 3; i++)
			domainsId[i] = null;

		return domainsId;
	}
	
	// Convert type to type id
	public static Integer[] convertTypeToTypeId(Globals.TypeValue[] types) {
		if (Globals.idTypeMap == null)
			return null;
		
		Integer[] typesId = new Integer[3];

		// Get the id of types
		for (int i = 0; i < types.length; i++)
			typesId[i] = types[i].value;

		for (int i = types.length; i < 3; i++)
			typesId[i] = null;

		return typesId;
	}
	
	// Clean up a file name
	public static String sanitizeFileDirectoryName(String fileName) {
		if (fileName == null)
			return null;
		
		// Trim white space
		fileName = fileName.trim();
		fileName = Helper.removeAccents(fileName);
		
		// Trim ending periods
		while (fileName.length() > 0 && fileName.charAt(fileName.length()-1) == '.') {
			fileName = fileName.substring(0, fileName.length()-1);
		}
		
		// File name can't be 0-length
		if (fileName.length() == 0)
			return null;

		int numInvalidChar = Helper.invalidFilenameChar.length;
		// Empty char
		char replaceChar = ' ';

		for (int i = 0; i < numInvalidChar; i++) {
			fileName = fileName.replace(Helper.invalidFilenameChar[i],
					replaceChar);
		}

		return fileName;
	}
	
	public static int hashStringToInt(String string) {
		if (string == null)
			return -1;
		
		int hash = 0;
		
		for (int i = 0; i < string.length(); i++) {
			hash += string.charAt(i);
		}
		
		return hash;
	}

	public static void main(String[] args) {
	}
}
