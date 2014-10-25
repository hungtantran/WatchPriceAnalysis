package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import commonlib.Globals;
import commonlib.LogManager;
import commonlib.NetworkingFunctions;
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
	protected String content = null;
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

	protected BaseParser(String link, String domain, Globals.Domain domainVal, MySqlConnection con,
			LogManager logManager, Scheduler scheduler) {
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

	// Get link to the page
	public String getLink() {
		return this.link;
	}

	// Get the content of the article
	public String getContent() {
		return this.content;
	}

	// Get time created
	public String getTimeCreated() {
		return this.timeCreated;
	}

	// Get date created
	public String getDateCreated() {
		return this.dateCreated;
	}

	// Set html content of parser
	public void setContent(String content) {
		this.content = content;
	}

	protected void downloadHtmlContent(String url, int numRetryDownloadPage) {
		this.doc = NetworkingFunctions.downloadHtmlContent(url,
				numRetryDownloadPage);

		if (this.doc != null) {
			this.content = this.doc.outerHtml();
		} else
			this.content = null;
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
	
	protected void postProcessUrl(String processedlink, int domainId,
			Integer priority, int persistent, Set<String> newLinks) {
		if (processedlink != null) {
			if (!this.mysqlConnection.insertIntoLinkCrawledTable(processedlink,
					domainId, priority, null, null))
				return;

			if (!this.mysqlConnection.removeFromLinkQueueTable(processedlink,
					domainId))
				return;
		}

		if (newLinks != null)
			for (String newLink : newLinks) {
				Integer newLinkPriority = TopicComparator
						.getStringPriority(newLink);
				if (!this.mysqlConnection.insertIntoLinkQueueTable(newLink,
						domainId, newLinkPriority, persistent, null, null))
					return;
			}
	}
}
