package newscrawler;

import commonlib.Globals;
import commonlib.Helper;
import commonlib.LogManager;
import dbconnection.MySqlConnection;

public class BaseCrawler extends Thread {
	protected MySqlConnection mysqlConnection = null;
	protected LogManager logManager = null;
	protected Scheduler scheduler = null;
	protected BaseParser parser = null;
	protected int numRetriesDownloadLink = 2;
	protected int lowerBoundWaitTimeSec = Globals.DEFAULTLOWERBOUNDWAITTIMESEC;
	protected int upperBoundWaitTimeSec = Globals.DEFAULTUPPERBOUNDWAITTIMESEC;

	// Base constructor
	protected BaseCrawler(MySqlConnection con, LogManager logManager,
			Scheduler scheduler) {
		this(Globals.DEFAULTLOWERBOUNDWAITTIMESEC,
				Globals.DEFAULTUPPERBOUNDWAITTIMESEC, con, logManager,
				scheduler);
	}

	protected BaseCrawler(int lowerBoundWaitTimeSec, int upperBoundWaitTimeSec,
			MySqlConnection con, LogManager logManager, Scheduler scheduler) {
		this.lowerBoundWaitTimeSec = lowerBoundWaitTimeSec;
		this.upperBoundWaitTimeSec = upperBoundWaitTimeSec;

		this.mysqlConnection = con;
		this.logManager = logManager;
		this.scheduler = scheduler;
	}

	private BaseParser chooseParser(String url) {
		if (url == null)
			return null;

		if (url.indexOf(Globals.Domain.ABLOGTOWATCH.domain) == 0)
			return new ABlogToWatchArticleParser(url, this.mysqlConnection,
					this.logManager, this.scheduler);

		if (url.indexOf(Globals.Domain.CHRONO24.domain) == 0)
			return new Chrono24EntryPageParser(url, this.mysqlConnection,
					this.logManager, this.scheduler);

		if (url.indexOf(Globals.Domain.HODINKEE.domain) == 0)
			return new HodinkeeArticleParser(url, this.mysqlConnection,
					this.logManager, this.scheduler);

		if (url.indexOf(Globals.Domain.WATCHREPORT.domain) == 0)
			return new WatchReportArticleParser(url, this.mysqlConnection,
					this.logManager, this.scheduler);

		return null;
	}

	// Function that start the crawling process
	public void startCrawl(boolean timeOut, long duration,
			int lowerBoundWaitTimeSec, int upperBoundWaitTimeSec) {
		if (this.scheduler == null || this.mysqlConnection == null)
			return;

		while (true) {
			String curUrl = this.scheduler.getNextLinkFromUrlsQueue();

			// If for some reason startUrl is null stop right away
			if (curUrl == null)
				return;

			this.parser = this.chooseParser(curUrl);

			if (this.parser == null)
				continue;

			// Process link (e.g. trim, truncate bad part, etc..)
			// Check if link is still valid or not
			curUrl = this.parser.processLink(curUrl);
			if (!this.parser.isValidLink(curUrl))
				return;

			if (this.logManager != null)
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
		this.startCrawl(false, 0, this.lowerBoundWaitTimeSec,
				this.upperBoundWaitTimeSec);
	}
}
