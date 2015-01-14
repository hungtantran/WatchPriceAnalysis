package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class TopicDAOJDBC implements TopicDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE topic_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "type_table_id int unsigned not null, "
		+ "topic char(255) not null, " + "PRIMARY KEY(id), "
		+ "FOREIGN KEY (type_table_id) REFERENCES type_table(id), "
		+ "UNIQUE (id), " + "UNIQUE (topic))";
	private final String SQL_SELECT_ALL = "SELECT * FROM topic_table";
	private final String SQL_SELECT_BY_TYPE = "SELECT * FROM topic_table WHERE type_table_id = ?";
	private final String SQL_INSERT = "INSERT INTO topic_table (id, type_table_id, topic) values (?, ?, ?)";

	private DAOFactory daoFactory;

	public TopicDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}
	
	private Topic constructTopicObject(ResultSet resultSet) throws SQLException {
		Topic topic = new Topic();
		
		topic.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) topic.setId(null);
		
		topic.setTypeTableId(resultSet.getInt("type_table_id"));
		if (resultSet.wasNull()) topic.setTypeTableId(null);
		
		topic.setTopic(resultSet.getString("topic"));
		if (resultSet.wasNull()) topic.setTopic(null);
		
		return topic;
	}
	
	// Insert all the types into the type tables
//	private void initializeTopicTable() {
//		try {
//			Statement st = this.con.createStatement();
//			st.executeQuery("USE " + this.database);
//		} catch (SQLException e) {
//			System.out.println("Fail to initialize topic table");
//			e.printStackTrace();
//		}
//
//		// Iteratate through each type to get the list of topics of that type
//		for (Map.Entry<Type, String[]> entry : Globals.typeTopicMap.entrySet()) {
//			Type type = entry.getKey();
//			String[] topics = entry.getValue();
//
//			// Iteratate through each topic in the list of topics
//			for (int i = 0; i < topics.length; i++) {
//				String topic = topics[i].trim();
//				try {
//					PreparedStatement stmt = null;
//					stmt = this.con
//							.prepareStatement("INSERT INTO topic_table (id, type_table_id, topic) values (?, ?, ?)");
//					stmt.setInt(1, i + 1);
//					stmt.setInt(2, type.value);
//					stmt.setString(3, topic);
//					stmt.executeUpdate();
//				} catch (SQLException e) {
//					System.out.println("Fail to insert topic '" + topic
//							+ "' into topic_table");
//				}
//			}
//		}
//	}

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
			Globals.crawlerLogManager.writeLog("Create topic relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<Topic> getTopics() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();
			
			List<Topic> topics = new ArrayList<Topic>();
			while (resultSet.next()) {
				Topic topic = this.constructTopicObject(resultSet);
				topics.add(topic);
			}

			return topics;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get topics fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<Topic> getTopics(int typeId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {typeId};
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_TYPE, false, values);
			resultSet = preparedStatement.executeQuery();
			
			List<Topic> topics = new ArrayList<Topic>();
			while (resultSet.next()) {
				Topic topic = this.constructTopicObject(resultSet);
				topics.add(topic);
			}

			return topics;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get topics fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createTopic(Topic topic) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				topic.getId(),
				topic.getTypeTableId(),
				topic.getTopic()
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
			Globals.crawlerLogManager.writeLog("Insert topic " + topic.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
