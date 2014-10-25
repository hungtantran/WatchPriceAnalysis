package newscrawler;

import java.sql.ResultSet;
import java.util.HashMap;

import commonlib.Globals;
import commonlib.LogManager;
import dbconnection.MySqlConnection;

public class MainCrawler {
	// Populate information of type, domain and topic information from existing
	// database
	public static boolean getDatabaseExistingInfo() {
		Globals.idTypeMap = new HashMap<Integer, String>();
		Globals.idDomainMap = new HashMap<Integer, String>();
		Globals.idTopicMap = new HashMap<String, Integer>();

		if (!MainCrawler.getTypeInfo())
			return false;

		if (!MainCrawler.getDomainInfo())
			return false;

		if (!MainCrawler.getTopicInfo())
			return false;

		return true;
	}

	public static boolean getDomainInfo() {
		if (Globals.con == null)
			return false;

		ResultSet resultSet = Globals.con.getDomainInfo();

		if (resultSet == null)
			return false;

		try {
			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String domain = resultSet.getString(2).trim();
				Globals.idDomainMap.put(id, domain);
			}

			if (Globals.DEBUG)
				Globals.crawlerLogManager.writeLog("Got "
						+ Globals.idDomainMap.size() + " domains");

			return true;
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return false;
	}

	public static boolean getTypeInfo() {
		if (Globals.con == null)
			return false;

		ResultSet resultSet = Globals.con.getTypeInfo();

		if (resultSet == null)
			return false;

		try {
			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String type = resultSet.getString(2).trim();
				Globals.idTypeMap.put(id, type);
			}

			Globals.crawlerLogManager.writeLog("Got "
					+ Globals.idTypeMap.size() + " types");

			return true;
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return false;
	}

	public static boolean getTopicInfo() {
		if (Globals.con == null)
			return false;

		ResultSet resultSet = Globals.con.getTopicInfo();

		if (resultSet == null)
			return false;

		try {
			// Iterate through the result set to populate the information
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				String topic = resultSet.getString(3).trim();
				Globals.idTopicMap.put(topic, id);
			}

			Globals.crawlerLogManager.writeLog("Got "
					+ Globals.idTopicMap.size() + " topics");

			return true;
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}

		return false;
	}

	public static boolean startUpState() {
		// TODO pass inconnection properties
		Globals.crawlerLogManager = new LogManager("crawlerLog", "crawlerLog");
		Globals.con = new MySqlConnection();

		if (!MainCrawler.getDatabaseExistingInfo())
			return false;

		return true;
	}

	public static void main(String[] args) {
		if (!MainCrawler.startUpState())
			return;

		Globals.scheduler = new Scheduler(Globals.crawlerLogManager,
				Globals.con, Globals.NUMMAXTHREADS);
		Globals.scheduler.start();
	}
}