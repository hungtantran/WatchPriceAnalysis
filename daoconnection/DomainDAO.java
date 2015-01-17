package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface DomainDAO {
	public boolean createRelation() throws SQLException;
	
	public List<Domain> getDomains() throws SQLException;
	
	public Integer createDomain(Domain domain) throws SQLException;
}
