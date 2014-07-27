package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class BaseParser {
	protected final String invalidCharacters[] = { "{", "}", "(", ")", "[",
			"]", ".", ";", ",", "/", "\\" };
	protected final String invalidWords[] = { "paper", "article", "this",
			"that", "with" };
	
	protected Document doc = null;
	protected String content = null;
	
	// public abstract String getLink();
	// public abstract Globals.Domain[] getDomains();
	// public abstract String getArticleName();
	// public abstract Globals.Type[] getTypes();
	// public abstract String[] getKeywords();
	// public abstract String[] getTopics();
	// public abstract String getContent();
	// public abstract String getTimeCreated();
	// public abstract String getDateCreated();
	
	protected void downloadHtmlContent(String url, int numRetryDownloadPage) {
		this.doc = NetworkingFunctions.downloadHtmlContent(url, numRetryDownloadPage);
		
		if (this.doc != null) {
			this.content = this.doc.outerHtml();
		} else this.content = null;
	}
	
	protected static boolean dedupNoun(String noun1, String noun2) {
		if (Math.abs(noun1.length() - noun2.length()) < 3) {
			String firstForm = noun1;
			String secondForm = noun2;
			if (noun1.length() > noun2.length()) {
				firstForm = noun2;
				secondForm = noun1;
			}

			int compareLength = firstForm.length() - 2;

			if (firstForm.length() < 5)
				compareLength += 1;

			for (int i = 0; i < compareLength; i++) {
				if (firstForm.charAt(i) != secondForm.charAt(i))
					return false;
			}

			return true;
		}

		return false;
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

	// Parse the document urls
	public static Set<String> parseUrls(String htmlDoc, String domain) {
		Set<String> resultUrls = new HashSet<String>();
		Document doc = Jsoup.parse(htmlDoc);
		Elements urlElems = doc.select("a[href]");

		for (int i = 0; i < urlElems.size(); i++) {
			String urlText = new String(urlElems.get(i).attr("href"));
			urlText = urlText.trim();

			if (urlText.length() == 0)
				continue;

			URL urlObject = null;
			try {
				urlObject = new URL(domain);
				urlObject = new URL(urlObject, urlText);
			} catch (MalformedURLException e) {
				continue;
			}

			urlText = urlObject.toString();
			// Ignore url not from the same domain or has position anchor
			if (urlText.indexOf(domain) == -1 || urlText.indexOf("#") != -1)
				continue;

			resultUrls.add(urlText);
		}

		return resultUrls;
	}

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
		Set<String> articleNameWordsSet = BaseParser.splitString(name,
				delimiters);
		
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
		
		// Combine a topic'words into 1 word and try to find that word in the name
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
