package newscrawler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import commonlib.Helper;
import commonlib.LogManager;
import commonlib.TopicComparator;

import dbconnection.MySqlConnection;

public class Scheduler {
	protected Random rand = null;
	protected Set<String> urlsCrawled = null;
	protected PriorityBlockingQueue<String>[] urlsQueue = null;
	protected LogManager logManager = null;
	protected MySqlConnection con = null;
	protected BaseCrawler[] crawlers = null;
	protected int maxCrawlers = 0;
	protected int numQueue = 0;

	private final ReentrantLock mutex = new ReentrantLock();

	@SuppressWarnings("unchecked")
	public Scheduler(LogManager logManager, MySqlConnection con,
			int maxCrawlers, int numQueue) {
		if (numQueue <= 0 || maxCrawlers <= 0)
			return;
		
		this.rand = new Random();
		this.urlsQueue = new PriorityBlockingQueue[numQueue];
		this.numQueue = numQueue;

		for (int i = 0; i < numQueue; i++) {
			this.urlsQueue[i] = new PriorityBlockingQueue<String>(100,
					new TopicComparator());
		}

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
				this.forceAddToUrlsQueue(queue.getString(1));
			}
			
			for (int i = 0; i < this.numQueue; i++) {
				this.logManager.writeLog("Urls in Queue "+i+" : "
						+ this.urlsQueue[i].size());
			}

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

		int index = Helper.hashStringToInt(url) % this.numQueue;

		if (!this.urlsCrawled.contains(url)
				&& !this.urlsQueue[index].contains(url)) {
			System.out.println("Add to queue "+index);
			this.urlsQueue[index].add(url);
			
			this.logManager.writeLog("Add link " + url + " to queue "+index);
			return true;
		}

		return false;

	}

	public boolean forceAddToUrlsQueue(String url) {
		if (url == null)
			return false;

		int hash = Helper.hashStringToInt(url);
		this.urlsQueue[hash % this.numQueue].add(url);

		return true;
	}

	public boolean addToUrlsCrawled(String url) {
		if (!this.isValidLink(url))
			return false;

		this.urlsCrawled.add(url);

		return true;
	}

	public int getUrlsQueueSize() {
		int size = 0;

		for (int i = 0; i < this.numQueue; i++) {
			size += this.urlsQueue[i].size();
		}

		return size;
	}

	public String getNextLinkFromUrlsQueue() {
		String nextLink = null;

		int numTries = 0;
		while (true) {
			int index = this.rand.nextInt(this.numQueue);
			
			if (this.urlsQueue[index].size() > 0) {
				nextLink = this.urlsQueue[index].remove();
				break;
			} else if (numTries % this.numQueue == 10) {
				// If we have too many tries, wait a bit
				Helper.waitSec(5, 10);
			}

			numTries++;
		}

		return nextLink;
	}

	public int getUrlsCrawledSize() {
		return this.urlsCrawled.size();
	}

	public boolean urlsQueueContain(String url) {
		if (!this.isValidLink(url))
			return false;

		int index = Helper.hashStringToInt(url) % this.numQueue;

		return this.urlsQueue[index].contains(url);
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
