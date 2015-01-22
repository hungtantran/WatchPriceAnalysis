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
	public static void createDB(DAOFactory daoFactory) throws ClassNotFoundException, SQLException {
		DomainDAO domainDAO = new DomainDAOJDBC(daoFactory);
		domainDAO.createRelation();
		
		TypeDAO typeDAO = new TypeDAOJDBC(daoFactory);
		typeDAO.createRelation();
		
		TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
		topicDAO.createRelation();
		
		TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(daoFactory);
		typeWordDAO.createRelation();
		
		LinkCrawledDAO linkCrawledDAO = new LinkCrawledDAOJDBC(daoFactory);
		linkCrawledDAO.createRelation();
		
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
		linkQueueDAO.createRelation();
		
		ArticleDAO articleDAO = new ArticleDAOJDBC(daoFactory);
		articleDAO.createRelation();
		
		ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(daoFactory);
		articleContentDAO.createRelation();
		
		ArticleTopicDAO articleTopicDAO = new ArticleTopicDAOJDBC(daoFactory);
		articleTopicDAO.createRelation();
		
		WatchDescDAO watchDescDAO = new WatchDescDAOJDBC(daoFactory);
		watchDescDAO.createRelation();
		
		WatchPageContentDAO watchPageContentDAO = new WatchPageContentDAOJDBC(daoFactory);
		watchPageContentDAO.createRelation();
		
		WatchPriceStatDAO watchPriceStatDAO = new WatchPriceStatDAOJDBC(daoFactory);
		watchPriceStatDAO.createRelation();
	}

	// Initialize the tables with initial data
	public static void initializeDomainTable(DAOFactory daoFactory, Set<Domain> initialDomainsSet)
			throws ClassNotFoundException, SQLException {
		DomainDAO domainDAO = new DomainDAOJDBC(daoFactory);
		
		for (Iterator<Domain> it = initialDomainsSet.iterator(); it.hasNext(); ) {
			domainDAO.createDomain(it.next());
		}
	}
	
	public static void initializeTypeTable(DAOFactory daoFactory, Set<Type> initialTypesSet)
			throws ClassNotFoundException, SQLException {
		TypeDAO typeDAO = new TypeDAOJDBC(daoFactory);
		
		for (Iterator<Type> it = initialTypesSet.iterator(); it.hasNext(); ) {
			typeDAO.createType(it.next());
		}
	}
	
	public static void initializeTopicTable(DAOFactory daoFactory, Set<Topic> initialTopicsSet)
			throws ClassNotFoundException, SQLException {
		TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
		
		for (Iterator<Topic> it = initialTopicsSet.iterator(); it.hasNext(); ) {
			topicDAO.createTopic(it.next());
		}
	}
	
	public static void initializeTypeWordTable(DAOFactory daoFactory, Set<TypeWord> initialTypeWordsSet)
			throws ClassNotFoundException, SQLException {
		TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(daoFactory);
		
		for (Iterator<TypeWord> it = initialTypeWordsSet.iterator(); it.hasNext(); ) {
			typeWordDAO.createTypeWord(it.next());
		}
	}
	
	public static void initializeLinkQueueTable(DAOFactory daoFactory, Set<LinkQueue> initialLinkQueuesSet)
			throws ClassNotFoundException, SQLException {
		LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
		
		for (Iterator<LinkQueue> it = initialLinkQueuesSet.iterator(); it.hasNext(); ) {
			linkQueueDAO.createLinkQueue(it.next());
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DAOFactory daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);
		InitializeDB.createDB(daoFactory);
		
		InitializeDB.initializeDomainTable(daoFactory, InitialValues.initialDomainsSet);
		InitializeDB.initializeTypeTable(daoFactory, InitialValues.initialTypesSet);
		InitializeDB.initializeTopicTable(daoFactory, InitialValues.initialTopicsSet);
		InitializeDB.initializeTypeWordTable(daoFactory, InitialValues.initialTypeWordsSet);
		InitializeDB.initializeLinkQueueTable(daoFactory, InitialValues.initialLinkQueuesSet);
	}
}
