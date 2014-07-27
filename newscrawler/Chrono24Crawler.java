package newscrawler;

import java.util.*;

import org.jsoup.nodes.Document;

public class Chrono24Crawler extends BaseCrawler {
	public static final String domain = "http://www.chrono24.com/";
	public static final String crawlerId = "chrono24";

	private final int lowerBoundWaitTimeSec = 10;
	private final int upperBoundWaitTimeSec = 20;

	public Chrono24Crawler(String startURL) {
		super(startURL, Chrono24Crawler.domain, Chrono24Crawler.crawlerId);
	}

	@Override
	public void startCrawl(boolean timeOut, long duration) {
		// If for some reason startUrl is null stop right away
		if (this.startURL == null)
			return;

		// Continuously pop the queue to parse the page content
		while (true) {
			if (this.urlsQueue.isEmpty())
				break;

			// Get the next link from the queue
			String curUrl = this.urlsQueue.remove();

			if (curUrl == null)
				continue;

			curUrl = curUrl.trim();
			curUrl = this.truncateToMainEntryWatchPage(curUrl);

			// If the link is a file, not a web page, skip it and continue to
			// the next link in the queue
			if (BaseCrawler.linkIsFile(curUrl))
				continue;

			System.out.println("Process url " + curUrl);

			// Process the new link
			this.processUrl(curUrl);

			// Wait for 5 to 10 sec before crawling the next page
			waitSec(this.lowerBoundWaitTimeSec, this.upperBoundWaitTimeSec);
		}
	}

	// Truncate a complementary pages of a watch entry page
	// (picture page, spec page, etc...) to the main page
	// Do nothing if the link is not watch entry page
	private String truncateToMainEntryWatchPage(String link) {
		String surfixLink = "?";

		if (link != null && link.indexOf(surfixLink) != -1) {
			link = link.substring(0, link.indexOf(surfixLink));
		}

		return link;
	}

	protected void checkDocumentUrl(String url) {
		Chrono24EntryPageParser parser = new Chrono24EntryPageParser(url);

		String htmlContent = null;

		// If the page is an watch entry page, parse it
		if (parser.isWatchEntryPage()) {
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
			String timeCrawled = BaseCrawler.getCurrentTime();
			String dateCrawled = BaseCrawler.getCurrentDate();

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
				linkInPage = this.truncateToMainEntryWatchPage(linkInPage);
				if (linkInPage.length() < 1)
					continue;

				if (!this.urlsCrawled.contains(linkInPage)
						&& linkInPage.contains(Chrono24Crawler.domain)
						&& !BaseCrawler.linkIsFile(linkInPage)
						&& !this.urlsQueue.contains(linkInPage)) {
					this.urlsQueue.add(linkInPage);
					if (Globals.DEBUG)
						System.out.println("Add link " + linkInPage);
				}
			}
		}

		System.out.println("Already Crawled " + this.urlsCrawled.size());
		System.out.println("Queue has " + this.urlsQueue.size());

		// Try to serialize existing data to disk
		this.serializeDataToDisk(Chrono24Crawler.crawlerId);
	}

	// Execute method for thread
	public void run() {
		// Chrono24Crawler crawler = new Chrono24Crawler(
		// "http://www.chrono24.com/en/rolex/gmt-master-ii-green-index-dial--id2122361.htm");
		// crawler.startCrawl(false, 0);

		this.startCrawl(false, 0);
	}

	public static void main(String[] args) {
		// Chrono24Crawler crawler = new Chrono24Crawler(
		// "http://www.chrono24.com/en/rolex/gmt-master-ii-green-index-dial--id2122361.htm");
		// crawler.startCrawl(false, 0);
	}
}
