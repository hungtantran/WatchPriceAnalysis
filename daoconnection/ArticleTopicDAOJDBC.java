package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class ArticleTopicDAOJDBC implements ArticleTopicDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE article_topic_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "article_table_id int unsigned not null, "
		+ "topic_table_id int unsigned not null, "
		+ "PRIMARY KEY(id), "
		+ "UNIQUE (id), "
		+ "UNIQUE (article_table_id, topic_table_id), "
		+ "FOREIGN KEY (article_table_id) REFERENCES article_table(id), "
		+ "FOREIGN KEY (topic_table_id) REFERENCES topic_table(id))";
	private final String SQL_SELECT_BY_TOPIC_ID = "SELECT * FROM article_topic_table WHERE topic_table_id = ?";
	private final String SQL_INSERT = "INSERT INTO article_topic_table (article_table_id, topic_table_id) values (?, ?)";
	private final String SQL_DELETE = "DELETE FROM article_topic_table WHERE article_table_id = ?";
	
	private DAOFactory daoFactory;

	public ArticleTopicDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}
	
	private ArticleTopic constructArticleTopicObject(ResultSet resultSet) throws SQLException {
		ArticleTopic articleTopic = new ArticleTopic();
		
		articleTopic.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) articleTopic.setId(null);
		
		articleTopic.setArticleTableId(resultSet.getInt("article_table_id"));
		if (resultSet.wasNull()) articleTopic.setArticleTableId(null);
		
		articleTopic.setTopicTableId(resultSet.getInt("topic_table_id"));
		if (resultSet.wasNull()) articleTopic.setTopicTableId(null);
		
		return articleTopic;
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
			Globals.crawlerLogManager.writeLog("Create article topic relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<ArticleTopic> getArticleTopicByTopic(Topic topic) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object values[] = { topic.getId() };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_TOPIC_ID, false, values);
			resultSet = preparedStatement.executeQuery();
			
			List<ArticleTopic> articleTopics = new ArrayList<ArticleTopic>();
			while (resultSet.next()) {
				ArticleTopic articleTopic = this.constructArticleTopicObject(resultSet);
				articleTopics.add(articleTopic);
			}

			return articleTopics;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get topics fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createArticleTopic(ArticleTopic articleTopic) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				articleTopic.getId(),
				articleTopic.getArticleTableId(),
				articleTopic.getTopicTableId()
			};
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			ResultSet result = preparedStatement.getGeneratedKeys();
			Integer genereatedKey = null;
			if (result != null && result.next()) {
				genereatedKey = result.getInt(1);
			}
			
			return genereatedKey;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Insert article topic " + articleTopic.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean removeArticleTopicByArticleTableId(int articleTableId) throws SQLException {
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
			Globals.crawlerLogManager.writeLog("REmove article topic by article table id " + articleTableId + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
