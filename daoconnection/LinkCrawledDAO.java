package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface LinkCrawledDAO {
	public boolean createRelation() throws SQLException;
	
	public List<LinkCrawled> get(int domainId) throws SQLException;
	
	public List<LinkCrawled> get() throws SQLException;
	
	public Integer createLinkCrawled(LinkCrawled linkCrawled) throws SQLException;
	
	public boolean update(LinkCrawled linkCrawled) throws SQLException;
}
