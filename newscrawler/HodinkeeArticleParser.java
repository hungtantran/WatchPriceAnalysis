package newscrawler;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.select.Elements;

import commonlib.Globals;
import commonlib.Globals.Domain;
import commonlib.Globals.Type;
import commonlib.Helper;
import commonlib.LogManager;

import dbconnection.MySqlConnection;

/*import edu.stanford.nlp.process.Tokenizer;
 import edu.stanford.nlp.process.TokenizerFactory;
 import edu.stanford.nlp.process.CoreLabelTokenFactory;
 import edu.stanford.nlp.process.DocumentPreprocessor;
 import edu.stanford.nlp.process.PTBTokenizer;
 import edu.stanford.nlp.ling.CoreLabel;
 import edu.stanford.nlp.ling.HasWord;
 import edu.stanford.nlp.ling.Sentence;
 import edu.stanford.nlp.trees.*;
 import edu.stanford.nlp.parser.lexparser.LexicalizedParser;*/

public class HodinkeeArticleParser extends BaseParser {
	private final int numRetryDownloadPage = 2;

	private String articleName = null;
	private Set<String> keywords = null;
	private Set<Globals.Type> types = null;
	private Set<String> topics = null;

	public HodinkeeArticleParser(String articleUrl, MySqlConnection con,
			LogManager logManager, Scheduler scheduler) {
		super(articleUrl, Globals.Domain.HODINKEE.domain, Domain.HODINKEE, con,
				logManager, scheduler);

		this.keywords = new HashSet<String>();
		this.types = new HashSet<Globals.Type>();
		this.types.add(Type.HOROLOGY);
		this.topics = new HashSet<String>();
		this.timeCreated = "00:00:00";
	}

	// Return true if the articleUrl is a valid article page of Hodinkee, false
	// if not
	public boolean isArticlePage() {
		if (!this.isValidLink(this.link))
			return false;
		
		return (this.link.indexOf(this.domain + "blog/") == 0);
	}

	// Get domains of the article
	public Globals.Domain[] getDomains() {
		Globals.Domain[] domains = { Domain.HODINKEE };
		return domains;
	}

	// Get the name of the article
	public String getArticleName() {
		return this.articleName;
	}

	// Get the types of the article
	public Globals.Type[] getTypes() {
		if (this.types == null)
			return null;

		Globals.Type[] typesArray = new Globals.Type[this.types.size()];
		int count = 0;
		for (Globals.Type type : this.types) {
			typesArray[count] = type;
			count++;
		}

		return typesArray;
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

	// Get the topics of the article
	public String[] getTopics() {
		if (this.topics == null)
			return null;

		String[] topicsArray = new String[this.topics.size()];
		int count = 0;
		for (String topic : this.topics) {
			topicsArray[count] = topic;
			count++;
		}

		return topicsArray;
	}

	public boolean parseDoc() {
		// Download the html content into a private variable
		this.downloadHtmlContent(this.link, this.numRetryDownloadPage);

		// If the download content fails, return
		if (this.doc == null)
			return false;

		if (!this.isArticlePage())
			return false;

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
		Elements artcileNameElems = doc.select("h2");
		if (artcileNameElems.size() == 1) {
			String articleNameText = new String(artcileNameElems.get(0).text());
			articleNameText = articleNameText.trim();
			this.articleName = articleNameText;

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Article Name = "
						+ this.articleName);
		}
	}

	// Parse the keywords of the article
	private void parseKeywords() {
		// TODO find keywords
		this.keywords = null;
	}

	// Parse the topics of the article
	private void parseTopics() {
		Set<String> topicsOfName = Helper.identifyTopicOfName(this.articleName,
				Globals.HOROLOGYTOPICS);

		if (topicsOfName != null) {
			for (String topic : topicsOfName) {
				this.topics.add(topic);
			}
		}
	}

	// Parse the date created the article
	private void parseDateCreated() {
		Elements dateCreatedElems = doc
				.select("meta[property=st:published_at]");
		if (dateCreatedElems.size() == 1) {
			String dateCreatedText = new String(dateCreatedElems.get(0)
					.attr("content").toString());
			dateCreatedText = dateCreatedText.trim();
			this.dateCreated = dateCreatedText;

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Date Created = "
						+ this.dateCreated);
		}
	}

	// Process link (e.g. trim, truncate bad part, etc..)
	protected String processLink(String url) {
		if (url == null)
			return url;

		url = url.trim();

		return url;
	}

	// Check if current url is valid or not
	public boolean isValidLink(String url) {
		if (url == null)
			return false;

		if (url.indexOf(this.domain) != 0)
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
		this.parseDoc();

		// If the page is an article page, parse it
		if (this.isArticlePage() && this.doc != null) {
			String link = this.getLink();
			Globals.Domain[] domains = this.getDomains();
			String articleName = this.getArticleName();
			Globals.Type[] types = this.getTypes();
			String[] keywords = this.getKeywords();
			String[] topics = this.getTopics();
			String content = this.doc.outerHtml();
			String timeCreated = this.getTimeCreated();
			String dateCreated = this.getDateCreated();

			// Calculated the time the article is crawled
			String timeCrawled = Helper.getCurrentTime();
			String dateCrawled = Helper.getCurrentDate();

			this.mysqlConnection.addArticle(link, domains, articleName, types,
					keywords, topics, timeCreated, dateCreated, timeCrawled,
					dateCrawled, content);
		}

		// Remove current link from queue, add it to crawl set
		// Adds all links in page to queue
		this.processLinksInPage(url);
	}

	public static void main(String[] args) {
		// HodinkeeArticleParser parser = new HodinkeeArticleParser(
		// "http://www.hodinkee.com/blog/a-look-at-jb-champions-unique-observatory-chronometer-wristwatch-the-other-patek-with-a-chance-to-become-the-most-expensive-watch-in-the-world");
		// parser.parseDoc();
	}
}
