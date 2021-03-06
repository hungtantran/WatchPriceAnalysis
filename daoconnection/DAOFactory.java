package daoconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DAOFactory {
	// Constants
	// ----------------------------------------------------------------------------------

	private static final String PROPERTY_USERNAME = null;
	private static final String PROPERTY_PASSWORD = null;
	private static final String PROPERTY_SERVER = null;
	private static final String PROPERTY_DATABASE = null;

	// Actions
	// ------------------------------------------------------------------------------------

	public static DAOFactory getInstance(String username, String password,
			String server) throws ClassNotFoundException {
		if (username == null || password == null || server == null) {
			System.out.println("here");
			// TODO throw exception
			return null;
		}

		DAOFactory instance = new MySQLDAOFactory(username, password, server);

		return instance;
	}

	/**
	 * Returns a connection to the database. Package private so that it can be
	 * used inside the DAO package only.
	 * 
	 * @return A connection to the database.
	 * @throws SQLException
	 *             If acquiring the connection fails.
	 */
	abstract Connection getConnection() throws SQLException;
}

// Default DAOFactory implementations
// -------------------------------------------------------------

/**
 * The DriverManager based DAOFactory.
 */
class MySQLDAOFactory extends DAOFactory {
	private String username;
	private String password;
	private String server;

	MySQLDAOFactory(String username, String password, String server) throws ClassNotFoundException {
		this.username = username;
		this.password = password;
		this.server = server;

		Class.forName("com.mysql.jdbc.Driver");
	}

	@Override
	Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + this.server,
				this.username, this.password);
	}
}
