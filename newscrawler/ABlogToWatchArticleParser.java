package newscrawler;

import java.util.Map;
import java.util.Set;

import org.jsoup.select.Elements;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;

import daoconnection.Domain;
import daoconnection.Topic;
import daoconnection.Type;

public class ABlogToWatchArticleParser extends BaseParser {
	private static final String domainString = "ABLOGTOWATCH";
	private static final String typeString = "HOROLOGY";
	private final int numRetryDownloadPage = 2;

	private String articleName = null;

	public ABlogToWatchArticleParser(
		String articleUrl,
		BaseCrawler crawler,
		LogManager logManager,
		Scheduler scheduler,
		String[] topicList,
		Map<String, Topic> topicStringToTopicMap,
		Set<String> typeWordList,
		Map<String, Domain> domainStringToDomainMap,
		Map<String, Type> typeStringToTypeMap) throws Exception
	{
		super(
			articleUrl,
			crawler,
			logManager,
			scheduler,
			topicList,
			typeWordList,
			domainStringToDomainMap,
			typeStringToTypeMap,
			topicStringToTopicMap,
			ABlogToWatchArticleParser.domainString,
			ABlogToWatchArticleParser.typeString);
	}

	// Return true if the articleUrl is a valid article page of ABlogToWatch,
	// false if not
	@Override
	public boolean isContentLink() {
		if (!this.isValidLink(this.link)) {
			return false;
		}

		if (this.doc == null) {
			return false;
		}

		String content = this.doc.outerHtml();

		return (content != null && content.indexOf("article:published_time") != -1);
	}

	// Get the name of the article
	public String getArticleName() {
		return this.articleName;
	}

	// Get the keywords of the article
	public String[] getKeywords() {
		if (this.keywords == null) {
			return null;
		}

		String[] keywordsArray = new String[this.keywords.size()];
		int count = 0;
		for (String keyword : this.keywords) {
			keywordsArray[count] = keyword;
			count++;
		}

		return keywordsArray;
	}

	@Override
	public boolean parseDoc() {
		// Download the html content into a private variable
		this.downloadHtmlContent(this.link, this.numRetryDownloadPage);

		// If the download content fails, return
		if (this.doc == null) {
			this.logManager.writeLog("Fails to parse doc because download HTML content fails for link " + this.link);
			return false;
		}

		// If the page is not an article page
		if (!this.isContentLink()) {
			this.logManager.writeLog("Fails to parse doc because " + this.link + " is not a content link");
			return false;
		}

		// Parse the name of the article
		this.parseArticleName();

		// Parse the keywords of the article
		this.parseKeywords();

		// Parse the topics of the article
		this.parseTopics();

		// Parse the date created the article
		this.parseDateCreated();

		return true;
	}

	// Parse the name of the article
	private void parseArticleName() {
		Elements artcileNameElems = this.doc.select("h1");
		if (artcileNameElems.size() == 1) {
			String articleNameText = new String(artcileNameElems.get(0).text());
			articleNameText = articleNameText.trim();
			this.articleName = articleNameText;

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog("Article Name = " + this.articleName);
			}
		}
	}

	// Parse the keywords of the article
	private void parseKeywords() {
		// TODO find keywords
		this.keywords = null;
	}

	// Parse the topics of the article
	private void parseTopics() {
		Set<String> topicsOfName = Helper.identifyTopicOfName(this.articleName, this.topicList, this.typeWordList);

		if (topicsOfName != null) {
			for (String topic : topicsOfName) {
				this.topics.add(topic);
			}
		}
	}

	// Parse the date created the article
	private void parseDateCreated() {
		Elements dateCreatedElems = this.doc
				.select("meta[property=article:published_time]");
		if (dateCreatedElems.size() == 1) {
			String dateCreatedText = new String(dateCreatedElems.get(0)
					.attr("content").toString());
			dateCreatedText = dateCreatedText.trim();
			dateCreatedText = dateCreatedText.substring(0,
					dateCreatedText.indexOf('T'));
			this.dateCreated = dateCreatedText;

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog("Date Created = " + this.dateCreated);
			}
		}
	}

	// Process link (e.g. trim, truncate bad part, etc..)
	@Override
	public String sanitizeLink(String url) {
		if (url == null) {
			return url;
		}

		url = url.trim();

		return url;
	}

	// Check if current url is valid or not
	@Override
	public boolean isValidLink(String url) {
		if (url == null) {
			return false;
		}

		if (url.indexOf(this.domain.getDomainString()) != 0) {
			return false;
		}

		if (url.indexOf("?attachment_id") != -1) {
			return false;
		}

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (Helper.linkIsFile(url)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean addCurrentContentToDatabase() {
		String link = this.getLink();
		String articleName = this.getArticleName();
		String[] keywords = this.getKeywords();
		int[] topics = this.getTopics();
		String content = this.getContent();
		String timeCreated = this.getTimeCreated();
		String dateCreated = this.getDateCreated();

		// Calculated the time the article is crawled
		String timeCrawled = Helper.getCurrentTime();
		String dateCrawled = Helper.getCurrentDate();

		boolean addArticleResult = false;

		try {
			addArticleResult = this.crawler.addArticle(link, this.domain, articleName, this.type,
				keywords, topics, timeCreated, dateCreated, timeCrawled,
				dateCrawled, content);
		} catch (Exception e) {
			this.logManager.writeLog("Can't add current content to database");
		}

		return addArticleResult;
	}

	public static void main(String[] args) {
		// ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
		// "http://www.ablogtowatch.com/charlie-sheen-father-debut-in-patek-philippe-watch-ad/");
		// ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
		// "http://www.ablogtowatch.com/jeanrichard-terrascope-watch-review/");
		// ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
		// "http://www.ablogtowatch.com/jaeger-lecoultre-geophysic-watches-hands/");
		// parser.parseDoc();
	}
}
