package newscrawler;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.select.Elements;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.Globals.Domain;
import commonlib.Globals.Type;

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

public class ABlogToWatchArticleParser extends BaseParser {
	private final String domain = "http://www.ablogtowatch.com/";
	private final int numRetryDownloadPage = 2;

	private String articleName = null;
	private Set<String> keywords = null;
	private Set<Globals.Type> types = null;
	private Set<String> topics = null;

	public ABlogToWatchArticleParser(String articleUrl) {
		this.link = articleUrl;
		this.keywords = new HashSet<String>();
		this.types = new HashSet<Globals.Type>();
		this.types.add(Type.HOROLOGY);
		this.topics = new HashSet<String>();
		this.timeCreated = "00:00:00";
	}

	// Return true if the articleUrl is a valid article page of ABlogToWatch,
	// false if not
	public boolean isArticlePage() {
		return (this.content != null && this.content
				.indexOf("article:published_time") != -1);
	}

	// Get domains of the article
	public Globals.Domain[] getDomains() {
		Globals.Domain[] domains = { Domain.ABLOGTOWATCH };
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

		// If the page is not an article page
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
		Elements artcileNameElems = doc.select("h1");
		if (artcileNameElems.size() == 1) {
			String articleNameText = new String(artcileNameElems.get(0).text());
			articleNameText = articleNameText.trim();
			this.articleName = articleNameText;

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Article Name = " + this.articleName);
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
				.select("meta[property=article:published_time]");
		if (dateCreatedElems.size() == 1) {
			String dateCreatedText = new String(dateCreatedElems.get(0)
					.attr("content").toString());
			dateCreatedText = dateCreatedText.trim();
			dateCreatedText = dateCreatedText.substring(0,
					dateCreatedText.indexOf('T'));
			this.dateCreated = dateCreatedText;

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Date Created = " + this.dateCreated);
		}
	}

	public static void main(String[] args) {
		// ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
		// "http://www.ablogtowatch.com/charlie-sheen-father-debut-in-patek-philippe-watch-ad/");
		ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
				"http://www.ablogtowatch.com/jeanrichard-terrascope-watch-review/");
		// ABlogToWatchArticleParser parser = new ABlogToWatchArticleParser(
		// "http://www.ablogtowatch.com/jaeger-lecoultre-geophysic-watches-hands/");
		parser.parseDoc();
	}
}
