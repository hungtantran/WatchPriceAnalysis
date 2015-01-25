package newscrawler;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;
import commonlib.TopicComparator;
import daoconnection.DAOFactory;
import daoconnection.Domain;
import daoconnection.LinkCrawled;
import daoconnection.LinkCrawledDAO;
import daoconnection.LinkCrawledDAOJDBC;
import daoconnection.LinkQueue;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;
import daoconnection.Type;

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
		if (this.daoFactory == null) {
			throw new Exception("Invalid argument, no database connection provided");
		}
		
		this.lowerBoundWaitTimeSec = lowerBoundWaitTimeSec;
		this.upperBoundWaitTimeSec = upperBoundWaitTimeSec;
		this.logManager = logManager;
		this.scheduler = scheduler;
		this.daoFactory = daoFactory;
		this.linkQueueDAO = new LinkQueueDAOJDBC(this.daoFactory);
		this.linkCrawledDAO = new LinkCrawledDAOJDBC(this.daoFactory);
		this.parserFactory = parserFactory;
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
		}
		
		boolean isContentLink = parser.isContentLink();
		if (!isContentLink) {
			this.logManager.writeLog("Link " + link + " is not a content link");
		}
		
		boolean populateDatabaseResult = parser.addCurrentContentToDatabase();
		if (!populateDatabaseResult) {
			this.logManager.writeLog("Fail to populate database with current content from link " + link);
		}
		
		// Remove current link from queue, add it to crawl set
		// Adds all links in page to queue
		this.processLinksInPage(parser);
	}
	
	// Add an article to the database with all its relevant information
	public boolean addArticle(String link, Domain domain, String articleName,
			Type type, String[] keywords, String[] topics, String timeCreated,
			String dateCreated, String timeCrawled, String dateCrawled,
			String content) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean addWatchEntry(String link, Domain domain, String watchName,
		int[] prices, String[] keywords, String[] topics,
		String timeCreated, String dateCreated, String timeCrawled,
		String dateCrawled, String content, String refNo, String movement,
		String caliber, String watchCondition, int watchYear,
		String caseMaterial, String dialColor, String gender,
		String location1, String location2, String location3) 
	{
		// TODO Auto-generated method stub
		return false;
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
