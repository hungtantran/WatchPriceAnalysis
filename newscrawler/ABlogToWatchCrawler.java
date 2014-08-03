package newscrawler;

import java.util.*;

public class ABlogToWatchCrawler extends BaseCrawler {
	public static final String domain = "http://www.ablogtowatch.com/";
	public static final String crawlerId = "ablogtowatch";

	private final int lowerBoundWaitTimeSec = 5;
	private final int upperBoundWaitTimeSec = 10;

	public ABlogToWatchCrawler(String startURL) {
		super(startURL, ABlogToWatchCrawler.domain,
				ABlogToWatchCrawler.crawlerId);
	}

	// Process link (e.g. trim, truncate bad part, etc..)
	protected String processLink(String url) {
		if (url == null)
			return url;

		url = url.trim();

		return url;
	}

	// Check if current url is valid or not
	protected boolean isValidLink(String url) {
		if (url == null)
			return false;
		
		if (url.indexOf(ABlogToWatchCrawler.domain) != 0)
			return false;
		
		if (url.indexOf("?attachment_id") != -1)
			return false;

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (Helper.linkIsFile(url))
			return false;

		return true;
	}

	protected void checkDocumentUrl(String url) {
		ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(url);

		String htmlContent = null;
		// If the page is an article page, parse it
		parser.parseDoc();
		htmlContent = parser.getContent();

		if (parser.isArticlePage()) {
			String link = parser.getLink();
			Globals.Domain[] domains = parser.getDomains();
			String articleName = parser.getArticleName();
			Globals.Type[] types = parser.getTypes();
			String[] keywords = parser.getKeywords();
			String[] topics = parser.getTopics();
			String content = parser.getContent();
			String timeCreated = parser.getTimeCreated();
			String dateCreated = parser.getDateCreated();

			// Calculated the time the article is crawled
			String timeCrawled = Helper.getCurrentTime();
			String dateCrawled = Helper.getCurrentDate();

			this.mysqlConnection.addArticle(link, domains, articleName, types,
					keywords, topics, timeCreated, dateCreated, timeCrawled,
					dateCrawled, content);
		}

		if (htmlContent == null)
			return;

		// Parse out all the links from hodinkee from the current page
		Set<String> linksInPage = BaseParser.parseUrls(htmlContent,
				ABlogToWatchCrawler.domain);

		// Add more urls to the queue
		if (linksInPage != null) {
			if (Globals.DEBUG)
				System.out.println("Found " + linksInPage.size()
						+ " links in page");

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();
				if (linkInPage.length() < 1)
					continue;

				if (!this.urlsCrawled.contains(linkInPage)
						&& linkInPage.contains(ABlogToWatchCrawler.domain)
						&& !Helper.linkIsFile(linkInPage)
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
		this.serializeDataToDisk(ABlogToWatchCrawler.crawlerId);
	}

	// Execute method for thread
	public void run() {
		// ABlogToWatchCrawler crawler = new ABlogToWatchCrawler(
		// "http://www.ablogtowatch.com/");
		// crawler.startCrawl(false, 0);
		this.startCrawl(false, 0, this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
	}

	public static void main(String[] args) {
		// ABlogToWatchCrawler crawler = new ABlogToWatchCrawler(
		// "http://www.ablogtowatch.com/");
		// crawler.startCrawl(false, 0);
	}
}
