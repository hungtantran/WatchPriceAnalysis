package dbconnection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import newscrawler.Globals;
import newscrawler.Helper;

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

	public Map<String, Integer> getIdTopicMap() {
		return this.idTopicMap;
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
				String type = resultSet.getString(2).trim();
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
				String domain = resultSet.getString(2).trim();
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
				String topic = resultSet.getString(3).trim();
				this.idTopicMap.put(topic, id);
				if (Globals.DEBUG)
					System.out.println(id + " : " + topic + " "
							+ this.idTopicMap.get(topic));
			}
		} catch (SQLException e) {
			System.out.println("Get topic_table information fails");
			e.printStackTrace();
		}
	}

	public void deleteDB() {
	}

	public Integer[] convertDomainToDomainId(Globals.Domain[] domains) {
		Integer[] domainsId = new Integer[3];

		// Get the id of domains
		for (int i = 0; i < domains.length; i++)
			domainsId[i] = domains[i].value;

		for (int i = domains.length; i < 3; i++)
			domainsId[i] = null;

		return domainsId;
	}

	public Integer[] convertTypeToTypeId(Globals.Type[] types) {
		Integer[] typesId = new Integer[3];

		// Get the id of types
		for (int i = 0; i < types.length; i++)
			typesId[i] = types[i].value;

		for (int i = types.length; i < 3; i++)
			typesId[i] = null;

		return typesId;
	}

	public Integer[] convertTopicToTopicId(String[] topics) {
		Integer[] topicsId = new Integer[topics.length];

		// Get the id of topics
		for (int i = 0; i < topics.length; i++) {
			topicsId[i] = this.idTopicMap.get(topics[i]);
			System.out.println(topics[i] + " : " + topicsId[i]);
		}

		return topicsId;
	}

	// Add new article-topic relationship
	public void addArticleTopicRelationship(int articleId, int topicId) {
		PreparedStatement stmt = null;

		try {
			stmt = this.con
					.prepareStatement("INSERT INTO article_topic_table (article_table_id, topic_table_id) values (?, ?)");
			stmt.setInt(1, articleId);
			stmt.setInt(2, topicId);
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Fail to insert into article_topic_table");
			// e.printStackTrace();
		}
	}

	// Add content for article into article_content_table
	public void addArticleContent(int articleId, String content) {
		PreparedStatement stmt = null;

		try {
			// Insert into article_content_table table
			stmt = this.con
					.prepareStatement("INSERT INTO article_content_table (article_table_id, content) values (?, ?)");
			stmt.setInt(1, articleId);
			InputStream is = new ByteArrayInputStream(content.getBytes());
			stmt.setBlob(2, is);
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Fail to insert into article_content_table");
			e.printStackTrace();
		}
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
				int articleId = generatedKeys.getInt(1);

				// Add into article_content_table
				this.addArticleContent(articleId, content);

				// Insert into article_topic_table table
				for (int topicId : topicsId) {
					this.addArticleTopicRelationship(articleId, topicId);
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
		stmt = this.con.prepareStatement("INSERT INTO watch_spec_table ("
				+ "watch_table_id, " + "ref_no, " + "topic_table_id_1, "
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

	// Get information from article_table
	public ResultSet getArticleInfo() {
		return getArticleInfo(-1, -1);
	}

	public ResultSet getArticleInfo(int lowerBound, int maxNumResult) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT * FROM article_table";

			if (lowerBound > 0 || maxNumResult > 0)
				query += " LIMIT " + lowerBound + "," + maxNumResult;

			return st.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Get article_table information fails");
			e.printStackTrace();
		}

		return null;
	}

	// Return the number of articles with given topicId
	public int getNumArticleWithTopicId(int topicId) {
		int numArticleWithTopicId = 0;
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT COUNT(*) AS SIZE FROM article_topic_table WHERE topic_table_id = "
					+ topicId;

			ResultSet result = st.executeQuery(query);

			if (result.next()) {
				numArticleWithTopicId = result.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("Get article_table information fails");
			e.printStackTrace();
		}

		return numArticleWithTopicId;
	}

	// Get content of article with given id
	public String getArticleContent(int articleId) {
		String content = null;

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT * FROM article_content_table WHERE article_table_id = "
					+ articleId;

			ResultSet resultSet = st.executeQuery(query);

			int count = 0;
			while (resultSet.next()) {
				count++;
				content = resultSet.getString(2);
			}

			if (count != 1)
				return null;
		} catch (SQLException e) {
			System.out.println("Get article_table information fails");
			e.printStackTrace();
		}

		return content;
	}

	// Get information from article_table
	public ResultSet getWatchInfo() {
		return getWatchInfo(0, -1, -1);
	}

	// If topicId is null, return all watch without topic
	// If topicId is non-positive, return all watch
	// If topicId is positive, return watches with topic_table_id_1 OR
	// topic_table_id_2 = topicId
	public ResultSet getWatchInfo(Integer topicId, int lowerBound,
			int maxNumResult) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT * FROM watch_table";

			if (topicId == null) {
				query += " WHERE topic_table_id_1 IS NULL";
			} else {
				if (topicId > 0)
					query += " WHERE topic_table_id_1 = " + topicId
							+ " OR topic_table_id_2 = " + topicId;
			}

			if (lowerBound > 0 || maxNumResult > 0)
				query += " LIMIT " + lowerBound + "," + maxNumResult;

			return st.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Get article_table information fails");
			e.printStackTrace();
		}

		return null;
	}

	// Update the topic_table_id_1 of watch_table to topicId
	public boolean updateWatchTopic(int watchId, Integer[] topicIds) {
		if (topicIds == null || topicIds.length < 1)
			return false;

		for (int i = 0; i < topicIds.length; i++)
			if (topicIds[i] == null || topicIds[i] < 1)
				return false;

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			// Clear the info of the specified watch
			String updateQuery = "UPDATE watch_table SET topic_table_id_1 = NULL, topic_table_id_2 = NULL WHERE id = "
					+ watchId;
			st.executeUpdate(updateQuery);

			// Insert the new info for the specified watch
			updateQuery = "UPDATE watch_table SET topic_table_id_1 = "
					+ topicIds[0];
			if (topicIds.length > 1)
				updateQuery += ", topic_table_id_2 = " + topicIds[1];
			updateQuery += " WHERE id = " + watchId;
			st.executeUpdate(updateQuery);

			// Clear the info of the specified watch
			updateQuery = "UPDATE watch_spec_table SET topic_table_id_1 = NULL, topic_table_id_2 = NULL WHERE watch_table_id = "
					+ watchId;
			st.executeUpdate(updateQuery);

			// Insert the new info for the specified watch
			updateQuery = "UPDATE watch_spec_table SET topic_table_id_1 = "
					+ topicIds[0];
			if (topicIds.length > 1)
				updateQuery += ", topic_table_id_2 = " + topicIds[1];
			updateQuery += " WHERE watch_table_id = " + watchId;
			st.executeUpdate(updateQuery);
		} catch (SQLException e) {
			System.out.println("Update watch_table topic information fails");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// Add new or update watch price statistic for specified topicId
	public boolean addWatchPriceStat(int topicId, int numArticles,
			int numWatches, int lowestPrice, int highestPrice, float mean,
			float median, float std, int[] values, int[] numbers) {
		// Not enough distribution to add into the database
		if (values.length < 20 || numbers.length < 20)
			return false;

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			st.executeUpdate("DELETE FROM watch_price_stat_table WHERE topic_table_id = "
					+ topicId);
		} catch (SQLException e) {
			// TODO do something ?
		}

		try {
			// Insert into watch_price_stat_table
			String prepareStmt = "INSERT INTO watch_price_stat_table ("
					+ "topic_table_id, " + "number_of_articles, "
					+ "number_of_watches, " + "lowest_price, "
					+ "highest_price, " + "mean_price, " + "median_price, "
					+ "standard_deviation_price";

			// Only 19 because the last 'value' and 'number' is added in
			// separately after the loop
			for (int i = 1; i <= 19; i++) {
				prepareStmt += ", " + i + "_5th_price, " + i + "_5th_number";
			}
			prepareStmt += ", 20_5th_price, 20_5th_number)";

			prepareStmt += " values (";
			// Only 47 because the last '?' is added in separately after the
			// loop
			for (int i = 0; i < 47; i++) {
				prepareStmt += "?, ";
			}
			prepareStmt += "?)";

			PreparedStatement stmt = this.con.prepareStatement(prepareStmt);
			stmt.setInt(1, topicId);
			stmt.setInt(2, numArticles);
			stmt.setInt(3, numWatches);
			stmt.setInt(4, lowestPrice);
			stmt.setInt(5, highestPrice);
			stmt.setInt(6, Math.round(mean));
			stmt.setInt(7, Math.round(median));
			stmt.setFloat(8, std);

			for (int i = 0; i < 20; i++) {
				stmt.setInt(9 + 2 * i, values[i]);
				stmt.setInt(10 + 2 * i, numbers[i]);
			}

			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Insert into watch_price_stat_table fails");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// Remove article from article_table and associated table
	public void removeArticle(int articleId) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			st.executeUpdate("DELETE FROM article_content_table WHERE article_table_id = "
					+ articleId);
			st.executeUpdate("DELETE FROM article_topic_table WHERE article_table_id = "
					+ articleId);
			st.executeUpdate("DELETE FROM article_table WHERE id = "
					+ articleId);
		} catch (SQLException e) {
			System.out.println("Fail to delete article with ID " + articleId);
			e.printStackTrace();
		}
	}

	// Insert link into link_queue_table
	public boolean insertIntoLinkQueueTable(String link, int domainId,
			Integer priority, Integer persistent, String timeCrawled,
			String dateCrawled) {
		if (link == null)
			return false;

		// If the time crawled is not specified, use the current time
		if (timeCrawled == null || dateCrawled == null) {
			timeCrawled = Helper.getCurrentTime();
			dateCrawled = Helper.getCurrentDate();
		}

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			// Insert into watch_price_stat_table
			String prepareStmt = "INSERT INTO link_queue_table (" + "link, "
					+ "domain_table_id_1, " + "priority, " + "persistent, "
					+ "time_crawled, "
					+ "date_crawled) values (?, ?, ?, ?, ?, ?)";

			PreparedStatement stmt = this.con.prepareStatement(prepareStmt);
			stmt.setString(1, link);
			stmt.setInt(2, domainId);

			if (priority != null)
				stmt.setInt(3, priority);
			else
				stmt.setNull(3, java.sql.Types.INTEGER);

			if (persistent != null)
				stmt.setInt(4, persistent);
			else
				stmt.setNull(4, java.sql.Types.INTEGER);

			stmt.setString(5, timeCrawled);
			stmt.setString(6, dateCrawled);

			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Insert into link_queue_table fails");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertIntoLinkCrawledTable(String link, int domainId,
			Integer priority, String timeCrawled, String dateCrawled) {
		if (link == null)
			return false;

		// If the time crawled is not specified, use the current time
		if (timeCrawled == null || dateCrawled == null) {
			timeCrawled = Helper.getCurrentTime();
			dateCrawled = Helper.getCurrentDate();
		}

		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			// Insert into watch_price_stat_table
			String prepareStmt = "INSERT INTO link_crawled_table (" + "link, "
					+ "domain_table_id_1, " + "priority, " + "time_crawled, "
					+ "date_crawled) values (?, ?, ?, ?, ?)";

			PreparedStatement stmt = this.con.prepareStatement(prepareStmt);
			stmt.setString(1, link);
			stmt.setInt(2, domainId);

			if (priority != null)
				stmt.setInt(3, priority);
			else
				stmt.setNull(3, java.sql.Types.INTEGER);

			stmt.setString(4, timeCrawled);
			stmt.setString(5, dateCrawled);

			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Insert into link_crawled_table fails");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
	}
}
