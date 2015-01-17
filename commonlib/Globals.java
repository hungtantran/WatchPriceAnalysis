package commonlib;

import java.util.Map;

import newscrawler.Scheduler;

public class Globals {
	public static final boolean DEBUG = true;
	public static int DEFAULTLOWERBOUNDWAITTIMESEC = 5;
	public static int DEFAULTUPPERBOUNDWAITTIMESEC = 10;
	public static int NUMMAXTHREADS = 5;
	public static int NUMMAXQUEUE = 10;
	
	public static final String[] fileExtenstions = { "jpg", "xml", "gif", "pdf", "png", "jpeg" };
	
	public static String username = "root";
	public static String password = "";
	public static String server = "localhost/";
	public static String database = "newscrawlertest";

	public static Map<Integer, String> idTypeMap = null;
	public static Map<Integer, String> idDomainMap = null;
	public static Map<String, Integer> idTopicMap = null;
	
	public static LogManager crawlerLogManager = new LogManager("crawlerLog", "crawlerLog");
	public static Scheduler scheduler = null;
}
