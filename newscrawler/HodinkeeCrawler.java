package newscrawler;

import java.util.*;

import org.jsoup.nodes.Document;

public class HodinkeeCrawler extends BaseCrawler {
	public static final String domain = "http://www.hodinkee.com";
	public static final String crawlerId = "hodinkee";

	private final int lowerBoundWaitTimeSec = 15;
	private final int upperBoundWaitTimeSec = 20;

	public HodinkeeCrawler(String startURL) {
		super(startURL, HodinkeeCrawler.domain, HodinkeeCrawler.crawlerId);
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

			if (!this.isValidLink(curUrl))
				continue;

			System.out.println("Process url " + curUrl);

			// Process the new link
			this.processUrl(curUrl);

			// Wait for 5 to 10 sec before crawling the next page
			waitSec(this.lowerBoundWaitTimeSec, this.upperBoundWaitTimeSec);
		}
	}

	private boolean isValidLink(String url) {
		if (url == null)
			return false;

		if (url.indexOf("?tag=") != -1)
			return false;

		if (url.indexOf("?offset=") != -1)
			return false;

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (BaseCrawler.linkIsFile(url))
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
			String timeCrawled = BaseCrawler.getCurrentTime();
			String dateCrawled = BaseCrawler.getCurrentDate();

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
		if (linksInPage != null) {
			if (Globals.DEBUG)
				System.out.println("Found " + linksInPage.size()
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
					if (Globals.DEBUG)
						System.out.println("Add link " + linkInPage);
				}
			}
		}

		System.out.println("Already Crawled " + this.urlsCrawled.size());
		System.out.println("Queue has " + this.urlsQueue.size());

		// Try to serialize existing data to disk
		this.serializeDataToDisk(HodinkeeCrawler.crawlerId);
	}

	// Execute method for thread
	public void run() {
		// HodinkeeCrawler crawler = new
		// HodinkeeCrawler("http://www.hodinkee.com");
		// crawler.startCrawl(false, 0);

		this.startCrawl(false, 0);
	}

	public static void main(String[] args) {
		// HodinkeeCrawler crawler = new
		// HodinkeeCrawler("http://www.hodinkee.com");
		// crawler.startCrawl(false, 0);
	}
}
