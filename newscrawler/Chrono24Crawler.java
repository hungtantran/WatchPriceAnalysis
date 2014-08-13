package newscrawler;

import java.util.*;

import org.jsoup.nodes.Document;

public class Chrono24Crawler extends BaseCrawler {
	public static final String domain = "http://www.chrono24.com/";
	public static final String crawlerId = "chrono24";

	// Constructors
	public Chrono24Crawler(String startURL) {
		super(startURL, Chrono24Crawler.domain, Chrono24Crawler.crawlerId);
	}

	public Chrono24Crawler(String startURL, int lowerBoundWaitTimeSec,
			int upperBoundWaitTimeSec) {
		super(startURL, Chrono24Crawler.domain, Chrono24Crawler.crawlerId,
				lowerBoundWaitTimeSec, upperBoundWaitTimeSec, new TopicComparator());
	}

	// Process link (e.g. trim, truncate bad part, etc..)
	protected String processLink(String url) {
		if (url == null)
			return url;

		url = url.trim();

		// Trim everything after "?"
		// Truncate a complementary pages of a watch entry page
		// (picture page, spec page, etc...) to the main page
		// Do nothing if the link is not watch entry page
		String surfixLink = "?";
		if (url != null && url.indexOf(surfixLink) != -1) {
			url = url.substring(0, url.indexOf(surfixLink));
		}

		return url;
	}

	// Check if current url is valid or not
	protected boolean isValidLink(String url) {
		if (url == null)
			return false;

		if (url.indexOf(Chrono24Crawler.domain) != 0)
			return false;

		if (url.indexOf("?") != -1)
			return false;

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (Helper.linkIsFile(url))
			return false;

		return true;
	}

	protected void checkDocumentUrl(String url) {
		Chrono24EntryPageParser parser = new Chrono24EntryPageParser(url);

		String htmlContent = null;

		// If the page is an watch entry page, parse it
		if (parser.isArticlePage()) {
			parser.parseDoc();
			htmlContent = parser.getContent();

			String link = parser.getLink();
			Globals.Domain[] domains = parser.getDomains();
			String[] topics = parser.getTopics();
			String watchName = parser.getWatchName();
			int[] prices = parser.getPrices();
			String[] keywords = parser.getKeywords();
			String content = parser.getContent();
			String timeCreated = parser.getTimeCreated();
			String dateCreated = parser.getDateCreated();

			// Calculated the time the article is crawled
			String timeCrawled = Helper.getCurrentTime();
			String dateCrawled = Helper.getCurrentDate();

			// Get spec of the watch
			String refNo = parser.getRefNo();
			String movement = parser.getMovement();
			String caliber = parser.getCaliber();
			String watchCondition = parser.getWatchCondition();
			int watchYear = parser.getWatchYear();
			String caseMaterial = parser.getCaseMaterial();
			String dialColor = parser.getDialColor();
			String gender = parser.getGender();
			String location = parser.getLocation();

			this.mysqlConnection.addWatchEntry(link, domains, watchName,
					prices, keywords, topics, timeCreated, dateCreated,
					timeCrawled, dateCrawled, content, refNo, movement,
					caliber, watchCondition, watchYear, caseMaterial,
					dialColor, gender, location);
		} else {
			// If the page is not an watch entry page, just get all the links
			// and add it to the queue
			Document htmlDoc = NetworkingFunctions.downloadHtmlContent(url,
					this.numRetriesDownloadLink);

			if (htmlDoc != null)
				htmlContent = htmlDoc.outerHtml();
		}

		if (htmlContent == null)
			return;

		// Parse out all the links from hodinkee from the current page
		Set<String> linksInPage = BaseParser.parseUrls(htmlContent,
				Chrono24Crawler.domain);

		// Add more urls to the queue
		if (linksInPage != null) {
			if (Globals.DEBUG)
				System.out.println("Found " + linksInPage.size()
						+ " links in page");

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();
				linkInPage = this.processLink(linkInPage);
				if (linkInPage.length() < 1)
					continue;

				if (!this.urlsCrawled.contains(linkInPage)
						&& linkInPage.contains(Chrono24Crawler.domain)
						&& !Helper.linkIsFile(linkInPage)
						&& !this.urlsQueue.contains(linkInPage)) {
					this.urlsQueue.add(linkInPage);
					if (Globals.DEBUG)
						System.out.println("Add link " + linkInPage);
				}
			}
		}

		// Perform tasks like serialization
		postProcessUrl(Chrono24Crawler.crawlerId);
	}

	// Execute method for thread
	public void run() {
		// Chrono24Crawler crawler = new Chrono24Crawler(
		// "http://www.chrono24.com/en/rolex/gmt-master-ii-green-index-dial--id2122361.htm");
		// crawler.startCrawl(false, 0);

		this.startCrawl(false, 0, this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
	}

	public static void main(String[] args) {
		// Chrono24Crawler crawler = new Chrono24Crawler(
		// "http://www.chrono24.com/en/rolex/gmt-master-ii-green-index-dial--id2122361.htm");
		// crawler.startCrawl(false, 0);
	}
}
