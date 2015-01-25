package script;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import commonlib.Globals;

public class FixWatchReportLinks {
	public static void main(String[] args) throws Exception {
//		MySqlConnection mysqlConnection = new MySqlConnection();
//
//		ResultSet resultSet = mysqlConnection
//				.getLinkCrawled(Globals.Domain.WATCHREPORT.value);
//
//		Class.forName("com.mysql.jdbc.Driver");
//		Connection con = DriverManager.getConnection("jdbc:mysql://"
//				+ "localhost", "root", "");
//
//		while (resultSet.next()) {
//			String link = resultSet.getString(1);
//			System.out.println(link);
//
//			if (link.indexOf("http://www.watchreport.com") != -1) {
//				String newLink = link.replace("http://www.watchreport.com",
//						"http://watchreport.com");
//				System.out.println(newLink);
//
//				if (newLink.indexOf("http://watchreport.com") == 0) {
//					Statement st = con.createStatement();
//					st.executeQuery("USE newscrawler");
//
//					System.out
//							.println("Delete from link_crawled_table where link = '"
//									+ newLink + "'");
//					st.executeUpdate("Delete from link_crawled_table where link = '"
//							+ newLink + "'");
//
//					st.executeUpdate("Update link_crawled_table set link = '"
//							+ newLink + "' where link = '" + link + "'");
//					System.out.println("Update link_crawled_table set link = '"
//							+ newLink + "' where link = '" + link + "'");
//
//					ResultSet result = st
//							.executeQuery("Select id from article_table where link = '"
//									+ newLink + "'");
//					int articleId = -1;
//					while (result.next()) {
//						articleId = result.getInt(1);
//					}
//
//					if (articleId > 0) {
//						System.out
//								.println("Delete from article_topic_table WHERE article_table_id = '"
//										+ articleId + "'");
//						st.executeUpdate("Delete from article_topic_table WHERE article_table_id = '"
//								+ articleId + "'");
//
//						System.out
//								.println("Delete from article_content_table WHERE article_table_id = '"
//										+ articleId + "'");
//						st.executeUpdate("Delete from article_content_table WHERE article_table_id = '"
//								+ articleId + "'");
//					}
//
//					System.out
//							.println("Delete from article_table WHERE link = '"
//									+ newLink + "'");
//					st.executeUpdate("Delete from article_table WHERE link = '"
//							+ newLink + "'");
//
//					st.executeUpdate("Update article_table set link = '"
//							+ newLink + "' where link = '" + link + "'");
//					System.out.println("Update article_table set link = '"
//							+ newLink + "' where link = '" + link + "'");
//				}
//			}
//		}
	}
}
