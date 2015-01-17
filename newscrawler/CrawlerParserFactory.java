package newscrawler;

import commonlib.Globals;
import commonlib.LogManager;

public class CrawlerParserFactory {
	// TODO make this function general
	public static BaseParser getParser(String link, LogManager logManager, Scheduler scheduler) {
		BaseParser parser = null;
		
		if (link == null) {
			return parser;
		}
		
		if (link.indexOf("http://www.ablogtowatch.com") == 0) {
			parser = new ABlogToWatchArticleParser(link, logManager, scheduler);
		}
		
		if (link.indexOf("http://www.hodinkee.com") == 0) {
			parser = new HodinkeeArticleParser(link, logManager, scheduler);
		}

		if (link.indexOf("http://watchreport.com") == 0) {
			parser = new WatchReportArticleParser(link, logManager, scheduler);
		}
		
		if (link.indexOf("http://www.chrono24.com") == 0) {
			parser = new Chrono24Parser(link, logManager, scheduler);
		}
		
		return parser;
	}
}
