package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;
import commonlib.HTMLCompressor;

public class ArticleContentDAOJDBC implements ArticleContentDAO {
	private final String SQL_CREATE =
		"CREATE TABLE article_content_table ("
		+ "article_table_id int unsigned not null, "
		+ "content MEDIUMTEXT not null, "
		+ "UNIQUE(article_table_id), "
		+ "FOREIGN KEY (article_table_id) REFERENCES article_table(id))";
	private final String SQL_SELECT_BY_ARTICLE_TABLE_ID = "SELECT * FROM article_content_table WHERE article_table_id = ?";
	private final String SQL_INSERT = "INSERT INTO article_content_table (article_table_id, content) values (?, ?)";
	private final String SQL_UPDATE = "UPDATE article_content_table SET content = ? WHERE article_table_id = ?";
	private final String SQL_SELECT_LIMIT = "SELECT * FROM article_content_table LIMIT ?, ?";
	private final String SQL_DELETE = "DELETE FROM article_content_table WHERE article_table_id = ?";

	private final int maxResultReturned = 10;

	private final DAOFactory daoFactory;
	private List<ArticleContent> contents = null;
	private int numContentReturned = 0;
	private int contentIndex = 0;

	public ArticleContentDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
		this.contents = new ArrayList<ArticleContent>();
		this.numContentReturned = 0;
		this.contentIndex = 0;
	}

	private ArticleContent constructArticleContentObject(ResultSet resultSet) throws SQLException {
		ArticleContent articleContent = new ArticleContent();

		articleContent.setArticleTableId(resultSet.getInt("article_table_id"));
		if (resultSet.wasNull()) {
			articleContent.setArticleTableId(null);
		}

		articleContent.setContent(resultSet.getString("content"));
		if (resultSet.wasNull()) {
			articleContent.setContent(null);
		}

		return articleContent;
	}

	@Override
	public boolean createRelation() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_CREATE, false);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Create article content relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public ArticleContent getArticleContent(Integer articleTableId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			Object[] values = { articleTableId };

			connection = this.daoFactory.getConnection();

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_BY_ARTICLE_TABLE_ID, false, values);
			resultSet = preparedStatement.executeQuery();

			ArticleContent articleContent = null;
			if (resultSet.next()) {
				articleContent = this.constructArticleContentObject(resultSet);
			}

			return articleContent;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get article content fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean createArticleContent(ArticleContent articleContent) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = {
				articleContent.getArticleTableId(),
				HTMLCompressor.compressHtmlContent(articleContent.getContent())
			};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Insert article content " + articleContent.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean updateArticleContent(ArticleContent articleContent) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = {
				articleContent.getContent(),
				articleContent.getArticleTableId()
			};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_UPDATE, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Update article content " + articleContent.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public ArticleContent getNextArticleContent(boolean startFromBeginning) throws SQLException {
		if (startFromBeginning) {
			this.numContentReturned = 0;
			this.contentIndex = 0;
			this.contents.clear();
		}

		if (this.contentIndex < this.numContentReturned) {
			ArticleContent articleContent = this.contents.get(this.contentIndex % this.maxResultReturned);
			++this.contentIndex;
			return articleContent;
		}

		this.contents.clear();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			Object[] values = { this.contentIndex + 1, this.maxResultReturned };

			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_LIMIT, false, values);
			resultSet = preparedStatement.executeQuery();

			int numFound = 0;

			while (resultSet.next()) {
				ArticleContent articleContent = this.constructArticleContentObject(resultSet);
				this.contents.add(articleContent);
				++this.numContentReturned;
				++numFound;
			}

			if (numFound == 0) {
				return null;
			} else {
				return this.getNextArticleContent(false);
			}
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get article content fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean deleteArticleContent(Integer articleTableId)
		throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = { articleTableId };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_DELETE, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Delete article content with id " + articleTableId + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
