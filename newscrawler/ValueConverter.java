package newscrawler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commonlib.Globals;
import daoconnection.DAOFactory;
import daoconnection.Domain;
import daoconnection.DomainDAO;
import daoconnection.DomainDAOJDBC;
import daoconnection.Topic;
import daoconnection.TopicDAO;
import daoconnection.TopicDAOJDBC;
import daoconnection.Type;
import daoconnection.TypeDAO;
import daoconnection.TypeDAOJDBC;

public class ValueConverter {
	private Map<Integer, String> idTypeMap = null;
	private Map<Integer, String> idDomainMap = null;
	private Map<Integer, String> idTopicMap = null;
	
	private Map<String, Integer> typeIdMap = null;
	private Map<String, Integer> domainIdMap = null;
	private Map<String, Integer> topicIdMap = null;
	
	// Populate information of type, domain and topic information from existing database
	private boolean populateMap(DAOFactory daoFactory) throws SQLException {
		if (!this.getTypeInfo(daoFactory)) {
			return false;
		}

		if (!this.getDomainInfo(daoFactory)) {
			return false;
		}

		if (!this.getTopicInfo(daoFactory)) {
			return false;
		}

		return true;
	}

	private boolean getDomainInfo(DAOFactory daoFactory) throws SQLException {
		this.idDomainMap = new HashMap<Integer, String>();
		this.domainIdMap = new HashMap<String, Integer>();
		
		DomainDAO domainDAO = new DomainDAOJDBC(daoFactory);
		List<Domain> domainList = domainDAO.getDomains();
		
		for (Domain domain : domainList) {
			this.idDomainMap.put(domain.getId(), domain.getDomain());
			this.domainIdMap.put(domain.getDomain(), domain.getId());
		}

		if (Globals.DEBUG) {
			Globals.crawlerLogManager.writeLog("Got " + this.idDomainMap.size() + " domains");
		}

		return true;
	}

	private boolean getTypeInfo(DAOFactory daoFactory) throws SQLException {
		this.idTypeMap = new HashMap<Integer, String>();
		this.typeIdMap = new HashMap<String, Integer>();
		
		TypeDAO tyepDAO = new TypeDAOJDBC(daoFactory);
		List<Type> typeList = tyepDAO.getTypes();
		
		for (Type type : typeList) {
			this.idTypeMap.put(type.getId(), type.getType());
			this.typeIdMap.put(type.getType(), type.getId());
		}
		
		if (Globals.DEBUG) {
			Globals.crawlerLogManager.writeLog("Got " + this.idTypeMap.size() + " types");
		}

		return true;
	}

	private boolean getTopicInfo(DAOFactory daoFactory) throws SQLException {
		this.idTopicMap = new HashMap<Integer, String>();
		this.topicIdMap = new HashMap<String, Integer>();
		
		TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
		List<Topic> topicList = topicDAO.getTopics();
		
		for (Topic topic : topicList) {
			this.idTopicMap.put(topic.getId(), topic.getTopic());
			this.topicIdMap.put(topic.getTopic(), topic.getId());
		}
		
		if (Globals.DEBUG) {
			Globals.crawlerLogManager.writeLog("Got " + this.idTopicMap.size() + " topics");
		}

		return true;
	}
	
	// Convert topic to topic id
	public Integer[] convertTopicToTopicId(String[] topics) {
		if (this.idTopicMap == null) {
			return null;
		}
		
		Integer[] topicsId = new Integer[topics.length];

		// Get the id of topics
		for (int i = 0; i < topics.length; i++) {
			topicsId[i] = this.topicIdMap.get(topics[i]);
		}

		return topicsId;
	}
	
	public ValueConverter(DAOFactory daoFactory) throws Exception {
		if (this.populateMap(daoFactory)) {
			throw new Exception("Can't populate value maps");
		}
	}
}
