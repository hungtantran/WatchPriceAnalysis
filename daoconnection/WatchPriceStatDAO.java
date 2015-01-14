package daoconnection;

import java.sql.SQLException;

public interface WatchPriceStatDAO {
	public boolean createRelation() throws SQLException;
	
	public Integer createWatchPriceStat(WatchPriceStat watchPriceStat);
}
