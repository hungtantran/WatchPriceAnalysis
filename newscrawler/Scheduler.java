package newscrawler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import commonlib.LogManager;
import commonlib.TopicComparator;

import dbconnection.MySqlConnection;

public class Scheduler {
	protected Set<String> urlsCrawled = null;
	protected PriorityBlockingQueue<String> urlsQueue = null;
	protected LogManager logManager = null;
	protected MySqlConnection con = null;
	protected BaseCrawler[] crawlers = null;
	protected int maxCrawlers = 0;

	private final ReentrantLock mutex = new ReentrantLock();

	public Scheduler(LogManager logManager, MySqlConnection con, int maxCrawlers) {
		this.urlsQueue = new PriorityBlockingQueue<String>(100,
				new TopicComparator());
		this.urlsCrawled = Collections.synchronizedSet(new HashSet<String>());

		this.logManager = logManager;
		this.con = con;
		this.maxCrawlers = maxCrawlers;
		this.crawlers = new BaseCrawler[this.maxCrawlers];
	}

	public boolean startUp() {
		this.logManager.writeLog("Begin start up scheduler");
		
		// Populate the queue with previous links in the queue
		try {
			ResultSet queue = this.con.getLinkQueue();
			while (queue.next()) {
				this.urlsQueue.add(queue.getString(1));
			}

			this.logManager.writeLog("Urls in Queue : "
					+ this.getUrlsQueueSize());

			ResultSet crawled = this.con.getLinkCrawled();
			while (crawled.next()) {
				this.urlsCrawled.add(crawled.getString(1));
			}

			this.logManager.writeLog("Urls in Crawled Set : "
					+ this.getUrlsCrawledSize());

			return true;
		} catch (SQLException e) {
			this.logManager.writeLog(e.getMessage());
		}

		return false;
	}

	private boolean isValidLink(String url) {
		if (url == null)
			return false;

		return true;
	}

	public boolean addToUrlsQueue(String url) {
		if (!this.isValidLink(url))
			return false;

		if (!this.urlsCrawled.contains(url) && !this.urlsQueue.contains(url)) {
			this.urlsQueue.add(url);
			return true;
		}

		return false;

	}

	public boolean addToUrlsCrawled(String url) {
		if (!this.isValidLink(url))
			return false;

		this.urlsCrawled.add(url);

		return true;
	}

	public int getUrlsQueueSize() {
		return this.urlsQueue.size();
	}

	public String getNextLinkFromUrlsQueue() {
		String nextLink = null;

		try {
			nextLink = this.urlsQueue.take();
		} catch (InterruptedException e) {
			this.logManager.writeLog(e.getMessage());
		}

		return nextLink;
	}

	public int getUrlsCrawledSize() {
		return this.urlsCrawled.size();
	}

	public boolean urlsQueueContain(String url) {
		if (!this.isValidLink(url))
			return false;

		return this.urlsQueue.contains(url);
	}

	public boolean urlsCrawledContain(String url) {
		if (!this.isValidLink(url))
			return false;

		return this.urlsCrawled.contains(url);
	}

	public void start() {
		if (!this.startUp()) {
			this.logManager.writeLog("Start up scheduler fails");
			return;
		}

		for (int i = 0; i < this.maxCrawlers; i++) {
			this.crawlers[i] = new BaseCrawler(this.con, this.logManager, this);
		}
		
		this.logManager.writeLog("Run threads for scheduler");
		
		for (int i = 0; i < this.maxCrawlers; i++) {
			this.crawlers[i].start();
		}

		try {
			for (int i = 0; i < this.maxCrawlers; i++)
				crawlers[i].join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
