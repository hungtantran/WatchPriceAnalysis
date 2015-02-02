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

public class WatchReportArticleParser extends BaseParser {
	private static final String domainString = "WATCHREPORT";
	private static final String typeString = "HOROLOGY";
	private final int numRetryDownloadPage = 2;

	private String articleName = null;

	public WatchReportArticleParser(
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
			WatchReportArticleParser.domainString,
			WatchReportArticleParser.typeString);
	}

	// Return true if the articleUrl is a valid article page of WatchReport,
	// false if not
	public boolean isContentLink() {
		if (!this.isValidLink(this.link)) {
			return false;
		}
		
		if (this.doc == null) {
			return false;
		}

		String content = this.doc.outerHtml();

		return (this.link.indexOf(this.domain.getDomainString()) == 0
			&& content != null
			&& content.indexOf("property=\"og:type\" content=\"article\"") != -1
			&& content.indexOf("<h1") != -1
			&& content.indexOf("<p class=\"post-meta\">") != -1 && Helper
				.numOccurance(this.link, "/") == 4);
	}

	// Get the name of the article
	public String getArticleName() {
		return this.articleName;
	}

	// Get the keywords of the article
	public String[] getKeywords() {
		if (this.keywords == null)
			return null;

		String[] keywordsArray = new String[this.keywords.size()];
		int count = 0;
		for (String keyword : this.keywords) {
			keywordsArray[count] = keyword;
			count++;
		}

		return keywordsArray;
	}

	public boolean parseDoc() {
		// Download the html content into a private variable
		this.downloadHtmlContent(this.link, this.numRetryDownloadPage);

		// If the download content fails, return
		if (this.doc == null) {
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
		Elements artcileNameElems = doc.select("h1");
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
		Elements postMetaElems = doc.select("p[class=post-meta]");
		if (postMetaElems.size() == 1) {
			Elements metaElems = postMetaElems.get(0).select("span");

			if (metaElems.size() >= 2) {
				String dateCreatedText = new String(metaElems.get(1).text());
				dateCreatedText = dateCreatedText.trim();
				this.dateCreated = Helper.formatDate(dateCreatedText);

				if (Globals.DEBUG) {
					Globals.crawlerLogManager.writeLog("Date Created = " + this.dateCreated);
				}
			}
		}
	}

	// Process link (e.g. trim, truncate bad part, etc..)
	public String sanitizeLink(String url) {
		if (url == null) {
			return url;
		}

		url = url.trim();

		return url;
	}

	// Check if current url is valid or not
	public boolean isValidLink(String url) {
		if (url == null) {
			return false;
		}

		if (url.indexOf(this.domain.getDomainString()) != 0) {
			return false;
		}

		if (url.indexOf("?") != -1) {
			return false;
		}

		// If the link is a file, not a web page, skip it and continue to
		// the next link in the queue
		if (Helper.linkIsFile(url)) {
			return false;
		}

		return true;
	}

	public boolean addCurrentContentToDatabase() throws Exception {
		String link = this.getLink();
		String articleName = this.getArticleName();
		String[] keywords = this.getKeywords();
		int[] topics = this.getTopics();
		String content = this.doc.outerHtml();
		String timeCreated = this.getTimeCreated();
		String dateCreated = this.getDateCreated();

		// Calculated the time the article is crawled
		String timeCrawled = Helper.getCurrentTime();
		String dateCrawled = Helper.getCurrentDate();

		return this.crawler.addArticle(link, this.domain, articleName, this.type,
			keywords, topics, timeCreated, dateCreated, timeCrawled,
			dateCrawled, content);
	}

	public static void main(String[] args) {
		// WatchReportArticleParser parser = new WatchReportArticleParser(
		// "http://www.watchreport.com/citizen-eco-drive-promaster-aqualand-depth-meter-bn2024-05e-2/");
		// parser.parseDoc();
	}
}
