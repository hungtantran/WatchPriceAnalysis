package newscrawler;

import java.sql.SQLException;

import commonlib.Globals;
import commonlib.LogManager;
import daoconnection.DAOFactory;

public class Main {
	public static void main(String[] args) {
		Globals.crawlerLogManager = new LogManager("crawlerLog", "crawlerLog");
		
		DAOFactory daoFactory = null;
		
		Globals.crawlerLogManager.writeLog("Try to create a connection to database");
		try {
			daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);
		} catch (ClassNotFoundException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
			return;
		}
		Globals.crawlerLogManager.writeLog("Finish creating a connection to database");
		
		Globals.crawlerLogManager.writeLog("Try to create a scheduler to crawl job");
		try {
			Globals.scheduler = new Scheduler(daoFactory, Globals.crawlerLogManager, Globals.NUMMAXTHREADS, Globals.NUMMAXQUEUE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Globals.crawlerLogManager.writeLog("Finish creating a scheduler to crawl job");
		
		Globals.crawlerLogManager.writeLog("Try to start scheduling for crawl job");
		try {
			Globals.scheduler.start();
		} catch (ClassNotFoundException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			e.printStackTrace();
		}
		Globals.crawlerLogManager.writeLog("Finish crawling");
	}
}
