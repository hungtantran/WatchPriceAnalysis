package script;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import commonlib.Globals;
import commonlib.TopicComparator;

public class InsertLinksIntoLinkTable {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		MySqlConnection mysqlConnection = new MySqlConnection();
//		Iterator<Entry<Domain, String>> it = Globals.domainNameMap.entrySet().iterator();
//		
//		while (it.hasNext()) {
//			Map.Entry<Domain, String> pairs = (Map.Entry<Domain, String>) it
//					.next();
//			
//			String crawlerId = pairs.getValue().toLowerCase();
//			int domainId = pairs.getKey().value;
//		
//			// Try to deserialize the saved term on disk into memory
//			try (InputStream file = new FileInputStream(crawlerId
//					+ "_urlsQueue.ser");
//					InputStream buffer = new BufferedInputStream(file);
//					ObjectInput input = new ObjectInputStream(buffer);) {
//				Queue<String> tempQueue = (Queue<String>) input.readObject();
//	
//				while (!tempQueue.isEmpty()) {
//					String link = tempQueue.remove();
//					Integer priority = TopicComparator.getStringPriority(link);
//					mysqlConnection.insertIntoLinkQueueTable(link, domainId, priority, 0, null, null);
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//	
//			try (InputStream file = new FileInputStream(crawlerId
//					+ "_urlsCrawled.ser");
//					InputStream buffer = new BufferedInputStream(file);
//					ObjectInput input = new ObjectInputStream(buffer);) {
//				Set<String> urlsCrawled = (Set<String>) input.readObject();
//				
//				for (String link : urlsCrawled) {
//					Integer priority = TopicComparator.getStringPriority(link);
//					mysqlConnection.insertIntoLinkCrawledTable(link, domainId, priority, null, null);
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
	}
}
