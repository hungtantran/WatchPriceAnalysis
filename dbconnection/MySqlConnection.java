package dbconnection;

import java.io.*;
import java.sql.*;
import java.util.*;

import newscrawler.Globals;
import newscrawler.Globals.Type;

public class MySqlConnection {
	private Connection con = null;
	private static String username = "root";
	private static String password = "";
	private static String server = "localhost";
	private String database = "newscrawler";
	private Map<Integer, String> idTypeMap = null;
	private Map<Integer, String> idDomainMap = null;
	private Map<String, Integer> idTopicMap = null;

	public MySqlConnection() {
		// Set up sql connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection("jdbc:mysql://" + server,
					username, password);
			this.getDatabaseExistingInfo();
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Populate information of type, domain and topic information from existing
	// database
	private void getDatabaseExistingInfo() {
		this.idTypeMap = new HashMap<Integer, String>();
		this.idDomainMap = new HashMap<Integer, String>();
		this.idTopicMap = new HashMap<String, Integer>();

		this.getTypeInfo();
		this.getDomainInfo();
		this.getTopicInfo();
	}

	// Populate information of type
	private void getTypeInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM type_table");

			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String type = resultSet.getString(2);
				this.idTypeMap.put(id, type);
				if (Globals.DEBUG)
					System.out.println(id + " : " + type);
			}
		} catch (SQLException e) {
			System.out.println("Get type_table information fails");
			e.printStackTrace();
		}
	}

	// Populate information of domain
	private void getDomainInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM domain_table");

			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String domain = resultSet.getString(2);
				this.idDomainMap.put(id, domain);
				if (Globals.DEBUG)
					System.out.println(id + " : " + domain);
			}
		} catch (SQLException e) {
			System.out.println("Get domain_table information fails");
			e.printStackTrace();
		}
	}

	// Populate information of topic
	private void getTopicInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM topic_table");

			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String topic = resultSet.getString(3);
				this.idTopicMap.put(topic, id);
				if (Globals.DEBUG)
					System.out.println(id + " : " + topic);
			}
		} catch (SQLException e) {
			System.out.println("Get topic_table information fails");
			e.printStackTrace();
		}
	}

	public void createDB() {
		this.createDomainTable();
		this.createTypeTable();
		this.createTopicTable();
		this.createArticleTable();
		this.createArticleContentTable();
		this.createArticleTopicTable();
		this.createWatchPriceTable();
		this.createWatchSpecTable();
		this.createWatchContentTable();
	}

	private void createArticleTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE article_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "link char(255) not null, "
					+ "domain_table_id_1 int unsigned not null, "
					+ "domain_table_id_2 int unsigned, "
					+ "domain_table_id_3 int unsigned, "
					+ "article_name char(255) not null, "
					+ "type_table_1 int unsigned not null, "
					+ "type_table_2 int unsigned, "
					+ "keywords char(255) not null, "
					+ "time_created char(128) not null, "
					+ "date_created char(128) not null, "
					+ "time_crawled char(128) not null, "
					+ "date_crawled char(128) not null, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE (id), "
					+ "UNIQUE (link), "
					+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id), "
					+ "FOREIGN KEY (domain_table_id_2) REFERENCES domain_table(id), "
					+ "FOREIGN KEY (domain_table_id_3) REFERENCES domain_table(id), "
					+ "FOREIGN KEY (type_table_1) REFERENCES type_table(id),"
					+ "FOREIGN KEY (type_table_2) REFERENCES type_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE article_table fails");
			e.printStackTrace();
		}
	}

	private void createArticleContentTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE article_content_table ("
					+ "article_table_id int unsigned not null, "
					+ "content LONGBLOB not null, "
					+ "UNIQUE(article_table_id), "
					+ "FOREIGN KEY (article_table_id) REFERENCES article_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE article_content_table fails");
			e.printStackTrace();
		}
	}

	private void createDomainTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE domain_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "domain char(255) not null, " + "PRIMARY KEY(id), "
					+ "UNIQUE (id), " + "UNIQUE (domain))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE domain_table fails");
			e.printStackTrace();
		}
	}

	private void createTypeTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE type_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "type char(255) not null, " + "PRIMARY KEY(id), "
					+ "UNIQUE (id), " + "UNIQUE (type))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE type_table fails");
			e.printStackTrace();
		}
	}

	private void createTopicTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE topic_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "type_table_id int unsigned not null, "
					+ "topic char(255) not null, " + "PRIMARY KEY(id), "
					+ "FOREIGN KEY (type_table_id) REFERENCES type_table(id), "
					+ "UNIQUE (id), " + "UNIQUE (topic))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE topic_table fails");
			e.printStackTrace();
		}
	}

	private void createArticleTopicTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE article_topic_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "article_table_id int unsigned not null, "
					+ "topic_table_id int unsigned not null, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE (id), "
					+ "UNIQUE (article_table_id, topic_table_id), "
					+ "FOREIGN KEY (article_table_id) REFERENCES article_table(id), "
					+ "FOREIGN KEY (topic_table_id) REFERENCES topic_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE article_topic_table fails");
			e.printStackTrace();
		}
	}

	private void createWatchPriceTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE watch_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "link char(255) not null, "
					+ "domain_table_id_1 int unsigned not null, "
					+ "topic_table_id_1 int unsigned, "
					+ "topic_table_id_2 int unsigned, "
					+ "watch_name char(255) not null, "
					+ "price_1 int unsigned not null, "
					+ "price_2 int unsigned, "
					+ "keywords char(255), "
					+ "time_created char(128) not null, "
					+ "date_created char(128) not null, "
					+ "time_crawled char(128) not null, "
					+ "date_crawled char(128) not null, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE (id), "
					+ "UNIQUE (link, price_1), "
					+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id), "
					+ "FOREIGN KEY (topic_table_id_1) REFERENCES topic_table(id), "
					+ "FOREIGN KEY (topic_table_id_2) REFERENCES topic_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE watch_table fails");
			e.printStackTrace();
		}
	}

	private void createWatchSpecTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE watch_spec_table ("
					+ "watch_table_id int unsigned not null, "
					+ "ref_no char(64), "
					+ "topic_table_id_1 int unsigned, "
					+ "topic_table_id_2 int unsigned, "
					+ "movement char(64), "
					+ "caliber char(64), "
					+ "watch_condition char(64), "
					+ "watch_year int unsigned, "
					+ "case_material char(64), "
					+ "dial_color char(64), "
					+ "gender char(64), "
					+ "location char(128), "
					+ "FOREIGN KEY (watch_table_id) REFERENCES watch_table(id), "
					+ "UNIQUE (watch_table_id), "
					+ "FOREIGN KEY (topic_table_id_1) REFERENCES topic_table(id), "
					+ "FOREIGN KEY (topic_table_id_2) REFERENCES topic_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE watch_spec_table fails");
			e.printStackTrace();
		}
	}

	private void createWatchContentTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE watch_content_table ("
					+ "watch_table_id int unsigned not null, "
					+ "content LONGBLOB not null, "
					+ "UNIQUE(watch_table_id), "
					+ "FOREIGN KEY (watch_table_id) REFERENCES watch_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE article_content_table fails");
			e.printStackTrace();
		}
	}

	public void deleteDB() {

	}

	public void initializeDB() {
		this.initializeDomainTable();
		this.initializeTypeTable();
		this.initializeTopicTable();
	}

	// Insert all the domains into the type tables
	private void initializeDomainTable() {
		// TODO try catch less generic
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
		} catch (SQLException e) {
			System.out.println("Fail to initialize domain table");
			e.printStackTrace();
		}

		for (Map.Entry<Globals.Domain, String> entry : Globals.domainNameMap
				.entrySet()) {
			Globals.Domain type = entry.getKey();
			String typeName = entry.getValue();
			try {
				PreparedStatement stmt = null;
				stmt = this.con
						.prepareStatement("INSERT INTO domain_table (id, domain) values (?, ?)");
				stmt.setInt(1, type.value);
				stmt.setString(2, typeName);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Fail to initialize domain table");
				e.printStackTrace();
			}
		}
	}

	// Insert all the types into the type tables
	private void initializeTypeTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			for (Map.Entry<Type, String> entry : Globals.typeNameMap.entrySet()) {
				Type type = entry.getKey();
				String typeName = entry.getValue();

				PreparedStatement stmt = null;
				stmt = this.con
						.prepareStatement("INSERT INTO type_table (id, type) values (?, ?)");
				stmt.setInt(1, type.value);
				stmt.setString(2, typeName);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Fail to initialize type table");
			e.printStackTrace();
		}
	}

	// Insert all the types into the type tables
	private void initializeTopicTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			// Iteratate through each type to get the list of topics of that
			// type
			for (Map.Entry<Type, String[]> entry : Globals.typeTopicMap
					.entrySet()) {
				Type type = entry.getKey();
				String[] topics = entry.getValue();

				// Iteratate through each topic in the list of topics
				for (String topic : topics) {
					PreparedStatement stmt = null;
					stmt = this.con
							.prepareStatement("INSERT INTO topic_table (type_table_id, topic) values (?, ?)");
					stmt.setInt(1, type.value);
					stmt.setString(2, topic);
					stmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			System.out.println("Fail to initialize topic table");
			e.printStackTrace();
		}
	}

	private Integer[] convertDomainToDomainId(Globals.Domain[] domains) {
		Integer[] domainsId = new Integer[3];

		// Get the id of domains
		for (int i = 0; i < domains.length; i++)
			domainsId[i] = domains[i].value;

		for (int i = domains.length; i < 3; i++)
			domainsId[i] = null;

		return domainsId;
	}

	private Integer[] convertTypeToTypeId(Globals.Type[] types) {
		Integer[] typesId = new Integer[3];

		// Get the id of types
		for (int i = 0; i < types.length; i++)
			typesId[i] = types[i].value;

		for (int i = types.length; i < 3; i++)
			typesId[i] = null;

		return typesId;
	}

	private Integer[] convertTopicToTopicId(String[] topics) {
		Integer[] topicsId = new Integer[topics.length];

		// Get the id of topics
		for (int i = 0; i < topics.length; i++) {
			topicsId[i] = this.idTopicMap.get(topics[i]);
			System.out.println(topics[i] + " : " + topicsId[i]);
		}

		return topicsId;
	}

	// Add new article into the database
	public void addArticle(String link, Globals.Domain[] domains,
			String articleName, Globals.Type[] types, String[] keywords,
			String[] topics, String timeCreated, String dateCreated,
			String timeCrawled, String dateCrawled, String content) {
		// Get the id of domains, types and topics
		Integer[] domainsId = convertDomainToDomainId(domains);
		Integer[] typesId = convertTypeToTypeId(types);
		Integer[] topicsId = convertTopicToTopicId(topics);

		String keywordsString = Arrays.toString(keywords);

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			PreparedStatement stmt = null;

			// Insert into article_table table
			stmt = this.con.prepareStatement("INSERT INTO article_table ("
					+ "link, " + "domain_table_id_1, " + "domain_table_id_2, "
					+ "domain_table_id_3, " + "article_name, "
					+ "type_table_1, " + "type_table_2, " + "keywords, "
					+ "time_created, " + "date_created, " + "time_crawled, "
					+ "date_crawled) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, link);
			stmt.setInt(2, domainsId[0]);

			if (domainsId[1] != null) {
				stmt.setInt(3, domainsId[1]);
			} else {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}

			if (domainsId[2] != null) {
				stmt.setInt(4, domainsId[2]);
			} else {
				stmt.setNull(4, java.sql.Types.INTEGER);
			}

			stmt.setString(5, articleName);

			stmt.setInt(6, typesId[0]);

			if (typesId[1] != null) {
				stmt.setInt(7, typesId[1]);
			} else {
				stmt.setNull(7, java.sql.Types.INTEGER);
			}

			stmt.setString(8, keywordsString);
			stmt.setString(9, timeCreated);
			stmt.setString(10, dateCreated);
			stmt.setString(11, timeCrawled);
			stmt.setString(12, dateCrawled);

			// Print out the SQL statement for debug purpose
			System.out.println(stmt.toString());
			stmt.executeUpdate();

			// Insert into article_content_table table
			ResultSet generatedKeys = stmt.getGeneratedKeys();

			if (generatedKeys.next()) {
				int aricleId = generatedKeys.getInt(1);

				try {
					// Insert into article_content_table table
					stmt = this.con
							.prepareStatement("INSERT INTO article_content_table (article_table_id, content) values (?, ?)");
					stmt.setInt(1, aricleId);
					InputStream is = new ByteArrayInputStream(
							content.getBytes());
					stmt.setBlob(2, is);
					stmt.executeUpdate();
				} catch (Exception e) {
					System.out
							.println("Fail to insert into article_content_table");
					e.printStackTrace();
				}

				// Insert into article_topic_table table
				for (int topicId : topicsId) {
					try {
						stmt = this.con
								.prepareStatement("INSERT INTO article_topic_table (article_table_id, topic_table_id) values (?, ?)");
						stmt.setInt(1, aricleId);
						stmt.setInt(2, topicId);
						stmt.executeUpdate();
					} catch (Exception e) {
						System.out
								.println("Fail to insert into article_topic_table");
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Fail to insert into article_table");
			e.printStackTrace();
		}
	}
	
	// Insert into watch_table table
	private int insertIntoWatchTable(String link, Globals.Domain[] domains,
			String watchName, int[] prices, String[] keywords, String[] topics,
			String timeCreated, String dateCreated, String timeCrawled,
			String dateCrawled, Integer[] domainsId, Integer[] topicsId)
			throws Exception {
		String keywordsString = Arrays.toString(keywords);

		Statement st = this.con.createStatement();
		st.executeQuery("USE " + this.database);

		PreparedStatement stmt = null;

		// Insert into watch_table table
		stmt = this.con.prepareStatement("INSERT INTO watch_table (" + "link, "
				+ "domain_table_id_1, " + "topic_table_id_1, "
				+ "topic_table_id_2, " + "watch_name, " + "price_1, "
				+ "price_2, " + "keywords, " + "time_created, "
				+ "date_created, " + "time_crawled, " + "date_crawled) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, link);
		stmt.setInt(2, domainsId[0]);

		if (topicsId.length > 0 && topicsId[0] != null) {
			stmt.setInt(3, topicsId[0]);
		} else {
			stmt.setNull(3, java.sql.Types.INTEGER);
		}

		if (topicsId.length > 1 && topicsId[1] != null) {
			stmt.setInt(4, topicsId[1]);
		} else {
			stmt.setNull(4, java.sql.Types.INTEGER);
		}

		stmt.setString(5, watchName);

		if (prices.length > 0 && prices[0] > 0) {
			stmt.setInt(6, prices[0]);
		} else {
			throw new Exception();
		}

		if (prices.length > 1 && prices[1] > 0) {
			stmt.setInt(7, prices[1]);
		} else {
			stmt.setNull(7, java.sql.Types.INTEGER);
		}

		stmt.setString(8, keywordsString);
		stmt.setString(9, timeCreated);
		stmt.setString(10, dateCreated);
		stmt.setString(11, timeCrawled);
		stmt.setString(12, dateCrawled);

		// Print out the SQL statement for debug purpose
		System.out.println(stmt.toString());
		stmt.executeUpdate();

		// Insert into article_content_table table
		ResultSet generatedKeys = stmt.getGeneratedKeys();

		if (generatedKeys.next()) {
			int watchId = generatedKeys.getInt(1);
			return watchId;
		}

		return -1;
	}
	
	// Insert into watch_spec_table table
	private void insertIntoWatchSpecTable(int watchId, String refNo,
			Integer[] topicsId, String movement, String caliber,
			String watchCondition, int watchYear, String caseMaterial,
			String dialColor, String gender, String location) throws Exception {
		Statement st = this.con.createStatement();
		st.executeQuery("USE " + this.database);
		PreparedStatement stmt = null;

		// Insert into watch_spec_table table
		stmt = this.con.prepareStatement("INSERT INTO watch_spec_table (" + "watch_table_id, "
				+ "ref_no, " + "topic_table_id_1, "
				+ "topic_table_id_2, " + "movement, " + "caliber, "
				+ "watch_condition, " + "watch_year, " + "case_material, "
				+ "dial_color, " + "gender, " + "location) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, watchId);
		
		if (refNo != null) {
			stmt.setString(2, refNo);
		} else {
			stmt.setNull(2, java.sql.Types.CHAR);
		}

		if (topicsId.length > 0 && topicsId[0] != null) {
			stmt.setInt(3, topicsId[0]);
		} else {
			stmt.setNull(3, java.sql.Types.INTEGER);
		}

		if (topicsId.length > 1 && topicsId[1] != null) {
			stmt.setInt(4, topicsId[1]);
		} else {
			stmt.setNull(4, java.sql.Types.INTEGER);
		}

		if (movement != null) {
			stmt.setString(5, movement);
		} else {
			stmt.setNull(5, java.sql.Types.CHAR);
		}

		if (caliber != null) {
			stmt.setString(6, caliber);
		} else {
			stmt.setNull(6, java.sql.Types.CHAR);
		}

		if (watchCondition != null) {
			stmt.setString(7, watchCondition);
		} else {
			stmt.setNull(7, java.sql.Types.CHAR);
		}

		if (watchYear > 0) {
			stmt.setInt(8, watchYear);
		} else {
			stmt.setNull(8, java.sql.Types.INTEGER);
		}
		
		if (caseMaterial != null) {
			stmt.setString(9, caseMaterial);
		} else {
			stmt.setNull(9, java.sql.Types.CHAR);
		}
		
		if (dialColor != null) {
			stmt.setString(10, dialColor);
		} else {
			stmt.setNull(10, java.sql.Types.CHAR);
		}
		
		if (gender != null) {
			stmt.setString(11, gender);
		} else {
			stmt.setNull(11, java.sql.Types.CHAR);
		}
		
		if (location != null) {
			stmt.setString(12, location);
		} else {
			stmt.setNull(12, java.sql.Types.CHAR);
		}
		// Print out the SQL statement for debug purpose
		System.out.println(stmt.toString());
		stmt.executeUpdate();
	}

	// Add new watch entry into the database
	public void addWatchEntry(String link, Globals.Domain[] domains,
			String watchName, int[] prices, String[] keywords, String[] topics,
			String timeCreated, String dateCreated, String timeCrawled,
			String dateCrawled, String content, String refNo, String movement,
			String caliber, String watchCondition, int watchYear,
			String caseMaterial, String dialColor, String gender,
			String location) {
		// Get the id of domains and topics
		Integer[] domainsId = convertDomainToDomainId(domains);
		Integer[] topicsId = convertTopicToTopicId(topics);

		try {
			// Try to insert a new entry into watch_table
			int watchId = insertIntoWatchTable(link, domains, watchName,
					prices, keywords, topics, timeCreated, dateCreated,
					timeCrawled, dateCrawled, domainsId, topicsId);

			// Fail to insert new watch entry, return right away
			if (watchId < 0)
				return;

			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			PreparedStatement stmt = null;

			try {
				// Insert into watch_content_table table
				stmt = this.con
						.prepareStatement("INSERT INTO watch_content_table (watch_table_id, content) values (?, ?)");
				stmt.setInt(1, watchId);
				InputStream is = new ByteArrayInputStream(content.getBytes());
				stmt.setBlob(2, is);
				stmt.executeUpdate();
			} catch (Exception e) {
				System.out.println("Fail to insert into watch_content_table");
				e.printStackTrace();
			}

			// Insert into watch_spec_table table
			try {
				insertIntoWatchSpecTable(watchId, refNo, topicsId, movement,
						caliber, watchCondition, watchYear, caseMaterial,
						dialColor, gender, location);

			} catch (Exception e) {
				System.out.println("Fail to insert into watch_spec_table");
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("Fail to insert into watch_table");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MySqlConnection con = new MySqlConnection();
		con.createDB();
		con.initializeDB();
	}
}
