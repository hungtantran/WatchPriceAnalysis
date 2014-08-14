package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import newscrawler.Globals;
import newscrawler.Globals.Type;

public class InitializeDB {
	private Connection con = null;
	private static String username = "root";
	private static String password = "";
	private static String server = "localhost";
	private String database = "newscrawler";

	public InitializeDB() {
		// Set up sql connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection("jdbc:mysql://" + server,
					username, password);
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Create all the tables
	public void createDB() {
		this.createDomainTable();
		this.createTypeTable();
		this.createTopicTable();
		this.createArticleTable();
		this.createArticleContentTable();
		this.createArticleTopicTable();
		this.createWatchDescTableTemp();
		this.createWatchContentTable();
		this.createWatchPriceStatTable();
		this.createLinkTable();
	}

	// Initialize the tables with initial data
	public void initializeDB() {
		this.initializeDomainTable();
		this.initializeTypeTable();
		this.initializeTopicTable();
	}

	// Create article_table
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

	// Create article_content_table
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

	// Create domain_table
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

	// Create type_table
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

	// Create topic_table
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

	// Create article_topic_table
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
	
	private void createWatchDescTableTemp() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE watch_desc_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "link char(255) not null, "
					+ "domain_table_id_1 int unsigned not null, "
					+ "topic_table_id_1 int unsigned, "
					+ "topic_table_id_2 int unsigned, "
					+ "watch_name char(255) not null, "
					+ "price_1 int unsigned not null, "
					+ "price_2 int unsigned, "
					+ "keywords char(255), "
					+ "ref_no char(64), "
					+ "movement char(64), "
					+ "caliber char(64), "
					+ "watch_condition char(64), "
					+ "watch_year int unsigned, "
					+ "case_material char(64), "
					+ "dial_color char(64), "
					+ "gender char(64), "
					+ "location_1 char(128), "
					+ "location_2 char(128), "
					+ "location_3 char(128), "
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
			System.out.println("CREATE TABLE watch_desc_table fails");
			e.printStackTrace();
		}
	}

	// Create watch_content_table
	private void createWatchContentTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("CREATE TABLE watch_page_content_table ("
					+ "watch_table_id int unsigned not null, "
					+ "content LONGBLOB not null, "
					+ "UNIQUE(watch_table_id), "
					+ "FOREIGN KEY (watch_table_id) REFERENCES watch_desc_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE watch_page_content_table fails");
			e.printStackTrace();
		}
	}

	// Create watch_price_analysis_table
	private void createWatchPriceStatTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "CREATE TABLE watch_price_stat_table ("
					+ "topic_table_id int unsigned not null, "
					+ "number_of_articles int unsigned not null, "
					+ "number_of_watches int unsigned not null, "
					+ "lowest_price int unsigned not null, "
					+ "highest_price int unsigned not null, "
					+ "mean_price int unsigned not null, "
					+ "median_price int unsigned not null, "
					+ "standard_deviation_price float unsigned not null, ";

			for (int i = 1; i <= 20; i++) {
				query += i + "_5th_price int unsigned not null, ";
				query += i + "_5th_number int unsigned not null, ";
			}

			query += "UNIQUE(topic_table_id), "
					+ "FOREIGN KEY (topic_table_id) REFERENCES topic_table(id))";

			st.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("CREATE TABLE watch_price_stat_table fails");
			e.printStackTrace();
		}
	}

	// Create link_queue_table and link_crawled_table
	private void createLinkTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			st.executeUpdate("CREATE TABLE link_queue_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "link char(255) not null, "
					+ "domain_table_id_1 int unsigned not null, "
					+ "priority int unsigned, "
					+ "persistent int unsigned, "
					+ "time_crawled char(128) not null, "
					+ "date_crawled char(128) not null, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE (id), "
					+ "UNIQUE (link), "
					+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))");
			
			st.executeUpdate("CREATE TABLE link_crawled_table ("
					+ "id int unsigned AUTO_INCREMENT not null, "
					+ "link char(255) not null, "
					+ "priority int unsigned, "
					+ "domain_table_id_1 int unsigned not null, "
					+ "time_crawled char(128) not null, "
					+ "date_crawled char(128) not null, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE (id), "
					+ "UNIQUE (link), "
					+ "FOREIGN KEY (domain_table_id_1) REFERENCES domain_table(id))");
		} catch (SQLException e) {
			System.out.println("CREATE TABLE link_queue_table or link_crawled_table fails");
			e.printStackTrace();
		}
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
			String domainName = entry.getValue().trim();
			try {
				PreparedStatement stmt = null;
				stmt = this.con
						.prepareStatement("INSERT INTO domain_table (id, domain) values (?, ?)");
				stmt.setInt(1, type.value);
				stmt.setString(2, domainName);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Fail to insert domain '" + domainName
						+ "' into domain_table");
				e.printStackTrace();
			}
		}
	}

	// Insert all the types into the type tables
	private void initializeTypeTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
		} catch (SQLException e) {
			System.out.println("Fail to initialize type table");
			e.printStackTrace();
		}

		for (Map.Entry<Type, String> entry : Globals.typeNameMap.entrySet()) {
			Type type = entry.getKey();
			String typeName = entry.getValue().trim();

			try {
				PreparedStatement stmt = null;
				stmt = this.con
						.prepareStatement("INSERT INTO type_table (id, type) values (?, ?)");
				stmt.setInt(1, type.value);
				stmt.setString(2, typeName);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Fail to insert type '" + type.value
						+ "' into type_table");
			}
		}
	}

	// Insert all the types into the type tables
	private void initializeTopicTable() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
		} catch (SQLException e) {
			System.out.println("Fail to initialize topic table");
			e.printStackTrace();
		}

		// Iteratate through each type to get the list of topics of that type
		for (Map.Entry<Type, String[]> entry : Globals.typeTopicMap.entrySet()) {
			Type type = entry.getKey();
			String[] topics = entry.getValue();

			// Iteratate through each topic in the list of topics
			for (int i = 0; i < topics.length; i++) {
				String topic = topics[i].trim();
				try {
					PreparedStatement stmt = null;
					stmt = this.con
							.prepareStatement("INSERT INTO topic_table (id, type_table_id, topic) values (?, ?, ?)");
					stmt.setInt(1, i + 1);
					stmt.setInt(2, type.value);
					stmt.setString(3, topic);
					stmt.executeUpdate();
				} catch (SQLException e) {
					System.out.println("Fail to insert topic '" + topic
							+ "' into topic_table");
				}
			}
		}
	}

	public static void main(String[] args) {
		InitializeDB con = new InitializeDB();
		con.createDB();
		con.initializeDB();
	}
}
