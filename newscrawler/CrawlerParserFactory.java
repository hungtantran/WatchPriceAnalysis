package newscrawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import commonlib.LogManager;

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
import daoconnection.TypeWord;
import daoconnection.TypeWordDAO;
import daoconnection.TypeWordDAOJDBC;

public class CrawlerParserFactory {
	private Set<String> typeWords = null;
	private String[] topics = null;
	private Map<String, Domain> domainStringToDomainMap = null;
	private Map<String, Type> typeStringToTypeMap = null;
	
	public CrawlerParserFactory(DAOFactory daoFactory) throws Exception {
		if (daoFactory == null) {
			throw new Exception("Invalid argument");
		}
		
		TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(daoFactory);
		TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
		DomainDAO domainDAO = new DomainDAOJDBC(daoFactory);
		TypeDAO typeDAO = new TypeDAOJDBC(daoFactory);
		
		List<TypeWord> typeWordList = typeWordDAO.getTypeWords();
		List<Topic> topicList = topicDAO.getTopics();
		List<Domain> domainList = domainDAO.getDomains();
		List<Type> typeList = typeDAO.getTypes();
		
		this.topics = new String[topicList.size()];
		for (int i = 0; i < topics.length; ++i) {
			this.topics[i] = topicList.get(i).getTopic();
		}
		
		this.typeWords = new HashSet<String>();
		for (int i = 0; i < typeWordList.size(); ++i) {
			this.typeWords.add(typeWordList.get(i).getTypeWord());
		}
		
		this.domainStringToDomainMap = new HashMap<String, Domain>();
		for (Domain domain : domainList) {
			this.domainStringToDomainMap.put(domain.getDomain(), domain);
		}
		
		this.typeStringToTypeMap = new HashMap<String, Type>();
		for (Type type : typeList) {
			this.typeStringToTypeMap.put(type.getType(), type);
		}
	}
	
	// TODO make this function general
	public BaseParser getParser(String link, BaseCrawler crawler, LogManager logManager, Scheduler scheduler) throws Exception {
		BaseParser parser = null;
		
		if (link == null) {
			return parser;
		}
		
		if (link.indexOf("http://www.ablogtowatch.com") == 0) {
			parser = new ABlogToWatchArticleParser(link, crawler, logManager, scheduler, this.topics, this.typeWords, this.domainStringToDomainMap, this.typeStringToTypeMap);
		}
		
		if (link.indexOf("http://www.hodinkee.com") == 0) {
			parser = new HodinkeeArticleParser(link, crawler, logManager, scheduler, this.topics, this.typeWords, this.domainStringToDomainMap, this.typeStringToTypeMap);
		}

		if (link.indexOf("http://watchreport.com") == 0) {
			parser = new WatchReportArticleParser(link, crawler, logManager, scheduler, this.topics, this.typeWords, this.domainStringToDomainMap, this.typeStringToTypeMap);
		}
		
		if (link.indexOf("http://www.chrono24.com") == 0) {
			parser = new Chrono24EntryPageParser(link, crawler, logManager, scheduler, this.topics, this.typeWords, this.domainStringToDomainMap, this.typeStringToTypeMap);
		}
		
		return parser;
	}
}
