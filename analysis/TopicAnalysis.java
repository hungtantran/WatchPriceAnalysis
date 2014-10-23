package analysis;

import java.sql.ResultSet;
import java.util.Set;

import commonlib.Globals;
import commonlib.Helper;
import dbconnection.MySqlConnection;


public class TopicAnalysis {
	private MySqlConnection mysqlConnection = null;

	public TopicAnalysis() {
		this.mysqlConnection = new MySqlConnection();
	}

	// Go through existing article, try to identify topic of them
	public void populateArticleTopic() {
		int lowerBound = 0;
		int maxNumResult = 2000;
		int articleCount = lowerBound;

		// Get 2000 articles at a time, until exhaust all the articles
		while (true) {
			ResultSet resultSet = this.mysqlConnection.getArticleInfo(
					lowerBound, maxNumResult);
			if (resultSet == null)
				break;

			try {
				int count = 0;
				// Iterate through the result set to populate the information
				while (resultSet.next()) {
					count++;
					articleCount++;

					int articleId = resultSet.getInt(1);
					String articleName = resultSet.getString(6).trim();
					if (Globals.DEBUG)
						System.out.println("(" + articleCount + ") Article id "
								+ articleId + ": " + articleName);

					Set<String> topicsOfName = Helper.identifyTopicOfName(
							articleName, Globals.HOROLOGYTOPICS);
					String[] topics = new String[topicsOfName.size()];

					int index = 0;
					for (String topic : topicsOfName) {
						topics[index] = topic;
						index++;
					}

					Integer[] topicsId = this.mysqlConnection
							.convertTopicToTopicId(topics);

					if (Globals.DEBUG)
						System.out.println("Topics " + topics.toString() + ": "
								+ topicsId.toString());

					// Insert into article_topic_table table
					for (int topicId : topicsId) {
						if (!this.mysqlConnection.addArticleTopicRelationship(
								articleId, topicId))
							continue;
					}
				}

				if (count == 0)
					break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

			lowerBound += maxNumResult;
		}
	}

	// Go through existing watch entry, try to identify topic of them
	public void populateWatchTopic(boolean fullPopulate) {
		int lowerBound = 0;
		int maxNumResult = 2000;
		int watchCount = lowerBound;
		boolean result = true;
		
		// Get 2000 articles at a time, until exhaust all the articles
		while (true) {
			ResultSet resultSet = null;
			
			if (fullPopulate) {
				resultSet = this.mysqlConnection.getWatchInfo(0, lowerBound, maxNumResult);
			} else {
				resultSet = this.mysqlConnection.getWatchInfo(null, lowerBound, maxNumResult);
			}
			
			if (resultSet == null)
				break;

			try {
				int count = 0;
				// Iterate through the result set to populate the information
				while (resultSet.next()) {
					count++;
					watchCount++;

					int watchId = resultSet.getInt(1);
					String watchName = resultSet.getString(6).trim();
					if (Globals.DEBUG)
						System.out.println("(" + watchCount + ") Watch id "
								+ watchId + ": " + watchName);

					Set<String> topicsOfName = Helper.identifyTopicOfName(
							watchName, Globals.HOROLOGYTOPICS);
					String[] topics = new String[topicsOfName.size()];

					int index = 0;
					for (String topic : topicsOfName) {
						topics[index] = topic;
						index++;
					}

					Integer[] topicsId = this.mysqlConnection
							.convertTopicToTopicId(topics);

					if (Globals.DEBUG)
						System.out.println("Topics " + topics.toString() + ": "
								+ topicsId.toString());
					
					// Insert into article_topic_table table
					result = this.mysqlConnection.updateWatchTopic(watchId, topicsId);
					
					if (!result && Globals.DEBUG)
						System.out.println("Fail to insert new topic for watch id "+watchId + ": "+watchName);
				}

				if (count == 0)
					break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			
			if (!result) break;
			lowerBound += maxNumResult;
		}
	}

	public static void main(String[] args) {
		TopicAnalysis topicAnalysis = new TopicAnalysis();
		topicAnalysis.populateArticleTopic();
		topicAnalysis.populateWatchTopic(false);
		//topicAnalysis.populateWatchTopic(true);
	}
}
