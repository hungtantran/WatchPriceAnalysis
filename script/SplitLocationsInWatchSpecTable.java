package script;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import commonlib.Helper;

public class SplitLocationsInWatchSpecTable {
	private static String username = "root";
	private static String password = "";
	private static String server = "localhost";
	private static String database = "newscrawler";

	public static void main(String[] args) {
//		Connection con = null;
//
//		// Set up sql connection
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			con = DriverManager.getConnection("jdbc:mysql://" + server,
//					username, password);
//		} catch (Exception e) {
//			System.out.println("Error connecting to mysql database");
//			return;
//		}
//
//		int count = 16;
//		while (true) {
//			ResultSet result = null;
//			int temp = 0;
//			try {
//				Statement st = con.createStatement();
//				st.executeQuery("USE " + database);
//
//				String query = "SELECT * FROM watch_spec_table LIMIT " + count
//						+ ", 1000";
//				result = st.executeQuery(query);
//				
//				while (result.next()) {
//					temp++;
//					int watchTableId = result.getInt(1);
//					// Split location into parts
//					String location = result.getString(12);
//					String[] locations = Helper.splitString(location, ",");
//
//					String location1 = null;
//					String location2 = null;
//					String location3 = null;
//					if (locations.length >= 1)
//						location1 = locations[0].trim();
//					if (locations.length >= 2)
//						location2 = locations[1].trim();
//					if (locations.length >= 3)
//						location3 = locations[2].trim();
//
//					String update = "UPDATE watch_spec_table SET location_1 = ?, location_2 = ?, location_3 = ? WHERE watch_table_id = ?";
//					PreparedStatement stmt = con.prepareStatement(update);
//
//					if (location1 != null) {
//						stmt.setString(1, location1);
//					} else {
//						stmt.setNull(1, java.sql.Types.CHAR);
//					}
//
//					if (location2 != null) {
//						stmt.setString(2, location2);
//					} else {
//						stmt.setNull(2, java.sql.Types.CHAR);
//					}
//
//					if (location3 != null) {
//						stmt.setString(3, location3);
//					} else {
//						stmt.setNull(3, java.sql.Types.CHAR);
//					}
//
//					stmt.setInt(4, watchTableId);
//					
//					System.out.println(stmt.toString());
//					stmt.executeUpdate();
//				}
//			} catch (SQLException e) {
//				System.out.println("Get from watch_spec_table fails");
//				e.printStackTrace();
//			}
//			
//			count += 1000;
//			if (temp == 0)
//				break;
//		}
	}
}
