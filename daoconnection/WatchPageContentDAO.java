package daoconnection;

import java.sql.SQLException;

public interface WatchPageContentDAO {
	public boolean createRelation() throws SQLException;
	
	public WatchPageContent getWatchPageContent(Integer watchTableId) throws SQLException;
	
	public WatchPageContent getNextWatchPageContent(boolean startFromBeginning) throws SQLException;
	
	public boolean createWatchPageContent(WatchPageContent watchPageContent) throws SQLException;
	
	public boolean updateWatchPageContent(WatchPageContent watchPageContent) throws SQLException;
	
	public boolean deleteWatchPageContent(Integer watchTableId) throws SQLException;
}
