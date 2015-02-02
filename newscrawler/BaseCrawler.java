package newscrawler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;
import commonlib.TopicComparator;

import daoconnection.Article;
import daoconnection.ArticleContent;
import daoconnection.ArticleContentDAO;
import daoconnection.ArticleContentDAOJDBC;
import daoconnection.ArticleDAO;
import daoconnection.ArticleDAOJDBC;
import daoconnection.ArticleTopic;
import daoconnection.ArticleTopicDAO;
import daoconnection.ArticleTopicDAOJDBC;
import daoconnection.DAOFactory;
import daoconnection.Domain;
import daoconnection.LinkCrawled;
import daoconnection.LinkCrawledDAO;
import daoconnection.LinkCrawledDAOJDBC;
import daoconnection.LinkQueue;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;
import daoconnection.Type;
import daoconnection.WatchDesc;
import daoconnection.WatchDescDAO;
import daoconnection.WatchDescDAOJDBC;
import daoconnection.WatchPageContent;
import daoconnection.WatchPageContentDAO;
import daoconnection.WatchPageContentDAOJDBC;

public class BaseCrawler extends Thread {
	protected DAOFactory daoFactory = null;
	protected CrawlerParserFactory parserFactory = null;
	protected LogManager logManager = null;
	protected Scheduler scheduler = null;
	protected int numRetriesDownloadLink = 2;
	protected int lowerBoundWaitTimeSec = Globals.DEFAULTLOWERBOUNDWAITTIMESEC;
	protected int upperBoundWaitTimeSec = Globals.DEFAULTUPPERBOUNDWAITTIMESEC;

	protected LinkQueueDAO linkQueueDAO = null;
	protected LinkCrawledDAO linkCrawledDAO = null;
	protected ArticleDAO articleDAO = null;
	protected ArticleContentDAO articleContentDAO = null;
	protected ArticleTopicDAO articleTopicDAO = null;
	protected WatchDescDAO watchDescDAO = null;
	protected WatchPageContentDAO watchPageContentDAO = null;

	// Base constructor
	protected BaseCrawler(LogManager logManager, Scheduler scheduler, DAOFactory daoFactory, CrawlerParserFactory parserFactory) throws Exception {
		this(
			Globals.DEFAULTLOWERBOUNDWAITTIMESEC,
			Globals.DEFAULTUPPERBOUNDWAITTIMESEC,
			logManager,
			scheduler,
			daoFactory,
			parserFactory);
	}

	protected BaseCrawler(
		int lowerBoundWaitTimeSec,
		int upperBoundWaitTimeSec,
		LogManager logManager,
		Scheduler scheduler,
		DAOFactory daoFactory,
		CrawlerParserFactory parserFactory) throws Exception
	{
		if (daoFactory == null) {
			throw new Exception("Invalid argument, no database connection provided");
		}

		this.lowerBoundWaitTimeSec = lowerBoundWaitTimeSec;
		this.upperBoundWaitTimeSec = upperBoundWaitTimeSec;
		this.logManager = logManager;
		this.scheduler = scheduler;
		this.parserFactory = parserFactory;

		this.daoFactory = daoFactory;
		this.linkQueueDAO = new LinkQueueDAOJDBC(this.daoFactory);
		this.linkCrawledDAO = new LinkCrawledDAOJDBC(this.daoFactory);
		this.articleDAO = new ArticleDAOJDBC(this.daoFactory);
		this.articleContentDAO = new ArticleContentDAOJDBC(this.daoFactory);
		this.articleTopicDAO = new ArticleTopicDAOJDBC(this.daoFactory);
		this.watchDescDAO = new WatchDescDAOJDBC(this.daoFactory);
		this.watchPageContentDAO = new WatchPageContentDAOJDBC(this.daoFactory);
	}

	// Function that start the crawling process
	public void startCrawl(
		boolean timeOut,
		long duration,
		int lowerBoundWaitTimeSec,
		int upperBoundWaitTimeSec) throws Exception
	{
		if (this.scheduler == null) {
			return;
		}

		while (true) {
			String curUrl = this.scheduler.getNextLinkFromUrlsQueue();
			System.out.println("Get link "+curUrl);
			// If for some reason startUrl is null stop right away
			if (curUrl == null) {
				return;
			}

			IParser parser = this.parserFactory.getParser(curUrl, this, this.logManager, this.scheduler);

			if (parser == null) {
				this.logManager.writeLog("Can't find parser for url "+curUrl);
				this.linkQueueDAO.removeLinkQueue(curUrl);
				continue;
			}

			if (!parser.isValidLink(curUrl)) {
				this.logManager.writeLog("Link " + curUrl + " is invalid. Assume already crawled, move on don't process");
				this.linkQueueDAO.removeLinkQueue(curUrl);
			}

			this.logManager.writeLog("Process url " + curUrl);

			// Process the new link
			this.processUrl(parser);

			// Wait for 5 to 10 sec before crawling the next page
			Helper.waitSec(lowerBoundWaitTimeSec, upperBoundWaitTimeSec);
		}
	}

	protected void processUrl(IParser parser) throws Exception {
		if (parser == null) {
			throw new Exception("Can't find parser");
		}

		String link = parser.getLink();

		this.scheduler.addToUrlsCrawled(link);

		// Parse the page
		boolean parseResult = parser.parseDoc();
		if (!parseResult) {
			this.logManager.writeLog("Fail to parse link " + link);
		} else {
			boolean isContentLink = parser.isContentLink();
			if (!isContentLink) {
				this.logManager.writeLog("Link " + link + " is not a content link");
			} else {
				boolean populateDatabaseResult = parser.addCurrentContentToDatabase();
				if (!populateDatabaseResult) {
					this.logManager.writeLog("Fail to populate database with current content from link " + link);
				}
			}
		}

		// Remove current link from queue, add it to crawl set
		// Adds all links in page to queue
		this.processLinksInPage(parser);
	}

	// Add an article to the database with all its relevant information
	public boolean addArticle(String link, Domain domain, String articleName,
		Type type, String[] keywords, int[] topics, String timeCreated,
		String dateCreated, String timeCrawled, String dateCrawled,
		String content) throws Exception
	{
		this.logManager.writeLog("Try to add article from link " + link + " to database");
		Article article = new Article();
		article.setLink(link);
		article.setDomainTableId1(domain.getId());
		article.setDomainTableId2(null);
		article.setDomainTableId3(null);
		article.setArticleName(articleName);
		article.setTypeTable1(type.getId());
		article.setTypeTable2(null);

		String keyWordStr = null;
		if (keywords != null) {
			for (String keyword : keywords) {
				keyWordStr += keyword + "|";
			}
		}
		article.setKeywords(keyWordStr);

		article.setTimeCreated(timeCreated);
		article.setDateCreated(dateCreated);
		article.setTimeCrawled(timeCrawled);
		article.setDateCrawled(dateCrawled);

		Integer articleID = this.articleDAO.createArticle(article);

		if (articleID == null) {
			throw new Exception("Can't insert article into database");
		}

		this.logManager.writeLog("Try to add article content from link " + link + " to database");
		ArticleContent articleContent = new ArticleContent();
		articleContent.setArticleTableId(articleID);
		articleContent.setContent(content);

		boolean createContentResult = this.articleContentDAO.createArticleContent(articleContent);

		if (!createContentResult) {
			throw new Exception("Can't insert article content into database");
		}

		this.logManager.writeLog("Try to add article topic from link " + link + " to database");

		for (int topic : topics) {
			ArticleTopic articleTopic = new ArticleTopic();
			articleTopic.setArticleTableId(articleID);
			articleTopic.setTopicTableId(topic);

			Integer articleTopicId = this.articleTopicDAO.createArticleTopic(articleTopic);

			if (articleTopicId == null) {
				throw new Exception("Can't insert article topic into database");
			}
		}

		return true;
	}

	public boolean addWatchEntry(String link, Domain domain, String watchName,
		int[] prices, String[] keywords, int[] topics,
		String timeCreated, String dateCreated, String timeCrawled,
		String dateCrawled, String content, String refNo, String movement,
		String caliber, String watchCondition, int watchYear,
		String caseMaterial, String dialColor, String gender,
		String location1, String location2, String location3) throws Exception
	{
		this.logManager.writeLog("Try to add watch description from link " + link + " to database");
		WatchDesc watchDesc = new WatchDesc();
		watchDesc.setLink(link);
		watchDesc.setDomainTableId1(domain.getId());

		if (topics != null && topics.length > 0) {
			watchDesc.setTopicTableId1(topics[0]);
		} else {
			watchDesc.setTopicTableId1(null);
		}

		if (topics != null && topics.length > 1) {
			watchDesc.setTopicTableId2(topics[1]);
		} else {
			watchDesc.setTopicTableId2(null);
		}

		watchDesc.setWatchName(watchName);

		if (prices != null && prices.length > 0) {
			watchDesc.setPrice1(prices[0]);
		} else {
			watchDesc.setPrice1(null);
		}

		if (prices != null && prices.length > 1) {
			watchDesc.setPrice2(prices[1]);
		} else {
			watchDesc.setPrice2(null);
		}

		String keyWordStr = null;
		if (keywords != null) {
			for (String keyword : keywords) {
				keyWordStr += keyword + "|";
			}
		}
		watchDesc.setKeywords(keyWordStr);

		watchDesc.setRefNo(refNo);
		watchDesc.setMovement(movement);
		watchDesc.setCaliber(caliber);
		watchDesc.setWatchCondition(watchCondition);
		watchDesc.setWatchYear(watchYear);
		watchDesc.setCaseMaterial(caseMaterial);
		watchDesc.setDialColor(dialColor);
		watchDesc.setGender(gender);
		watchDesc.setLocation1(location1);
		watchDesc.setLocation2(location2);
		watchDesc.setLocation3(location3);
		watchDesc.setTimeCreated(timeCreated);
		watchDesc.setDateCreated(dateCreated);
		watchDesc.setTimeCrawled(timeCrawled);
		watchDesc.setDateCrawled(dateCrawled);

		Integer watchID = this.watchDescDAO.createWatchDesc(watchDesc);

		if (watchID == null) {
			throw new Exception("Can't insert watch description into database");
		}

		this.logManager.writeLog("Try to add watch content from link " + link + " to database");
		WatchPageContent watchPageContent = new WatchPageContent();
		watchPageContent.setWatchTableId(watchID);
		watchPageContent.setContent(content);

		boolean createContentResult = this.watchPageContentDAO.createWatchPageContent(watchPageContent);

		if (!createContentResult) {
			throw new Exception("Can't insert article content into database");
		}

		return true;
	}

	//Add links in the page into the queue, add current link into the crawled set.
	// Remove current link from the queue set.
	protected void processLinksInPage(IParser parser) throws SQLException {
		String link = parser.getLink();
		Domain domain = parser.getDomain();
		String content = parser.getContent();

		if (content == null) {
			this.logManager.writeLog("Remove link " + link + " because of failure to retrieve content");

			this.postProcessUrl(link, domain.getId(), null, 0, null);

			return;
		}

		// If the page fails to download for reasons like 404, remove it
		/* if (this.exception.getClass() == HttpStatusException.class) {
			HttpStatusException e = (HttpStatusException) this.exception;

			if (e.getStatusCode() == 404) {
				this.logManager.writeLog("Remove link " + url + " because of 404");
				postProcessUrl(url, domain.getId(), null, 0, null);
			}
		}

		if (this.exception.getClass() == MalformedURLException.class) {
			this.logManager.writeLog("Remove link " + url + " because of malformed url");
			postProcessUrl(url, domain.getId(), null, 0, null);
		}

		if (this.exception.getClass() == IllegalArgumentException.class) {
			this.logManager.writeLog("Remove link " + url + " because of ");
			postProcessUrl(url, domain.getId(), null, 0, null);
		}

		return; */

		// Parse out all the links from the current page
		String[] linksInPage = parser.getLinksInContent();;

		// Add more urls to the queue
		Set<String> newStrings = new HashSet<String>();
		if (linksInPage != null) {
			if (Globals.DEBUG) {
				this.logManager.writeLog("Found " + linksInPage.length + " links in page");
			}

			for (String linkInPage : linksInPage) {
				linkInPage = linkInPage.trim();

				if (linkInPage.contains(domain.getDomainString()) && !Helper.linkIsFile(linkInPage) && parser.isValidLink(linkInPage)) {
					newStrings.add(linkInPage);
				}
			}
		}

		// Perform tasks like insert link into crawled set, remove it from queue from sql db
		Integer priority = TopicComparator.getStringPriority(link);
		this.postProcessUrl(link, domain.getId(), priority, 0, newStrings);
	}

	protected void postProcessUrl(String processedlink, int domainId, Integer priority, int persistent, Set<String> newLinks) throws SQLException {
		if (processedlink != null) {
			this.linkQueueDAO.removeLinkQueue(processedlink);

			LinkCrawled linkCrawled = new LinkCrawled();
			linkCrawled.setLink(processedlink);
			linkCrawled.setDomainTableId1(domainId);
			linkCrawled.setPriority(priority);
			linkCrawled.setDateCrawled(null);
			linkCrawled.setTimeCrawled(null);

			this.linkCrawledDAO.createLinkCrawled(linkCrawled);
		}

		if (newLinks == null) {
			return;
		}

		for (String newLink : newLinks) {
			if (!this.scheduler.urlsCrawledContain(newLink) && !this.scheduler.urlsQueueContain(newLink)) {
				Integer newLinkPriority = TopicComparator.getStringPriority(newLink);

				this.scheduler.addToUrlsQueue(newLink);

				LinkQueue linkQueue = new LinkQueue();
				linkQueue.setLink(newLink);
				linkQueue.setDomainTableId1(domainId);
				linkQueue.setPriority(newLinkPriority);
				linkQueue.setPersistent(persistent);
				linkQueue.setDateCrawled(null);
				linkQueue.setTimeCrawled(null);

				if (this.linkQueueDAO.createLinkQueue(linkQueue) == null) {
					continue;
				}
			}
		}
	}

	// Execute method for thread
	@Override
	public void run() {
		try {
			this.startCrawl(
				false,
				0,
				this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
