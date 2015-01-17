package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface TypeWordDAO {
	public boolean createRelation() throws SQLException;
	
	public List<TypeWord> getTypeWords() throws SQLException;
	
	public List<TypeWord> getTypeWords(int typeTableId) throws SQLException;
	
	public Integer createTypeWord(TypeWord typeWord) throws SQLException;
}
