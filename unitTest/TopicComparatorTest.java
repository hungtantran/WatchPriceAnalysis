package unitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;

import org.junit.Test;

import commonlib.TopicComparator;

public class TopicComparatorTest {

	@Test
	public void test() {
		TestgetStringPriority();
		TestComparator();
	}
	
	public void TestgetStringPriority() {
		Integer result = null;
		
		result = TopicComparator.getStringPriority("http://www.chrono24.com/en/rolex/rolex-seadweller-deepsea-116660--neu--verklebt--id1997638.htm");
		assertTrue(result != null && result == 1);
		
		result = TopicComparator.getStringPriority("http://www.chrono24.com/en/omega/seamaster-planet-ocean--id2703969.htm");
		assertTrue(result != null && result == 2);
		
		result = TopicComparator.getStringPriority("http://www.chrono24.com/en/patekphilippe/orologio--twenty-4-491010a-001--id2813833.htm");
		assertTrue(result != null && result == 3);
		
		result = TopicComparator.getStringPriority("http://www.chrono24.com/en/audemarspiguet/royal-oak-qp-blue-dial-automatic-in-steel-2008-b--p--id2839848.htm");
		assertTrue(result != null && result == 4);
		
		result = TopicComparator.getStringPriority("http://www.chrono24.com/en/breitling/superocean-a17391-stainless-steel-black-quick-set-44mm-watch--id2869228.htm");
		assertTrue(result == null);
	}
	
	public void TestComparator() {
		PriorityQueue<String> urlsQueue = new PriorityQueue<String>(100, new TopicComparator());
		urlsQueue.add("http://www.chrono24.com/en/audemarspiguet/royal-oak-qp-blue-dial-automatic-in-steel-2008-b--p--id2839848.htm");
		urlsQueue.add("http://www.chrono24.com/en/omega/seamaster-planet-ocean--id2703969.htm");
		urlsQueue.add("http://www.chrono24.com/en/rolex/rolex-seadweller-deepsea-116660--neu--verklebt--id1997638.htm");
		urlsQueue.add("http://www.chrono24.com/en/breitling/superocean-a17391-stainless-steel-black-quick-set-44mm-watch--id2869228.htm");
		urlsQueue.add("http://www.chrono24.com/en/patekphilippe/orologio--twenty-4-491010a-001--id2813833.htm");
		
		assertEquals(urlsQueue.size(), 5);
		assertEquals(urlsQueue.remove(), "http://www.chrono24.com/en/rolex/rolex-seadweller-deepsea-116660--neu--verklebt--id1997638.htm");
		assertEquals(urlsQueue.remove(), "http://www.chrono24.com/en/omega/seamaster-planet-ocean--id2703969.htm");
		assertEquals(urlsQueue.remove(), "http://www.chrono24.com/en/patekphilippe/orologio--twenty-4-491010a-001--id2813833.htm");
		assertEquals(urlsQueue.remove(), "http://www.chrono24.com/en/audemarspiguet/royal-oak-qp-blue-dial-automatic-in-steel-2008-b--p--id2839848.htm");
		assertEquals(urlsQueue.remove(), "http://www.chrono24.com/en/breitling/superocean-a17391-stainless-steel-black-quick-set-44mm-watch--id2869228.htm");
		assertEquals(urlsQueue.size(), 0);
	}
}
