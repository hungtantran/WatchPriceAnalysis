package initializeDB;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import commonlib.Globals;
import daoconnection.ArticleContentDAO;
import daoconnection.ArticleContentDAOJDBC;
import daoconnection.ArticleDAO;
import daoconnection.ArticleDAOJDBC;
import daoconnection.ArticleTopicDAO;
import daoconnection.ArticleTopicDAOJDBC;
import daoconnection.DAOFactory;
import daoconnection.Domain;
import daoconnection.DomainDAO;
import daoconnection.DomainDAOJDBC;
import daoconnection.LinkCrawledDAO;
import daoconnection.LinkCrawledDAOJDBC;
import daoconnection.LinkQueue;
import daoconnection.LinkQueueDAO;
import daoconnection.LinkQueueDAOJDBC;
import daoconnection.Topic;
import daoconnection.TopicDAO;
import daoconnection.TopicDAOJDBC;
import daoconnection.Type;
import daoconnection.TypeDAO;
import daoconnection.TypeDAOJDBC;
import daoconnection.TypeWord;
import daoconnection.TypeWordDAO;
import daoconnection.TypeWordDAOJDBC;
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
		
		TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		typeWordDAO.createRelation();
		
		LinkCrawledDAO linkCrawledDAO = new LinkCrawledDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		linkCrawledDAO.createRelation();
		
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		linkQueueDAO.createRelation();
		
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
	}

	// Initialize the tables with initial data
	public static void initializeDomainTable(String username, String password, String server, String database, Set<Domain> initialDomainsSet)
			throws ClassNotFoundException, SQLException {
		DomainDAO domainDAO = new DomainDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		
		for (Iterator<Domain> it = initialDomainsSet.iterator(); it.hasNext(); ) {
			domainDAO.createDomain(it.next());
		}
	}
	
	public static void initializeTypeTable(String username, String password, String server, String database, Set<Type> initialTypesSet)
			throws ClassNotFoundException, SQLException {
		TypeDAO typeDAO = new TypeDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		
		for (Iterator<Type> it = initialTypesSet.iterator(); it.hasNext(); ) {
			typeDAO.createType(it.next());
		}
	}
	
	public static void initializeTopicTable(String username, String password, String server, String database, Set<Topic> initialTopicsSet)
			throws ClassNotFoundException, SQLException {
		TopicDAO topicDAO = new TopicDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		
		for (Iterator<Topic> it = initialTopicsSet.iterator(); it.hasNext(); ) {
			topicDAO.createTopic(it.next());
		}
	}
	
	public static void initializeTypeWordTable(String username, String password, String server, String database, Set<TypeWord> initialTypeWordsSet)
			throws ClassNotFoundException, SQLException {
		TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		
		for (Iterator<TypeWord> it = initialTypeWordsSet.iterator(); it.hasNext(); ) {
			typeWordDAO.createTypeWord(it.next());
		}
	}
	
	public static void initializeLinkQueueTable(String username, String password, String server, String database, Set<LinkQueue> initialLinkQueuesSet)
			throws ClassNotFoundException, SQLException {
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(DAOFactory.getInstance(username, password, server + database));
		
		for (Iterator<LinkQueue> it = initialLinkQueuesSet.iterator(); it.hasNext(); ) {
			linkQueueDAO.createLinkQueue(it.next());
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		InitializeDB.createDB(Globals.username, Globals.password, Globals.server, Globals.database);
		
		InitializeDB.initializeDomainTable(Globals.username, Globals.password, Globals.server, Globals.database, InitialValues.initialDomainsSet);
		InitializeDB.initializeTypeTable(Globals.username, Globals.password, Globals.server, Globals.database, InitialValues.initialTypesSet);
		InitializeDB.initializeTopicTable(Globals.username, Globals.password, Globals.server, Globals.database, InitialValues.initialTopicsSet);
		InitializeDB.initializeTypeWordTable(Globals.username, Globals.password, Globals.server, Globals.database, InitialValues.initialTypeWordsSet);
		InitializeDB.initializeLinkQueueTable(Globals.username, Globals.password, Globals.server, Globals.database, InitialValues.initialLinkQueuesSet);
	}
}
