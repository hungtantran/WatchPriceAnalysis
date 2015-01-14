package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface TopicDAO {
	public boolean createRelation() throws SQLException;
	
	public List<Topic> getTopics() throws SQLException;
	
	public List<Topic> getTopics(int typeId) throws SQLException;
	
	public Integer createTopic(Topic topic) throws SQLException;
}
