package newscrawler;

import commonlib.Globals;
import commonlib.LogManager;
import daoconnection.DAOFactory;

public class CrawlerParserFactory {
	private DAOFactory daoFactory = null;
	
	public CrawlerParserFactory(DAOFactory daoFactory) throws Exception {
		if (daoFactory == null) {
			throw new Exception("Invalid argument");
		}
		
		this.daoFactory = daoFactory;
	}
	
	// TODO make this function general
	public BaseParser getParser(String link, LogManager logManager, Scheduler scheduler) throws Exception {
		if (daoFactory == null) {
			throw new Exception("Unexpected, there is no database connection provided");
		}
		
		BaseParser parser = null;
		
		if (link == null) {
			return parser;
		}
		
		if (link.indexOf("http://www.ablogtowatch.com") == 0) {
			parser = new ABlogToWatchArticleParser(link, logManager, scheduler, daoFactory);
		}
		
		if (link.indexOf("http://www.hodinkee.com") == 0) {
			parser = new HodinkeeArticleParser(link, logManager, scheduler, daoFactory);
		}

		if (link.indexOf("http://watchreport.com") == 0) {
			parser = new WatchReportArticleParser(link, logManager, scheduler, daoFactory);
		}
		
		if (link.indexOf("http://www.chrono24.com") == 0) {
			parser = new Chrono24Parser(link, logManager, scheduler, daoFactory);
		}
		
		return parser;
	}
}
