package newscrawler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import dbconnection.MySqlConnection;

public abstract class BaseCrawler extends Thread {
	protected String startURL = null;
	protected Set<String> urlsCrawled = null;
	protected Queue<String> urlsQueue = null;
	protected MySqlConnection mysqlConnection = null;
	protected int numRetriesDownloadLink = 2;

	// All subclass of basecrawler needs to implement these methods
	abstract void checkDocumentUrl(String url);

	abstract String processLink(String url);

	abstract boolean isValidLink(String url);

	// Base constructor
	@SuppressWarnings("unchecked")
	protected BaseCrawler(String startURL, String domain, String crawlerId) {
		System.out.println("Start url = " + startURL);

		// Start Url is not hodinkee link. initialize it to the homepage
		if (startURL.indexOf(domain) != 0) {
			this.startURL = domain;
		} else {
			// Initialize private variable
			this.startURL = startURL;
		}

		this.mysqlConnection = new MySqlConnection();
		this.urlsQueue = new LinkedList<String>();
		this.urlsCrawled = new HashSet<String>();
		urlsQueue.add(this.startURL);

		// Try to deserialize the saved term on disk into memory
		try (InputStream file = new FileInputStream(crawlerId
				+ "_urlsQueue.ser");
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {
			Queue<String> tempQueue = (Queue<String>) input.readObject();

			while (!tempQueue.isEmpty()) {
				this.urlsQueue.add(tempQueue.remove());
			}

			System.out.println("Urls in " + crawlerId + " Queue : "
					+ this.urlsQueue.size());

			// TODO make it generic
			if (Globals.DEBUG)
				// display its data
				for (String term : this.urlsQueue) {
					System.out.println("Urls in Queue : " + term);
				}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try (InputStream file = new FileInputStream(crawlerId
				+ "_urlsCrawled.ser");
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {
			this.urlsCrawled = (Set<String>) input.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
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
