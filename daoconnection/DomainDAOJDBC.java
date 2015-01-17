package daoconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commonlib.Globals;

public class DomainDAOJDBC implements DomainDAO {
	private final String SQL_CREATE = 
		"CREATE TABLE domain_table ("
		+ "id int unsigned AUTO_INCREMENT not null, "
		+ "domain char(255) not null, domain_string char(255) not null, "
		+ "PRIMARY KEY(id), UNIQUE (id))";
	private final String SQL_SELECT_ALL = "SELECT * FROM domain_table";
	private final String SQL_INSERT = "INSERT INTO domain_table (id, domain, domain_string) values (?, ?, ?)";

	private DAOFactory daoFactory;

	public DomainDAOJDBC(DAOFactory daoFactory) throws SQLException {
		this.daoFactory = daoFactory;
	}
	
	private Domain constructDomainObject(ResultSet resultSet) throws SQLException {
		Domain domain = new Domain();
		
		domain.setId(resultSet.getInt("id"));
		if (resultSet.wasNull()) domain.setId(null);
		
		domain.setDomain(resultSet.getString("domain"));
		if (resultSet.wasNull()) domain.setDomain(null);
		
		domain.setDomainString(resultSet.getString("domain_string"));
		if (resultSet.wasNull()) domain.setDomainString(null);
		
		return domain;
	}

	// Insert all the domains into the type tables
//	private static void initializeDomainTable() {
//		// TODO try catch less generic
//		try {
//			Statement st = this.con.createStatement();
//			st.executeQuery("USE " + this.database);
//		} catch (SQLException e) {
//			System.out.println("Fail to initialize domain table");
//			e.printStackTrace();
//		}
//
//		for (Map.Entry<Globals.Domain, String> entry : Globals.domainNameMap
//				.entrySet()) {
//			Globals.Domain type = entry.getKey();
//			String domainName = entry.getValue().trim();
//			try {
//				PreparedStatement stmt = null;
//				stmt = this.con
//						.prepareStatement("INSERT INTO domain_table (id, domain) values (?, ?)");
//				stmt.setInt(1, type.value);
//				stmt.setString(2, domainName);
//				stmt.executeUpdate();
//			} catch (SQLException e) {
//				System.out.println("Fail to insert domain '" + domainName
//						+ "' into domain_table");
//				e.printStackTrace();
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
			Globals.crawlerLogManager.writeLog("Create domain relation fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return false;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public List<Domain> getDomains() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, SQL_SELECT_ALL, false);
			resultSet = preparedStatement.executeQuery();
			
			List<Domain> domains = new ArrayList<Domain>();
			while (resultSet.next()) {
				Domain domain = this.constructDomainObject(resultSet);
				domains.add(domain);
			}

			return domains;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get domain information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}

	@Override
	public Integer createDomain(Domain domain) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = this.daoFactory.getConnection();
			
			Object[] values = {
				domain.getId(),
				domain.getDomain(),
				domain.getDomainString(),
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
			Globals.crawlerLogManager.writeLog("Insert domain " + domain.toString() + " fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			
			return null;
		} finally {
			DAOUtil.close(connection, preparedStatement, resultSet);
		}
	}
}
