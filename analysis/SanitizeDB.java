package analysis;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import newscrawler.ABlogToWatchArticleParser;
import newscrawler.BaseParser;
import newscrawler.Chrono24EntryPageParser;
import newscrawler.HodinkeeArticleParser;
import newscrawler.MainCrawler;
import newscrawler.WatchReportArticleParser;
import commonlib.Globals;
import commonlib.HTMLCompressor;
import commonlib.Helper;
import dbconnection.MySqlConnection;

public class SanitizeDB {
	private MySqlConnection mysqlConnection = null;

	public SanitizeDB() {
	}

	public boolean isArticleLink(int articleId, String link) {
		if (link == null)
			return false;

		String content = this.mysqlConnection.getArticleContent(articleId);

		// If there is no content, can't decide whether link is valid or not
		if (content == null)
			return true;

		BaseParser parser = null;

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.ABLOGTOWATCH.domain) == 0) {
			parser = new ABlogToWatchArticleParser(link, null, null, null);
		}

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.HODINKEE.domain) == 0) {
			parser = new HodinkeeArticleParser(link, null, null, null);
		}

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.WATCHREPORT.domain) == 0) {
			parser = new WatchReportArticleParser(link, null, null, null);
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
					boolean isValidLink = this.isArticleLink(articleId,
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

	public boolean isValidLink(String link) {
		if (link == null)
			return false;

		BaseParser parser = null;

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.ABLOGTOWATCH.domain) == 0) {
			parser = new ABlogToWatchArticleParser(link, null, null, null);
		}

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.HODINKEE.domain) == 0) {
			parser = new HodinkeeArticleParser(link, null, null, null);
		}

		// ABlogToWatch link check
		if (link.indexOf(Globals.Domain.WATCHREPORT.domain) == 0) {
			parser = new WatchReportArticleParser(link, null, null, null);
		}

		// Chrono24 link check
		if (link.indexOf(Globals.Domain.CHRONO24.domain) == 0) {
			parser = new Chrono24EntryPageParser(link, null, null, null);
		}

		// Check if link is valid
		if (parser == null) {
			if (Globals.DEBUG)
				System.out
						.println("Link is invalid because it is not of existing domains");
			return false;
		}

		if (!parser.isValidLink(link)) {
			if (Globals.DEBUG)
				System.out
						.println("Link is invalid because it is not a valid link");
			return false;
		}

		return true;
	}

	// Remove from database articles with bad link
	public void sanitizeInvalidLinks() {
		this.mysqlConnection = new MySqlConnection();
		ResultSet resultSet = this.mysqlConnection.getLinkQueue();
		if (resultSet == null)
			return;

		try {
			int count = 0;
			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				String link = resultSet.getString(1);

				// Check if link is valid or not. If not, delete the article
				boolean isValidLink = this.isValidLink(link);
				if (!isValidLink) {
					count++;
					System.out.println("("+count+") Delete link " + link);
					this.mysqlConnection.removeFromLinkQueueTable(link);
				}
			}
		} catch (Exception e) {

		}
	}

	// Remove lin-ks with the same html content
	public void sanitizeDuplicateArticleLink() {
		int lowerBound = 0;
		int maxNumResult = 500;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			Map<String, Integer> articleIdToHashStringMap = new HashMap<String, Integer>();

			// Get maxNumResult articles at a time, until exhaust all the
			// articles
			while (true) {
				this.mysqlConnection = new MySqlConnection();
				ResultSet resultSet = this.mysqlConnection.getArticleContent(
						lowerBound, maxNumResult);
				if (resultSet == null)
					break;

				int count = 0;
				// Iterate through the result set to populate the
				// information
				while (resultSet.next()) {
					count++;
					// Hash the html content
					byte[] bytesOfMessage = resultSet.getString(2).getBytes(
							"UTF-8");
					byte[] thedigest = md.digest(bytesOfMessage);
					String digestMsg = new String(thedigest);

					if (articleIdToHashStringMap.containsKey(digestMsg)) {
						Globals.crawlerLogManager.writeLog("Remove article "
								+ resultSet.getInt(1)
								+ " has the same html with article "
								+ articleIdToHashStringMap.get(digestMsg));
						this.mysqlConnection.removeArticle(resultSet.getInt(1));
					} else {
						articleIdToHashStringMap.put(digestMsg,
								resultSet.getInt(1));
					}
				}

				if (count == 0)
					break;

				lowerBound += maxNumResult;
				Helper.waitSec(2, 5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Compressed html content
	public void compressArticleContents() {
		int lowerBound = 6000;
		int maxNumResult = 500;

		try {
			// Get maxNumResult articles at a time, until exhaust all the
			// articles
			while (true) {
				this.mysqlConnection = new MySqlConnection();
				ResultSet resultSet = this.mysqlConnection.getArticleContent(
						lowerBound, maxNumResult);
				if (resultSet == null)
					break;

				int count = 0;
				// Iterate through the result set to populate the
				// information
				while (resultSet.next()) {
					count++;
					// Hash the html content
					Globals.crawlerLogManager
							.writeLog("Try to compress article id "
									+ resultSet.getInt(1));
					String originalHtmlContent = resultSet.getString(2);
					String compressedHtmlContent = HTMLCompressor
							.compressHtmlContent(originalHtmlContent);

					if (!this.mysqlConnection.addArticleContent(
							resultSet.getInt(1), compressedHtmlContent))
						continue;
				}

				if (count == 0)
					break;

				lowerBound += maxNumResult;
				Helper.waitSec(2, 5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Compressed html content
	public void compressWatchContents() {
		int lowerBound = 0;
		int maxNumResult = 500;

		try {
			// Get maxNumResult articles at a time, until exhaust all the
			// articles
			while (true) {
				this.mysqlConnection = new MySqlConnection();
				ResultSet resultSet = this.mysqlConnection.getWatchPageContent(
						lowerBound, maxNumResult);
				if (resultSet == null)
					break;

				int count = 0;
				// Iterate through the result set to populate the
				// information
				while (resultSet.next()) {
					count++;
					// Hash the html content
					Globals.crawlerLogManager
							.writeLog("Try to compress watch id "
									+ resultSet.getInt(1));
					String originalHtmlContent = resultSet.getString(2);
					String compressedHtmlContent = HTMLCompressor
							.compressHtmlContent(originalHtmlContent);
					if (!this.mysqlConnection.addWatchPageContent(
							resultSet.getInt(1), compressedHtmlContent))
						continue;
				}

				if (count == 0)
					break;

				lowerBound += maxNumResult;
				Helper.waitSec(2, 5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainCrawler.startUpState();
		
		SanitizeDB sanitizer = new SanitizeDB();
		// sanitizer.sanitizeInvalidLinks();
		// sanitizer.sanitizeBadLinkArticles();
		// sanitizer.sanitizeDuplicateArticleLink();
		// sanitizer.compressArticleContents(); /* Should not need to use this
		// anymore because of default compress */
		// sanitizer.compressWatchContents(); /* Should not need to use this
		// anymore because of default compress */
	}
}
