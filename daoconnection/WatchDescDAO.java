package daoconnection;

import java.sql.SQLException;

public interface WatchDescDAO {
	public boolean createRelation() throws SQLException;
	
	public Integer createWatchDesc(WatchDesc watchDesc) throws SQLException;

	public WatchDesc getWatchDesc(Integer WatchDescId) throws SQLException;

	public boolean deleteWatchDesc(Integer WatchDescId) throws SQLException;
	
	public WatchDesc getNextWatchDesc(boolean startFromBeginning) throws SQLException;
}
