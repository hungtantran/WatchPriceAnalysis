package commonlib;

import java.io.File;

import newscrawler.Scheduler;

public class Globals {
	public static final boolean DEBUG = true;
	public static int DEFAULTLOWERBOUNDWAITTIMESEC = 30;
	public static int DEFAULTUPPERBOUNDWAITTIMESEC = 60;
	public static int NUMMAXTHREADS = 5;
	public static int NUMMAXQUEUE = 10;

	public static final String[] fileExtenstions = { "jpg", "xml", "gif", "pdf", "png", "jpeg" };

	public static String username = "root";
	public static String password = "";
	public static String server = "localhost/";
	public static String database = "newscrawler";

	public static LogManager crawlerLogManager = new LogManager("crawlerLog", "crawlerLog");
	public static Scheduler scheduler = null;

	public static String pathSeparator = File.separator;
}
