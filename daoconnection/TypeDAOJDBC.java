package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class TypeDAOJDBC implements TypeDAO {
	private final String SQL_CREATE =
		"CREATE TABLE type_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "type char(255) not null, " + "PRIMARY KEY(id), "
		+ "UNIQUE (id), " + "UNIQUE (type))";
	private final String SQL_SELECT_ALL = "SELECT * FROM type_table";
	private final String SQL_INSERT = "INSERT INTO type_table (id, type) values (?, ?)";

	private final DAOFactory daoFactory;

	public TypeDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}

	private Type constructTypeObject(ResultSet resultSet) throws SQLException {
		Type type = new Type();

		type.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) {
		    type.setId(null);
		}

		type.setType(resultSet.getString("type"));
		if (resultSet.wasNull()) {
		    type.setType(null);
		}

		return type;
	}

	// Insert all the types into the type tables
//	private static void initializeTypeTable() {
//		try {
//			Statement st = this.con.createStatement();
//			st.executeQuery("USE " + this.database);
//		} catch (SQLException e) {
//			System.out.println("Fail to initialize type table");
//			e.printStackTrace();
//		}
//
//		for (Map.Entry<Type, String> entry : Globals.typeNameMap.entrySet()) {
//			Type type = entry.getKey();
//			String typeName = entry.getValue().trim();
//
//			try {
//				PreparedStatement stmt = null;
//				stmt = this.con
//						.prepareStatement("INSERT INTO type_table (id, type) values (?, ?)");
//				stmt.setInt(1, type.value);
//				stmt.setString(2, typeName);
//				stmt.executeUpdate();
//			} catch (SQLException e) {
//				System.out.println("Fail to insert type '" + type.value
//						+ "' into type_table");
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

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_CREATE, false);

			if (Globals.DEBUG) {
				Globals.crawlerLogManager.writeLog(preparedStatement.toString());
			}

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Create type relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<Type> getTypes() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();

			List<Type> types = new ArrayList<Type>();
			while (resultSet.next()) {
				Type type = this.constructTypeObject(resultSet);
				types.add(type);
			}

			return types;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get topics fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createType(Type type) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = this.daoFactory.getConnection();

			Object[] values = {
				type.getId(),
				type.getType()
			};

			preparedStatement = DAOUtil.prepareStatement(connection, this.SQL_INSERT, true, values);

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
			Globals.crawlerLogManager.writeLog("Insert type " + type.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
