package newscrawler;

import java.util.HashSet;
import java.util.Set;

import newscrawler.Globals.Domain;
import newscrawler.Globals.Type;

import org.jsoup.select.Elements;

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
	private final String domain = "http://www.hodinkee.com";
	private final int numRetryDownloadPage = 2;
	
	private String articleUrl = null;
	private String articleName = null;
	private Set<String> keywords = null;
	private Set<Globals.Type> types = null;
	private Set<String> topics = null;
	private String timeCreated = null;
	private String dateCreated = null;

	public HodinkeeArticleParser(String articleUrl) {
		if (articleUrl.indexOf("http://www.hodinkee.com/blog/") == 0) {
			this.articleUrl = articleUrl;
			this.keywords = new HashSet<String>();
			this.types = new HashSet<Globals.Type>();
			this.types.add(Type.HOROLOGY);
			this.topics = new HashSet<String>();
			this.timeCreated = "00:00:00";
		}
	}

	// Return true if the articleUrl is a valid article page of Hodinkee, false
	// if not
	public boolean isArticlePage() {
		return this.articleUrl != null;
	}

	// Get link to the article
	public String getLink() {
		return this.articleUrl;
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

	public void parseDoc() {
		// Download the html content into a private variable
		this.downloadHtmlContent(this.articleUrl, this.numRetryDownloadPage);

		// If the download content fails, return
		if (this.doc == null)
			return;

		// Parse the name of the article
		this.parseArticleName();

		// Parse the keywords of the article
		this.parseKeywords();

		// Parse the topics of the article
		this.parseTopics();

		// Parse the date created the article
		this.parseDateCreated();
	}

	// Parse the name of the article
	private void parseArticleName() {
		Elements artcileNameElems = doc.select("h2");
		if (artcileNameElems.size() == 1) {
			String articleNameText = new String(artcileNameElems.get(0).text());
			articleNameText = articleNameText.trim();
			this.articleName = articleNameText;

			if (Globals.DEBUG)
				System.out.println("Article Name = " + this.articleName);
		}
	}

	// Parse the keywords of the article
	private void parseKeywords() {
		// TODO find keywords
		this.keywords = null;
	}

	// Parse the topics of the article
	private void parseTopics() {
		Set<String> topicsOfName = BaseParser.identifyTopicOfName(
				this.articleName, Globals.HOROLOGYTOPICS);

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
				System.out.println("Date Created = " + this.dateCreated);
		}
	}

	public static void main(String[] args) {
		HodinkeeArticleParser parser = new HodinkeeArticleParser(
				"http://www.hodinkee.com/blog/a-look-at-jb-champions-unique-observatory-chronometer-wristwatch-the-other-patek-with-a-chance-to-become-the-most-expensive-watch-in-the-world");
		parser.parseDoc();
	}
}
