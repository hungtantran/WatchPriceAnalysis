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
	public static void createDB(DAOFactory daoFactory) throws ClassNotFoundException {
		try {
			DomainDAO domainDAO = new DomainDAOJDBC(daoFactory);
			domainDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			TypeDAO typeDAO = new TypeDAOJDBC(daoFactory);
			typeDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
			topicDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(daoFactory);
			typeWordDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			LinkCrawledDAO linkCrawledDAO = new LinkCrawledDAOJDBC(daoFactory);
			linkCrawledDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			LinkQueueDAO linkQueueDAO = new LinkQueueDAOJDBC(daoFactory);
			linkQueueDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ArticleDAO articleDAO = new ArticleDAOJDBC(daoFactory);
			articleDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ArticleContentDAO articleContentDAO = new ArticleContentDAOJDBC(daoFactory);
			articleContentDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ArticleTopicDAO articleTopicDAO = new ArticleTopicDAOJDBC(daoFactory);
			articleTopicDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			WatchDescDAO watchDescDAO = new WatchDescDAOJDBC(daoFactory);
			watchDescDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			WatchPageContentDAO watchPageContentDAO = new WatchPageContentDAOJDBC(daoFactory);
			watchPageContentDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			WatchPriceStatDAO watchPriceStatDAO = new WatchPriceStatDAOJDBC(daoFactory);
			watchPriceStatDAO.createRelation();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public static void main(String[] args) throws ClassNotFoundException {
		DAOFactory daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);
		InitializeDB.createDB(daoFactory);

		try {
			InitializeDB.initializeDomainTable(daoFactory, InitialValues.initialDomainsSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InitializeDB.initializeTypeTable(daoFactory, InitialValues.initialTypesSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InitializeDB.initializeTopicTable(daoFactory, InitialValues.initialTopicsSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InitializeDB.initializeTypeWordTable(daoFactory, InitialValues.initialTypeWordsSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			InitializeDB.initializeLinkQueueTable(daoFactory, InitialValues.initialLinkQueuesSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
