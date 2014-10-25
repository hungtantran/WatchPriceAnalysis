package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;
import commonlib.NetworkingFunctions;
import commonlib.NetworkingFunctions.NetPkg;
import commonlib.TopicComparator;
import dbconnection.MySqlConnection;

public abstract class BaseParser {
	protected final String invalidCharacters[] = { "{", "}", "(", ")", "[",
			"]", ".", ";", ",", "/", "\\" };
	protected final String invalidWords[] = { "paper", "article", "this",
			"that", "with" };

	protected MySqlConnection mysqlConnection = null;
	protected LogManager logManager = null;
	protected Scheduler scheduler = null;

	protected String link = null;
	protected String domain = null;
	protected Globals.Domain domainVal = null;
	protected Document doc = null;
	protected Exception exception = null;
	protected String timeCreated = null;
	protected String dateCreated = null;

	public abstract boolean isArticlePage();

	public abstract boolean parseDoc();

	// All subclass of baseparser needs to implement these methods
	abstract void checkDocumentUrl(String url);

	abstract String processLink(String url);

	abstract boolean isValidLink(String url);

	// public abstract String getLink();
	// public abstract Globals.Domain[] getDomains();
	// public abstract String getArticleName();
	// public abstract Globals.Type[] getTypes();
	// public abstract String[] getKeywords();
	// public abstract String[] getTopics();
	// public abstract String getContent();
	// public abstract String getTimeCreated();
	// public abstract String getDateCreated();

	protected BaseParser(String link, String domain, Globals.Domain domainVal,
			MySqlConnection con, LogManager logManager, Scheduler scheduler) {
		this.domain = domain;
		this.domainVal = domainVal;

		if (link.indexOf(this.domain) != 0) {
			this.link = this.domain;
		} else {
			// Initialize private variable
			this.link = link;
		}

		this.mysqlConnection = con;
		this.logManager = logManager;
		this.scheduler = scheduler;
	}

	public boolean setContent(String content) {
		if (content == null)
			return false;

		this.doc = Jsoup.parse(content);

		return true;
	}

	// Get link to the page
	public String getLink() {
		return this.link;
	}

	// Get time created
	public String getTimeCreated() {
		return this.timeCreated;
	}

	// Get date created
	public String getDateCreated() {
		return this.dateCreated;
	}

	protected void downloadHtmlContent(String url, int numRetryDownloadPage) {
		NetPkg pkg = NetworkingFunctions.downloadHtmlContent(url,
				numRetryDownloadPage);

		this.doc = pkg.doc;
		this.exception = pkg.e;
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

	// Add links in the page into the queue, add current link into the crawled
	// set
	// Remove current link from the queue set
	protected void processLinksInPage(String url) {
		if (this.doc == null) {
			// If the page fails to download for reasons like 404, remove it
			if (this.exception.getClass() == HttpStatusException.class) {
				HttpStatusException e = (HttpStatusException) this.exception;

				if (e.getStatusCode() == 404) {
					this.logManager.writeLog("Remove link " + url
							+ " because of 404");
					postProcessUrl(url, this.domainVal.value, null, 0, null);
				}
			}

			return;
		}

		// Parse out all the links from the current page
		Set<String> linksInPage = BaseParser.parseUrls(this.doc.outerHtml(),
				this.domain);

		// Add more urls to the queue
		Set<String> newStrings = new HashSet<String>();
		if (linksInPage != null) {
			if (Globals.DEBUG)
				this.logManager.writeLog("Found " + linksInPage.size()
						+ " links in page");

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();
				if (linkInPage.length() < 1)
					continue;

				if (linkInPage.contains(this.domain)
						&& !Helper.linkIsFile(linkInPage)) {
					this.scheduler.addToUrlsQueue(linkInPage);
					newStrings.add(linkInPage);
				}
			}
		}

		// Perform tasks like insert link into crawled set, remove it from queue
		// from sql db
		Integer priority = TopicComparator.getStringPriority(url);
		postProcessUrl(url, this.domainVal.value, priority, 0, newStrings);
	}

	protected void postProcessUrl(String processedlink, int domainId,
			Integer priority, int persistent, Set<String> newLinks) {
		if (processedlink != null) {
			this.mysqlConnection.removeFromLinkQueueTable(processedlink,
					domainId);

			this.mysqlConnection.insertIntoLinkCrawledTable(processedlink,
					domainId, priority, null, null);
		}
		
		if (newLinks == null)
			return;

		for (String newLink : newLinks) {
			if (this.scheduler.urlsCrawledContain(newLink)
					&& !this.scheduler.urlsQueueContain(newLink)) {
				Integer newLinkPriority = TopicComparator
						.getStringPriority(newLink);
				if (!this.mysqlConnection.insertIntoLinkQueueTable(newLink,
						domainId, newLinkPriority, persistent, null, null))
					continue;
			}
		}
	}
}
