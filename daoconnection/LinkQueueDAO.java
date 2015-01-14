package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface LinkQueueDAO {
	public boolean createRelation() throws SQLException;
	
	public List<LinkQueue> getLinksQueued() throws SQLException;
	
	public List<LinkQueue> getLinksQueuedByDomain(Domain domain) throws SQLException;
	
	public Integer createLinkQueue(LinkQueue linkQueue) throws SQLException;
	
	public boolean removeLinkQueue(String link) throws SQLException;
}
