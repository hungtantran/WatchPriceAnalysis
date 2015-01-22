package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;
import commonlib.Helper;

public class LinkCrawledDAOJDBC implements LinkCrawledDAO {
	private final String SQL_SELECT_BY_DOMAINID = "SELECT * FROM link_crawled_table WHERE domain_table_id_1 = ?";
	private final String SQL_SELECT_ALL = "SELECT * FROM link_crawled_table";
	private final String SQL_INSERT = "INSERT INTO link_crawled_table (link, domain_table_id_1, priority, time_crawled, date_crawled) values (?, ?, ?, ?, ?)";
	private final String SQL_UPDATE = "UPDATE link_crawled_table SET link = ?, priority = ? WHERE id = ?";
	private final String SQL_CREATE =
		"CREATE TABLE link_crawled_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "link char(255) not null, "
		+ "priority int unsigned, "
		+ "domain_table_id_1 int unsigned not null, "
		+ "time_crawled char(128) not null, "
		+ "date_crawled char(128) not null, "
		+ "PRIMARY KEY(id), "
		+ "UNIQUE (id), "
		+ "UNIQUE (link), "
		+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))";
	
	private DAOFactory daoFactory;

	public LinkCrawledDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private LinkCrawled constructLinkCrawledObject(ResultSet resultSet)
			throws SQLException {
		LinkCrawled linkCrawled = new LinkCrawled();
		
		linkCrawled.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) linkCrawled.setId(null);
		
		linkCrawled.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) linkCrawled.setLink(null);
		
		linkCrawled.setPriority(resultSet.getInt("priority"));
		if (resultSet.wasNull()) linkCrawled.setPriority(null);
		
		linkCrawled.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) linkCrawled.setDomainTableId1(null);
		
		linkCrawled.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) linkCrawled.setTimeCrawled(null);
		
		linkCrawled.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) linkCrawled.setDateCrawled(null);
		
		return linkCrawled;
	}

	@Override
	public List<LinkCrawled> get(int domainId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_DOMAINID, false, domainId);
			resultSet = preparedStatement.executeQuery();
			
			List<LinkCrawled> linksCrawled = new ArrayList<LinkCrawled>();
			while (resultSet.next()) {
				LinkCrawled linkCrawled = this.constructLinkCrawledObject(resultSet);
				linksCrawled.add(linkCrawled);
			}

			return linksCrawled;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public List<LinkCrawled> get() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();
			
			List<LinkCrawled> linksCrawled = new ArrayList<LinkCrawled>();
			while (resultSet.next()) {
				LinkCrawled linkCrawled = this.constructLinkCrawledObject(resultSet);
				linksCrawled.add(linkCrawled);
			}

			return linksCrawled;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createLinkCrawled(LinkCrawled linkCrawled) throws SQLException {
		if (linkCrawled.getLink() == null) {
			return -1;
		}

		// If the time crawled is not specified, use the current time
		if (linkCrawled.getTimeCrawled() == null || linkCrawled.getDateCrawled() == null) {
			linkCrawled.setTimeCrawled(Helper.getCurrentTime());
			linkCrawled.setDateCrawled(Helper.getCurrentDate());
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				linkCrawled.getLink(),
				linkCrawled.getDomainTableId1(),
				linkCrawled.getPriority(),
				linkCrawled.getTimeCrawled(),
				linkCrawled.getDateCrawled()
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
			Globals.crawlerLogManager.writeLog("Insert into link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public boolean update(LinkCrawled linkCrawled) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				linkCrawled.getLink(),
				linkCrawled.getPriority(),
				linkCrawled.getId()
			};
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_UPDATE, false, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Update link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
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
			Globals.crawlerLogManager.writeLog("Create link crawled relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
