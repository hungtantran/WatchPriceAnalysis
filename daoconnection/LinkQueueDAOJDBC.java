package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;
import commonlib.Helper;

public class LinkQueueDAOJDBC implements LinkQueueDAO {
	private final String SQL_SELECT_ALL = "SELECT * FROM link_queue_table";
	private final String SQL_SELECT_BY_DOMAIN_ID = "SELECT link, domain_table_id_1, priority FROM link_queue_table WHERE domain_table_id_1 = ?";
	private final String SQL_INSERT = "INSERT INTO link_queue_table (link, domain_table_id_1, priority, persistent, time_crawled, date_crawled) values (?, ?, ?, ?, ?, ?)";
	private final String SQL_DELETE = "DELETE FROM link_queue_table WHERE link = ? AND persistent = 0";
	private final String SQL_CREATE =
		"CREATE TABLE link_queue_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "link char(255) not null, "
		+ "domain_table_id_1 int unsigned not null, "
		+ "priority int unsigned, "
		+ "persistent int unsigned, "
		+ "time_crawled char(128) not null, "
		+ "date_crawled char(128) not null, "
		+ "PRIMARY KEY(id), "
		+ "UNIQUE (id), "
		+ "UNIQUE (link), "
		+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))";
		
	private DAOFactory daoFactory;

	public LinkQueueDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private LinkQueue constructLinkQueueObject(ResultSet resultSet)
			throws SQLException {
		LinkQueue linkQueue = new LinkQueue();
		
		linkQueue.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) linkQueue.setId(null);
		
		linkQueue.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) linkQueue.setLink(null);
		
		linkQueue.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) linkQueue.setDomainTableId1(null);
		
		linkQueue.setPriority(resultSet.getInt("priority"));
		if (resultSet.wasNull()) linkQueue.setPriority(null);
		
		linkQueue.setPersistent(resultSet.getInt("persistent"));
		if (resultSet.wasNull()) linkQueue.setPersistent(null);
		
		linkQueue.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) linkQueue.setTimeCrawled(null);
		
		linkQueue.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) linkQueue.setDateCrawled(null);
		
		return linkQueue;
	}

	@Override
	public List<LinkQueue> getLinksQueued() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();
			
			List<LinkQueue> linksQueue = new ArrayList<LinkQueue>();
			while (resultSet.next()) {
				LinkQueue linkQueue = this.constructLinkQueueObject(resultSet);
				linksQueue.add(linkQueue);
			}

			return linksQueue;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public List<LinkQueue> getLinksQueuedByDomain(Domain domain) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object values[] = { domain.getId() };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_DOMAIN_ID, false, values);
			resultSet = preparedStatement.executeQuery();
			
			List<LinkQueue> linksQueue = new ArrayList<LinkQueue>();
			while (resultSet.next()) {
				LinkQueue linkQueue = this.constructLinkQueueObject(resultSet);
				linksQueue.add(linkQueue);
			}

			return linksQueue;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createLinkQueue(LinkQueue linkQueue) throws SQLException {
		if (linkQueue.getLink() == null) {
			return -1;
		}

		// If the time crawled is not specified, use the current time
		if (linkQueue.getTimeCrawled() == null || linkQueue.getDateCrawled() == null) {
			linkQueue.setTimeCrawled(Helper.getCurrentTime());
			linkQueue.setDateCrawled(Helper.getCurrentDate());
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				linkQueue.getLink(),
				linkQueue.getDomainTableId1(),
				linkQueue.getPriority(),
				linkQueue.getPersistent(),
				linkQueue.getTimeCrawled(),
				linkQueue.getDateCrawled()
			};
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_INSERT, true, values);

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			
			preparedStatement.executeUpdate();

			ResultSet result = preparedStatement.getGeneratedKeys();
			Integer genereatedKey = null;
			if (result != null && result.next()) {
				genereatedKey = result.getInt(1);
			}
			
			return genereatedKey;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Insert into link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
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
			Globals.crawlerLogManager.writeLog("Create link queue relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public boolean removeLinkQueue(String link) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object values[] = { link };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_DELETE, false, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Create link queue relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
