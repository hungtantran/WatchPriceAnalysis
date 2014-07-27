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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import dbconnection.MySqlConnection;

public abstract class BaseCrawler extends Thread {
	protected String startURL = null;
	protected Set<String> urlsCrawled = null;
	protected Queue<String> urlsQueue = null;
	protected MySqlConnection mysqlConnection = null;
	protected int numRetriesDownloadLink = 2;

	// All subclass of basecrawler needs to implement these methods
	abstract void checkDocumentUrl(String url);

	abstract void startCrawl(boolean timeOut, long duration);

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

	// Make the current thread wait for a random amount of time between
	// lowerBound and upperBound number of seconds
	protected void waitSec(int lowerBound, int upperBound) {
		try {
			int waitTime = lowerBound
					* 1000
					+ (int) (Math.random() * ((upperBound * 1000 - lowerBound * 1000) + 1));
			System.out.println("Wait for " + waitTime);
			Thread.currentThread();
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void processUrl(String curUrl) {
		this.urlsCrawled.add(curUrl);

		this.checkDocumentUrl(curUrl);
	}

	// Serialize urls already crawled and urls in the queue to disk
	protected void serializeDataToDisk(String crawlerId) {
		if (this.urlsCrawled.size() > 0) {
			try (OutputStream file = new FileOutputStream(
					crawlerId + "_urlsCrawled.ser");
					OutputStream buffer = new BufferedOutputStream(file);
					ObjectOutput output = new ObjectOutputStream(buffer);) {
				output.writeObject(this.urlsCrawled);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if (this.urlsQueue.size() > 0) {
			try (OutputStream file = new FileOutputStream(
					crawlerId + "_urlsQueue.ser");
					OutputStream buffer = new BufferedOutputStream(file);
					ObjectOutput output = new ObjectOutputStream(buffer);) {
				output.writeObject(this.urlsQueue);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Hash a plain string text
	protected String hash(String plainText) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		return (new HexBinaryAdapter()).marshal(messageDigest.digest(plainText
				.getBytes()));
	}

	// Return the current date, e.g: 2014-05-23
	@SuppressWarnings("deprecation")
	public static String getCurrentDate() {
		Date currentDate = new Date();
		String year = new String("" + (1900 + currentDate.getYear()));
		String month = new String("" + currentDate.getMonth());
		String date = new String("" + currentDate.getDate());

		String dateCrawled = year + "-" + month + "-" + date;

		return dateCrawled;
	}

	// Return the current time 22:11:30
	@SuppressWarnings("deprecation")
	public static String getCurrentTime() {
		Date currentDate = new Date();
		String hour = new String("" + currentDate.getHours());
		String minute = new String("" + currentDate.getMinutes());
		String second = new String("" + currentDate.getSeconds());

		String timeCrawled = hour + ":" + minute + ":" + second;

		return timeCrawled;
	}

	public static boolean linkIsFile(String url) {
		if (url == null)
			return false;

		for (String exts : Globals.fileExtenstions) {
			if (url.indexOf(exts) == (url.length() - exts.length()))
				return true;
		}

		return false;
	}
}
