package newscrawler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import dbconnection.MySqlConnection;

public abstract class BaseCrawler extends Thread {
	protected String startURL = null;
	protected Set<String> urlsCrawled = null;
	protected PriorityQueue<String> urlsQueue = null;
	protected MySqlConnection mysqlConnection = null;
	protected int numRetriesDownloadLink = 2;
	protected int lowerBoundWaitTimeSec = Globals.DEFAULTLOWERBOUNDWAITTIMESEC;
	protected int upperBoundWaitTimeSec = Globals.DEFAULTUPPERBOUNDWAITTIMESEC;

	// All subclass of basecrawler needs to implement these methods
	abstract void checkDocumentUrl(String url);

	abstract String processLink(String url);

	abstract boolean isValidLink(String url);

	// Base constructor
	protected BaseCrawler(String startURL, String domain, int domainId) {
		this(startURL, domain, domainId, Globals.DEFAULTLOWERBOUNDWAITTIMESEC,
				Globals.DEFAULTUPPERBOUNDWAITTIMESEC, new TopicComparator());
	}

	protected BaseCrawler(String startURL, String domain, int domainId,
			int lowerBoundWaitTimeSec, int upperBoundWaitTimeSec,
			Comparator<String> comparator) {
		System.out.println("Start url = " + startURL);

		// Start Url is not hodinkee link. initialize it to the homepage
		if (startURL.indexOf(domain) != 0) {
			this.startURL = domain;
		} else {
			// Initialize private variable
			this.startURL = startURL;
		}

		this.lowerBoundWaitTimeSec = lowerBoundWaitTimeSec;
		this.upperBoundWaitTimeSec = upperBoundWaitTimeSec;

		this.mysqlConnection = new MySqlConnection();
		this.urlsQueue = new PriorityQueue<String>(100, comparator);
		this.urlsCrawled = new HashSet<String>();
		urlsQueue.add(this.startURL);

		// Populate the queue with previous links in the queue
		ResultSet queue = this.mysqlConnection.getLinkQueue(domainId);
		try {
			while (queue.next()) {
				this.urlsQueue.add(queue.getString(1));
			}
			
			if (Globals.DEBUG)
				System.out.println("Urls in Queue : " + this.urlsQueue.size());

			ResultSet crawled = this.mysqlConnection.getLinkCrawled(domainId);
			while (crawled.next()) {
				this.urlsCrawled.add(crawled.getString(1));
			}
			
			if (Globals.DEBUG)
				System.out.println("Urls in Crawled Set : " + this.urlsCrawled.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Function that start the crawling process
	public void startCrawl(boolean timeOut, long duration,
			int lowerBoundWaitTimeSec, int upperBoundWaitTimeSec) {
		// If for some reason startUrl is null stop right away
		if (this.startURL == null)
			return;

		// Continuously pop the queue to parse the page content
		while (true) {
			if (this.urlsQueue.isEmpty())
				break;

			// Get the next link from the queue
			String curUrl = this.urlsQueue.remove();

			// Process link (e.g. trim, truncate bad part, etc..)
			// Check if link is still valid or not
			curUrl = this.processLink(curUrl);
			if (!this.isValidLink(curUrl))
				continue;

			System.out.println("Process url " + curUrl);

			// Process the new link
			this.processUrl(curUrl);

			// Wait for 5 to 10 sec before crawling the next page
			Helper.waitSec(lowerBoundWaitTimeSec, upperBoundWaitTimeSec);
		}
	}

	protected void processUrl(String curUrl) {
		this.urlsCrawled.add(curUrl);

		this.checkDocumentUrl(curUrl);
	}

	protected void postProcessUrl(String processedlink, int domainId, Integer priority, int persistent, Set<String> newLinks) {
		System.out.println("Already Crawled " + this.urlsCrawled.size());
		System.out.println("Queue has " + this.urlsQueue.size());
		
		if (processedlink != null) {
			this.mysqlConnection.insertIntoLinkCrawledTable(processedlink, domainId, priority, null, null);
			this.mysqlConnection.removeFromLinkQueueTable(processedlink, domainId);
		}
		
		if (newLinks != null)
			for (String newLink : newLinks) {
				this.mysqlConnection.insertIntoLinkQueueTable(newLink, domainId, priority, persistent, null, null);
			}
	}

	// Serialize urls already crawled and urls in the queue to disk
	protected void serializeDataToDisk(String crawlerId) {
		if (this.urlsCrawled.size() > 0) {
			try (OutputStream file = new FileOutputStream(crawlerId
					+ "_urlsCrawled.ser");
					OutputStream buffer = new BufferedOutputStream(file);
					ObjectOutput output = new ObjectOutputStream(buffer);) {
				output.writeObject(this.urlsCrawled);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if (this.urlsQueue.size() > 0) {
			try (OutputStream file = new FileOutputStream(crawlerId
					+ "_urlsQueue.ser");
					OutputStream buffer = new BufferedOutputStream(file);
					ObjectOutput output = new ObjectOutputStream(buffer);) {
				output.writeObject(this.urlsQueue);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
