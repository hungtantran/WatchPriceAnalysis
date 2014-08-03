package analysis;

import java.sql.ResultSet;

import newscrawler.ABlogToWatchArticleParser;
import newscrawler.ABlogToWatchCrawler;
import newscrawler.BaseParser;
import newscrawler.Globals;
import newscrawler.Helper;
import newscrawler.HodinkeeArticleParser;
import newscrawler.HodinkeeCrawler;
import newscrawler.WatchReportArticleParser;
import newscrawler.WatchReportCrawler;
import dbconnection.MySqlConnection;

public class SanitizeDB {
	private MySqlConnection mysqlConnection = null;

	public SanitizeDB() {
	}

	public boolean isValidLink(int articleId, String link) {
		if (link == null)
			return false;
		
		String content = this.mysqlConnection.getArticleContent(articleId);

		// If there is no content, can't decide whether link is valid or not
		if (content == null)
			return true;

		BaseParser parser = null;

		// ABlogToWatch link check
		if (link.indexOf(ABlogToWatchCrawler.domain) == 0) {
			parser = new ABlogToWatchArticleParser(link);
		}

		// ABlogToWatch link check
		if (link.indexOf(HodinkeeCrawler.domain) == 0) {
			parser = new HodinkeeArticleParser(link);
		}

		// ABlogToWatch link check
		if (link.indexOf(WatchReportCrawler.domain) == 0) {
			parser = new WatchReportArticleParser(link);
		}

		// Check if link is valid
		if (parser == null) {
			if (Globals.DEBUG)
				System.out
						.println("Link is invalid because it is not of existing domains");
			return false;
		}

		parser.setContent(content);
		if (!parser.isArticlePage()) {
			if (Globals.DEBUG)
				System.out
						.println("Link is invalid because it is not article page");
			return false;
		}

		return true;
	}

	// Remove from database articles with bad link
	public void sanitizeBadLinkArticles() {
		int lowerBound = 0;
		int maxNumResult = 500;
		int articleCount = lowerBound;

		// Get 2000 articles at a time, until exhaust all the articles
		while (true) {
			this.mysqlConnection = new MySqlConnection();
			ResultSet resultSet = this.mysqlConnection.getArticleInfo(
					lowerBound, maxNumResult);
			if (resultSet == null)
				break;

			try {
				int count = 0;
				// Iterate through the result set to populate the information
				while (resultSet.next()) {
					count++;
					articleCount++;

					int articleId = resultSet.getInt(1);
					String articleLink = resultSet.getString(2).trim();
					if (Globals.DEBUG)
						System.out.println("(" + articleCount
								+ ") Check Article id " + articleId + ": "
								+ articleLink);

					// Check if link is valid or not. If not, delete the article
					boolean isValidLink = this.isValidLink(articleId,
							articleLink);
					if (!isValidLink) {
						if (Globals.DEBUG)
							System.out
									.println("Delete Article id " + articleId);
						this.mysqlConnection.removeArticle(articleId);
					}

				}
				
				if (count == 0)
					break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

			lowerBound += maxNumResult;
			// Suggest the garbage collector to run to avoid out of heap space
			System.gc();
			Helper.waitSec(2, 5);
		}
	}

	public static void main(String[] args) {
		SanitizeDB sanitizer = new SanitizeDB();
		sanitizer.sanitizeBadLinkArticles();
	}
}
