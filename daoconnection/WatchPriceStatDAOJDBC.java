package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import commonlib.Globals;

public class WatchPriceStatDAOJDBC implements WatchPriceStatDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE watch_price_stat_table ("
		+ "topic_table_id int unsigned not null, "
		+ "number_of_articles int unsigned not null, "
		+ "number_of_watches int unsigned not null, "
		+ "lowest_price int unsigned not null, "
		+ "highest_price int unsigned not null, "
		+ "mean_price int unsigned not null, "
		+ "median_price int unsigned not null, "
		+ "standard_deviation_price float unsigned not null, "
		+ "1_5th_price int unsigned not null, "
		+ "1_5th_number int unsigned not null, "
		+ "2_5th_price int unsigned not null, "
		+ "2_5th_number int unsigned not null, "
		+ "3_5th_price int unsigned not null, "
		+ "3_5th_number int unsigned not null, "
		+ "4_5th_price int unsigned not null, "
		+ "4_5th_number int unsigned not null, "
		+ "5_5th_price int unsigned not null, "
		+ "5_5th_number int unsigned not null, "
		+ "6_5th_price int unsigned not null, "
		+ "6_5th_number int unsigned not null, "
		+ "7_5th_price int unsigned not null, "
		+ "7_5th_number int unsigned not null, "
		+ "8_5th_price int unsigned not null, "
		+ "8_5th_number int unsigned not null, "
		+ "9_5th_price int unsigned not null, "
		+ "9_5th_number int unsigned not null, "
		+ "10_5th_price int unsigned not null, "
		+ "10_5th_number int unsigned not null, "
		+ "11_5th_price int unsigned not null, "
		+ "11_5th_number int unsigned not null, "
		+ "12_5th_price int unsigned not null, "
		+ "12_5th_number int unsigned not null, "
		+ "13_5th_price int unsigned not null, "
		+ "13_5th_number int unsigned not null, "
		+ "14_5th_price int unsigned not null, "
		+ "14_5th_number int unsigned not null, "
		+ "15_5th_price int unsigned not null, "
		+ "15_5th_number int unsigned not null, "
		+ "16_5th_price int unsigned not null, "
		+ "16_5th_number int unsigned not null, "
		+ "17_5th_price int unsigned not null, "
		+ "17_5th_number int unsigned not null, "
		+ "18_5th_price int unsigned not null, "
		+ "18_5th_number int unsigned not null, "
		+ "19_5th_price int unsigned not null, "
		+ "19_5th_number int unsigned not null, "
		+ "20_5th_price int unsigned not null, "
		+ "20_5th_number int unsigned not null, "
		+ "UNIQUE(topic_table_id), "
		+ "FOREIGN KEY (topic_table_id) REFERENCES topic_table(id))";
	
	private final String SQL_INSERT = 
		"INSERT INTO watch_price_stat_table ("
		+ "topic_table_id, number_of_articles, "
		+ "number_of_watches, lowest_price, "
		+ "highest_price, mean_price, median_price, "
		+ "standard_deviation_price"
		+ ", 1_5th_price, 1_5th_number"
		+ ", 2_5th_price, 2_5th_number"
		+ ", 3_5th_price, 3_5th_number"
		+ ", 4_5th_price, 4_5th_number"
		+ ", 5_5th_price, 5_5th_number"
		+ ", 6_5th_price, 6_5th_number"
		+ ", 7_5th_price, 7_5th_number"
		+ ", 8_5th_price, 8_5th_number"
		+ ", 9_5th_price, 9_5th_number"
		+ ", 10_5th_price, 10_5th_number"
		+ ", 11_5th_price, 11_5th_number"
		+ ", 12_5th_price, 12_5th_number"
		+ ", 13_5th_price, 13_5th_number"
		+ ", 14_5th_price, 14_5th_number"
		+ ", 15_5th_price, 15_5th_number"
		+ ", 16_5th_price, 16_5th_number"
		+ ", 17_5th_price, 17_5th_number"
		+ ", 18_5th_price, 18_5th_number"
		+ ", 19_5th_price, 19_5th_number"
		+ ", 20_5th_price, 20_5th_number)"
		+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
		+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
		+ ", ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private DAOFactory daoFactory;
	
	public WatchPriceStatDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
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
			Globals.crawlerLogManager.writeLog("Create watch price stat relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createWatchPriceStat(WatchPriceStat watchPriceStat) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				watchPriceStat.getNumberOfArticles(),
				watchPriceStat.getNumberOfWatches(),
				watchPriceStat.getLowestPrice(),
				watchPriceStat.getHighestPrice(),
				watchPriceStat.getMeanPrice(),
				watchPriceStat.getMedianPrice(),
				watchPriceStat.getStandardDeviationPrice(),
				watchPriceStat.get_1_5thPrice(),
				watchPriceStat.get_1_5thNumber(),
				watchPriceStat.get_2_5thPrice(),
				watchPriceStat.get_2_5thNumber(),
				watchPriceStat.get_3_5thPrice(),
				watchPriceStat.get_3_5thNumber(),
				watchPriceStat.get_4_5thPrice(),
				watchPriceStat.get_4_5thNumber(),
				watchPriceStat.get_5_5thPrice(),
				watchPriceStat.get_5_5thNumber(),
				watchPriceStat.get_6_5thPrice(),
				watchPriceStat.get_6_5thNumber(),
				watchPriceStat.get_7_5thPrice(),
				watchPriceStat.get_7_5thNumber(),
				watchPriceStat.get_8_5thPrice(),
				watchPriceStat.get_8_5thNumber(),
				watchPriceStat.get_9_5thPrice(),
				watchPriceStat.get_9_5thNumber(),
				watchPriceStat.get_10_5thPrice(),
				watchPriceStat.get_10_5thNumber(),
				watchPriceStat.get_11_5thPrice(),
				watchPriceStat.get_11_5thNumber(),
				watchPriceStat.get_12_5thPrice(),
				watchPriceStat.get_12_5thNumber(),
				watchPriceStat.get_13_5thPrice(),
				watchPriceStat.get_13_5thNumber(),
				watchPriceStat.get_14_5thPrice(),
				watchPriceStat.get_14_5thNumber(),
				watchPriceStat.get_15_5thPrice(),
				watchPriceStat.get_15_5thNumber(),
				watchPriceStat.get_16_5thPrice(),
				watchPriceStat.get_16_5thNumber(),
				watchPriceStat.get_17_5thPrice(),
				watchPriceStat.get_17_5thNumber(),
				watchPriceStat.get_18_5thPrice(),
				watchPriceStat.get_18_5thNumber(),
				watchPriceStat.get_19_5thPrice(),
				watchPriceStat.get_19_5thNumber(),
				watchPriceStat.get_20_5thPrice(),
				watchPriceStat.get_20_5thNumber(),
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
			Globals.crawlerLogManager.writeLog("Insert watch price stat " + watchPriceStat.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
