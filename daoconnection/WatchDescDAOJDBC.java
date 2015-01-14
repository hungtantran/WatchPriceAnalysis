package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import commonlib.Globals;

public class WatchDescDAOJDBC implements WatchDescDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE watch_desc_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "link char(255) not null, "
		+ "domain_table_id_1 int unsigned not null, "
		+ "topic_table_id_1 int unsigned, "
		+ "topic_table_id_2 int unsigned, "
		+ "watch_name char(255) not null, "
		+ "price_1 int unsigned not null, "
		+ "price_2 int unsigned, "
		+ "keywords char(255), "
		+ "ref_no char(64), "
		+ "movement char(64), "
		+ "caliber char(64), "
		+ "watch_condition char(64), "
		+ "watch_year int unsigned, "
		+ "case_material char(64), "
		+ "dial_color char(64), "
		+ "gender char(64), "
		+ "location_1 char(128), "
		+ "location_2 char(128), "
		+ "location_3 char(128), "
		+ "time_created char(128) not null, "
		+ "date_created char(128) not null, "
		+ "time_crawled char(128) not null, "
		+ "date_crawled char(128) not null, "
		+ "PRIMARY KEY(id), "
		+ "UNIQUE (id), "
		+ "UNIQUE (link, price_1), "
		+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id), "
		+ "FOREIGN KEY (topic_table_id_1) REFERENCES topic_table(id), "
		+ "FOREIGN KEY (topic_table_id_2) REFERENCES topic_table(id))";
	private final String SQL_INSERT =
		"INSERT INTO watch_desc_table ("
		+ "link, domain_table_id_1, topic_table_id_1, topic_table_id_2, "
		+ "watch_name, price_1, price_2, keywords, ref_no, movement, "
		+ "caliber, watch_condition, watch_year, case_material, "
		+ "dial_color, gender, location_1, location_2, location_3, "
		+ "time_created, date_created, time_crawled, date_crawled) "
		+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private final String SQL_SELECT_BY_WATCH_DESC_ID = "SELECT * FROM watch_desc_table WHERE id = ?";
	private final String SQL_DELETE = "DELETE FROM watch_desc_table WHERE id = ?";
	private final String SQL_SELECT_LIMIT = "SELECT * FROM watch_desc_table LIMIT ?, ?";
	
	private final int maxResultReturned = 10;
	
	private DAOFactory daoFactory;
	private List<WatchDesc> watchDescs = null;
	private int numWatchDescReturned = 0;
	private int watchDescIndex = 0;
	
	public WatchDescDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}
	
	private WatchDesc constructWatchDescObject(ResultSet resultSet) throws SQLException {
		WatchDesc watchDesc = new WatchDesc();
		
		watchDesc.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) watchDesc.setId(null);
		
		watchDesc.setLink(resultSet.getString("link"));
		if (resultSet.wasNull()) watchDesc.setLink(null);
		
		watchDesc.setDomainTableId1(resultSet.getInt("domain_table_id_1"));
		if (resultSet.wasNull()) watchDesc.setDomainTableId1(null);
		
		watchDesc.setTopicTableId1(resultSet.getInt("topic_table_id_1"));
		if (resultSet.wasNull()) watchDesc.setTopicTableId1(null);
		
		watchDesc.setTopicTableId2(resultSet.getInt("topic_table_id_2"));
		if (resultSet.wasNull()) watchDesc.setLink(null);
		
		watchDesc.setWatchName(resultSet.getString("watch_name"));
		if (resultSet.wasNull()) watchDesc.setWatchName(null);
		
		watchDesc.setPrice1(resultSet.getInt("price_1"));
		if (resultSet.wasNull()) watchDesc.setPrice1(null);
		
		watchDesc.setPrice2(resultSet.getInt("price_2"));
		if (resultSet.wasNull()) watchDesc.setPrice2(null);
		
		watchDesc.setKeywords(resultSet.getString("keywords"));
		if (resultSet.wasNull()) watchDesc.setKeywords(null);
		
		watchDesc.setRefNo(resultSet.getString("ref_no"));
		if (resultSet.wasNull()) watchDesc.setRefNo(null);
		
		watchDesc.setMovement(resultSet.getString("movement"));
		if (resultSet.wasNull()) watchDesc.setMovement(null);
		
		watchDesc.setCaliber(resultSet.getString("caliber"));
		if (resultSet.wasNull()) watchDesc.setCaliber(null);
		
		watchDesc.setWatchCondition(resultSet.getString("watch_condition"));
		if (resultSet.wasNull()) watchDesc.setWatchCondition(null);
		
		watchDesc.setWatchYear(resultSet.getInt("watch_year"));
		if (resultSet.wasNull()) watchDesc.setWatchYear(null);
		
		watchDesc.setCaseMaterial(resultSet.getString("case_material"));
		if (resultSet.wasNull()) watchDesc.setCaseMaterial(null);
		
		watchDesc.setDialColor(resultSet.getString("dial_color"));
		if (resultSet.wasNull()) watchDesc.setDialColor(null);
		
		watchDesc.setGender(resultSet.getString("gender"));
		if (resultSet.wasNull()) watchDesc.setGender(null);
		
		watchDesc.setLocation1(resultSet.getString("location_1"));
		if (resultSet.wasNull()) watchDesc.setLocation1(null);
		
		watchDesc.setLocation2(resultSet.getString("location_2"));
		if (resultSet.wasNull()) watchDesc.setLocation2(null);
		
		watchDesc.setLocation3(resultSet.getString("location_3"));
		if (resultSet.wasNull()) watchDesc.setLocation3(null);
		
		watchDesc.setTimeCreated(resultSet.getString("time_created"));
		if (resultSet.wasNull()) watchDesc.setTimeCreated(null);
		
		watchDesc.setDateCreated(resultSet.getString("date_created"));
		if (resultSet.wasNull()) watchDesc.setDateCreated(null);
		
		watchDesc.setTimeCrawled(resultSet.getString("time_crawled"));
		if (resultSet.wasNull()) watchDesc.setTimeCrawled(null);
		
		watchDesc.setDateCrawled(resultSet.getString("date_crawled"));
		if (resultSet.wasNull()) watchDesc.setDateCrawled(null);
		
		return watchDesc;
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
			Globals.crawlerLogManager.writeLog("Create watch description relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public WatchDesc getWatchDesc(Integer watchDescId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			Object[] values = { watchDescId };
			
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_WATCH_DESC_ID, false, values);
			resultSet = preparedStatement.executeQuery();
			
			WatchDesc watchDesc = null;
			if (resultSet.next()) {
				watchDesc = this.constructWatchDescObject(resultSet);
			}

			return watchDesc;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get watch description fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	public Integer createWatchDesc(WatchDesc watchDesc) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				watchDesc.getLink(),
				watchDesc.getDomainTableId1(),
				watchDesc.getTopicTableId1(),
				watchDesc.getTopicTableId2(),
				watchDesc.getWatchName(),
				watchDesc.getPrice1(),
				watchDesc.getPrice2(),
				watchDesc.getKeywords(),
				watchDesc.getRefNo(),
				watchDesc.getMovement(),
				watchDesc.getMovement(),
				watchDesc.getCaliber(),
				watchDesc.getWatchCondition(),
				watchDesc.getWatchYear(),
				watchDesc.getCaseMaterial(),
				watchDesc.getDialColor(),
				watchDesc.getGender(),
				watchDesc.getLocation1(),
				watchDesc.getLocation2(),
				watchDesc.getLocation3(),
				watchDesc.getTimeCreated(),
				watchDesc.getDateCreated(),
				watchDesc.getTimeCrawled(),
				watchDesc.getDateCrawled()
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
			Globals.crawlerLogManager.writeLog("Insert watch description " + watchDesc.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
	
	@Override
	public boolean deleteWatchDesc(Integer watchDescId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = { watchDescId };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_DELETE, false, values);
			
			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Delete watch description with id " + watchDescId + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public WatchDesc getNextWatchDesc(boolean startFromBeginning) throws SQLException {
		if (startFromBeginning) {
			this.numWatchDescReturned = 0;
			this.watchDescIndex = 0;
			this.watchDescs.clear();
		}
		
		if (this.watchDescIndex < this.numWatchDescReturned) {
			WatchDesc watchPageContent = this.watchDescs.get(this.watchDescIndex % this.maxResultReturned);
			++this.watchDescIndex;
			return watchPageContent; 
		}
		
		this.watchDescs.clear();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			Object[] values = { this.watchDescIndex + 1, this.maxResultReturned };
			
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_LIMIT, false, values);
			resultSet = preparedStatement.executeQuery();
			
			int numFound = 0;
			
			while (resultSet.next()) {
				WatchDesc watchDesc = this.constructWatchDescObject(resultSet);
				this.watchDescs.add(watchDesc);
				++this.numWatchDescReturned;
				++numFound;
			}
			
			if (numFound == 0) {
				return null;
			} else {
				return this.getNextWatchDesc(false);
			}
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get watch page content fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
