package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;
import commonlib.HTMLCompressor;

public class WatchPageContentDAOJDBC implements WatchPageContentDAO {
	private final String SQL_CREATE =
		"CREATE TABLE watch_page_content_table ("
		+ "watch_table_id int unsigned not null, "
		+ "content MEDIUMTEXT not null, "
		+ "UNIQUE(watch_table_id), "
		+ "FOREIGN KEY (watch_table_id) REFERENCES watch_desc_table(id))";
	private final String SQL_SELECT_BY_WATCH_TABLE_ID = "SELECT * FROM watch_page_content_table WHERE watch_table_id = ?";
	private final String SQL_INSERT = "INSERT INTO watch_page_content_table (watch_table_id, content) values (?, ?)";
	private final String SQL_UPDATE = "UPDATE watch_page_content_table SET content = ? WHERE watch_table_id = ?";
	private final String SQL_SELECT_LIMIT = "SELECT * FROM watch_page_content_table LIMIT ?, ?";
	private final String SQL_DELETE = "DELETE FROM watch_page_content_table WHERE watch_table_id = ?";

	private final int maxResultReturned = 10;

	private final DAOFactory daoFactory;
	private List<WatchPageContent> contents = null;
	private int numContentReturned = 0;
	private int contentIndex = 0;

	public WatchPageContentDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
		this.contents = new ArrayList<WatchPageContent>();
		this.numContentReturned = 0;
		this.contentIndex = 0;
	}

	private WatchPageContent constructWatchPageContentObject(ResultSet resultSet) throws SQLException {
		WatchPageContent watchPageContent = new WatchPageContent();

		watchPageContent.setWatchTableId(resultSet.getInt("watch_table_id"));
		if (resultSet.wasNull()) {
			watchPageContent.setWatchTableId(null);
		}

		watchPageContent.setContent(resultSet.getString("content"));
		if (resultSet.wasNull()) {
			watchPageContent.setContent(null);
		}

		return watchPageContent;
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
			Globals.crawlerLogManager.writeLog("Create watch page content relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public WatchPageContent getWatchPageContent(Integer watchTableId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			Object[] values = { watchTableId };

			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_BY_WATCH_TABLE_ID, false, values);
			resultSet = preparedStatement.executeQuery();

			WatchPageContent watchPageContent = null;
			if (resultSet.next()) {
				watchPageContent = this.constructWatchPageContentObject(resultSet);
			}

			return watchPageContent;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get watch page content fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean createWatchPageContent(WatchPageContent watchPageContent) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = {
				watchPageContent.getWatchTableId(),
				HTMLCompressor.compressHtmlContent(watchPageContent.getContent())
			};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Insert watch page content " + watchPageContent.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean updateWatchPageContent(WatchPageContent watchPageContent) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = {
				watchPageContent.getContent(),
				watchPageContent.getWatchTableId()
			};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_UPDATE, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Update watch page content " + watchPageContent.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public WatchPageContent getNextWatchPageContent(boolean startFromBeginning) throws SQLException {
		if (startFromBeginning) {
			this.numContentReturned = 0;
			this.contentIndex = 0;
			this.contents.clear();
		}

		if (this.contentIndex < this.numContentReturned) {
			WatchPageContent watchPageContent = this.contents.get(this.contentIndex % this.maxResultReturned);
			++this.contentIndex;
			return watchPageContent;
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
				WatchPageContent watchPageContent = this.constructWatchPageContentObject(resultSet);
				this.contents.add(watchPageContent);
				++this.numContentReturned;
				++numFound;
			}

			if (numFound == 0) {
				return null;
			} else {
				return this.getNextWatchPageContent(false);
			}
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get watch page content fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public boolean deleteWatchPageContent(Integer watchTableId)
		throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = { watchTableId };

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_DELETE, false, values);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Delete watch page content with id " + watchTableId + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
