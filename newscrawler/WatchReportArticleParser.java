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

public class WatchReportArticleParser extends BaseParser {
	private final String domain = "http://www.watchreport.com/";
	private final int numRetryDownloadPage = 2;

	private String articleName = null;
	private Set<String> keywords = null;
	private Set<Globals.Type> types = null;
	private Set<String> topics = null;

	public WatchReportArticleParser(String articleUrl) {
		this.link = articleUrl;
		this.keywords = new HashSet<String>();
		this.types = new HashSet<Globals.Type>();
		this.types.add(Type.HOROLOGY);
		this.topics = new HashSet<String>();
		this.timeCreated = "00:00:00";
	}

	// Return true if the articleUrl is a valid article page of WatchReport,
	// false if not
	public boolean isArticlePage() {
		return (this.link.indexOf(this.domain) == 0
				&& this.content != null
				&& this.content
						.indexOf("property=\"og:type\" content=\"article\"") != -1
				&& this.content.indexOf("<h1") != -1
				&& this.content.indexOf("<p class=\"post-meta\">") != -1 && Helper
					.numOccurance(this.link, "/") == 4);
	}

	// Get domains of the article
	public Globals.Domain[] getDomains() {
		Globals.Domain[] domains = { Domain.WATCHREPORT };
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
		Elements postMetaElems = doc.select("p[class=post-meta]");
		if (postMetaElems.size() == 1) {
			Elements metaElems = postMetaElems.get(0).select("span");

			if (metaElems.size() >= 2) {
				String dateCreatedText = new String(metaElems.get(1).text());
				dateCreatedText = dateCreatedText.trim();
				this.dateCreated = Helper.formatDate(dateCreatedText);

				if (Globals.DEBUG)
					Globals.crawlerLogManager.writeLog("Date Created = " + this.dateCreated);
			}
		}
	}

	public static void main(String[] args) {
		WatchReportArticleParser parser = new WatchReportArticleParser(
				"http://www.watchreport.com/citizen-eco-drive-promaster-aqualand-depth-meter-bn2024-05e-2/");
		parser.parseDoc();
	}
}
