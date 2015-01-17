package analysis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newscrawler.ABlogToWatchArticleParser;
import newscrawler.BaseParser;
import newscrawler.Chrono24EntryPageParser;
import newscrawler.CrawlerParserFactory;
import newscrawler.HodinkeeArticleParser;
import newscrawler.MainCrawler;
import newscrawler.WatchReportArticleParser;
import commonlib.Globals;
import commonlib.HTMLCompressor;
import commonlib.Helper;
import daoconnection.*;

public class SanitizeDB {
	public SanitizeDB() {
	}

	public boolean isArticleLink(int articleId, String link) throws ClassNotFoundException, SQLException {
		if (link == null)
			return false;
		
		ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(DAOFactory.getInstance(
			Globals.username,
			Globals.password,
			Globals.server + Globals.database));
		ArticleContent content = articleContentDAO.getArticleContent(articleId);

		// If there is no content, can't decide whether link is valid or not
		if (content == null)
			return true;

		BaseParser parser = CrawlerParserFactory.getParser(link, null, null);

		// Check if link is valid
		if (parser == null) {
			if (Globals.DEBUG) {
				System.out.println("Link is invalid because it is not of existing domains");
			}
			
			return false;
		}

		parser.setContent(content.getContent());
		if (!parser.isArticlePage()) {
			if (Globals.DEBUG) {
				System.out.println("Link is invalid because it is not article page");
			}
			
			return false;
		}

		return true;
	}

	// Remove from database articles with bad link
	public void sanitizeBadLinkArticles() throws ClassNotFoundException, SQLException {
		int articleCount = 0;
		
		ArticleDAO articleDAO = new ArticleDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		ArticleTopicDAO articleTopicDAO = new ArticleTopicDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		
		boolean startFromBeginning = true;
		
		// Get 2000 articles at a time, until exhaust all the articles
		while (true) {
			Article article = articleDAO.getNextArticle(startFromBeginning);
			
			if (startFromBeginning) {
				startFromBeginning = false;
			}
			
			if (article == null) {
				break;
			}

			articleCount++;

			int articleId = article.getId();
			String articleLink = article.getLink().trim();
			if (Globals.DEBUG) {
				System.out.println("(" + articleCount + ") Check Article id " + articleId + ": " + articleLink);
			}

			// Check if link is valid or not. If not, delete the article
			boolean isValidLink = this.isArticleLink(articleId, articleLink);
			if (!isValidLink) {
				if (Globals.DEBUG) {
					System.out.println("Delete Article id " + articleId);
				}
				
				articleContentDAO.deleteArticleContent(articleId);
				articleTopicDAO.removeArticleTopicByArticleTableId(articleId);
				articleDAO.deleteArticle(articleId);
			}
		}
	}

	public boolean isValidLink(String link) {
		if (link == null) {
			return false;
		}

		BaseParser parser = CrawlerParserFactory.getParser(link, null, null);

		// Check if link is valid
		if (parser == null) {
			if (Globals.DEBUG) {
				System.out.println("Link is invalid because it is not of existing domains");
			}
			
			return false;
		}

		if (!parser.isValidLink(link)) {
			if (Globals.DEBUG) {
				System.out.println("Link is invalid because it is not a valid link");
			}
			
			return false;
		}

		return true;
	}

	// Remove from database articles with bad link
	public void sanitizeInvalidLinks() throws SQLException, ClassNotFoundException {
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		
		List<LinkQueue> linkQueues = linkQueueDAO.getLinksQueued();
		
		if (linkQueues == null) {
			return;
		}
		
		int count = 0;
		
		// Iterate through the result set to populate the information
		for (LinkQueue linkQueue : linkQueues) {
			String link = linkQueue.getLink();

			// Check if link is valid or not. If not, delete the article
			boolean isValidLink = this.isValidLink(link);
			
			if (!isValidLink) {
				++count;
				System.out.println("(" + count + ") Delete link " + link);
				linkQueueDAO.removeLinkQueue(link);
			}
		}
	}

	// Remove lin-ks with the same html content
	public void sanitizeDuplicateArticleLink() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		ArticleDAO articleDAO = new ArticleDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		ArticleTopicDAO articleTopicDAO = new ArticleTopicDAOJDBC(DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
		
		boolean startFromBeginning = true;
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		Map<String, Integer> articleIdToHashStringMap = new HashMap<String, Integer>();

		// Get maxNumResult articles at a time, until exhaust all the
		// articles
		while (true) {
			ArticleContent articleContent = articleContentDAO.getNextArticleContent(startFromBeginning);
			
			if (articleContent == null) {
				break;
			}
			
			if (startFromBeginning) {
				startFromBeginning = false;
			}
			
			// Hash the html content
			byte[] bytesOfMessage = articleContent.getContent().getBytes("UTF-8");
			byte[] thedigest = md.digest(bytesOfMessage);
			String digestMsg = new String(thedigest);
			
			Integer articleId = articleContent.getArticleTableId();
			
			if (articleIdToHashStringMap.containsKey(digestMsg)) {
				Globals.crawlerLogManager.writeLog("Remove article " + articleId
					+ " has the same html with article " + articleIdToHashStringMap.get(digestMsg));
				
				articleContentDAO.deleteArticleContent(articleId);
				articleTopicDAO.removeArticleTopicByArticleTableId(articleId);
				articleDAO.deleteArticle(articleId);
			} else {
				articleIdToHashStringMap.put(digestMsg, articleId);
			}
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
				ResultSet resultSet = this.mysqlConnection.getArticleContent(lowerBound, maxNumResult);
				if (resultSet == null)
					break;

				int count = 0;
				// Iterate through the result set to populate the
				// information
				while (resultSet.next()) {
					count++;
					// Hash the html content
					Globals.crawlerLogManager.writeLog("Try to compress article id " + resultSet.getInt(1));
					String originalHtmlContent = resultSet.getString(2);
					String compressedHtmlContent = HTMLCompressor
							.compressHtmlContent(originalHtmlContent);

					if (!this.mysqlConnection.addArticleContent(resultSet.getInt(1), compressedHtmlContent))
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
		// MainCrawler.startUpState();
		// SanitizeDB sanitizer = new SanitizeDB();
		// sanitizer.sanitizeInvalidLinks();
		// sanitizer.sanitizeBadLinkArticles();
		// sanitizer.sanitizeDuplicateArticleLink();
		// sanitizer.compressArticleContents(); /* Should not need to use this
		// anymore because of default compress */
		// sanitizer.compressWatchContents(); /* Should not need to use this
		// anymore because of default compress */
	}
}
