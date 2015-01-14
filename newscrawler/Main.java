package newscrawler;

import commonlib.Globals;

public class Main {
	public static void main(String[] args) {
		if (!MainCrawler.startUpState())
			return;

		Globals.scheduler = new Scheduler(Globals.crawlerLogManager,
				Globals.con, Globals.NUMMAXTHREADS, Globals.NUMMAXQUEUE);
		Globals.scheduler.start();
	}
}
