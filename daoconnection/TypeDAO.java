package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface TypeDAO {
	public boolean createRelation() throws SQLException;
	
	public List<Type> getTypes() throws SQLException;
	
	public Integer createType(Type type) throws SQLException;
}
