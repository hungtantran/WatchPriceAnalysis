package newscrawler;

import commonlib.Globals;
import commonlib.LogManager;

import daoconnection.DAOFactory;

public class Main {
	public static void main(String[] args) {
		Globals.crawlerLogManager = new LogManager("crawlerLog", "crawlerLog");
		
		Globals.crawlerLogManager.writeLog("Try to create a connection to database");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);
		} catch (ClassNotFoundException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
			return;
		}
		Globals.crawlerLogManager.writeLog("Finish creating a connection to database");
		
		Globals.crawlerLogManager.writeLog("Try to create a parser factory");
		CrawlerParserFactory parserFactory = null;
		try {
			parserFactory = new CrawlerParserFactory(daoFactory);
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
			return;
		}
		Globals.crawlerLogManager.writeLog("Finish creating a parser factory");
		
		Globals.crawlerLogManager.writeLog("Try to create a scheduler to crawl job");
		try {
			Globals.scheduler = new Scheduler(daoFactory, Globals.crawlerLogManager, parserFactory, Globals.NUMMAXTHREADS, Globals.NUMMAXQUEUE);
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
			return;
		}
		Globals.crawlerLogManager.writeLog("Finish creating a scheduler to crawl job");
		
		Globals.crawlerLogManager.writeLog("Try to start scheduling for crawl job");
		try {
			Globals.scheduler.start();
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
			return;
		}
		Globals.crawlerLogManager.writeLog("Finish crawling");
	}
}
