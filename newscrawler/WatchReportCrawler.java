package newscrawler;

import java.util.*;

import newscrawler.Globals.Domain;

public class WatchReportCrawler extends BaseCrawler {
	public static final String domain = "http://www.watchreport.com/";
	public static final String crawlerId = "watchreport";

	// Constructors
	public WatchReportCrawler(String startURL) {
		super(startURL, WatchReportCrawler.domain, Domain.WATCHREPORT.value);
	}

	public WatchReportCrawler(String startURL, int lowerBoundWaitTimeSec,
			int upperBoundWaitTimeSec) {
		super(startURL, WatchReportCrawler.domain,
				Domain.WATCHREPORT.value, lowerBoundWaitTimeSec,
				upperBoundWaitTimeSec, new TopicComparator());
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

		if (url.indexOf(WatchReportCrawler.domain) != 0)
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
		WatchReportArticleParser parser = new WatchReportArticleParser(url);

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
				WatchReportCrawler.domain);

		// Add more urls to the queue
		Set<String> newStrings = new HashSet<String>();
		if (linksInPage != null) {
			if (Globals.DEBUG)
				System.out.println("Found " + linksInPage.size()
						+ " links in page");

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();
				if (linkInPage.length() < 1)
					continue;

				if (!this.urlsCrawled.contains(linkInPage)
						&& linkInPage.contains(WatchReportCrawler.domain)
						&& !Helper.linkIsFile(linkInPage)
						&& !this.urlsQueue.contains(linkInPage)) {
					this.urlsQueue.add(linkInPage);
					newStrings.add(linkInPage);
					if (Globals.DEBUG)
						System.out.println("Add link " + linkInPage);
				}
			}
		}

		// Perform tasks like insert link into crawled set, remove it from queue
		// from sql db
		Integer priority = TopicComparator.getStringPriority(url);
		postProcessUrl(url, Domain.WATCHREPORT.value, priority, 0, newStrings);
	}

	// Execute method for thread
	public void run() {
		this.startCrawl(false, 0, this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
	}

	public static void main(String[] args) {
		// WatchReportCrawler crawler = new
		// WatchReportCrawler("http://www.watchreport.com/");
		// crawler.startCrawl(false, 0, 5, 10);
	}
}
