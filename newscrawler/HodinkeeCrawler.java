package newscrawler;

import java.util.*;

import org.jsoup.nodes.Document;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.Globals.Domain;

public class HodinkeeCrawler extends BaseCrawler {
	public static final String domain = "http://www.hodinkee.com";
	public static final String crawlerId = "hodinkee";

	// Constructor
	public HodinkeeCrawler(String startURL) {
		super(startURL, HodinkeeCrawler.domain, Domain.HODINKEE.value);
	}

	public HodinkeeCrawler(String startURL, int lowerBoundWaitTimeSec,
			int upperBoundWaitTimeSec) {
		super(startURL, HodinkeeCrawler.domain, Domain.HODINKEE.value,
				lowerBoundWaitTimeSec, upperBoundWaitTimeSec, new TopicComparator());
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

		if (url.indexOf(HodinkeeCrawler.domain) != 0)
			return false;

		if (url.indexOf("?tag=") != -1)
			return false;

		if (url.indexOf("?offset=") != -1)
			return false;

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (Helper.linkIsFile(url))
			return false;

		return true;
	}

	protected void checkDocumentUrl(String url) {
		HodinkeeArticleParser parser = new HodinkeeArticleParser(url);

		String htmlContent = null;
		// If the page is an article page, parse it
		if (parser.isArticlePage()) {
			parser.parseDoc();
			htmlContent = parser.getContent();

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
				HodinkeeCrawler.domain);

		// Add more urls to the queue
		Set<String> newStrings = new HashSet<String>();
		if (linksInPage != null) {
			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Found " + linksInPage.size()
						+ " links in page");

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();
				if (linkInPage.length() < 1)
					continue;

				if (!this.urlsCrawled.contains(linkInPage)
						&& linkInPage.contains(HodinkeeCrawler.domain)
						&& !this.urlsQueue.contains(linkInPage)
						&& this.isValidLink(linkInPage)) {
					this.urlsQueue.add(linkInPage);
					newStrings.add(linkInPage);
					if (Globals.DEBUG)
						Globals.crawlerLogManager.writeLog("Add link " + linkInPage);
				}
			}
		}
		
		// Perform tasks like insert link into crawled set, remove it from queue
		// from sql db
		Integer priority = TopicComparator.getStringPriority(url);
		postProcessUrl(url, Domain.HODINKEE.value, priority, 0, newStrings);
	}

	// Execute method for thread
	public void run() {
		// HodinkeeCrawler crawler = new
		// HodinkeeCrawler("http://www.hodinkee.com");
		// crawler.startCrawl(false, 0);

		this.startCrawl(false, 0, this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
	}

	public static void main(String[] args) {
		// HodinkeeCrawler crawler = new
		// HodinkeeCrawler("http://www.hodinkee.com");
		// crawler.startCrawl(false, 0);
	}
}
