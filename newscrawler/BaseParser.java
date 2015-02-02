package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import commonlib.LogManager;
import commonlib.NetworkingFunctions;
import commonlib.NetworkingFunctions.NetPkg;
import daoconnection.Domain;
import daoconnection.Topic;
import daoconnection.Type;

public abstract class BaseParser implements IParser {
	protected final String invalidCharacters[] = { "{", "}", "(", ")", "[",
			"]", ".", ";", ",", "/", "\\" };
	protected final String invalidWords[] = { "paper", "article", "this",
			"that", "with" };
	
	protected BaseCrawler crawler = null;
	protected LogManager logManager = null;
	protected Scheduler scheduler = null;
	protected Map<String, Topic> topicStringToTopicMap = null;
	
	protected String[] topicList = null;
	protected Set<String> typeWordList = null;
	
	protected Set<String> keywords = null;
	protected Set<String> topics = null;
	
	protected String link = null;
	protected Domain domain = null;
	protected Type type = null;
	protected Document doc = null;
	protected Exception exception = null;
	protected String timeCreated = null;
	protected String dateCreated = null;

	// All subclass of baseparser needs to implement these methods
	public abstract boolean parseDoc();
	
	public abstract String sanitizeLink(String url);

	public abstract boolean isValidLink(String link);
	
	public abstract boolean isContentLink();
	
	public abstract boolean addCurrentContentToDatabase() throws Exception;

	protected BaseParser(
		String link,
		BaseCrawler crawler,
		LogManager logManager,
		Scheduler scheduler,
		String[] topicList,
		Set<String> typeWordList,
		Map<String, Domain> domainStringToDomainMap,
		Map<String, Type> typeStringToTypeMap,
		Map<String, Topic> topicStringToTopicMap,
		String domainString,
		String typeString) throws Exception
	{
		if (link == null || scheduler == null) {
			throw new Exception("Invalid arguments");
		}
		
		this.link = link;
		this.crawler = crawler;
		this.logManager = logManager;
		this.scheduler = scheduler;
		this.topicList = topicList;
		this.typeWordList = typeWordList;
		this.timeCreated = "00:00:00";
		
		if (domainStringToDomainMap == null || !domainStringToDomainMap.containsKey(domainString)) {
			throw new Exception("Fail to resolve domain of parser");
		}
		
		this.domain = domainStringToDomainMap.get(domainString);
		
		if (typeStringToTypeMap == null || !typeStringToTypeMap.containsKey(typeString)) {
			throw new Exception("Fail to resolve type of parser");
		}
		
		this.topicStringToTopicMap = topicStringToTopicMap;
		
		this.type = typeStringToTypeMap.get(typeString);
		
		this.keywords = new HashSet<String>();
		this.topics = new HashSet<String>();
	}

	public boolean setContent(String content) {
		if (content == null) {
			return false;
		}

		this.doc = Jsoup.parse(content);

		return true;
	}

	// Get link to the page
	@Override
	public String getLink() {
		return this.link;
	}
	
	// Get the content of the page
	@Override
	public String getContent() {
		if (this.doc == null) {
			return null;
		}
		
		return this.doc.outerHtml();
	}
	
	// Get the domain of the page
	@Override
	public Domain getDomain() {
		return this.domain;
	}
	
	@Override
	public String[] getLinksInContent() {
		Set<String> linksInPage = BaseParser.parseUrls(this.doc.outerHtml(), this.domain.getDomainString());
		
		String[] links = new String[linksInPage.size()];
		
		int index = 0;
		for (String link : linksInPage) {
			links[index] = link;
			++index;
		}
		
		return links;
	}

	// Get time created
	public String getTimeCreated() {
		return this.timeCreated;
	}
	
	// Get the topics of the article
	public int[] getTopics() {
		if (this.topics == null) {
			return null;
		}

		int[] topicsArray = new int[this.topics.size()];
		int count = 0;
		for (String topic : this.topics) {
			if (this.topicStringToTopicMap.containsKey(topic)) {
				topicsArray[count] = this.topicStringToTopicMap.get(topic).getId();
				++count;
			}
		}

		return topicsArray;
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
}
