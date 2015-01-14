package dbconnection;

import java.sql.SQLException;

import commonlib.Globals;

import daoconnection.ArticleContentDAO;
import daoconnection.ArticleContentDAOJDBC;
import daoconnection.ArticleDAO;
import daoconnection.ArticleDAOJDBC;
import daoconnection.ArticleTopicDAO;
import daoconnection.ArticleTopicDAOJDBC;
import daoconnection.DAOFactory;
import daoconnection.DomainDAO;
import daoconnection.DomainDAOJDBC;
import daoconnection.LinkCrawledDAO;
import daoconnection.LinkCrawledDAOJDBC;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;
import daoconnection.TopicDAO;
import daoconnection.TopicDAOJDBC;
import daoconnection.TypeDAO;
import daoconnection.TypeDAOJDBC;
import daoconnection.WatchDescDAO;
import daoconnection.WatchDescDAOJDBC;
import daoconnection.WatchPageContentDAO;
import daoconnection.WatchPageContentDAOJDBC;
import daoconnection.WatchPriceStatDAO;
import daoconnection.WatchPriceStatDAOJDBC;

public class InitializeDB {
	public InitializeDB() {
	}

	// Create all the tables
	public static void createDB(String username, String password, String server, String database) throws ClassNotFoundException, SQLException {
		DomainDAO domainDAO = new DomainDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		domainDAO.createRelation();
		
		TypeDAO typeDAO = new TypeDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		typeDAO.createRelation();
		
		TopicDAO topicDAO = new TopicDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		topicDAO.createRelation();
		
		ArticleDAO articleDAO = new ArticleDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		articleDAO.createRelation();
		
		ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		articleContentDAO.createRelation();
		
		ArticleTopicDAO articleTopicDAO = new ArticleTopicDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		articleTopicDAO.createRelation();
		
		WatchDescDAO watchDescDAO = new WatchDescDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		watchDescDAO.createRelation();
		
		WatchPageContentDAO watchPageContentDAO = new WatchPageContentDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		watchPageContentDAO.createRelation();
		
		WatchPriceStatDAO watchPriceStatDAO = new WatchPriceStatDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		watchPriceStatDAO.createRelation();
		
		LinkCrawledDAO linkCrawledDAO = new LinkCrawledDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		linkCrawledDAO.createRelation();
		
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		linkQueueDAO.createRelation();
	}

	// Initialize the tables with initial data
	public static void initializeDomainTable() {
	}
	
	public static void initializeTypeTable() {
	}
	
	public static void initializeTopicTable() {
	}
	
	public static void initializeLinkQueueTable() {
		// Iteratate through each type to get the list of topics of that type
//		for (Map.Entry<String, Globals.Domain> entry : Globals.startUrlDomainMap
//				.entrySet()) {
//			String startUrl = entry.getKey();
//			Globals.Domain[] domain = { entry.getValue() };
//			Integer[] domainIds = Helper.convertDomainToDomainId(domain);
//			if (domainIds == null)
//				return;
//
//			try {
//				PreparedStatement stmt = null;
//				stmt = this.con.prepareStatement("INSERT INTO link_queue_table (link, domain_table_id_1, priority, persistent, time_crawled, date_crawled) values (?, ?, ?, ?, ?, ?)");
//				stmt.setString(1, startUrl); // link
//				stmt.setInt(2, domainIds[0]); // domain_table_id
//				stmt.setInt(3, 0); // priority
//				stmt.setInt(4, 1); // persistent
//				stmt.setString(5, "00:00:00"); // time crawled
//				stmt.setString(6, "0000-00-00"); // date crawled
//				stmt.executeUpdate();
//
//				Globals.crawlerLogManager.writeLog(stmt.toString());
//			} catch (SQLException e) {
//				System.out.println("Fail to insert startUrl '" + startUrl + "' with domain " + domainIds[0] + " into link_queue_table");
//			}
//		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		InitializeDB.createDB(Globals.username, Globals.password, Globals.server, Globals.database);
		InitializeDB.initializeDomainTable();
		InitializeDB.initializeTypeTable();
		InitializeDB.initializeTopicTable();
		InitializeDB.initializeLinkQueueTable();
	}
}
