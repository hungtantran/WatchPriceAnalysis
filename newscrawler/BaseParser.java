package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
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
import daoconnection.DAOFactory;
import daoconnection.Domain;
import daoconnection.LinkCrawled;
import daoconnection.LinkCrawledDAO;
import daoconnection.LinkCrawledDAOJDBC;
import daoconnection.LinkQueue;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;

public abstract class BaseParser {
	protected final String invalidCharacters[] = { "{", "}", "(", ")", "[",
			"]", ".", ";", ",", "/", "\\" };
	protected final String invalidWords[] = { "paper", "article", "this",
			"that", "with" };

	protected LogManager logManager = null;
	protected Scheduler scheduler = null;

	protected String link = null;
	protected Domain domain = null;
	protected Document doc = null;
	protected Exception exception = null;
	protected String timeCreated = null;
	protected String dateCreated = null;
	protected LinkQueueDAO linkQueueDAO = null;
	protected LinkCrawledDAO linkCrawledDAO = null;

	public abstract boolean isArticlePage();

	public abstract boolean parseDoc();

	// All subclass of baseparser needs to implement these methods
	abstract void checkDocumentUrl(String url);

	abstract String processLink(String url);

	public abstract boolean isValidLink(String url);

	protected BaseParser(String link, Domain domain, LogManager logManager, Scheduler scheduler, DAOFactory daoFactory) throws SQLException {
		this.domain = domain;

		if (link.indexOf(domain.getDomainString()) != 0) {
			this.link = domain.getDomainString();
		} else {
			// Initialize private variable
			this.link = link;
		}

		this.logManager = logManager;
		this.scheduler = scheduler;
		this.linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
		this.linkCrawledDAO = new LinkCrawledDAOJDBC(daoFactory);
	}

	public boolean setContent(String content) {
		if (content == null) {
			return false;
		}

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

	// Add links in the page into the queue, add current link into the crawled set.
	// Remove current link from the queue set.
	protected void processLinksInPage(String url) throws SQLException {
		if (this.doc == null) {
			// If the page fails to download for reasons like 404, remove it
			if (this.exception.getClass() == HttpStatusException.class) {
				HttpStatusException e = (HttpStatusException) this.exception;

				if (e.getStatusCode() == 404) {
					this.logManager.writeLog("Remove link " + url + " because of 404");
					postProcessUrl(url, this.domain.getId(), null, 0, null);
				}
			}

			if (this.exception.getClass() == MalformedURLException.class) {
				this.logManager.writeLog("Remove link " + url + " because of malformed url");
				postProcessUrl(url, this.domain.getId(), null, 0, null);
			}

			if (this.exception.getClass() == IllegalArgumentException.class) {
				this.logManager.writeLog("Remove link " + url + " because of ");
				postProcessUrl(url, this.domain.getId(), null, 0, null);
			}

			return;
		}

		// Parse out all the links from the current page
		Set<String> linksInPage = BaseParser.parseUrls(this.doc.outerHtml(), this.domain.getDomainString());

		// Add more urls to the queue
		Set<String> newStrings = new HashSet<String>();
		if (linksInPage != null) {
			if (Globals.DEBUG) {
				this.logManager.writeLog("Found " + linksInPage.size() + " links in page");
			}

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();

				if (linkInPage.contains(this.domain.getDomainString()) && !Helper.linkIsFile(linkInPage) && this.isValidLink(linkInPage)) {
					newStrings.add(linkInPage);
				}
			}
		}

		// Perform tasks like insert link into crawled set, remove it from queue from sql db
		Integer priority = TopicComparator.getStringPriority(url);
		postProcessUrl(url, this.domain.getId(), priority, 0, newStrings);
	}

	protected void postProcessUrl(String processedlink, int domainId, Integer priority, int persistent, Set<String> newLinks) throws SQLException {
		if (processedlink != null) {
			this.linkQueueDAO.removeLinkQueue(processedlink);
			
			LinkCrawled linkCrawled = new LinkCrawled();
			linkCrawled.setLink(processedlink);
			linkCrawled.setDomainTableId1(domainId);
			linkCrawled.setPriority(priority);
			linkCrawled.setDateCrawled(null);
			linkCrawled.setTimeCrawled(null);
			
			this.linkCrawledDAO.createLinkCrawled(linkCrawled);
		}

		if (newLinks == null) {
			return;
		}

		for (String newLink : newLinks) {
			if (!this.scheduler.urlsCrawledContain(newLink) && !this.scheduler.urlsQueueContain(newLink)) {
				Integer newLinkPriority = TopicComparator.getStringPriority(newLink);
				
				this.scheduler.addToUrlsQueue(newLink);
				
				LinkQueue linkQueue = new LinkQueue();
				linkQueue.setLink(newLink);
				linkQueue.setDomainTableId1(domainId);
				linkQueue.setPriority(newLinkPriority);
				linkQueue.setPersistent(persistent);
				linkQueue.setDateCrawled(null);
				linkQueue.setTimeCrawled(null);
				
				if (this.linkQueueDAO.createLinkQueue(linkQueue) == null) {
					continue;
				}
			}
		}
	}
}
