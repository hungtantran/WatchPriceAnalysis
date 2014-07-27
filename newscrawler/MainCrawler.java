package newscrawler;

public class MainCrawler {
	public static void main(String[] args) {
		HodinkeeCrawler hodinkeeCrawler = new HodinkeeCrawler(
				"http://www.hodinkee.com");

		ABlogToWatchCrawler aBlogToWatchCrawler = new ABlogToWatchCrawler(
				"http://www.ablogtowatch.com/");

		Chrono24Crawler chrono24Crawler = new Chrono24Crawler(
				"http://www.chrono24.com/en/rolex/gmt-master-ii-green-index-dial--id2122361.htm");

		// Each crawler has 1 thread. Start them all
		hodinkeeCrawler.start();
		aBlogToWatchCrawler.start();
		chrono24Crawler.start();

		System.out.println("Start Crawling");

		try {
			hodinkeeCrawler.join();
			aBlogToWatchCrawler.join();
			chrono24Crawler.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finish Crawling");
	}
}