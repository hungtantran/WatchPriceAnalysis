package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class ArticleDAOJDBC implements ArticleDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE article_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "link char(255) not null, "
		+ "domain_table_id_1 int unsigned not null, "
		+ "domain_table_id_2 int unsigned, "
		+ "domain_table_id_3 int unsigned, "
		+ "article_name char(255) not null, "
		+ "type_table_1 int unsigned not null, "
		+ "type_table_2 int unsigned, "
		+ "keywords char(255) not null, "
		+ "time_created char(128) not null, "
		+ "date_created char(128) not null, "
		+ "time_crawled char(128) not null, "
		+ "date_crawled char(128) not null, "
		+ "PRIMARY KEY(id), "
		+ "UNIQUE (id), "
		+ "UNIQUE (link), "
		+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id), "
		+ "FOREIGN KEY (domain_table_id_2) REFERENCES domain_table(id), "
		+ "FOREIGN KEY (domain_table_id_3) REFERENCES domain_table(id), "
		+ "FOREIGN KEY (type_table_1) REFERENCES type_table(id),"
		+ "FOREIGN KEY (type_table_2) REFERENCES type_table(id))";
	private final String SQL_INSERT =
		"INSERT INTO article_table ("
		+ "link, " + "domain_table_id_1, " + "domain_table_id_2, "
		+ "domain_table_id_3, " + "article_name, "
		+ "type_table_1, " + "type_table_2, " + "keywords, "
		+ "time_created, " + "date_created, " + "time_crawled, "
		+ "date_crawled) "
		+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private final String SQL_SELECT_BY_ARTICLE_TABLE_ID = "SELECT * FROM article_table WHERE article_table_id = ?";
	private final String SQL_SELECT_LIMIT = "SELECT * FROM article_table LIMIT ?, ?";
	private final String SQL_DELETE = "DELETE FROM article_table WHERE id = ?";
	
	private final int maxResultReturned = 10;
	
	private DAOFactory daoFactory;
	private List<Article> articles = null;
	private int numArticleReturned = 0;
	private int articleIndex = 0;
	
	public ArticleDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
		this.articles = new ArrayList<Article>();
		this.numArticleReturned = 0;
		this.articleIndex = 0;
	}
	
	private Article constructArticleObject(ResultSet resultSet) throws SQLException {
		Article article = new Article();
		
		article.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) article.setId(null);
		
		article.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) article.setLink(null);
		
		article.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) article.setDomainTableId1(null);
		
		article.setDomainTableId2(resultSet.getInt("domain_table_id_2"));
		if (resultSet.wasNull()) article.setDomainTableId2(null);
		
		article.setDomainTableId3(resultSet.getInt("domain_table_id_3"));
		if (resultSet.wasNull()) article.setDomainTableId3(null);
		
		article.setArticleName(resultSet.getString("article_name"));
		if (resultSet.wasNull()) article.setArticleName(null);
		
		article.setTypeTable1(resultSet.getInt("type_table_1"));
		if (resultSet.wasNull()) article.setTypeTable1(null);
		
		article.setTypeTable2(resultSet.getInt("type_table_2"));
		if (resultSet.wasNull()) article.setTypeTable2(null);
		
		article.setKeywords(resultSet.getString("keywords"));
		if (resultSet.wasNull()) article.setKeywords(null);
		
		article.setTimeCreated(resultSet.getString("time_created"));
		if (resultSet.wasNull()) article.setTimeCreated(null);
		
		article.setDateCreated(resultSet.getString("date_created"));
		if (resultSet.wasNull()) article.setDateCreated(null);
		
		article.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) article.setTimeCrawled(null);
		
		article.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) article.setDateCrawled(null);
		
		return article;
	}
	
	@Override
	public boolean createRelation() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_CREATE, false);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Create article relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Article getArticle(Integer articleTableId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			Object[] values = { articleTableId };
			
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_ARTICLE_TABLE_ID, false, values);
			resultSet = preparedStatement.executeQuery();
			
			Article article = null;
			if (resultSet.next()) {
				article = this.constructArticleObject(resultSet);
			}

			return article;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get article fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	public Integer createArticle(Article article) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				article.getLink(),
				article.getDomainTableId1(),
				article.getDomainTableId2(),
				article.getDomainTableId3(),
				article.getArticleName(),
				article.getTypeTable1(),
				article.getTypeTable2(),
				article.getKeywords(),
				article.getTimeCreated(),
				article.getDateCreated(),
				article.getTimeCrawled(),
				article.getDateCrawled()
			};
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			// Insert into article_topic_table table
			/* for (int topicId : topicsId) {
				this.addArticleTopicRelationship(articleId, topicId);
			} */
			
			ResultSet result = preparedStatement.getGeneratedKeys();
			Integer genereatedKey = null;
			if (result != null && result.next()) {
				genereatedKey = result.getInt(1);
			}
			
			return genereatedKey;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Insert article " + article.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Article getNextArticle(boolean startFromBeginning) throws SQLException {
		if (startFromBeginning) {
			this.numArticleReturned = 0;
			this.articleIndex = 0;
			this.articles.clear();
		}
		
		if (this.articleIndex < this.numArticleReturned) {
			Article article = this.articles.get(this.articleIndex % this.maxResultReturned);
			++this.articleIndex;
			return article; 
		}
		
		this.articles.clear();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			Object[] values = { this.articleIndex + 1, this.maxResultReturned };
			
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_LIMIT, false, values);
			resultSet = preparedStatement.executeQuery();
			
			int numFound = 0;
			
			while (resultSet.next()) {
				Article article = this.constructArticleObject(resultSet);
				this.articles.add(article);
				++this.numArticleReturned;
				++numFound;
			}
			
			if (numFound == 0) {
				return null;
			} else {
				return this.getNextArticle(false);
			}
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get article fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public boolean deleteArticle(Integer articleTableId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = { articleTableId };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_DELETE, false, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Delete article with id " + articleTableId + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
