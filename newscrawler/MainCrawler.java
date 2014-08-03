package newscrawler;

public class MainCrawler {
	public static void main(String[] args) {
		BaseCrawler[] crawlers = new BaseCrawler[4];
		
		crawlers[0] = new HodinkeeCrawler(
				"http://www.hodinkee.com");

		crawlers[1] = new ABlogToWatchCrawler(
				"http://www.ablogtowatch.com/");

		crawlers[2] = new Chrono24Crawler(
				"http://www.chrono24.com/");
		
		crawlers[3] = new WatchReportCrawler(
				"http://www.watchreport.com");

		// Each crawler has 1 thread. Start them all
		for (int i = 0; i < 4; i++)
			crawlers[i].start();

		System.out.println("Start Crawling");

		try {
			for (int i = 0; i < 4; i++)
				crawlers[i].join();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finish Crawling");
	}
}