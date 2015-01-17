package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class TypeWordDAOJDBC implements TypeWordDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE type_word_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "type_table_id int unsigned not null, "
		+ "type_word char(255) not null, PRIMARY KEY(id), "
		+ "FOREIGN KEY (type_table_id) REFERENCES type_table(id), "
		+ "UNIQUE (id), " + "UNIQUE (type_table_id, type_word))";
	private final String SQL_SELECT_ALL = "SELECT * FROM type_word_table";
	private final String SQL_SELECT_BY_TYPE = "SELECT * FROM type_word_table WHERE type_table_id = ?";
	private final String SQL_INSERT = "INSERT INTO type_word_table (id, type_table_id, type_word) values (?, ?, ?)";

	private DAOFactory daoFactory;

	public TypeWordDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}
	
	private TypeWord constructTypeWordObject(ResultSet resultSet) throws SQLException {
		TypeWord typeWord = new TypeWord();
		
		typeWord.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) typeWord.setId(null);
		
		typeWord.setTypeTableId(resultSet.getInt("type_table_id"));
		if (resultSet.wasNull()) typeWord.setTypeTableId(null);
		
		typeWord.setTypeWord(resultSet.getString("type_word"));
		if (resultSet.wasNull()) typeWord.setTypeWord(null);
		
		return typeWord;
	}

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
			Globals.crawlerLogManager.writeLog("Create type word relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<TypeWord> getTypeWords() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();
			
			List<TypeWord> typeWords = new ArrayList<TypeWord>();
			while (resultSet.next()) {
				TypeWord typeWord = this.constructTypeWordObject(resultSet);
				typeWords.add(typeWord);
			}

			return typeWords;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get type word fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<TypeWord> getTypeWords(int typeTableId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = { typeTableId };
			
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_BY_TYPE, false, values);
			resultSet = preparedStatement.executeQuery();
			
			List<TypeWord> typeWords = new ArrayList<TypeWord>();
			while (resultSet.next()) {
				TypeWord typeWord = this.constructTypeWordObject(resultSet);
				typeWords.add(typeWord);
			}

			return typeWords;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get type words fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createTypeWord(TypeWord typeWord) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				typeWord.getId(),
				typeWord.getTypeTableId(),
				typeWord.getTypeWord()
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
			Globals.crawlerLogManager.writeLog("Insert type word " + typeWord.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
