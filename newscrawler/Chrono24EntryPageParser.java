package newscrawler;

import java.util.HashSet;
import java.util.Set;

import newscrawler.Globals.Domain;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*import edu.stanford.nlp.process.Tokenizer;
 import edu.stanford.nlp.process.TokenizerFactory;
 import edu.stanford.nlp.process.CoreLabelTokenFactory;
 import edu.stanford.nlp.process.DocumentPreprocessor;
 import edu.stanford.nlp.process.PTBTokenizer;
 import edu.stanford.nlp.ling.CoreLabel;
 import edu.stanford.nlp.ling.HasWord;
 import edu.stanford.nlp.ling.Sentence;
 import edu.stanford.nlp.trees.*;
 import edu.stanford.nlp.parser.lexparser.LexicalizedParser;*/

public class Chrono24EntryPageParser extends BaseParser {
	private final String domain = "http://www.chrono24.com/";
	private final int numRetryDownloadPage = 3;

	private String watchName = null;
	private int[] prices = null;
	private Set<String> keywords = null;
	private Set<String> topics = null;
	private String refNo = null;
	private String movement = null;
	private String caliber = null;
	private String watchCondition = null;
	private int watchYear = -1;
	private String caseMaterial = null;
	private String gender = null;
	private String dialColor = null;
	private String location = null;

	public Chrono24EntryPageParser(String articleUrl) {
		if (articleUrl.indexOf(this.domain) == 0
				&& articleUrl.indexOf("--id") != -1) {
			this.link = articleUrl;
			this.prices = new int[2];
			this.prices[0] = -1;
			this.prices[1] = -1;
			this.keywords = new HashSet<String>();
			this.topics = new HashSet<String>();
			this.timeCreated = "00:00:00";
		}
	}

	// Return true if the articleUrl is a valid watch entry page of chrono24,
	// false if not
	public boolean isWatchEntryPage() {
		return this.link != null;
	}

	// Get domains of the article
	public Globals.Domain[] getDomains() {
		Globals.Domain[] domains = { Domain.CHRONO24 };
		return domains;
	}

	// Get the topics of the article
	public String[] getTopics() {
		if (this.topics == null)
			return null;

		String[] topicsArray = new String[this.topics.size()];
		int count = 0;
		for (String topic : this.topics) {
			topicsArray[count] = topic;
			count++;
		}

		return topicsArray;
	}

	// Get the name of the watch
	public String getWatchName() {
		return this.watchName;
	}

	// Get the types of the article
	public int[] getPrices() {
		if (this.prices == null)
			return null;

		int[] typesArray = null;
		if (this.prices[0] < 0) {
			typesArray = new int[0];
		} else if (this.prices[1] < 0) {
			typesArray = new int[1];
			typesArray[0] = this.prices[0];
		} else {
			typesArray = new int[2];
			typesArray[0] = this.prices[0];
			typesArray[1] = this.prices[1];
		}

		return typesArray;
	}

	// Get the keywords of the article
	public String[] getKeywords() {
		if (this.keywords == null)
			return null;

		String[] keywordsArray = new String[this.keywords.size()];
		int count = 0;
		for (String keyword : this.keywords) {
			keywordsArray[count] = keyword;
			count++;
		}

		return keywordsArray;
	}

	// Get the reference number of the watch
	public String getRefNo() {
		return this.refNo;
	}

	// Get the description of the watch's movement
	public String getMovement() {
		return this.movement;
	}

	// Get the caliber of the watch's movement
	public String getCaliber() {
		return this.caliber;
	}

	// Get the condition of the watch
	public String getWatchCondition() {
		return this.watchCondition;
	}

	// Get the production year of the watch
	public int getWatchYear() {
		return this.watchYear;
	}

	// Get the material of the case of the watch
	public String getCaseMaterial() {
		return this.caseMaterial;
	}

	// Get the gender of the watch
	public String getGender() {
		return this.gender;
	}

	// Get the color of the dial of the watch
	public String getDialColor() {
		return this.dialColor;
	}

	// Get the location of the watch
	public String getLocation() {
		return this.location;
	}

	public void parseDoc() {
		// Download the html content into a private variable
		this.downloadHtmlContent(this.link, this.numRetryDownloadPage);

		// If the download content fails, return
		if (this.doc == null)
			return;

		// Parse the name of the watch
		this.parseWatchName();

		// Parse the price of the watch
		this.parsePrices();

		// Parse the keywords of the watch entry page
		this.parseKeywords();

		// Parse the topics of the watch entry page
		this.parseTopics();

		// Parse the date created the watch entry page
		this.parseDateCreated();

		// Parse the spec of watch (ref no, movement, caliber, color, etc...)
		this.parseSpec();
	}

	// Parse the name of the watch
	private void parseWatchName() {
		Elements artcileNameElems = doc.select("h2");
		if (artcileNameElems.size() == 1) {
			String articleNameText = new String(artcileNameElems.get(0).text());
			articleNameText = articleNameText.trim();
			this.watchName = articleNameText;

			if (Globals.DEBUG)
				System.out.println("Watch Name = " + this.watchName);
		}
	}

	private int extractIntFromString(String string) {
		int result = 0;

		if (string == null)
			return result;

		for (int i = 0; i < string.length(); i++) {
			char curChar = string.charAt(i);

			if (curChar >= '0' && curChar <= '9')
				result = result * 10 + (curChar - '0');
		}

		return result;
	}

	// Parse the price of the watch
	private void parsePrices() {
		Elements priceElems = doc.select("div[class=bold price-cell]");
		if (priceElems.size() == 1) {
			String priceText = new String(priceElems.get(0).text());
			priceText = priceText.trim();
			this.prices[0] = this.extractIntFromString(priceText);

			if (Globals.DEBUG)
				System.out.println("Watch Price = " + this.prices[0]);
		}
	}

	// Parse the keywords of the watch entry page
	private void parseKeywords() {
		// TODO find keywords
		this.keywords = null;
	}

	// Parse the topics of the watch entry page
	private void parseTopics() {
		Set<String> topicsOfName = Helper.identifyTopicOfName(
				this.watchName, Globals.HOROLOGYTOPICS);

		if (topicsOfName != null) {
			for (String topic : topicsOfName) {
				this.topics.add(topic);
			}
		}
	}

	// Parse the date created of the watch entry page
	private void parseDateCreated() {
		// There is none for chrono24 so I sub the current time as a rough
		// estimate
		this.timeCreated = Helper.getCurrentTime();
		this.dateCreated = Helper.getCurrentDate();
	}

	// Parse the spec of the watch
	private void parseSpec() {
		Elements specElems = doc.select("div[class=spec]");

		if (specElems.size() == 1) {
			Element specElem = specElems.get(0);

			specElems = specElem.select("tr");

			for (int i = 0; i < specElems.size(); i++) {
				specElem = specElems.get(i);

				Elements tdElems = specElem.select("td");

				if (tdElems.size() == 2) {
					String tdName = tdElems.get(0).text().trim();
					String tdValue = tdElems.get(1).text().trim();

					// Ref No
					if (tdName.equals("Ref. No."))
						this.refNo = tdValue;

					// Movement
					if (tdName.equals("Movement"))
						this.movement = tdValue;

					// Caliber
					if (tdName.contains("Caliber"))
						this.caliber = tdValue;

					// Condition
					if (tdName.equals("Condition"))
						this.watchCondition = ""
								+ this.extractIntFromString(tdValue);

					// Year
					if (tdName.equals("Year"))
						this.watchYear = this.extractIntFromString(tdValue);

					// Case material
					if (tdName.equals("Case Material"))
						this.caseMaterial = tdValue;

					// Gender
					if (tdName.equals("Gender")) {
						if (tdValue.indexOf("Women") != -1
								|| tdValue.indexOf("Woman") != -1
								|| tdValue.indexOf("Ladies") != -1
								|| tdValue.indexOf("Lady") != -1) {
							this.gender = "Women";
						} else
							this.gender = "Men";
					}

					// Dial color
					if (tdName.equals("Dial"))
						this.dialColor = tdValue;

					// Location
					if (tdName.equals("Location"))
						this.location = tdValue;
				}
			}
		}

		// Last attempt to use name to identify gender of the target of the
		// watch
		if (this.gender == null)
			if (this.watchName.indexOf("Women") != -1
					|| this.watchName.indexOf("Woman") != -1)
				this.gender = "Women";

		if (Globals.DEBUG) {
			System.out.println("Ref No = " + this.refNo);
			System.out.println("Movement = " + this.movement);
			System.out.println("Caliber = " + this.caliber);
			System.out.println("Condition = " + this.watchCondition);
			System.out.println("Year = " + this.watchYear);
			System.out.println("Case Material = " + this.caseMaterial);
			System.out.println("Gender = " + this.gender);
			System.out.println("Dial = " + this.dialColor);
			System.out.println("Location = " + this.location);
		}
	}

	public static void main(String[] args) {
		Chrono24EntryPageParser parser = new Chrono24EntryPageParser(
				"http://www.chrono24.com/en/omega/omega-seamaster-annual-calendar-2-tone-18k-pink-full-package--id2768035.htm");
		parser.parseDoc();
	}
}
