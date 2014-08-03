package newscrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class BaseParser {
	protected final String invalidCharacters[] = { "{", "}", "(", ")", "[",
			"]", ".", ";", ",", "/", "\\" };
	protected final String invalidWords[] = { "paper", "article", "this",
			"that", "with" };
	
	protected String link = null;
	protected Document doc = null;
	protected String content = null;
	protected String timeCreated = null;
	protected String dateCreated = null;
	
	// public abstract String getLink();
	// public abstract Globals.Domain[] getDomains();
	// public abstract String getArticleName();
	// public abstract Globals.Type[] getTypes();
	// public abstract String[] getKeywords();
	// public abstract String[] getTopics();
	// public abstract String getContent();
	// public abstract String getTimeCreated();
	// public abstract String getDateCreated();
	
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
}
