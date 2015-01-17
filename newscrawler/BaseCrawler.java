package newscrawler;

import java.sql.SQLException;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;

import daoconnection.DAOFactory;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;

public class BaseCrawler extends Thread {
	protected LogManager logManager = null;
	protected Scheduler scheduler = null;
	protected BaseParser parser = null;
	protected int numRetriesDownloadLink = 2;
	protected int lowerBoundWaitTimeSec = Globals.DEFAULTLOWERBOUNDWAITTIMESEC;
	protected int upperBoundWaitTimeSec = Globals.DEFAULTUPPERBOUNDWAITTIMESEC;
	protected LinkQueueDAO linkQueueDAO = null;
	
	// Base constructor
	protected BaseCrawler(LogManager logManager, Scheduler scheduler) throws ClassNotFoundException, SQLException {
		this(
			Globals.DEFAULTLOWERBOUNDWAITTIMESEC,
			Globals.DEFAULTUPPERBOUNDWAITTIMESEC,
			logManager,
			scheduler,
			DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database));
	}

	protected BaseCrawler(
		int lowerBoundWaitTimeSec,
		int upperBoundWaitTimeSec,
		LogManager logManager,
		Scheduler scheduler,
		DAOFactory daoFactory) throws SQLException
	{
		this.lowerBoundWaitTimeSec = lowerBoundWaitTimeSec;
		this.upperBoundWaitTimeSec = upperBoundWaitTimeSec;
		this.logManager = logManager;
		this.scheduler = scheduler;
		this.linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
	}

	// Function that start the crawling process
	public void startCrawl(
		boolean timeOut,
		long duration,
		int lowerBoundWaitTimeSec,
		int upperBoundWaitTimeSec) throws SQLException
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

			this.parser = CrawlerParserFactory.getParser(curUrl, this.logManager, this.scheduler);
			
			if (this.parser == null) {
				this.logManager.writeLog("Can't find parser for url "+curUrl);
				this.linkQueueDAO.removeLinkQueue(curUrl);
				continue;
			}
			
			if (!this.parser.isValidLink(curUrl)) {
				this.logManager.writeLog("Link "+curUrl+" is invalid. Assume already crawled, move on don't process");
				this.linkQueueDAO.removeLinkQueue(curUrl);
			}

			// Process link (e.g. trim, truncate bad part, etc..)
			curUrl = this.parser.processLink(curUrl);

			this.logManager.writeLog("Process url " + curUrl);

			// Process the new link
			this.processUrl(curUrl);

			// Wait for 5 to 10 sec before crawling the next page
			Helper.waitSec(lowerBoundWaitTimeSec, upperBoundWaitTimeSec);
		}
	}

	protected void processUrl(String curUrl) {
		if (this.parser != null) {
			this.scheduler.addToUrlsCrawled(curUrl);
			this.parser.checkDocumentUrl(curUrl);
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
