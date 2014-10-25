package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import commonlib.Globals;
import commonlib.HTMLCompressor;
import commonlib.Helper;

public class MySqlConnection {
	private Connection con = null;
	private String username = "root";
	private String password = "";
	private String server = "localhost";
	private String database = "newscrawler";

	public MySqlConnection() {
		if (!this.establishConnection())
			return;
	}
	
	public MySqlConnection(String username, String password, String server,
			String database) {
		this.username = username;
		this.password = password;
		this.server = server;
		this.database = database;
		
		if (!this.establishConnection())
			return;
	}

	private boolean establishConnection() {
		// Set up sql connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection("jdbc:mysql://" + server,
					username, password);
		} catch (ClassNotFoundException e) {
			Globals.crawlerLogManager.writeLog("Driver not found");
			return false;
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			return false;
		}
		
		return true;
	}

	// Populate information of type
	public ResultSet getTypeInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM type_table");

			return resultSet;
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Get type_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Populate information of domain
	public ResultSet getDomainInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM domain_table");
			
			return resultSet;
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Get domain_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Populate information of topic
	public ResultSet getTopicInfo() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);
			ResultSet resultSet = st.executeQuery("SELECT * FROM topic_table");
			
			return resultSet;
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Get topic_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	public void deleteDB() {
	}

	// Add new article-topic relationship
	public boolean addArticleTopicRelationship(int articleId, int topicId) {
		PreparedStatement stmt = null;

		try {
			stmt = this.con
					.prepareStatement("INSERT INTO article_topic_table (article_table_id, topic_table_id) values (?, ?)");
			stmt.setInt(1, articleId);
			stmt.setInt(2, topicId);
			stmt.executeUpdate();
		} catch (Exception e) {
			Globals.crawlerLogManager
					.writeLog("Fail to insert into article_topic_table");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		}

		return true;
	}

	// Add content for article into article_content_table
	public boolean addArticleContent(int articleId, String content) {
		if (content == null) {
			Globals.crawlerLogManager
					.writeLog("Insert null into article_content_table");
			return false;
		} else {
			content = HTMLCompressor.compressHtmlContent(content);
		}

		PreparedStatement stmt = null;

		try {
			// Insert into article_content_table table
			stmt = this.con
					.prepareStatement("INSERT INTO article_content_table (article_table_id, content) values (?, ?)");
			stmt.setInt(1, articleId);
			stmt.setString(2, content);
			stmt.executeUpdate();
		} catch (Exception e1) {
			try {
				// Try to update the content of article_content_table table
				stmt = this.con
						.prepareStatement("UPDATE article_content_table SET content = ? WHERE article_table_id = ?");
				stmt.setString(1, content);
				stmt.setInt(2, articleId);
				stmt.executeUpdate();
			} catch (Exception e2) {
				Globals.crawlerLogManager
						.writeLog("Fail to insert into article_content_table");
				Globals.crawlerLogManager.writeLog(e2.getMessage());

				return false;
			}
		}

		return true;
	}

	// Add new article into the database
	public boolean addArticle(String link, Globals.Domain[] domains,
			String articleName, Globals.Type[] types, String[] keywords,
			String[] topics, String timeCreated, String dateCreated,
			String timeCrawled, String dateCrawled, String content) {
		// Get the id of domains, types and topics
		Integer[] domainsId = Helper.convertDomainToDomainId(domains);
		Integer[] typesId = Helper.convertTypeToTypeId(types);
		Integer[] topicsId = Helper.convertTopicToTopicId(topics);
		
		if (domainsId == null || typesId == null || topicsId == null)
			return false;
		
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
			Globals.crawlerLogManager.writeLog(stmt.toString());
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
			Globals.crawlerLogManager
					.writeLog("Fail to insert into article_table");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		}

		return true;
	}

	// Insert into watch_desc_table table
	private int insertIntoWatchDescTable(String link, Globals.Domain[] domains,
			String watchName, int[] prices, String[] keywords, String refNo,
			String movement, String caliber, String watchCondition,
			int watchYear, String caseMaterial, String dialColor,
			String gender, String location1, String location2,
			String location3, String[] topics, String timeCreated,
			String dateCreated, String timeCrawled, String dateCrawled,
			Integer[] domainsId, Integer[] topicsId) throws Exception {
		String keywordsString = Arrays.toString(keywords);

		Statement st = this.con.createStatement();
		st.executeQuery("USE " + this.database);

		PreparedStatement stmt = null;

		// Insert into watch_desc_table table
		stmt = this.con
				.prepareStatement(
						"INSERT INTO watch_desc_table ("
								+ "link, "
								+ "domain_table_id_1, "
								+ "topic_table_id_1, "
								+ "topic_table_id_2, "
								+ "watch_name, "
								+ "price_1, "
								+ "price_2, "
								+ "keywords, "
								+ "ref_no, "
								+ "movement, "
								+ "caliber, "
								+ "watch_condition, "
								+ "watch_year, "
								+ "case_material, "
								+ "dial_color, "
								+ "gender, "
								+ "location_1, "
								+ "location_2, "
								+ "location_3, "
								+ "time_created, "
								+ "date_created, "
								+ "time_crawled, "
								+ "date_crawled) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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

		if (refNo != null) {
			stmt.setString(9, refNo);
		} else {
			stmt.setNull(9, java.sql.Types.CHAR);
		}

		if (movement != null) {
			stmt.setString(10, movement);
		} else {
			stmt.setNull(10, java.sql.Types.CHAR);
		}

		if (caliber != null) {
			stmt.setString(11, caliber);
		} else {
			stmt.setNull(11, java.sql.Types.CHAR);
		}

		if (watchCondition != null) {
			stmt.setString(12, watchCondition);
		} else {
			stmt.setNull(12, java.sql.Types.CHAR);
		}

		if (watchYear > 0) {
			stmt.setInt(13, watchYear);
		} else {
			stmt.setNull(13, java.sql.Types.INTEGER);
		}

		if (caseMaterial != null) {
			stmt.setString(14, caseMaterial);
		} else {
			stmt.setNull(14, java.sql.Types.CHAR);
		}

		if (dialColor != null) {
			stmt.setString(15, dialColor);
		} else {
			stmt.setNull(15, java.sql.Types.CHAR);
		}

		if (gender != null) {
			stmt.setString(16, gender);
		} else {
			stmt.setNull(16, java.sql.Types.CHAR);
		}

		if (location1 != null) {
			stmt.setString(17, location1);
		} else {
			stmt.setNull(17, java.sql.Types.CHAR);
		}

		if (location2 != null) {
			stmt.setString(18, location2);
		} else {
			stmt.setNull(18, java.sql.Types.CHAR);
		}

		if (location3 != null) {
			stmt.setString(19, location3);
		} else {
			stmt.setNull(19, java.sql.Types.CHAR);
		}

		stmt.setString(20, timeCreated);
		stmt.setString(21, dateCreated);
		stmt.setString(22, timeCrawled);
		stmt.setString(23, dateCrawled);

		// Print out the SQL statement for debug purpose
		Globals.crawlerLogManager.writeLog(stmt.toString());
		stmt.executeUpdate();

		// Insert into article_content_table table
		ResultSet generatedKeys = stmt.getGeneratedKeys();

		if (generatedKeys.next()) {
			int watchId = generatedKeys.getInt(1);
			return watchId;
		}

		return -1;
	}

	// Add new watch entry into the database
	public boolean addWatchEntry(String link, Globals.Domain[] domains,
			String watchName, int[] prices, String[] keywords, String[] topics,
			String timeCreated, String dateCreated, String timeCrawled,
			String dateCrawled, String content, String refNo, String movement,
			String caliber, String watchCondition, int watchYear,
			String caseMaterial, String dialColor, String gender,
			String location1, String location2, String location3) {
		// Get the id of domains and topics
		Integer[] domainsId = Helper.convertDomainToDomainId(domains);
		Integer[] topicsId = Helper.convertTopicToTopicId(topics);
		
		if (domainsId == null || topicsId == null)
			return false;
		
		try {
			// Try to insert a new entry into watch_desc_table
			int watchId = insertIntoWatchDescTable(link, domains, watchName,
					prices, keywords, refNo, movement, caliber, watchCondition,
					watchYear, caseMaterial, dialColor, gender, location1,
					location2, location3, topics, timeCreated, dateCreated,
					timeCrawled, dateCrawled, domainsId, topicsId);

			// Fail to insert new watch entry, return right away
			if (watchId < 0)
				return false;

			this.addWatchPageContent(watchId, content);

		} catch (Exception e) {
			Globals.crawlerLogManager
					.writeLog("Fail to insert into watch_desc_table");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		}

		return true;
	}

	// Add content for article into article_content_table
	public boolean addWatchPageContent(int watchId, String content) {
		if (content == null) {
			Globals.crawlerLogManager
					.writeLog("Insert null into watch_page_content_table");
			return false;
		} else {
			content = HTMLCompressor.compressHtmlContent(content);
		}

		PreparedStatement stmt = null;

		try {
			// Insert into article_content_table table
			stmt = this.con
					.prepareStatement("INSERT INTO watch_page_content_table (watch_table_id, content) values (?, ?)");
			stmt.setInt(1, watchId);
			stmt.setString(2, content);
			stmt.executeUpdate();
		} catch (Exception e1) {
			try {
				// Try to update the content of article_content_table table
				stmt = this.con
						.prepareStatement("UPDATE watch_page_content_table SET content = ? WHERE watch_table_id = ?");
				stmt.setString(1, content);
				stmt.setInt(2, watchId);
				stmt.executeUpdate();
			} catch (Exception e2) {
				Globals.crawlerLogManager
						.writeLog("Fail to insert into watch_page_content_table");
				Globals.crawlerLogManager.writeLog(e2.getMessage());

				return false;
			}
		}

		return true;
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
			Globals.crawlerLogManager
					.writeLog("Get article_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
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
			Globals.crawlerLogManager
					.writeLog("Get article_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return numArticleWithTopicId;
	}

	// Get content of article with given id
	public ResultSet getArticleContent(int lowerBound, int maxNumResult) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT * FROM article_content_table";

			if (lowerBound > 0 || maxNumResult > 0)
				query += " LIMIT " + lowerBound + "," + maxNumResult;

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Get article_content_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Get content of article with given id
	public ResultSet getWatchPageContent(int lowerBound, int maxNumResult) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT * FROM watch_page_content_table";

			if (lowerBound > 0 || maxNumResult > 0)
				query += " LIMIT " + lowerBound + "," + maxNumResult;

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Get watch_page_content_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
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
			Globals.crawlerLogManager
					.writeLog("Get article_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
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

			String query = "SELECT * FROM watch_desc_table";

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
			Globals.crawlerLogManager
					.writeLog("Get article_table information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Update the topic_table_id_1 of watch_desc_table to topicId
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
			String updateQuery = "UPDATE watch_desc_table SET topic_table_id_1 = NULL, topic_table_id_2 = NULL WHERE id = "
					+ watchId;
			st.executeUpdate(updateQuery);

			// Insert the new info for the specified watch
			updateQuery = "UPDATE watch_desc_table SET topic_table_id_1 = "
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
			Globals.crawlerLogManager
					.writeLog("Update watch_desc_table topic information fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
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
			return false;
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

			Globals.crawlerLogManager.writeLog(stmt.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Insert into watch_price_stat_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		}

		return true;
	}

	// Remove article from article_table and associated table
	public boolean removeArticle(int articleId) {
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
			Globals.crawlerLogManager
					.writeLog("Fail to delete article with ID " + articleId);
			Globals.crawlerLogManager.writeLog(e.getMessage());

			return false;
		}

		return true;
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

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog(stmt.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Insert into link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
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

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog(stmt.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			Globals.crawlerLogManager
					.writeLog("Insert into link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			return false;
		}

		return true;
	}

	// Get link from the queue given the domainId
	public ResultSet getLinkQueue(int domainId) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT link FROM link_queue_table WHERE domain_table_id_1 = "
					+ domainId;

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}
	
	public ResultSet getLinkQueue() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT link FROM link_queue_table";

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Get link from the crawled set given the domainId
	public ResultSet getLinkCrawled(int domainId) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT link FROM link_crawled_table WHERE domain_table_id_1 = "
					+ domainId;

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}
	
	public ResultSet getLinkCrawled() {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "SELECT link FROM link_crawled_table";

			return st.executeQuery(query);
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Get link_crawled_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return null;
	}

	// Remove link from link_queue_table
	public boolean removeFromLinkQueueTable(String link, int domainId) {
		try {
			Statement st = this.con.createStatement();
			st.executeQuery("USE " + this.database);

			String query = "DELETE FROM link_queue_table WHERE domain_table_id_1 = ? AND link = ? AND persistent = 0";
			PreparedStatement stmt = this.con.prepareStatement(query);
			stmt.setInt(1, domainId);
			stmt.setString(2, link);

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog(stmt.toString());
			// execute the preparedstatement
			stmt.execute();
		} catch (SQLException e) {
			Globals.crawlerLogManager.writeLog("Delete link_queue_table fails");
			Globals.crawlerLogManager.writeLog(e.getMessage());
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
	}
}
